import sqlite3
import traceback

from utils import logger, status_codes

ENDPOINT = 'GET /auth/get_apps'

async def get_apps(token):
    logger.info(f'{ENDPOINT}')

    user_id = token['data']['id']

    conn = sqlite3.connect("../db/db.db", check_same_thread=False)
    cur = conn.cursor()

    statement = '''
        SELECT id, issuer, secret FROM secrets WHERE user_id = ?;
    '''
    values = (user_id,)

    try:
        cur.execute(statement, values)
        results = cur.fetchall()

        if not results:
            return {'status': status_codes['not_found'], 'errors': None, 'results': []}

        response = {'status': status_codes['success'], 'errors': None, 'results': results}

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

# end of get_apps.py