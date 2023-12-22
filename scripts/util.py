import pyautogui
import platform
import subprocess
from collections import namedtuple
from omegaconf import OmegaConf
from pynput.mouse import Button, Controller
import time

config = OmegaConf.load('config.yaml')
mouse = Controller()
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

