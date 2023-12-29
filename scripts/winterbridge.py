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
#pyautogui.FAILSAFE = False

#mouse.click(button_mouse4)
poll = 0.005
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
        time.sleep(0.05 if i else 0.01)
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
    if True: return
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

def start_bridge():
    # Press keys 's', 'd', shift
    pyautogui.keyUp('s')
    pyautogui.keyUp('d')
    pyautogui.keyUp(shift)
    pyautogui.keyUp('space')
    time.sleep(0.01)
    pyautogui.keyDown(shift)
    time.sleep(0.1)
    pyautogui.keyDown('s')
    pyautogui.keyDown('d')
    time.sleep(0.01)

def cancel_bridge():
    print("Cancel")
    pyautogui.keyDown(shift)
    pyautogui.keyUp('s')
    pyautogui.keyUp('d')
    time.sleep(0.2)
    pyautogui.keyUp(shift)

def bridge_pos_dir(position, direction):
    # Calculate bridge position and direction
    if abs(direction[0]) > abs(direction[2]):
        dir_go = np.array([
            np.sign(direction[0]),
            0,
            ], dtype=np.int64)
        dir2 = np.array([
            config.bridge.direction[0] * np.sign(direction[0]),
            config.bridge.direction[1],
            config.bridge.direction[2] * np.sign(direction[0]),
            ])
        pos2 = np.array([
            np.floor(position[0]) + 0.5 + config.bridge.position[0] * np.sign(direction[0]),
            np.floor(position[2]) + 0.5 + config.bridge.position[1] * np.sign(direction[0])
            ])
        d_s = np.array([0., np.sign(direction[0])])
    else:
        dir_go = np.array([
            0,
            np.sign(direction[2]),
            ], dtype=np.int64)
        dir2 = np.array([
            -config.bridge.direction[2] * np.sign(direction[2]),
            config.bridge.direction[1],
            config.bridge.direction[0] * np.sign(direction[2]),
            ])
        pos2 = np.array([
            np.floor(position[0]) + 0.5 - config.bridge.position[1] * np.sign(direction[2]),
            np.floor(position[2]) + 0.5 + config.bridge.position[0] * np.sign(direction[2])
            ])
        d_s = np.array([-np.sign(direction[2]), 0.])
    return dir_go, dir2, pos2, d_s

def bridge_adjust(dir_go, dir2, pos2, d_s):
    # Adjust position

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

def ninja_bridge(position, eye, direction, blocks):
    global start_t
    start_t = time.time()
    direction = get_dir_vec3(*direction)
    dir_go, dir2, pos2, d_s = bridge_pos_dir(position, direction)
    #print(dir2)
    time.sleep(2)
    rotate_to(dir2)
    t1 = time.time()
    start_bridge()
    if not bridge_adjust(dir_go, dir2, pos2, d_s):
        return

    # Start ninja bridge
    #t1 = time.time()
    while True:
        if not ninja_1block(dir_go, pos2):
            return

    pyautogui.keyUp('s')
    pyautogui.keyUp('d')
    time.sleep(0.2)
    pyautogui.keyUp(shift)
    return

def inc3_bridge(position, eye, direction, blocks):
    global start_t
    start_t = time.time()
    direction = get_dir_vec3(*direction)
    dir_go, dir2, pos2, d_s = bridge_pos_dir(position, direction)
    #print(dir2)
    time.sleep(2)
    rotate_to(dir2)
    t1 = time.time()
    start_bridge()
    if not bridge_adjust(dir_go, dir2, pos2, d_s):
        return

    # Start ninja bridge
    #t1 = time.time()
    left_forward = 1
    left_upward = 5
    num_forward = 3
    while True:
        if left_upward>0 and left_forward==1:
            print("Fuck")
            if not ninja_1stair(dir_go, pos2):
                return
            left_forward = num_forward
            left_upward -= 1
        else:
            if not ninja_1block(dir_go, pos2):
                return
            left_forward -= 1

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
    eye_blockpos = eye.astype(np.int64)
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

def rotate_to(dir2):
    t1 = 0.
    poll = 0.05
    pitch2, yaw2 = get_pitch_yaw(dir2)
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
        mouse.move(dx, dy)
        yaw1 += dx * config.mouse.sensitivity_yaw
        pitch1 += dy * config.mouse.sensitivity_pitch
        time.sleep(0.005)
    return

def rotate_2(dir1, dir2):  # deprecated
    yaw1, pitch1 = get_yaw_pitch(dir1)  # in degrees
    yaw2, pitch2 = get_yaw_pitch(dir2)
    yaw, pitch = yaw2-yaw1, pitch2-pitch1
    if yaw>180: yaw-=360
    if yaw< -180: yaw+=360
    pixel_x = round(yaw / config.mouse.sensitivity_yaw)
    pixel_y = round(pitch / config.mouse.sensitivity_pitch)
    print("New yaw, pitch:", yaw2, pitch2)
    print("Change yaw, pitch:", yaw, pitch)
    print("Pixel x:", pixel_x)
    print("Pixel y:", pixel_y)
    #time.sleep(1)
    def gen_gap(pixel, speed):
        # speed: How many pixels per ms
        n = abs(pixel)
        gap = np.random.uniform(0.9/speed, 1.1/speed, size=n)
        for i in range(min(pixel//2, 3)):
            extra_delay = 40.0
            gap[i] = np.random.uniform(extra_delay/(i+2), extra_delay/(i+1))
            gap[n-1-i] = np.random.uniform(extra_delay/(i+2), extra_delay/(i+1))
        return gap
    gap_x = gen_gap(pixel_x, 8.0)
    gap_y = gen_gap(pixel_y, 6.0)
    gap_x = list(gap_x)[::-1]
    gap_y = list(gap_y)[::-1]
    gaps_pixel = []
    while gap_x or gap_y:
        if np.random.randint(len(gap_x)+len(gap_y)) < len(gap_x):
            gaps_pixel.append([1, 0, gap_x.pop()])
        else:
            gaps_pixel.append([0, 1, gap_y.pop()])
    n = len(gaps_pixel)
    i = 0
    _b = 1
    gaps = []
    while i<n:
        if i<10:
            b = 1
        elif i<n-10:
            b = _b + np.random.randint(0, 2)
            b = min(b, 6)
            if b==6 and np.random.uniform()<0.1:
                b -= 1
        else:
            b = 1
        _b = b
        dx, dy, delay = 0, 0, 0.
        for j in range(i,i+b):
            if gaps_pixel[j][0]:
                dx += 1 if pixel_x>0 else -1
            if gaps_pixel[j][1]:
                dy += 1 if pixel_y>0 else -1
            delay += gaps_pixel[j][2]
        gaps.append([dx, dy, delay])
        i += b
    for dx, dy, delay in gaps:
        mouse.move(dx, dy)
        time.sleep(delay/1e3)
    return

def intersects(player_pos, direction, block, padding=0.):  # GPT4
    # Extract block coordinates
    bx, by, bz = block

    # Define the bounds of the block
    min_bound = (bx-padding, by-padding, bz-padding)
    max_bound = (bx+1+padding, by+1+padding, bz+1+padding)

    # Initialize parameters
    eps = 1e-6
    tmin, tmax = -float('inf'), float('inf')

    for i in range(3):
        if abs(direction[i])>eps:
            t1 = (min_bound[i] - player_pos[i]) / direction[i]
            t2 = (max_bound[i] - player_pos[i]) / direction[i]

            tmin = max(tmin, min(t1, t2))
            tmax = min(tmax, max(t1, t2))
        elif player_pos[i]<min_bound[i] or player_pos[i]>max_bound[i]:
            return False  # Parallel and outside the slab

    return tmax >= tmin and tmax > 0

def manh_dist(pos1, pos2):
    return np.sum(np.abs(pos1-pos2))

class BlockIn:

    def __init__(self, position, eye, direction, blocks):
        self.position = np.array(position, dtype=np.int64)
        self.direction = get_dir_vec3(*direction)
        self._blocks = np.array(blocks)
        self.blocks = self._blocks.copy()
        self.eye = np.array(eye)
        self.eye_blockpos = self.eye.astype(np.int64)

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
            if get_block(self.blocks, self.position, pos)!='Air':
                continue
            direction = self.get_place_direction(pos)
            if direction is not None:
                print("place block on", *pos)
                rotate_to(self.direction, direction)
                time.sleep(1e-3)
                self.direction = direction
                mouse.press(Button.right)
                time.sleep(2e-2)
                mouse.release(Button.right)
                time.sleep(1e-3)
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
                            target[k] += np.random.uniform(0.25, 0.75)
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

            if data['type']=='sort':
                thread = Thread(target=sort_items, args=(data['hotbar'],))

            if data['type']=='test':
                #print('Direction:', data['dir'])
                #print('Position:', data['pos'])
                #print('Eye:', data['eye'])
                #print('Block:', data['blocks'])
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
                thread = Thread(target=inc3_bridge, args=(
                    list(data['pos'].values()),
                    list(data['eye'].values()),
                    list(data['dir'].values()),
                    data['blocks'],
                    ))

            if data['type']=='cancel':
                with threading.Lock():
                    global cancel_t
                    cancel_t = time.time()

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

