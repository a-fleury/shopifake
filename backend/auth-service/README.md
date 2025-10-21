# Auth Service avec Keycloak

Service d'authentification simple utilisant Keycloak pour Shopifake.

## 🚀 Démarrage rapide

### Prérequis
- Docker et Docker Compose
- Java 17 (pour le développement local)
- Maven (pour le développement local)

### Lancer avec Docker Compose

```bash
cd ../..  # Retour à la racine du projet
docker-compose up -d
```

Cela va démarrer :
- **Keycloak** sur http://localhost:8080
- **PostgreSQL** pour Keycloak
- **Auth Service** sur http://localhost:8081

### Configuration Keycloak (100% Automatique ✨)

**Aucune configuration manuelle requise !**

Le realm `shopifake` est automatiquement importé au démarrage avec :
- ✅ Client `shopifake-client` pré-configuré
- ✅ Client secret : `ZGr8yBKqP3kX9wN2vL5mH7jT4cF6sA1d`
- ✅ Rôles `user` et `admin`
- ✅ CORS et redirections configurés

La configuration est dans `keycloak/realm-export.json` et importée automatiquement par Keycloak.

## 📡 API Endpoints

### Health Check
```bash
GET /api/auth/health
```

### Register (Inscription)
```bash
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe"
}
```

### Login (Connexion)
```bash
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

Réponse :
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "eyJhbGc...",
  "tokenType": "Bearer",
  "expiresIn": 300,
  "username": "john_doe"
}
```

### Refresh Token
```bash
POST /api/auth/refresh
Content-Type: application/json

{
  "refreshToken": "your-refresh-token"
}
```

## 🛠️ Développement local

### Build
```bash
mvn clean package
```

### Run
```bash
mvn spring-boot:run
```

### Configuration
Les variables d'environnement peuvent être définies dans `application.yml` ou via des variables d'environnement :

- `KEYCLOAK_SERVER_URL` : URL du serveur Keycloak (défaut: http://localhost:8080)
- `KEYCLOAK_REALM` : Nom du realm (défaut: shopifake)
- `KEYCLOAK_CLIENT_ID` : ID du client (défaut: shopifake-client)
- `KEYCLOAK_CLIENT_SECRET` : Secret du client
- `KEYCLOAK_ADMIN_USERNAME` : Username admin Keycloak (défaut: admin)
- `KEYCLOAK_ADMIN_PASSWORD` : Password admin Keycloak (défaut: admin)

## 🧪 Test avec curl

### Register
```bash
curl -X POST http://localhost:8081/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "test123",
    "firstName": "Test",
    "lastName": "User"
  }'
```

### Login
```bash
curl -X POST http://localhost:8081/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "test123"
  }'
```

## 🔒 Sécurité

- Les mots de passe sont gérés par Keycloak
- Les tokens JWT sont signés par Keycloak
- CORS est configuré pour accepter les origines locales
- Les sessions sont stateless (JWT)

## 📝 Notes

- Ce service utilise OAuth2/OpenID Connect via Keycloak
- Les tokens JWT contiennent les informations d'authentification
- Le refresh token permet de renouveler l'access token sans re-authentification
