from fastapi import Request
import sqlite3
import bcrypt
from globals import logger, status_codes

ENDPOINT = 'POST /register'

async def register(request: Request):
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
        INSERT INTO users (username, password) VALUES (?, ?)
        RETURNING id;
    '''

    password = bcrypt.hashpw(payload['password'].encode('utf-8'), bcrypt.gensalt()).decode('utf-8')

    values = (payload['username'], password,)
    
    try:
        cur.execute(statement, values)

        result = cur.fetchone()

        if result is None:
            raise Exception('User registration failed')
        
        response = {'status': status_codes['success'], 'errors': None, 'results': result[0]}

        conn.commit()

    except (Exception, sqlite3.Error) as error:
        conn.rollback()

        logger.error(f'{ENDPOINT} - error: {error}')
        response = {'status': status_codes['api_error'], 'errors': str(error), 'results': None}
        
    finally:
        cur.close()
        conn.close()

    return response

# end of register.py