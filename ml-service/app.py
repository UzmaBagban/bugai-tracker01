"""
BugAI ML Service - Flask-based microservice
Port: 5000
"""

from flask import Flask, request, jsonify
from flask_cors import CORS
import logging
from datetime import datetime

# Initialize Flask app
app = Flask(__name__)
CORS(app)

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


@app.route('/health', methods=['GET'])
def health_check():
    """Health check endpoint"""
    return jsonify({
        'status': 'ML Service is running',
        'port': 5000,
        'timestamp': datetime.utcnow().isoformat()
    }), 200


@app.route('/predict/resolution-time', methods=['POST'])
def predict_resolution_time():
    """Predict bug resolution time in hours"""
    try:
        data = request.get_json()

        # Validate input
        required_fields = ['priority', 'severity', 'category', 'description_length']
        if not all(field in data for field in required_fields):
            return jsonify({'error': f'Missing required fields: {required_fields}'}), 400

        # Simple prediction logic (placeholder)
        priority_map = {'HIGH': 1.0, 'MEDIUM': 0.5, 'LOW': 0.2}
        severity_map = {'CRITICAL': 1.0, 'MAJOR': 0.75, 'MINOR': 0.5, 'TRIVIAL': 0.25}

        priority_val = priority_map.get(data['priority'], 0.5)
        severity_val = severity_map.get(data['severity'], 0.5)

        # Simple formula: (priority + severity) * description_length / 10
        predicted_hours = (priority_val + severity_val) * (data['description_length'] / 10)
        predicted_hours = max(1, min(predicted_hours, 168))  # Clamp 1-168 hours

        logger.info(f"Prediction: {predicted_hours:.2f} hours")

        return jsonify({
            'predicted_hours': round(predicted_hours, 2),
            'predicted_days': round(predicted_hours / 24, 2),
            'confidence': 'HIGH',
            'timestamp': datetime.utcnow().isoformat()
        }), 200

    except Exception as e:
        logger.error(f"Error: {str(e)}")
        return jsonify({'error': str(e)}), 500


@app.route('/assign/developer', methods=['POST'])
def assign_developer():
    """Assign best developer for a bug"""
    try:
        data = request.get_json()

        # Placeholder developers
        developers = [
            {'developer_id': 'dev-001', 'name': 'Alice Kumar', 'score': 0.95},
            {'developer_id': 'dev-002', 'name': 'Bob Singh', 'score': 0.85},
            {'developer_id': 'dev-003', 'name': 'Carol Patel', 'score': 0.88}
        ]

        # Sort by score (descending)
        sorted_devs = sorted(developers, key=lambda x: x['score'], reverse=True)

        return jsonify({
            'recommended_developers': sorted_devs,
            'timestamp': datetime.utcnow().isoformat()
        }), 200

    except Exception as e:
        logger.error(f"Error: {str(e)}")
        return jsonify({'error': str(e)}), 500


@app.route('/detect/duplicate', methods=['POST'])
def detect_duplicate():
    """Detect duplicate bugs"""
    try:
        data = request.get_json()

        # Placeholder duplicate detection
        duplicate_candidates = [
            {'bug_id': 'BUG-999', 'similarity_score': 0.85},
            {'bug_id': 'BUG-998', 'similarity_score': 0.72}
        ]

        return jsonify({
            'duplicate_candidates': duplicate_candidates,
            'likely_duplicate': len(duplicate_candidates) > 0,
            'timestamp': datetime.utcnow().isoformat()
        }), 200

    except Exception as e:
        logger.error(f"Error: {str(e)}")
        return jsonify({'error': str(e)}), 500


if __name__ == '__main__':
    logger.info("Starting ML Service on port 5000...")
    app.run(host='0.0.0.0', port=5000, debug=False)