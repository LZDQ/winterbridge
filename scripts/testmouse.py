import evdev
from evdev import InputDevice, categorize, ecodes, list_devices
import time

# Find the device file for the mouse
devices = [InputDevice(path) for path in list_devices()]
print(devices)
for device in devices:
    print(device.name)
    if 'mouse' in device.name.lower():
        print("Found")

device = InputDevice('/dev/input/event21')
for event in device.read_loop():
    if True or event.type == evdev.ecodes.EV_REL:
        #breakpoint()
        print(evdev.categorize(event), event.value)
