import pyautogui
import pynput
import time

mouse = pynput.mouse.Controller()

def test_mouse_pos():
    cnt = 0
    t1 = time.time()
    while time.time()-t1 < 1.:
        position = mouse.position
        cnt += 1
    print('pynput in 1s:', cnt)

    cnt = 0
    t1 = time.time()
    while time.time()-t1 < 1.:
        position = pyautogui.position()
        cnt += 1
    print('pyautogui in 1s:', cnt)

test_mouse_pos()
