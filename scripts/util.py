import platform
import subprocess
from collections import namedtuple
from omegaconf import OmegaConf
import pynput
from pynput.mouse import Button
import time
import numpy as np
import threading

config = OmegaConf.load('config.yaml')
mouse = pynput.mouse.Controller()
cancel_t = 0.
cancel_cause = None
start_t = 0.
latest_info = None


if platform.system()=='Windows':
    button_mouse4 = Button.x1
    button_mouse5 = Button.x2
else:
    button_mouse4 = Button.button8
    button_mouse5 = Button.button9

def get_window_info():
    if platform.system() == 'Windows':
        res = namedtuple('window_info', ['X', 'Y', 'WIDTH', 'HEIGHT'])(0, 0, 2560, 1440)
    else:
        assert platform.system() == 'Linux'
        res = namedtuple('window_info', ['X', 'Y', 'WIDTH', 'HEIGHT'])(0, 0, 2560, 1440)
        #output = subprocess.getoutput("xdotool getwindowfocus getwindowgeometry --shell")
        #info = dict(s.split('=') for s in output.splitlines() if s)
        #res = namedtuple('window_info', info.keys())(*[int(val) for val in info.values()])
        '''
        xdotool getwindowfocus getwindowgeometry --shell
        WINDOW=71309332
        X=452
        Y=246
        WIDTH=1492
        HEIGHT=958
        SCREEN=0
        '''
    return res

def get_hotbar_info():
    print('Now place your mouse onto the first slot in 10s')
    time.sleep(10)
    x1, y1 = mouse.position
    print('Now place your mouse onto the last slot in 10s')
    time.sleep(10)
    x2, y2 = mouse.position
    window = get_window_info()
    print('INV_X1:', (x1-window.X)/window.WIDTH)
    print('INV_X9:', (x2-window.X)/window.WIDTH)
    print('INV_Y:', (y1+y2-window.Y*2)/window.HEIGHT/2)

def get_sensitivity():
    print("""
    First press TEST key to record the (yaw, pitch)
    wait 10s
    Second press TEST key to record the new (yaw, pitch)
    """)
    time.sleep(10)
    window = get_window_info()
    dist_x = window.WIDTH//4
    dist_y = window.HEIGHT//4
    pyautogui.move(dist_x, dist_y)


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

def gen_disc():
    # Generate a series of discrete points from (0,0) to (1,0)
    n = 1000
    x = np.arange(n, dtype=float)
    y = np.full_like(x, 0.)
    m = round(np.random.uniform(100))
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


def gen_line(s, t):
    disc = gen_disc()
    _s = disc[0].copy()
    _t = disc[-1].copy()
    for i in range(len(disc)):
        #print(disc[i])
        disc[i] = convert_coordinate(disc[i], _s, _t, s, t)
        #print("Converted:", disc[i])
    #plt.scatter(*zip(*disc)); plt.show()
    return disc




