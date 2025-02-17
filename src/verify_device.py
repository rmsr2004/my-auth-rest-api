import sqlite3
import traceback

from auth_jwt import create_token
from utils import status_codes, logger

ENDPOINT = 'GET /auth/verify_device'

async def verify_device(device_id: str):
    logger.info(f'{ENDPOINT}/{device_id}')

    conn = sqlite3.connect("../db/db.db", check_same_thread=False)
    cur = conn.cursor()

    statement = '''
        SELECT user_id FROM devices WHERE device_id = ?;
    '''
    values = (device_id,)

    try:
        cur.execute(statement, values)
        user_id = cur.fetchone()

        if user_id is None:
            response = {'status': status_codes['success'], 'errors': None, 'results': False}
        else:
            jwt_token = create_token({'id': int(user_id[0])})
            response = {'status': status_codes['success'], 'errors': None, 'results': jwt_token}

    except (Exception, sqlite3.Error) as error:
        conn.rollback()

        error_message = traceback.format_exc()

        logger.error(f'{ENDPOINT} - error: {error_message}')

        response = {'status': status_codes['api_error'], 'errors': str(error), 'results': None}

    finally:
        cur.close()
        conn.close()

    return response

# end of verify_device.py