#import pyautogui
from util import *
#from winterbridge import *
import matplotlib.pyplot as plt
import numpy as np

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

#pyautogui.keyUp('space'); time.sleep(5); pyautogui.keyDown('space'); pyautogui.keyUp('space')

def test_freq():
    t1 = time.time()
    n = 1000
    for _ in range(n):
        mouse.move(1, 1)
        #mouse.click(Button.right)
        #mouse.press(Button.right); mouse.release(Button.right)
    t2 = time.time()
    print(f"{n} times within {t2-t1} second")

#test_freq()

disc = gen_line((-100, -50), (1, 1)); plt.scatter(x=[xy[0] for xy in disc], y=[xy[1] for xy in disc]); plt.show()


