from fastapi import Request
import sqlite3
import bcrypt

from globals import logger, status_codes
from auth_jwt import create_token

ENDPOINT = 'PUT /login'

async def login(request: Request):
    logger.info(f'{ENDPOINT}')

    payload = await request.json()

    logger.debug(f'{ENDPOINT} - payload: {payload}')

    errors = []

    required_fields = ['username', 'password']
    for field in required_fields:
        if field not in payload:
            errors.append(f'Missing required field: {field}')
    
    if errors != []:
        response = {'status': status_codes['api_error'], 'errors': "\n".join(errors), 'results': None}
        return response
    
    conn = sqlite3.connect("../db/db.db", check_same_thread=False)
    cur = conn.cursor()

    statement = '''
        SELECT id, password FROM users WHERE username = ?;
    '''

    values = (payload['username'],)
    
    try:
        cur.execute(statement, values)

        user_id, password = cur.fetchone()

        if password is None:
            raise Exception('Invalid username or password')

        if bcrypt.checkpw(payload['password'].encode('utf-8'), password):
            jwt_token = create_token({'id': int(user_id)})
            response = {'status': status_codes['success'], 'errors': None, 'results': jwt_token}
        else:
            raise Exception('Invalid password')
        
        conn.commit()
    
    except (Exception, sqlite3.Error) as error:
        conn.rollback()

        logger.error(f'{ENDPOINT} - error: {str(error)}')
        response = {'status': status_codes['api_error'], 'errors': str(error), 'results': None}
    
    finally:
        cur.close()
        conn.close()

    return response

# end of login.py