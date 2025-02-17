from fastapi import FastAPI, Request, Depends
from fastapi.middleware.cors import CORSMiddleware
from auth_jwt import validate_token
from login import login
from register import register
from add_app import add_app
from get_apps import get_apps
from verify_device import verify_device
from add_device import add_device

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 🔥 Para desenvolvimento, permite qualquer origem
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

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

@app.post('/auth/add_device')
async def add_device_endpoint(request: Request, token: str = Depends(validate_token)):
    return await add_device(request, token)

@app.get('/auth/verify_device/{device_id}')
async def verify_device_endpoint(device_id: str):
    return await verify_device(device_id)

# end of main.py