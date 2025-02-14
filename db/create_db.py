import sqlite3

def create_db():
    with sqlite3.connect("db/db.db") as conn:
        cur = conn.cursor()

        cur.execute('''
            DROP TABLE IF EXISTS users;
        ''')
        cur.execute('''
            CREATE TABLE users (
                id	        INTEGER PRIMARY KEY AUTOINCREMENT,
                username    TEXT(512) NOT NULL UNIQUE,
                password    TEXT(512) NOT NULL
            );
        ''')

        cur.execute('''
            DROP TABLE IF EXISTS secrets;
        ''')
        cur.execute('''         
            CREATE TABLE secrets (
                id	        INTEGER PRIMARY KEY AUTOINCREMENT,
                issuer	    TEXT(512) NOT NULL,
                secret	    TEXT(512) NOT NULL,
                user_id     INTEGER NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id)
            );
        ''')

        cur.execute('''
            DROP TABLE IF EXISTS devices;
        ''')
        cur.execute('''
            CREATE TABLE devices (
                id          INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id     INTEGER NOT NULL,
                device_id   TEXT(512) NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(id)
            );
        ''')
        
        conn.commit()
        
    return

if __name__ == '__main__':
    create_db()
    print('-'*50)
    print('Database created successfully!')
    print('-'*50)

# end of create_db.py