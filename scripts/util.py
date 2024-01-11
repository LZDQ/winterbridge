import platform
import subprocess
import os
from collections import namedtuple
import time
import numpy as np
import math
import shared
import json
from omegaconf import OmegaConf

config = OmegaConf.load('config.yaml')


poll = 0.05

if platform.system() == 'Linux':
    from raw_input_linux import RawInputLinux
    ri = RawInputLinux()
else:
    pass


def get_window_info():
    res = namedtuple('window_info', ['X', 'Y', 'WIDTH', 'HEIGHT'])(0, 0, 2560, 1440)
    return res

def get_hotbar_info():
    print('Now place your mouse onto the first slot in 10s')
    time.sleep(10)
    import pyautogui
    x1, y1 = pyautogui.position()
    print('Now place your mouse onto the last slot in 10s')
    time.sleep(10)
    x2, y2 = pyautogui.position()
    window = get_window_info()
    print('INV_X1:', (x1-window.X)/window.WIDTH)
    print('INV_X9:', (x2-window.X)/window.WIDTH)
    print('INV_Y:', (y1+y2-window.Y*2)/window.HEIGHT/2)

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

def gen_disc(n=1000):
    # Generate n points from (0,0) to (1,0)
    x = np.arange(n, dtype=float)
    y = np.full_like(x, 0.)
    m = round(np.random.uniform(np.sqrt(n)+1))
    inc = []
    for _ in range(m*2):
        t = np.random.uniform(-1., 1.)
        t = np.arcsin(t)
        t = t / np.pi
        t = np.sign(t)/2 - t
        t += 0.5
        t = t * n
        inc.append(np.clip(round(t), 0, n-1))
    inc.sort()
    sgn = np.random.choice([-1, 1])
    j = 0
    s = 0
    for i in range(n-1):
        while j<m*2 and i==inc[j]:
            if j<m:
                s += sgn
            else:
                s -= sgn
            j += 1
        y[i] = s
    return np.stack((x, y), 1)

def convert_coordinate(point, line1_start, line1_end, line2_start, line2_end):
    # Not used
    line1 = np.array(line1_end) - np.array(line1_start)
    line2 = np.array(line2_end) - np.array(line2_start)
    len1 = np.linalg.norm(line1)
    line1 /= len1
    len2 = np.linalg.norm(line2)
    line2 /= len2

    point = np.array(point) - np.array(line1_start)
    point /= len1
    point = np.matmul(np.array([
        [line1[0], line1[1]],
        [-line1[1], line1[0]],
        ]), point)  # rotate inversely wrt line1
    point = np.matmul(np.array([
        [line2[0], -line2[1]],
        [line2[1], line2[0]],
        ]), point)  # rotate wrt line2
    point = point * len2 + np.array(line2_start)
    return point

def gen_line(s, t, n=1000):
    #t1 = time.time()
    disc = gen_disc(n)
    '''
    for i in range(len(disc)):  # Slow method
        #print(disc[i])
        disc[i] = convert_coordinate(disc[i], _s, _t, s, t)
        #print("Converted:", disc[i])
    '''
    disc /= np.linalg.norm(disc[-1]-disc[0])
    s = np.array(s, dtype=float)
    t = np.array(t, dtype=float)
    st = t - s
    disc = np.matmul(disc, np.array([
        [st[0], st[1]],
        [-st[1], st[0]]
        ])) + s
    #t2 = time.time()
    #print("Calc time:", t2-t1)
    return disc

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
    if len(direction)==2: return direction
    pitch_rad = math.asin(-direction[1])
    yaw_rad = -math.atan2(direction[0], direction[2])
    pitch, yaw = math.degrees(pitch_rad), math.degrees(yaw_rad)
    return pitch, yaw

def get_rotate_pixel(dir2, dir1):
    pitch2, yaw2 = get_pitch_yaw(dir2)
    pitch1, yaw1 = get_pitch_yaw(dir1)
    pitch, yaw = pitch2-pitch1, yaw2-yaw1
    yaw %= 360
    if yaw > 180:
        yaw -= 360
    pixel_x = yaw / config.mouse.sensitivity_yaw
    pixel_y = pitch / config.mouse.sensitivity_pitch
    return round(pixel_x), round(pixel_y)

