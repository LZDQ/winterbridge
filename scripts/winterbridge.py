from http.server import HTTPServer, BaseHTTPRequestHandler
import pyautogui
import json
import numpy as np
import time
from util import *
import math
import threading
from threading import Thread
import logging
pyautogui.FAILSAFE = False

#mouse.click(button_mouse4)
poll = 0.05
shift = config.keymappings.sneak

def sort_items(items):
    n = len(items)
    p = [-1] * n
    q = [-1] * n
    vis = [False] * n
    done = True
    for i in range(n):
        for name, slot in config.sort_hotbar.items():
            if name in items[i]:
                if q[slot]==-1:
                    p[i] = slot
                    q[slot] = i
                    if i!=slot:
                        done = False
                else:
                    print("Error. Two " + name + " found")
    if done:
        print('Already sorted')
        return
    window = get_window_info()
    pyautogui.press(config.keymappings.inventory, interval=0.01)  # open menu
    time.sleep(0.05)
    rec = []
    for i in range(n):
        if vis[i] or q[i]==-1: continue
        s = i
        while p[s]!=-1 and p[s]!=i:
            s = p[s]
        u = s
        while True:
            vis[u] = True
            v = q[u]
            if v==-1 or v==s: break
            rec.append((u, v))
            u = v
    for i, (u, v) in enumerate(rec):
        print("swap", u, "and", v)
        x_u = window.X + round(window.WIDTH * (config.window.INV_X1*(n-1-u) + config.window.INV_X9*u) / (n-1))
        y = window.Y + round(window.HEIGHT * config.window.INV_Y)
        #print("x", x_u, "y", y, 'key', config.keymappings.hotbar[v])
        mouse.position = (x_u, y)  # hover
        time.sleep(0.05 if i else 0.03)
        hotkey = config.keymappings.hotbar[v]
        if hotkey.startswith('mouse'):
            if hotkey=='mouse4':
                mouse.click(button_mouse4)
            elif hotkey=='mouse5':
                mouse.click(button_mouse5)
            else:
                print("Unrecognized mouse:", hotkey)
        else:
            pyautogui.press(hotkey, interval=0.01)  # press another hotbar key
        time.sleep(0.05 if i+1<len(rec) else 0.01)
    pyautogui.press(config.keymappings.inventory, interval=0.01)
    return

def block_in(position, eye, direction, blocks):
    start_t = time.time()
    blockin = BlockIn(position, eye, direction, blocks)
    blockin.work()
    return

def test(position, eye, direction, blocks):
    #with threading.Lock(): print(latest_info)
    direction = get_dir_vec3(*direction)
    print("Position:", *position)
    print("Direction:", *direction)
    #rotate_to(np.array([1.0, 0.0, 0.0]))
    rotate_to(np.array([ 0.14412782,-0.97922529, 0.14263592]))

def cancel_bridge():
    print("Cancel")
    if cancel_cause=='manual':
        pyautogui.keyDown(shift)
        pyautogui.keyUp('s')
        pyautogui.keyUp('d')
        pyautogui.keyDown('a')
        pyautogui.keyDown('w')
        time.sleep(0.01)
        pyautogui.keyUp('a')
        pyautogui.keyUp('w')
        pyautogui.keyUp(shift)
        pyautogui.keyUp('space')
    elif cancel_cause=='hit':
        pyautogui.keyUp('s')
        pyautogui.keyUp('d')
        pyautogui.keyUp(shift)
        pyautogui.keyUp('space')

def bridge_pos_dir(position, direction, blocks):
    '''
    Calculate bridge position and direction

    The procedure is:
    1. If the player is pointing down (pitch>20), the direction will be just as the 
        player is starting to bridge (yaw+=135)
    2. If the player is not pointing down (pitch<=20), go to the direction the player is pointing.
        Sleep for 1s to avoid being banned.
    3. If the player is not standing on a block, change the starting block_pos.
    '''
    pitch, yaw = get_pitch_yaw(direction)
    position = np.floor(position).astype(np.int64)
    block_pos = position

    # Store direction to go in _dir
    if pitch>20:
        yaw += 135
        _dir = get_dir_vec3(0., yaw)
    else:
        _dir = direction

    # Get dir_go
    if abs(_dir[0]) > abs(_dir[2]):
        dir_go = np.array([
            np.sign(_dir[0]),
            0,
            ], dtype=np.int64)
    else:
        dir_go = np.array([
            0,
            np.sign(_dir[2]),
            ], dtype=np.int64)

    # Check whether the player is reaching out edge
    if get_block(blocks, position, position-(0, 1, 0))=='Air':
        block_pos = position - (dir_go[0], 0, dir_go[1])

    # Get other info
    if dir_go[0]:
        dir2 = np.array([
            config.bridge.direction[0] * dir_go[0],
            config.bridge.direction[1],
            config.bridge.direction[2] * dir_go[0],
            ])
        pos2 = np.array([
            block_pos[0] + 0.5 + config.bridge.position[0] * dir_go[0],
            block_pos[2] + 0.5 + config.bridge.position[1] * dir_go[0]
            ])
        d_s = np.array([0., dir_go[0]])
    else:
        dir2 = np.array([
            -config.bridge.direction[2] * dir_go[1],
            config.bridge.direction[1],
            config.bridge.direction[0] * dir_go[1],
            ])
        pos2 = np.array([
            block_pos[0] + 0.5 - config.bridge.position[1] * dir_go[1],
            block_pos[2] + 0.5 + config.bridge.position[0] * dir_go[1]
            ])
        d_s = np.array([-dir_go[1], 0.])

    return dir_go, dir2, pos2, d_s

def bridge_adjust(dir_go, dir2, pos2, d_s):
    # Enable hotkey, adjust position and direction

    pyautogui.keyUp('s')
    pyautogui.keyUp('d')
    pyautogui.keyUp(shift)
    pyautogui.keyUp('space')
    #time.sleep(0.01)
    pyautogui.keyDown(shift)
    rotate_to(dir2)
    #time.sleep(0.1)
    pyautogui.keyDown('s')
    pyautogui.keyDown('d')
    #time.sleep(0.01)

    # Wait until reaching the edge
    while True:
        with threading.Lock():
            if cancel_t > start_t:
                cancel_bridge()
                return False
            position = np.array(list(latest_info['pos'].values())[::2])
        dist = manh_dist(position * dir_go, pos2 * dir_go)
        #print(dist)
        time.sleep(poll)
        if dist<0.1: break
        #if time.time()-t1>3: break
    print("Reached edge")

    # Move to the correct side-axis
    if manh_dist(position, pos2)>0.03:
        if np.dot(d_s, pos2-position)>0:
            # go s
            key = 'd'
        else:
            key = 's'
        pyautogui.keyUp(key)
        print("Key:", key)
        last_dist = 100.
        while True:
            with threading.Lock():
                if cancel_t > start_t:
                    cancel_bridge()
                    return False
                position = np.array(list(latest_info['pos'].values())[::2])
            dist = manh_dist(position, pos2)
            print("Position:", position)
            print("Pos2:", pos2)
            print(dist)
            if dist<0.03 or dist>last_dist: break
            last_dist = dist
            time.sleep(poll)
        pyautogui.keyDown(key)
        time.sleep(0.01)
    print("Position corrected")
    return True

def ninja_1block_realtime(dir_go, pos2):
    # 1 block forward

    # From standing at the edge, to the next edge
    t1 = time.time()
    mouse.press(Button.right)
    #pyautogui.rightClick()
    pyautogui.keyUp(shift)
    mouse.release(Button.right)

    pos2 += dir_go

    # Walk till near the edge
    time_walk = np.random.randn() * 0.01 + 0.199
    time_walk = np.clip(time_walk, 0.1, 0.2)
    #time_walk = 0.2
    # Wait for the delay
    while True:
        t2 = time.time()
        if t2-t1+poll>=time_walk:
            break
        time.sleep(poll)
    while True:
        with threading.Lock():
            if cancel_t > start_t:
                cancel_bridge()
                return False
            position = np.array(list(latest_info['pos'].values())[::2])
        dist = manh_dist(position * dir_go, pos2 * dir_go)
        if True or dist<0.3:
            pyautogui.keyDown(shift)
            break
        time.sleep(poll)
    print("Start sneaking")

    # Sneak till the edge
    time_sneak = np.random.randn() * 0.001 + 0.02
    time_sneak = np.clip(time_sneak, 0.01, 0.018)
    #time_sneak = 0.02
    time.sleep(time_sneak)
    while True:
        with threading.Lock():
            if cancel_t > start_t:
                cancel_bridge()
                return
            position = np.array(list(latest_info['pos'].values())[::2])
        dist = manh_dist(position * dir_go, pos2 * dir_go)
        print(dist)
        if dist<0.1:
            break
        time.sleep(poll)
    print("Reached new edge")
    return True

def ninja_1block(dir_go, pos2):
    # 1 block forward

    # From standing at the edge, to the next edge
    t1 = time.time()
    mouse.press(Button.right)
    #pyautogui.rightClick()
    pyautogui.keyUp(shift)
    mouse.release(Button.right)

    pos2 += dir_go

    # Walk till near the edge
    time_walk = np.random.randn() * 0.01 + 0.199
    time_walk = np.clip(time_walk, 0.1, 0.2)
    #time_walk = 0.2
    # Wait for the delay
    time.sleep(t1 + time_walk - time.time())
    pyautogui.keyDown(shift)
    #print("Start sneaking")

    # Sneak till the edge
    time_sneak = np.random.randn() * 0.001 + 0.02
    time_sneak = np.clip(time_sneak, 0.01, 0.018)
    #time_sneak = 0.02
    time.sleep(time_sneak)
    while True:
        with threading.Lock():
            if cancel_t > start_t:
                cancel_bridge()
                return
            position = np.array(list(latest_info['pos'].values())[::2])
        dist = manh_dist(position * dir_go, pos2 * dir_go)
        print(dist)
        if dist<0.1:
            break
        print("Not breaked")
        time.sleep(poll)
    #print("Reached new edge")
    return True

def ninja_1stair(dir_go, pos2):
    # 1 block forward and upward

    # From standing at the edge, to the next edge
    t1 = time.time()
    mouse.click(Button.right)
    #pyautogui.rightClick()
    pyautogui.keyDown('space')
    time.sleep(0.01)
    pyautogui.keyUp('space')
    time.sleep(0.01)
    mouse.click(Button.right)
    time.sleep(0.01)
    mouse.click(Button.right)

    pos2 += dir_go

    # Sneak till the edge
    time_sneak = np.random.randn() * 0.001 + 0.02
    time_sneak = np.clip(time_sneak, 0.01, 0.022)
    #time_sneak = 0.02
    time.sleep(time_sneak)
    while True:
        with threading.Lock():
            if cancel_t > start_t:
                cancel_bridge()
                return
            position = np.array(list(latest_info['pos'].values())[::2])
        dist = manh_dist(position * dir_go, pos2 * dir_go)
        print(dist)
        if dist<0.1:
            break
        time.sleep(poll)
    print("Reached new edge")
    return True

def ninja_bridge(position, eye, direction, blocks, inc=None):
    global start_t
    start_t = time.time()
    direction = get_dir_vec3(*direction)
    blocks = np.array(blocks)
    out = bridge_pos_dir(position, direction, blocks)
    if type(out)==str:
        print(out)
        return
    dir_go, dir2, pos2, d_s = out
    #print(dir2)
    if not bridge_adjust(dir_go, dir2, pos2, d_s):
        return

    # Start ninja bridge
    if inc is None:
        left_upward = 0
        left_forward = 10000
    elif inc==2:
        num_forward = 2
        left_upward = 10000
        left_forward = 1
    elif inc==3:
        num_forward = 3
        left_upward = 5
        left_forward = 1
    while True:
        if left_upward>0 and left_forward==1:
            if not ninja_1stair(dir_go, pos2):
                return
            left_forward = num_forward
            left_upward -= 1
        else:
            if not ninja_1block(dir_go, pos2):
                return
            left_forward -= 1

    # Following not meant to run
    pyautogui.keyUp('s')
    pyautogui.keyUp('d')
    time.sleep(0.2)
    pyautogui.keyUp(shift)
    return

def test_pointing(position, eye, direction, blocks):
    position = np.array(position)
    direction = get_dir_vec3(*direction)
    blocks = np.array(blocks)
    eye = np.array(eye)
    eye_blockpos = np.floor(eye).astype(np.int64)
    print('Eye', eye[0], eye[1], eye[2])
    print('Position', position[0], position[1], position[2])
    res = None
    res_name = None
    for (i, j, k), block in np.ndenumerate(blocks):
        if block=='Air': continue
        x = int(position[0]) + i - blocks.shape[0]//2
        y = int(position[1]) + j - blocks.shape[1]//2
        z = int(position[2]) + k - blocks.shape[2]//2
        if intersects(eye, direction, (x, y, z)):
            print("Found", block, "at", x, y, z)
            if res is None or manh_dist(res, eye_blockpos)>manh_dist((x, y, z), eye_blockpos):
                res = (x, y, z)
                res_name = block
    if res:
        print(res_name)
    else:
        print("Not pointing")
    return

def get_dir_vec3(pitch, yaw):  # GPT4
    # Convert angles to radians
    yaw_rad = math.radians(yaw)
    pitch_rad = math.radians(pitch)

    # Calculate direction vector components
    x = -math.sin(yaw_rad) * math.cos(pitch_rad)
    y = -math.sin(pitch_rad)
    z = math.cos(yaw_rad) * math.cos(pitch_rad)

    direction = np.array([x,y,z])
    #print("dir len:", np.linalg.norm(direction))
    #print("Pointing:", *direction)
    return direction

def get_pitch_yaw(direction):
    pitch_rad = math.asin(-direction[1])
    yaw_rad = -math.atan2(direction[0], direction[2])
    pitch, yaw = math.degrees(pitch_rad), math.degrees(yaw_rad)
    return pitch, yaw

def get_block(blocks, player_pos, pos):
    # player_pos contains integer
    pos = pos - player_pos + np.array(blocks.shape)//2
    #print(*pos)
    return blocks[tuple(pos)]

def set_block(blocks, player_pos, pos, value):
    # player_pos contains integer
    pos = pos - player_pos + np.array(blocks.shape)//2
    blocks[tuple(pos)] = value
    return

def rotate_to(dir2, dir1=None):
    pitch2, yaw2 = get_pitch_yaw(dir2)
    t1 = time.time()
    if dir1 is not None:
        pitch1, yaw1 = get_pitch_yaw(dir1)
    else:
        time.sleep(poll)
    while True:
        t2 = time.time()
        if t2-t1>poll:
            with threading.Lock():
                if cancel_t > start_t: return
                #print(latest_info)
                pitch1, yaw1 = latest_info['dir'].values()
            t1 = t2
        pitch, yaw = pitch2-pitch1, yaw2-yaw1
        yaw %= 360
        if yaw>180: yaw-=360
        pixel_x = round(yaw / config.mouse.sensitivity_yaw)
        pixel_y = round(pitch / config.mouse.sensitivity_pitch)
        mi = min(abs(pixel_x), abs(pixel_y)) + 1
        dx, dy = round(pixel_x / mi), round(pixel_y / mi)
        if abs(dx)+abs(dy)<=0:
            return
        max_d = 5
        if abs(dx)>max_d:
            dx = max_d * np.sign(dx)
        if abs(dy)>max_d:
            dy = max_d * np.sign(dy)
        #print("move", dx, dy)
        mouse.move(dx, dy)
        yaw1 += dx * config.mouse.sensitivity_yaw
        pitch1 += dy * config.mouse.sensitivity_pitch
        time.sleep(0.005)
    return

def rotate_fast(dir2, dir1, duration=0.05):
    pitch2, yaw2 = get_pitch_yaw(dir2)
    pitch1, yaw1 = get_pitch_yaw(dir1)
    pitch, yaw = pitch2-pitch1, yaw2-yaw1
    pixel_x = yaw / config.mouse.sensitivity_yaw
    pixel_y = pitch / config.mouse.sensitivity_pitch
    if abs(pixel_x) + abs(pixel_y) <= 1:
        return
    points = gen_line((0., 0.), (pixel_x, pixel_y))
    points = np.round(points).astype(int)
    n = 100
    for i in range(n):
        p1 = points[round(n*i/(n+1))]
        p2 = points[round(n*(i+1)/(n+1))]
        d = p2-p1
        mouse.move(*d)
        time.sleep(duration/n)

    return

class BlockIn:

    def __init__(self, position, eye, direction, blocks):
        self.position = np.floor(position).astype(np.int64)
        self.direction = get_dir_vec3(*direction)
        self._blocks = np.array(blocks)
        self.blocks = self._blocks.copy()
        self.eye = np.array(eye)
        self.eye_blockpos = np.floor(self.eye).astype(np.int64)

    def work(self):
        que = []
        for dy in [-1, 0, 1, 2]:
            for dx, dz in [(0,1), (1,0), (0,-1), (-1,0)]:
                x = self.position[0] + dx
                y = self.position[1] + dy
                z = self.position[2] + dz
                que.append(np.array([x, y, z]))
        x, y, z = self.position
        y += 2
        que.append(np.array([x, y, z]))
        for pos in que:
            with threading.Lock():
                if cancel_t > start_t:
                    return
            if get_block(self.blocks, self.position, pos)!='Air':
                continue
            direction = self.get_place_direction(pos)
            if direction is not None:
                print("place block on", *pos)
                rotate_fast(direction, self.direction)
                time.sleep(1)
                self.direction = direction
                mouse.click(Button.right)
                time.sleep(1)
                set_block(self.blocks, self.position, pos, 'Wool')
            else:
                print("can't place on", *pos)

    def get_place_direction(self, pos):
        #breakpoint()
        for i in range(3):
            for j in [-1, 1]:
                base = pos.copy()
                base[i] += j
                if get_block(self.blocks, self.position, base)=='Air':
                    continue
                if manh_dist(self.eye_blockpos, base)<manh_dist(self.eye_blockpos, pos):
                    continue
                for _ in range(30):
                    if _:
                        target = base.copy().astype(np.float64)
                        target[i] = max(base[i], pos[i])
                        for k in range(3):
                            if i==k: continue
                            target[k] += np.random.uniform(0.2, 0.8)
                        direction = target - self.eye
                        direction /= np.linalg.norm(direction)
                    else:
                        if not intersects(self.eye, self.direction, base, padding=-0.01):
                            continue
                        direction = self.direction
                    dist = manh_dist(self.eye_blockpos, base)
                    ex, ey, ez = self.eye_blockpos
                    ok = True
                    for x in range(ex-dist+1, ex+dist):
                        for y in range(ey-dist+abs(x-ex)+1, ey+dist-abs(x-ex)):
                            for z in range(ez-dist+abs(x-ex)+abs(y-ey)+1, ez+dist-abs(x-ex)-abs(y-ey)):
                                if get_block(self.blocks, self.position, np.array([x, y, z]))=='Air':
                                    continue
                                if intersects(self.eye, direction, np.array([x, y, z]), padding=0.05):
                                    ok = False
                                    break
                            if not ok: break
                        if not ok: break
                    if ok:
                        return direction
        return None


# Network

logging.basicConfig(filename='server.log', level=logging.INFO, format='%(asctime)s:%(levelname)s:%(message)s')

sensitivity_log = []
timestamp = []

class HTTPHandler(BaseHTTPRequestHandler):

    def log_message(self, format, *args):
        # Override log_message to log to the file
        logging.info("%s - - [%s] %s\n" %
                (self.client_address[0],
                    self.log_date_time_string(),
                    format % args))

    def do_GET(self):
        self.send_response(200)
        self.send_header('Content-type', 'text/html')
        self.end_headers()

        # Send message back to client
        message = "Hello, world! This is a response from the server."
        # Write content as utf-8 data
        self.wfile.write(bytes(message, "utf8"))


    def do_POST(self):
        #t_s = time.time()
        content_length = int(self.headers['Content-Length'])
        # Read the data
        post_data = self.rfile.read(content_length)

        # Try to parse JSON data
        try:
            data = json.loads(post_data.decode())
            if True:
                with open("received_data.json", "w") as file:
                    json.dump(data, file, indent=4)
            response = "JSON data received and saved."
        except json.JSONDecodeError:
            response = "Failed to decode JSON data."

        # Send response
        self.send_response(200)
        self.end_headers()
        self.wfile.write(response.encode())

        # Handle to winterbridge
        thread = None
        if type(data)==dict and 'type' in data.keys():

            if data['type']=='info':
                #print("Info")
                with threading.Lock():
                    global latest_info
                    latest_info = data
                #print(latest_info['dir'])

            if data['type']=='sort':
                thread = Thread(target=sort_items, args=(data['hotbar'],))

            if data['type']=='test':
                #print('Direction:', data['dir'])
                #print('Position:', data['pos'])
                #print('Eye:', data['eye'])
                #print('Block:', data['blocks'])
                print('Mouse:', data['mouse_info'])
                thread = Thread(target=test, args=(
                    #(data['pos']['x'], data['pos']['y'], data['pos']['z']),
                    #(data['eye']['x'], data['eye']['y'], data['eye']['z']),
                    #(data['dir']['x'], data['dir']['y']),
                    list(data['pos'].values()),
                    list(data['eye'].values()),
                    list(data['dir'].values()),
                    data['blocks'],
                    ))
                sensitivity_log.append(data['dir'])
                if False and len(sensitivity_log)==2:
                    window = get_window_info()
                    # data['pos']['x'] is the pitch. Looking up: -90, looking down: 90
                    print('Sensitivity pitch:', (sensitivity_log[1]['x']-sensitivity_log[0]['x'])/(window.HEIGHT//4))
                    print('Sensitivity yaw:', (sensitivity_log[1]['y']-sensitivity_log[0]['y'])/(window.WIDTH//4))


            if data['type']=='blockin':
                thread = Thread(target=block_in, args=(
                    list(data['pos'].values()),
                    list(data['eye'].values()),
                    list(data['dir'].values()),
                    data['blocks'],
                    ))

            if data['type']=='ninja':
                thread = Thread(target=ninja_bridge, args=(
                    list(data['pos'].values()),
                    list(data['eye'].values()),
                    list(data['dir'].values()),
                    data['blocks'],
                    ))

            if data['type']=='inc3':
                thread = Thread(target=ninja_bridge, args=(
                    list(data['pos'].values()),
                    list(data['eye'].values()),
                    list(data['dir'].values()),
                    data['blocks'],
                    3,
                    ))

            if data['type']=='inc2':
                thread = Thread(target=ninja_bridge, args=(
                    list(data['pos'].values()),
                    list(data['eye'].values()),
                    list(data['dir'].values()),
                    data['blocks'],
                    2,
                    ))

            if data['type']=='cancel':
                with threading.Lock():
                    global cancel_t, cancel_cause
                    cancel_t = time.time()
                    cancel_cause = data['cause']

        else:
            # Test
            #print(data)
            #timestamp.append(time.time())
            if False and len(timestamp)==1000:
                print("Sec:", timestamp[-1]-timestamp[0])
                exit()

        if thread:
            thread.start()
        #t_t = time.time()
        #print("This post costs:", t_t-t_s)


httpd = HTTPServer(('', config.network.port), HTTPHandler)
httpd.serve_forever()

