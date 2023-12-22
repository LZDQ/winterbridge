import pyautogui
from pynput.mouse import Button, Controller
import time
from util import *

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


