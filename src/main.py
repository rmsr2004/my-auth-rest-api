from fastapi import FastAPI, Request, Depends

from auth_jwt import validate_token
from login import login
from register import register
from add_app import add_app
from get_apps import get_apps

app = FastAPI()

@app.get('/auth')
def index():
    return 'My-Auth REST API developing...'

@app.post('/auth/register')
async def register_endpoint(request: Request):
    return await register(request)

@app.put('/auth/login')
async def login_endpoint(request: Request):
    return await login(request)

@app.post('/auth/add_app')
async def add_app_endpoint(request: Request, token: str = Depends(validate_token)):
    return await add_app(request, token)

@app.get('/auth/get_apps')
async def get_apps_endpoint(token: str = Depends(validate_token)):
    return await get_apps(token)

# end of main.py