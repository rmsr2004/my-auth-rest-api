import sqlite3
import traceback

from utils import status_codes, logger

ENDPOINT = 'GET /auth/verify_device'

async def verify_device(user_id: str, device_id: str):
    logger.info(f'{ENDPOINT}/{user_id}/{device_id}')

    conn = sqlite3.connect("../db/db.db", check_same_thread=False)
    cur = conn.cursor()

    statement = '''
        SELECT * FROM devices WHERE user_id = ? AND device_id = ?;
    '''
    values = (user_id, device_id,)

    try:
        cur.execute(statement, values)
        result = cur.fetchone()

        if result is None:
            response = {'status': status_codes['success'], 'errors': None, 'results': False}
        else:
            response = {'status': status_codes['success'], 'errors': None, 'results': True}

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