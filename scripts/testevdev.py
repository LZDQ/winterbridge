import evdev
from evdev import InputDevice, categorize, ecodes, list_devices
import numpy as np
import matplotlib.pyplot as plt

def record_mouse():
    # Find the device file for the mouse
    devices = [InputDevice(path) for path in list_devices()]
    print(devices)
    for device in devices:
        print(device.name)
        if device.name=='Razer Razer Basilisk V3':
            print("Found:", device.path)

    device = InputDevice('/dev/input/event16')
    enable_rec = False
    log_mouse = []

    for event in device.read_loop():
        if False or event.type == evdev.ecodes.EV_REL:
            print(evdev.categorize(event), event.value)
            if enable_rec:
                #breakpoint()
                log_mouse.append((event.code, event.value, event.sec, event.usec))
                #print(event.code)
        if event.type == ecodes.EV_KEY:
            if event.code == ecodes.BTN_SIDE and event.value == 1:
                enable_rec = not enable_rec
                if not enable_rec:
                    break

    log_mouse = np.array(log_mouse)
    print(log_mouse.shape, log_mouse.dtype)
    np.save('log_mouse.npy', log_mouse)

def print_rec():
    log_mouse = np.load('log_mouse.npy')
    p = [0, 0]
    a = [p[:]]
    for e in log_mouse:
        p[e[0]] += e[1]
        a.append(p[:])
    plt.scatter(x=[xy[0] for xy in a], y=[-xy[1] for xy in a])
    plt.show()

#record_mouse()
print_rec()
