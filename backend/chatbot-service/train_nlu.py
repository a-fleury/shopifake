import json
from pathlib import Path

import joblib
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.linear_model import LogisticRegression
from sklearn.pipeline import Pipeline


DATA_PATH = Path(__file__).parent / "data" / "faq.json"
MODEL_DIR = Path(__file__).parent / "model"
MODEL_PATH = MODEL_DIR / "nlu_model.pkl"


def load_data(path: Path):
    with open(path, encoding="utf-8") as f:
        faq = json.load(f)
    x, y = [], []
    for item in faq:
        intent = item.get("intent")
        for pattern in item.get("patterns", []):
            if pattern:
                x.append(pattern)
                y.append(intent)
    return x, y


def build_pipeline():
    return Pipeline(
        [
            ("vectorizer", TfidfVectorizer(ngram_range=(1, 2), min_df=1)),
            ("clf", LogisticRegression(max_iter=1000)),
        ]
    )


def main():
    if not DATA_PATH.exists():
        raise FileNotFoundError(f"Data file not found: {DATA_PATH}")

    x, y = load_data(DATA_PATH)
    if not x:
        raise ValueError("No training data found. Please add patterns in data/faq.json")

    model = build_pipeline()
    model.fit(x, y)

    MODEL_DIR.mkdir(parents=True, exist_ok=True)
    joblib.dump(model, MODEL_PATH)
    print(f"Model trained and saved to {MODEL_PATH}")


if __name__ == "__main__":
    main()
