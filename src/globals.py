from fastapi.security import OAuth2PasswordBearer
import logging

status_codes = {
    'success': 200,
    'api_error': 400,
    'internal_error': 500
} # Status codes for the API

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="/auth/login")  # OAuth2 scheme for FastAPI

logger = logging.getLogger("uvicorn")
logger.setLevel(logging.DEBUG)
logging.basicConfig(level=logging.DEBUG)  # Set the logging level to debug

# end of globals.py