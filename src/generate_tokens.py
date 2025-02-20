import sqlite3
import traceback
import pyotp

from utils import logger, status_codes

ENDPOINT = 'GET /auth/generate_tokens'

async def generate_tokens(user_id: str, device_id: str):
    logger.info(f'{ENDPOINT}/{user_id}/{device_id}')

    conn = sqlite3.connect("../db/db.db", check_same_thread=False)
    cur = conn.cursor()

    statement = '''
        SELECT 1 FROM devices WHERE user_id = ? AND device_id = ?;
    '''
    values = (user_id, device_id,)

    try:
        cur.execute(statement, values)
        results = cur.fetchall()

        if not results:
            raise Exception('user_id or device_id don\'t match')
        
        statement = '''
            SELECT issuer, secret FROM secrets WHERE user_id = ?;
        '''
        values = (user_id,)

        cur.execute(statement, values)
        results = cur.fetchall()

        if not results:
            raise Exception('No secrets found')
        
        tokens = []
        
        for result in results:
            issuer, secret = result

            tokens.append({
                'issuer': issuer,
                'code': pyotp.TOTP(secret).now()
            })

        response = {'status': status_codes['success'], 'errors': None, 'results': tokens}

    except (Exception, sqlite3.Error) as error:
        error_message = traceback.format_exc()

        logger.error(f'{ENDPOINT}/{user_id}/{device_id} - error: {error_message}')

        response = {'status': status_codes['api_error'], 'errors': str(error), 'results': None}

    finally:
        cur.close()
        conn.close()
    
    return response

# end of generate_tokens.py