# Étapes simples avant un commit

Ces étapes garantissent que le code du microservice Chatbot respecte le formatage, le lint et les types.
Exécute-les depuis le dossier du microservice: `~/Documents/shopifake/backend/chatbot-service`.

---

## 1) Créer/activer l'environnement virtuel

Dans le dossier chatbot-service, créer ou activer l'environnement virtuel :

### Linux / macOS (bash)
```bash
python3 -m venv .venv
source .venv/bin/activate
```

### Windows (CMD)
```cmd
python -m venv .venv
.venv\Scripts\activate.bat
```

---

## 2) Installer les dépendances

Depuis le dossier du microservice:

### Linux / macOS
```bash
.venv/bin/pip install -r requirements.txt
.venv/bin/pip install -r requirements-dev.txt
```

### Windows
```cmd
.venv\Scripts\pip install -r requirements.txt
.venv\Scripts\pip install -r requirements-dev.txt
```

---

## 3) Vérifier formatage, lint et types

### Option A (recommandé) - Scripts prêts à l'emploi

- Linux/macOS/WSL
```bash
./pre-commit-lint.sh
```

- Windows (CMD/PowerShell)
```cmd
pre-commit-lint.bat
```

Ces scripts:
- formatent avec Ruff (`ruff format`),
- vérifient le lint (`ruff check`),
- vérifient les types (`mypy`).

### Option B - Commandes manuelles (si besoin)
Exécuter depuis ce dossier en ayant activé le venv:

- Linux/macOS
```bash
ruff format . && ruff check . && mypy .
```

- Windows
```cmd
ruff format . && ruff check . && mypy .
```

---

## 4) Commit si tout est OK

```bash
git add -A
git commit -m "feat: commit message"
git push
```

Si un check échoue, corrige les points affichés puis relance le script.
