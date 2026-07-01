"""
Configuration for ML Service
"""

import os
from pathlib import Path

# Service info
SERVICE_NAME = 'BugAI ML Service'
SERVICE_PORT = 5000
SERVICE_HOST = '0.0.0.0'

# Paths
BASE_DIR = Path(__file__).parent
MODELS_DIR = BASE_DIR / 'models'
DATA_DIR = BASE_DIR / 'data'

# Create directories if they don't exist
MODELS_DIR.mkdir(exist_ok=True)
DATA_DIR.mkdir(exist_ok=True)

# Model settings
MIN_TRAINING_SAMPLES = 10
DUPLICATE_SIMILARITY_THRESHOLD = 0.7