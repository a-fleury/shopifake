import os
from pathlib import Path

from dotenv import load_dotenv
from mistralai import Mistral


# Charge les variables d'environnement depuis le fichier .env
load_dotenv()

# Récupère la clé API
api_key = os.getenv("MISTRAL_API_KEY")
if not api_key:
    raise ValueError("La clé API n'est pas définie dans le fichier .env")

# Initialise le client Mistral
client = Mistral(api_key=api_key)


def _load_shop_context() -> str:
    """Charge le contexte de la boutique depuis le fichier `shopifake/backend/chatbot-service/context.md`.

    Retourne le texte complet à utiliser comme message 'system'.
    """
    base = Path(__file__).resolve().parent
    ctx_path = base / "shopifake" / "backend" / "chatbot-service" / "context.md"
    if not ctx_path.exists():
        return ""
    return ctx_path.read_text(encoding="utf-8")


def chat_with_customer(user_message: str, model: str = "mistral-medium-latest") -> str:
    """Envoie `user_message` au modèle Mistral en préfixant le contexte système.

    Inputs:
      - user_message: la question ou message du client.
      - model: nom du modèle Mistral à utiliser.

    Output: La réponse textuelle formatée.

    Comportement: charge le contexte magasin et l'envoie comme message de rôle 'system'
    pour orienter le ton et les informations (délais, retours, etc.).
    """
    system_context = _load_shop_context()

    messages: list[dict[str, str]] = []
    if system_context:
        messages.append({"role": "system", "content": system_context})

    messages.append({"role": "user", "content": user_message})

    resp = client.chat.complete(model=model, messages=messages)  # type: ignore[arg-type]

    # Défensive: vérifier que la structure attendue existe
    try:
        content = resp.choices[0].message.content
        # Convertir en str si nécessaire
        return str(content) if content is not None else ""
    except Exception:
        # Si la réponse n'est pas dans le format attendu, retourner la représentation brute
        return str(resp)


if __name__ == "__main__":
    print("Mode interactif Mistral — tapez 'exit' pour quitter")
    while True:
        q = input("Vous : ")
        if q.strip().lower() in ("exit", "quit"):
            break
        try:
            answer = chat_with_customer(q)
        except Exception as e:
            print(f"Erreur lors de l'appel à l'API : {e}")
            break
        print(f"ShopiFake : {answer}\n")
