import pyautogui
import numpy as np
from pynput.mouse import Button, Controller
import time
from util import *
import math
import threading
pyautogui.FAILSAFE = False

#mouse.click(button_mouse4)

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
    time.sleep(0.05)
    pyautogui.press(config.keymappings.inventory)  # open menu
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
        pyautogui.moveTo(x_u, y)  # hover
        time.sleep(0.1 if i else 0.01)
        hotkey = config.keymappings.hotbar[v]
        if hotkey.startswith('mouse'):
            if hotkey=='mouse4':
                mouse.click(button_mouse4)
            elif hotkey=='mouse5':
                mouse.click(button_mouse5)
        else:
            pyautogui.press(hotkey)  # press another hotbar key
        time.sleep(0.1 if i+1<len(rec) else 0.01)
    pyautogui.press(config.keymappings.inventory)
    return

def block_in(position, eye, direction, blocks):
    blockin = BlockIn(position, eye, direction, blocks)
    blockin.work()
    return

def test(position, eye, direction, blocks):
    #pitch, yaw = direction
    #while yaw>180: yaw-=360
    #while yaw< -180: yaw+=360
    #print("Original yaw, pitch:", yaw, pitch)
    direction = get_dir_vec3(*direction)
    yaw, pitch = get_yaw_pitch(direction)
    #print("After yaw, pitch:", yaw, pitch)
    rotate_to(direction, np.array([1.0, 0.0, 0.0]))

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
    direction /= np.linalg.norm(direction)
    #print("dir len:", np.linalg.norm(direction))
    #print("Pointing:", *direction)
    return direction

def get_yaw_pitch(direction):
    yaw_rad = -math.atan2(direction[0], direction[2])
    pitch_rad = math.asin(-direction[1])
    yaw, pitch = math.degrees(yaw_rad), math.degrees(pitch_rad)
    return yaw, pitch

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

def rotate_to(dir1, dir2):
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
    #pyautogui.moveRel(pixel_x, pixel_y, duration=0.0)
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



