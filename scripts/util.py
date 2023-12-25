import pyautogui
import platform
import subprocess
from collections import namedtuple
from omegaconf import OmegaConf
from pynput.mouse import Button, Controller, Listener
import time
import numpy as np
import matplotlib.pyplot as plt

config = OmegaConf.load('config.yaml')
mouse = Controller()
cancel_t = -1.

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
    x1, y1 = pyautogui.position()
    print('Now place your mouse onto the last slot in 10s')
    time.sleep(10)
    x2, y2 = pyautogui.position()
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

def record_mouse():
    # time.time() 1.5e7 in 1s
    # pyautogui.position() 5e4 in 1s
    '''
    s = 0
    t1 = time.time()
    while True:
        s += 1
        t2 = time.time()
        x, y = pyautogui.position()
        if t2-t1>=1: break
    print(s)
    '''
    pos_log = []
    global lx, ly, _x, _y, t1
    t1 = time.time_ns()
    lx, ly = pyautogui.position()
    _x, _y = lx, ly
    def on_click(x, y, button, pressed):
        print('{0} at {1}'.format(
            'Pressed' if pressed else 'Released',
            (x, y)))
        print(button)
    def on_move(x, y):
        global lx, ly, _x, _y, t1
        if (lx, ly) != (x, y):
            t2 = time.time_ns()
            if (x, y) != (_x, _y):
                pos_log.append([x-lx, y-ly, t2-t1])
                print("%2d %2d   %.2fms" % (x-lx, y-ly, (t2-t1)/1e6))
                t1 = t2
            lx, ly = x, y
        if x>3000:
            return False
    with Listener(on_move=on_move, on_click=on_click) as listener:
        listener.join()
    pos_log = np.array(pos_log, dtype=np.int64)
    #np.save("mouse_log.npy", pos_log)

def vis_mouse_log():
    pos_log = np.load("mouse_log.npy")
    print(pos_log.shape)
    for i in range(len(pos_log)):
        print("%2d %2d   %.2fms" % (pos_log[i][0], pos_log[i][1], pos_log[i][2]/1e6))


