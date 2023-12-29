import pyautogui
from util import *
#from winterbridge import *
import numpy as np
import matplotlib.pyplot as plt

'''
print(intersects((0.5, 1.62000000476837, 0.5), (0.0, -1.0, 0.0), (0, -1, 0)))
print(intersects((0.5, 1.62000000476837, 0.5), (0.0, -1.0, 0.0), (0, -2, 0)))
print(intersects((0.5, 1.62000000476837, 0.5), (0.0, -1.0, 0.0), (-1, -1, 0)))
print(intersects((0.5, 1.62000000476837, 0.5), (0.0, -1.0, 0.0), (-1, -1, -1)))
print(intersects((0.5, 1.62000000476837, 0.5), (0.0, -1.0, 0.0), (0, -1, -1)))
'''

#print(get_block(np.ones(shape=(11, 21, 11)), np.array([100, 100, 100]), np.array([99, 99, 99])))

a = np.array([1,2,3])
x,y,z = a
#print(x,y,z)

for i in range(2,1): print("fuck")

#get_sensitivity()



#mouse.move(100,100)

#time.sleep(5); record_mouse()

#vis_mouse_log()

'''
time.sleep(1)
pyautogui.keyUp('shiftleft')
#pyautogui.keyUp('shiftleft')
#pyautogui.keyUp('shiftright')
time.sleep(5)
pyautogui.keyDown('shiftleft')
#pyautogui.press('a')
time.sleep(1)
#pyautogui.press('a')
#pyautogui.keyUp('shiftleft')
#pyautogui.keyUp('shiftright')
pyautogui.keyUp('shiftleft')
'''

pyautogui.keyUp('space'); time.sleep(5); pyautogui.keyDown('space'); pyautogui.keyUp('space')
