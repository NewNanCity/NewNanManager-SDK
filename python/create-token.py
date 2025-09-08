import asyncio

from newnanmanager import NewNanManagerClient
from newnanmanager.models.requests import (
    CreateApiTokenRequest,
)


async def f():
    client = NewNanManagerClient(
        base_url="https://manager-api.newnan.city",
        token="oPjrb509VqEApZzioDbELbH5sJBfhrvu",
    )
    try:
        resp = await client.tokens.create_api_token(CreateApiTokenRequest(
            name='曼巴服Guardian插件',
            role='server',
            description='用于曼巴服的Guardian插件'
        ))
        print(resp.token_value)
    except Exception as e:
        print(f"Error: {e}")
    await client.close()

if __name__ == "__main__":
    asyncio.run(f())
