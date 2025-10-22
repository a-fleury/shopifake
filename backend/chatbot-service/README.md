# Chatbot NLU Service

Petit microservice NLU pour Shopifake basé sur Python, FastAPI et scikit-learn.

## Démarrage rapide

### Prérequis
- Python 3.10+
- pip

### Installation et entraînement (local)

```bash
pip install -r requirements.txt
python train_nlu.py
uvicorn app:app --reload --port 8080
```

Endpoints:
- GET http://localhost:8080/health
- POST http://localhost:8080/chatbot/ask

Exemple de requête JSON:
```json
{ "message": "Quel est le délai de livraison ?" }
```

### Docker

Par défaut l'image ne contient pas encore le modèle. Deux options :
1) Former le modèle avant de lancer le serveur dans le conteneur (au runtime)
```bash
docker build -t chatbot-nlu .
docker run -p 8080:8080 chatbot-nlu sh -c "python train_nlu.py && uvicorn app:app --host 0.0.0.0 --port 8080"
```
2) Ou bien modifier le Dockerfile pour exécuter `python train_nlu.py` au build si vous voulez embarquer le modèle.

## Structure
```
chatbot-service/
├── app.py               # API FastAPI
├── train_nlu.py         # Script d'entraînement du modèle
├── data/
│   └── faq.json         # Données d'intentions/réponses
├── model/               # Dossier où le modèle sera sauvegardé
├── requirements.txt     # Dépendances Python
└── Dockerfile           # Image Docker
```

## Notes
- Modifiez `data/faq.json` pour adapter les intentions.
- Relancez `python train_nlu.py` après modification des données.
- L’endpoint `/health` retourne `{ "status": "ok" }` pour un check simple.
