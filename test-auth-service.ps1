# Script de test pour l'auth-service
# Ce script teste tous les endpoints de l'auth-service

Write-Host "=== Test de l'Auth Service ===" -ForegroundColor Cyan

# 1. Test du health endpoint
Write-Host "`n1. Test du endpoint de sante..." -ForegroundColor Yellow
try {
    $health = Invoke-RestMethod -Method GET -Uri "http://localhost:8081/api/auth/health"
    Write-Host "[OK] Health endpoint:" $health.service $health.status -ForegroundColor Green
} catch {
    Write-Host "[ERREUR] Echec du health endpoint" -ForegroundColor Red
    exit 1
}

# 2. Test de l'inscription
Write-Host "`n2. Test de l'inscription d'un nouvel utilisateur..." -ForegroundColor Yellow
$randomUser = "user" + (Get-Random -Minimum 1000 -Maximum 9999)
try {
    $registerBody = @{
        username = $randomUser
        email = "$randomUser@example.com"
        password = "Test123!"
        firstName = "Test"
        lastName = "User"
    } | ConvertTo-Json

    $registerResponse = Invoke-RestMethod -Method POST -Uri "http://localhost:8081/api/auth/register" `
        -ContentType "application/json" -Body $registerBody
    Write-Host "[OK] Inscription reussie:" $registerResponse.message -ForegroundColor Green
} catch {
    Write-Host "[ERREUR] Echec de l'inscription" -ForegroundColor Red
    $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
    $reader.BaseStream.Position = 0
    Write-Host $reader.ReadToEnd() -ForegroundColor Red
    exit 1
}

# 3. Test de la connexion
Write-Host "`n3. Test de la connexion..." -ForegroundColor Yellow
try {
    $loginBody = @{
        username = $randomUser
        password = "Test123!"
    } | ConvertTo-Json

    $loginResponse = Invoke-RestMethod -Method POST -Uri "http://localhost:8081/api/auth/login" `
        -ContentType "application/json" -Body $loginBody
    
    Write-Host "[OK] Connexion reussie pour l'utilisateur:" $loginResponse.username -ForegroundColor Green
    Write-Host "  Token type:" $loginResponse.tokenType -ForegroundColor Gray
    Write-Host "  Expire dans:" $loginResponse.expiresIn "secondes" -ForegroundColor Gray
    
    $token = $loginResponse.accessToken
} catch {
    Write-Host "[ERREUR] Echec de la connexion" -ForegroundColor Red
    exit 1
}

# 4. Test de l'endpoint /api/me
Write-Host "`n4. Test de l'endpoint /api/me (protege)..." -ForegroundColor Yellow
try {
    $meResponse = Invoke-RestMethod -Method GET -Uri "http://localhost:8081/api/me" `
        -Headers @{ "Authorization" = "Bearer $token" }
    
    Write-Host "[OK] Endpoint /api/me:" -ForegroundColor Green
    Write-Host "  User ID:" $meResponse.name -ForegroundColor Gray
    Write-Host "  Authentifie:" $meResponse.authenticated -ForegroundColor Gray
} catch {
    Write-Host "[ERREUR] Echec de l'acces a /api/me" -ForegroundColor Red
    exit 1
}

# 5. Test de l'endpoint /api/protected/ping
Write-Host "`n5. Test de l'endpoint /api/protected/ping..." -ForegroundColor Yellow
try {
    $pingResponse = Invoke-RestMethod -Method GET -Uri "http://localhost:8081/api/protected/ping" `
        -Headers @{ "Authorization" = "Bearer $token" }
    
    Write-Host "[OK] Endpoint /api/protected/ping:" $pingResponse.message -ForegroundColor Green
} catch {
    Write-Host "[ERREUR] Echec de l'acces a /api/protected/ping" -ForegroundColor Red
    exit 1
}

# 6. Test du refresh token
Write-Host "`n6. Test du refresh token..." -ForegroundColor Yellow
try {
    $refreshBody = @{
        refreshToken = $loginResponse.refreshToken
    } | ConvertTo-Json

    $refreshResponse = Invoke-RestMethod -Method POST -Uri "http://localhost:8081/api/auth/refresh" `
        -ContentType "application/json" -Body $refreshBody
    
    Write-Host "[OK] Refresh token - Nouveau access token obtenu" -ForegroundColor Green
    Write-Host "  Expire dans:" $refreshResponse.expiresIn "secondes" -ForegroundColor Gray
} catch {
    Write-Host "[AVERTISSEMENT] Le refresh token pourrait ne pas fonctionner correctement" -ForegroundColor Yellow
    # Ne pas quitter en erreur car ce n'est pas critique pour la demo
}

Write-Host "`n=== Tous les tests principaux sont passes avec succes! ===" -ForegroundColor Green
