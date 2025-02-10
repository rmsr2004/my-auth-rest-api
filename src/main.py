from fastapi import FastAPI, Request, Depends
import uvicorn

from auth_jwt import validate_token
from login import login
from register import register

app = FastAPI()

@app.get('/')
def index():
    return 'My-Auth REST API developing...'

@app.post('/auth/register')
async def register_endpoint(request: Request):
    return await register(request)

@app.put('/auth/login')
async def login_endpoint(request: Request):
    return await login(request)

# end of main.py