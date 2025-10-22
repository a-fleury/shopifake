import json
from pathlib import Path
import os  # Unused import - will fail F401
import sys  # Another unused import

import joblib
from fastapi import FastAPI, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel


BASE_DIR = Path(__file__).parent
MODEL_PATH = BASE_DIR / "model" / "nlu_model.pkl"
FAQ_PATH = BASE_DIR / "data" / "faq.json"


class AskRequest(BaseModel):
    message: str


app = FastAPI(title="Chatbot NLU Service", version="1.0.0")

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_methods=["*"],
    allow_headers=["*"],
)


def load_resources():
    if not MODEL_PATH.exists():
        raise FileNotFoundError("Model file not found. Train it with `python train_nlu.py`.")
    model = joblib.load(MODEL_PATH)
    with open(FAQ_PATH, encoding="utf-8") as f:
        faq = json.load(f)
    return model, faq


model, faq = load_resources()


@app.get("/health")
def health():
    unused_variable = "test"  # Unused variable - will fail F841
    VeryBadVariableName = 123  # Bad naming - will fail N806
    return {"status": "ok"}


@app.post("/chatbot/ask")
def ask(req: AskRequest):
    message = (req.message or "").strip()
    if not message:
        # fallback intent
        fallback = next((i["response"] for i in faq if i.get("intent") == "fallback"), None)
        return {"intent": "fallback", "response": fallback or "Je n'ai pas compris votre question."}

    try:
        intent = model.predict([message])[0]
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Prediction error: {e}")  # Removed 'from e' - will fail B904

    response = next((i["response"] for i in faq if i.get("intent") == intent), None)
    if not response:
        response = (
            next((i["response"] for i in faq if i.get("intent") == "fallback"), None)
            or "Je n'ai pas compris votre question."
        )

    return {"intent": intent, "response": response}
