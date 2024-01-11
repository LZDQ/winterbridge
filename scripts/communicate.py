import os
import json
import shared


pipe_path = "/home/ldq/mc/winterbridge/scripts/pipe"

def receive_packets():
    remove_pipe()
    os.mkfifo(pipe_path)
    pipe = open(pipe_path, "r")
    while True:
        data = json.loads(pipe.readline())
        shared.pack_que.put(data)

def remove_pipe():
    if os.path.exists(pipe_path):
        os.remove(pipe_path)
