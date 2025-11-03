# Docker Guide for Chatbot Microservice

## 🎯 What Changed from Basic Dockerfile

### **Before (Basic):**
- Single stage (bloated)
- Python 3.11 (didn't match your dev environment)
- Running as root (security risk)
- No health check
- No .dockerignore (copied unnecessary files)

### **After (Production-Ready):**
- ✅ Multi-stage build (smaller image: ~200MB vs ~400MB)
- ✅ Python 3.12 (matches your local)
- ✅ Non-root user (security best practice)
- ✅ Health check (for Kubernetes/Docker Compose)
- ✅ .dockerignore (excludes .venv, __pycache__, etc.)
- ✅ Optimized layer caching
- ✅ Configurable PORT via environment variable

---

## 🏗️ Key Improvements Explained

### **1. Multi-stage Build**
```dockerfile
# Stage 1: builder - installs deps with build tools
FROM python:3.12-slim as builder
RUN apt-get install gcc g++  # Needed for scikit-learn
RUN pip install --user -r requirements.txt

# Stage 2: runtime - copies only deps, no build tools
FROM python:3.12-slim
COPY --from=builder /root/.local /home/appuser/.local
```

**Why?** Final image doesn't include gcc, build tools → ~50% smaller

---

### **2. Non-root User**
```dockerfile
RUN groupadd -r appuser && useradd -r -g appuser appuser
USER appuser
```

**Why?**
- Security: if container is compromised, attacker doesn't have root
- Best practice for production
- Required by many Kubernetes clusters

---

### **3. Layer Caching Optimization**
```dockerfile
COPY requirements.txt .        # Changes rarely
RUN pip install -r requirements.txt  # Cached if requirements.txt unchanged
COPY . .                        # Changes frequently
```

**Why?** Rebuild is fast when only code changes (pip install cached)

---

### **4. Health Check**
```dockerfile
HEALTHCHECK CMD python -c "import urllib.request; ..."
```

**Why?**
- Docker/Kubernetes can restart unhealthy containers
- Load balancers know when service is ready
- Shows container health in `docker ps`

---

## 🚀 How to Use

### **Build the Image**
```bash
cd ~/Documents/shopifake/backend/chatbot-service

# Development build
docker build -t chatbot-service:dev .

# Production build with tag
docker build -t chatbot-service:1.0.0 .

# Build with custom PORT
docker build --build-arg PORT=8000 -t chatbot-service:latest .
```

### **Run the Container**

**Basic run:**
```bash
docker run -p 8080:8080 chatbot-service:dev
```

**With environment variables:**
```bash
docker run -p 8080:8080 \
  -e PORT=8080 \
  -e MISTRAL_API_KEY=your_key_here \
  chatbot-service:dev
```

**With .env file:**
```bash
docker run -p 8080:8080 --env-file .env chatbot-service:dev
```

**Detached mode (background):**
```bash
docker run -d -p 8080:8080 --name chatbot chatbot-service:dev
```

**Check logs:**
```bash
docker logs -f chatbot
```

**Check health:**
```bash
docker inspect --format='{{.State.Health.Status}}' chatbot
```

### **Stop & Remove**
```bash
docker stop chatbot
docker rm chatbot
```

---

## 📊 Image Size Comparison

| Dockerfile Type | Size | Notes |
|----------------|------|-------|
| **Basic (before)** | ~400MB | Single stage, root user |
| **Multi-stage (after)** | ~200MB | No build tools in final image |
| **Alpine-based** | ~150MB | Even smaller but harder to debug |

---

## 🐛 Common Issues & Solutions

### **Issue 1: "Model file not found"**
```bash
# If model/nlu_model.pkl is missing, train it first
python train_nlu.py

# Then rebuild
docker build -t chatbot-service:dev .
```

### **Issue 2: "Permission denied"**
- Dockerfile now runs as `appuser` (non-root)
- Files must be readable by this user
- COPY commands use `--chown=appuser:appuser`

### **Issue 3: "Build takes forever"**
- Check .dockerignore excludes .venv, __pycache__
- Use BuildKit: `DOCKER_BUILDKIT=1 docker build .`

### **Issue 4: "Health check failing"**
```bash
# Check if /health endpoint works
curl http://localhost:8080/health

# View health logs
docker inspect chatbot | grep Health -A 10
```

---

## 🔒 Security Best Practices

### **✅ DO:**
- Use specific Python version (3.12-slim, not latest)
- Run as non-root user
- Keep secrets in environment variables, not in image
- Scan images: `docker scan chatbot-service:dev`
- Update base image regularly

### **❌ DON'T:**
- Copy .env files into image
- Use `:latest` tag
- Run as root in production
- Include development tools in final image

---

## 🎯 Production Deployment

### **Docker Compose (recommended for local dev)**
```yaml
# docker-compose.yml
version: '3.8'
services:
  chatbot:
    build: ./backend/chatbot-service
    ports:
      - "8080:8080"
    environment:
      - MISTRAL_API_KEY=${MISTRAL_API_KEY}
    healthcheck:
      test: ["CMD", "curl", "-f", "http://localhost:8080/health"]
      interval: 30s
      timeout: 3s
      retries: 3
```

Run: `docker-compose up`

### **Kubernetes**
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: chatbot-service
spec:
  replicas: 3
  template:
    spec:
      containers:
      - name: chatbot
        image: chatbot-service:1.0.0
        ports:
        - containerPort: 8080
        env:
        - name: MISTRAL_API_KEY
          valueFrom:
            secretKeyRef:
              name: chatbot-secrets
              key: mistral-key
        livenessProbe:
          httpGet:
            path: /health
            port: 8080
```

---

## 📦 Advanced: Multi-platform Builds

Build for AMD64 and ARM64 (Apple Silicon, AWS Graviton):
```bash
docker buildx create --use
docker buildx build --platform linux/amd64,linux/arm64 \
  -t chatbot-service:latest .
```

---

## 💡 Quick Tips

1. **Use BuildKit** (faster builds):
   ```bash
   export DOCKER_BUILDKIT=1
   ```

2. **Clean up unused images**:
   ```bash
   docker system prune -a
   ```

3. **View layers**:
   ```bash
   docker history chatbot-service:dev
   ```

4. **Enter running container** (debugging):
   ```bash
   docker exec -it chatbot /bin/bash
   ```

5. **Copy files from container**:
   ```bash
   docker cp chatbot:/app/logs ./logs
   ```

---

## 🧪 Testing the Docker Image

```bash
# Build
docker build -t chatbot-service:test .

# Run
docker run -d -p 8080:8080 --name test-chatbot chatbot-service:test

# Test health endpoint
curl http://localhost:8080/health

# Test chatbot endpoint
curl -X POST http://localhost:8080/chatbot/ask \
  -H "Content-Type: application/json" \
  -d '{"message": "Hello"}'

# Check logs
docker logs test-chatbot

# Cleanup
docker stop test-chatbot && docker rm test-chatbot
```

---

## 📚 Next Steps

1. **Add to CI/CD**: Build Docker image in GitHub Actions
2. **Push to Registry**: Docker Hub, GitHub Container Registry, or AWS ECR
3. **Deploy**: Use Docker Compose, Kubernetes, or cloud services
4. **Monitor**: Add logging, metrics, tracing

Want me to create a docker-compose.yml or add Docker build to your CI pipeline?
