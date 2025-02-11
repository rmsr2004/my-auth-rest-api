from fastapi import Request
import sqlite3
import traceback

from globals import logger, status_codes

ENDPOINT = 'POST /auth/add_app'

async def add_app(request: Request, token):
    logger.info(f'{ENDPOINT}')

    payload = await request.json()

    logger.debug(f'{ENDPOINT} - payload: {payload}')

    errors = []

    required_fields = ['issuer', 'secret']
    for field in required_fields:
        if field not in payload:
            errors.append(f'Missing required field: {field}')
            return response
        
    if errors != []:
        response = {'status': status_codes['api_error'], 'errors': "\n".join(errors), 'results': None}
        return response
        
    user_id = token['data']['id']

    conn = sqlite3.connect("../db/db.db", check_same_thread=False)
    cur = conn.cursor()

    statement = '''
        INSERT INTO secrets (issuer, secret, user_id) VALUES (?, ?, ?)
        RETURNING id;
    '''
    values = (payload['issuer'], payload['secret'], user_id)

    try:
        cur.execute(statement, values)
        result = cur.fetchone()

        if not result:
            raise Exception('Secret not added')

        response = {'status': status_codes['success'], 'errors': None, 'results': 'Secret added successfully'}

        conn.commit()

    except (Exception, sqlite3.Error) as error:
        conn.rollback()

        error_message = traceback.format_exc()

        logger.error(f'{ENDPOINT} - error: {error_message}')

        response = {'status': status_codes['api_error'], 'errors': str(error), 'results': None}
    
    finally:
        cur.close()
        conn.close()
    
    return response
    
# end of add_app.py