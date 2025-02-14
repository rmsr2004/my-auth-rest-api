from fastapi import Depends, HTTPException
import jwt
import datetime
import secrets

from utils import oauth2_scheme

SECRET_KEY = secrets.token_hex(64)  # Generate a random secret key to encode/decode JWT tokens
ALGORITHM = 'HS256'                 # Algorithm to encode/decode JWT tokens
TOKEN_EXPIRATION = 120              # Token expiration time in seconds

def create_token(data):
    payload = {
        'data': data,
        'exp': datetime.datetime.now(datetime.timezone.utc) + datetime.timedelta(seconds=TOKEN_EXPIRATION)
    }
    token = jwt.encode(payload, SECRET_KEY, algorithm=ALGORITHM)
    
    return token

def validate_token(token: str = Depends(oauth2_scheme)):
    if not token:
        raise HTTPException(status_code=401, detail='Token is missing')

    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        return payload
    except jwt.ExpiredSignatureError:
        raise HTTPException(status_code=401, detail='Token has expired')
    except jwt.InvalidTokenError:
        raise HTTPException(status_code=401, detail='Invalid token')
    except Exception as error:
        raise HTTPException(status_code=401, detail=str(error))
    
# end of auth_jwt.py