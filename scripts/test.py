from util import *
from winterbridge import *
import numpy as np
import pyautogui
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


#time.sleep(5); pyautogui.moveRel(100, 0)
#time.sleep(5); pyautogui.moveRel(2560//4, 1440//4)

#mouse.move(100,100)

time.sleep(5); record_mouse()

#vis_mouse_log()
