import os
import time
from raw_input import RawInput
from evdev import UInput, ecodes as e
from pynput.mouse import Controller

class RawInputLinux(RawInput):
    def __init__(self):
        if not os.access('/dev/uinput', os.W_OK):
            os.system("sudo chmod 777 /dev/uinput")
        self.ui = UInput(
                {
                    e.EV_REL : [e.REL_X, e.REL_Y],
                    #e.EV_ABS : [e.ABS_X, e.ABS_Y],
                    e.EV_KEY : [
                        e.BTN_LEFT,
                        e.BTN_RIGHT,
                        e.BTN_SIDE,
                        e.BTN_EXTRA,
                        e.KEY_W,
                        e.KEY_A,
                        e.KEY_S,
                        e.KEY_D,
                        e.KEY_SPACE,
                        e.KEY_LEFTSHIFT,
                        e.KEY_LEFTCTRL,
                        ],
                    },
                name='winterbridge',
                version=0x1
                )
        self.pyn_mouse = Controller()
        self.key_code = {
                'w': e.KEY_W,
                'a': e.KEY_A,
                's': e.KEY_S,
                'd': e.KEY_D,
                'space': e.KEY_SPACE,
                'shift': e.KEY_LEFTSHIFT,
                'ctrl': e.KEY_LEFTCTRL,
                }
        self.button_code = {
                'left': e.BTN_LEFT,
                'right': e.BTN_RIGHT,
                'mouse4': e.BTN_SIDE,
                'mouse5': e.BTN_EXTRA,
                }

    def click(self, button, t):
        self.ui.write(e.EV_KEY, self.getButton(button), 1)
        self.ui.syn()
        time.sleep(t)
        self.ui.write(e.EV_KEY, self.getButton(button), 0)
        self.ui.syn()

    def rightClick(self, t):
        self.ui.write(e.EV_KEY, e.BTN_RIGHT, 1)
        self.ui.syn()
        time.sleep(t)
        self.ui.write(e.EV_KEY, e.BTN_RIGHT, 0)
        self.ui.syn()

    def rightDown(self):
        self.ui.write(e.EV_KEY, e.BTN_RIGHT, 1)
        self.ui.syn()

    def rightUp(self):
        self.ui.write(e.EV_KEY, e.BTN_RIGHT, 0)
        self.ui.syn()

    def mouseDown(self, button):
        self.ui.write(e.EV_KEY, self.getButton(button), 1)
        self.ui.syn()

    def mouseUp(self, button):
        self.ui.write(e.EV_KEY, self.getButton(button), 0)
        self.ui.syn()

    def move(self, position):
        if position[0] or position[1]:
            if position[0]:
                self.ui.write(e.EV_REL, e.REL_X, position[0])
            if position[1]:
                self.ui.write(e.EV_REL, e.REL_Y, position[1])
            self.ui.syn()

    def moveTo(self, position):
        # Not using raw input
        self.pyn_mouse.position = position

    def press(self, key, t):
        self.ui.write(e.EV_KEY, self.getKey(key), 1)
        self.ui.syn()
        time.sleep(t)
        self.ui.write(e.EV_KEY, self.getKey(key), 0)
        self.ui.syn()

    def keyDown(self, key):
        self.ui.write(e.EV_KEY, self.getKey(key), 1)
        self.ui.syn()

    def keyUp(self, key):
        self.ui.write(e.EV_KEY, self.getKey(key), 0)
        self.ui.syn()

