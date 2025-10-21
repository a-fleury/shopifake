# Guide des tests pour Auth Service

## Structure des tests

```
src/test/java/com/shopifake/auth/
├── controller/
│   ├── AuthControllerTest.java       # Tests unitaires du contrôleur d'auth
│   ├── UserControllerTest.java       # Tests unitaires du contrôleur user
│   └── TestSecurityConfig.java       # Configuration de sécurité pour les tests
└── service/
    └── AuthServiceTest.java           # Tests unitaires du service métier
```

## Exécuter les tests

### Tous les tests
```bash
cd backend/auth-service
mvn test
```

### Tests avec couverture de code
```bash
mvn test jacoco:report
```

Le rapport de couverture sera disponible dans `target/site/jacoco/index.html`

### Tests d'un fichier spécifique
```bash
mvn test -Dtest=AuthControllerTest
```

### Tests avec logs détaillés
```bash
mvn test -X
```

## Description des tests

### AuthControllerTest
Tests du contrôleur REST d'authentification :
- ✅ `healthCheck_shouldReturnOk` - Vérifie le endpoint de santé
- ✅ `login_withValidCredentials_shouldReturnTokens` - Login réussi
- ✅ `login_withInvalidCredentials_shouldReturnUnauthorized` - Login échoué
- ✅ `login_withMissingUsername_shouldReturnBadRequest` - Validation username
- ✅ `login_withMissingPassword_shouldReturnBadRequest` - Validation password
- ✅ `register_withValidData_shouldReturnCreated` - Inscription réussie
- ✅ `register_withExistingUsername_shouldReturnBadRequest` - User existant
- ✅ `register_withInvalidEmail_shouldReturnBadRequest` - Validation email
- ✅ `register_withShortPassword_shouldReturnBadRequest` - Validation mot de passe
- ✅ `refreshToken_withValidToken_shouldReturnNewTokens` - Refresh réussi
- ✅ `refreshToken_withInvalidToken_shouldReturnUnauthorized` - Refresh échoué

### UserControllerTest
Tests des endpoints protégés :
- ✅ `protectedPing_withAuthentication_shouldReturnPong` - Endpoint protégé avec JWT
- ✅ `me_withAuthentication_shouldReturnUserInfo` - Info utilisateur

### AuthServiceTest
Tests du service métier :
- ✅ `login_withValidCredentials_shouldReturnAuthResponse` - Logique de login
- ✅ `refreshToken_shouldReturnNewTokens` - Logique de refresh
- ✅ `register_withValidData_shouldCreateUser` - Création utilisateur dans Keycloak
- ✅ `register_whenCreationFails_shouldThrowException` - Gestion d'erreur

## Technologies utilisées

- **JUnit 5** - Framework de test
- **Mockito** - Mocking des dépendances
- **MockMvc** - Tests des contrôleurs REST
- **Spring Security Test** - Tests de sécurité
- **@WebMvcTest** - Tests de la couche web sans le contexte complet
- **@MockBean** - Mock des beans Spring

## Bonnes pratiques

### Structure des tests (Given-When-Then)
```java
@Test
void testName() {
    // Given - Préparation des données et mocks
    String input = "test";
    when(service.method()).thenReturn(expected);
    
    // When - Exécution de l'action à tester
    Result result = controller.action(input);
    
    // Then - Vérification des résultats
    assertEquals(expected, result);
    verify(service).method();
}
```

### Nommage des tests
Format: `methodName_condition_expectedResult`
- Exemple: `login_withInvalidCredentials_shouldReturnUnauthorized`

### Couverture de code
Objectif: > 80% de couverture
- Tester les cas normaux
- Tester les cas d'erreur
- Tester les validations
- Tester les edge cases

## Ajouter de nouveaux tests

1. Créer une classe de test avec le suffixe `Test`
2. Annoter avec `@ExtendWith(MockitoExtension.class)` ou `@WebMvcTest`
3. Injecter les mocks nécessaires avec `@Mock` ou `@MockBean`
4. Écrire les tests en suivant Given-When-Then
5. Exécuter `mvn test` pour valider

## CI/CD

Les tests sont automatiquement exécutés dans la pipeline CI/CD :
- À chaque push sur une branche
- À chaque Pull Request
- Avant chaque déploiement

Un build échoue si :
- Un test échoue
- La couverture de code est < 70%
- Il y a des erreurs de compilation
