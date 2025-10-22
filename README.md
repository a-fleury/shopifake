# 🛍️ Shopifake – Projet DevOps & IA

Application web inspirée de Shopify permettant :
- La gestion d’un **catalogue produits** (CRUD, variantes, stocks, médias, catégories)
- Un **front e-commerce léger** pour la navigation et la consultation
- Un **moteur de recommandations produits**
- Un **chatbot d’assistance** pour les FAQ et la recherche produits
- Un **cycle DevOps industrialisé** (CI/CD, IaC, observabilité)
- Des **tests multi-niveaux** (unitaires → e2e) et tests de charge

---

## 🚀 Installation et exécution

### 1. Cloner le projet
```bash
git clone https://github.com/a-fleury/shopifake.git
cd shopifake
````

### 2. Démarrer l’environnement (exemple Docker)

```bash
docker-compose up --build
```

### 3. Accéder à l’application

* **Front boutique** : [http://localhost:3000](http://localhost:3000)

---

## 🧩 Structure du projet

```
/backend        # API, logique métier, base de données
/frontend       # Application web (SPA/SSR)
/.github/workflows  # Pipelines CI/CD
README.md       # Documentation principale du projet
```

---

## 🌿 Stratégie GitFlow

Le projet utilise une **organisation GitFlow standard** pour structurer le développement.

| Branche     | Rôle                                                 |
| ----------- | ---------------------------------------------------- |
| `main`      | Code stable en production                            |
| `develop`   | Code en cours d’intégration                          |
| `feature/*` | Développement d’une nouvelle fonctionnalité          |
| `fix/*`     | Correction, debug                                    |

### 🔧 Règles de contribution

1. Créer une branche à partir de `develop` :

   ```bash
   git checkout develop
   git pull
   git checkout -b feature/nom-fonctionnalité
   ```
2. Commiter régulièrement avec des messages clairs.
3. Pousser la branche sur le dépôt :

   ```bash
   git push origin feature/nom-fonctionnalité
   ```
4. Créer une **Pull Request vers `develop`** pour revue.
5. Ne jamais merger directement sur `main`.

---

## 🔁 Pipeline CI/CD

### CI – Intégration Continue

Automatise la validation du code à chaque commit/pull request :

* Analyse statique du code (lint)
* Tests unitaires
* Build des images Docker versionnées
* Vérification des dépendances (SCA)
* Génération d’un **SBOM** (Software Bill of Materials)

Déclencheurs :

* `push` ou `pull_request` sur `develop` ou une branche `feature/*`

### CD – Déploiement Continu

Déploiement automatisé sur les différents environnements :

* **Staging** : déploiement après merge sur `develop`
* **Production** : déploiement après merge sur `main`

Chaque déploiement inclut :

* Health checks
* Migrations de base de données
* Plan de rollback

---

## 🧪 Tests

### Types de tests prévus :

* **Unitaires** : logique métier (≥ 70 % de couverture)
* **Intégration** : API + base de données
* **E2E** : parcours utilisateur complet sur le front
* **Performance** : tests de charge et de montée en charge
* **Sécurité** : contrôle OWASP (injections, XSS, etc.)


## 👥 Équipe

| Membre    | Rôle                 |
| ----------|--------------------- |
| Camille   |                      |
| Poomedy   |                      |
| Alexandre |                      |
| Lennon    |                      |
| Etienne   |                      |

---

## 🪪 Licence

Projet académique – usage éducatif uniquement.
© 2025 – Tous droits réservés à l’équipe Shopifake.
