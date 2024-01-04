import asyncio
import websockets
import time


async def hello():
    uri = "ws://localhost:8765"
    async with websockets.connect(uri) as websocket:
        await websocket.send('{"name": "John", "age": 30}')
        n = 3
        for _ in range(n):
            await websocket.send("Hello, Server!" + str(_))
            response = await websocket.recv()
            print(f"Received from server: {response}")
            time.sleep(1)

asyncio.get_event_loop().run_until_complete(hello())
