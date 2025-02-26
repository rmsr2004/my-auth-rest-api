from fastapi import Request
import sqlite3
import traceback

from utils import status_codes, logger

ENDPOINT = 'POST /auth/add_device'

async def add_device(request: Request, token):
    logger.info(f'{ENDPOINT}')

    payload = await request.json()

    logger.debug(f'{ENDPOINT} - payload: {payload}')

    if 'device_id' not in payload:
        response = {'status': status_codes['api_error'], 'errors': 'Missing required field: device_id', 'results': None}
        return response

    conn = sqlite3.connect("../db/db.db", check_same_thread=False)
    cur = conn.cursor()

    statement = '''
        INSERT INTO devices (user_id, device_id) VALUES (?, ?)
        RETURNING id;
    '''
    values = (token['data']['id'], payload['device_id'],)

    try:
        cur.execute(statement, values)
        result = cur.fetchone()

        if result is None:
            raise Exception('Device registration failed')
        else:
            response = {'status': status_codes['success'], 'errors': None, 'results': result[0]}

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

# end of add_device.py