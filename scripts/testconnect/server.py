import asyncio
import websockets
import json

# Global list to store messages
messages = []

async def echo(websocket, path):
    print("New Connection lol")
    global messages
    async for message in websocket:
        #print(f"Received message: {message}")
        messages.append(message)
        await websocket.send(f"Echo: {message}")

async def process_messages():
    global messages
    while True:
        # Process messages every 5 seconds
        await asyncio.sleep(5)
        if messages:
            print("Processing new messages...")
            while messages:
                message = messages.pop(0)
                # Process each message
                print(f"Processed message: {message}")
                try:
                    data = json.loads(message)
                    print("JSON data:", data)
                except:
                    print("Failed to load json")
        else:
            print("No new messages")

async def main():
    server = await websockets.serve(echo, "localhost", 1234)
    print("Server started")
    await asyncio.gather(server.wait_closed(), process_messages())

asyncio.run(main())
