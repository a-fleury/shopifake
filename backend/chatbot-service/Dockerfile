# ============================================================================
# Multi-stage Dockerfile for Chatbot Microservice
# ============================================================================

# Stage 1: Builder - Install dependencies with build tools
FROM python:3.12-slim AS builder

# Set working directory
WORKDIR /app

# Install system dependencies needed for Python packages
# gcc, build-essential: for compiling Python extensions (scikit-learn, numpy)
RUN apt-get update && apt-get install -y --no-install-recommends \
    gcc \
    g++ \
    && rm -rf /var/lib/apt/lists/*

# Copy only requirements first (better caching)
COPY requirements.txt .

# Install Python dependencies to user directory (no root needed)
# --user: install to ~/.local
# --no-cache-dir: don't cache to reduce image size
# --no-warn-script-location: suppress pip warnings
RUN pip install --user --no-cache-dir --no-warn-script-location \
    -r requirements.txt


# ============================================================================
# Stage 2: Runtime - Minimal production image
# ============================================================================
FROM python:3.12-slim

# Python environment variables
ENV PYTHONUNBUFFERED=1 \
    PYTHONDONTWRITEBYTECODE=1 \
    PATH=/home/appuser/.local/bin:$PATH \
    PORT=8080

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Set working directory
WORKDIR /app

# Copy Python dependencies from builder stage
COPY --from=builder --chown=appuser:appuser /root/.local /home/appuser/.local

# Copy application code
COPY --chown=appuser:appuser . .

# Switch to non-root user
USER appuser

# Expose port (documentation, not enforcement)
EXPOSE ${PORT}

# Health check endpoint (FastAPI /health endpoint)
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
    CMD python -c "import urllib.request; urllib.request.urlopen('http://localhost:${PORT}/health')" || exit 1

# Run uvicorn server
# --host 0.0.0.0: listen on all interfaces (required in containers)
# --port: use PORT env var (default 8080)
# --workers: number of worker processes (default 1, increase for production)
CMD ["sh", "-c", "uvicorn app:app --host 0.0.0.0 --port ${PORT} --workers 1"]
