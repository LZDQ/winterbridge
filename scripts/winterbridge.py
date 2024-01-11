import pyautogui
import numpy as np
from util import *
import shared
from status import stats_parser
from communicate import receive_packets
from threading import Thread, Lock
import os
import time
import matplotlib
matplotlib.use('agg')
import matplotlib.pyplot as plt
#pyautogui.FAILSAFE = False

shift = config.keymappings.sneak

def wait_info():
    # Clear and wait for info. Will block until new info comes
    # Return False if cancelled
    t = time.time()
    while True:
        data = shared.pack_que.get()
        tp = data['type']
        if tp=='info':
            #return data
            if data['time'] >= t*1000:
                return data
        elif tp=='cancel':
            shared.cancelled = True
            shared.cancel_cause = data['cause']
            return False
        else:
            update_skill(data)

def last_info():
    # Get the latest info. Will not block
    # Return False if cancelled, None if no new info
    info = None
    while not shared.pack_que.empty():
        data = shared.pack_que.get()
        tp = data['type']
        if tp=='info':
            info = data
        elif tp=='cancel':
            shared.cancelled = True
            shared.cancel_cause = data['cause']
            return False
        else:
            update_skill(data)
    return info


def sort_items(items):
    n = len(items)
    p = [-1] * n
    q = [-1] * n
    vis = [False] * n
    done = True
    for name, slot in config.sort_hotbar.items():
        for i in range(n):
            if name in items[i]:
                if q[slot]==-1:
                    p[i] = slot
                    q[slot] = i
                    if i!=slot:
                        done = False
                else:
                    print("Already occupied:", name)
    if done:
        print('Already sorted')
        return
    window = get_window_info()
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
        #print("swap", u, "and", v)
        x_u = window.X + round(window.WIDTH * (config.window.INV_X1*(n-1-u) + config.window.INV_X9*u) / (n-1))
        y = window.Y + round(window.HEIGHT * config.window.INV_Y)
        #print("x", x_u, "y", y, 'key', config.keymappings.hotbar[v])
        ri.moveTo((x_u, y))
        time.sleep(0.05 if i else 0.03)
        hotkey = config.keymappings.hotbar[v]
        if hotkey.startswith('mouse'):
            ri.click(hotkey, 0.01)
        else:
            pyautogui.press(hotkey, interval=0.01)  # press another hotbar key
        time.sleep(0.05 if i+1<len(rec) else 0.01)
    pyautogui.press(config.keymappings.inventory)
    return

def test(data):
    direction = get_dir_vec3(*data['dir'].values())
    pos_delta = np.array(list(data['pos_delta'].values()))
    print("Direction:", *direction)
    print("Pos Delta:", *pos_delta)

def record():
    rec = []
    lag = []
    gap = []
    t1 = None
    while True:
        info = wait_info()
        if info is None:
            break
        rec.append(info)
        lag.append(round(time.time()*1000)-info['time'])
        t2 = info['time']
        if t1 is not None:
            gap.append(t2-t1)
        t1 = t2

    # Analyze
    print(f"Lag: min {min(lag)} max {max(lag)} avg {np.mean(lag)}")
    print(f"Gap: min {min(gap)} max {max(gap)} avg {np.mean(gap)}")
    rec_pos = np.array([list(r['pos'].values()) for r in rec])
    if abs(rec_pos[-1,0]-rec_pos[0,0]) > abs(rec_pos[-1,2]-rec_pos[0,2]):
        rec_pos_side = rec_pos[:,:2]
    else:
        rec_pos_side = rec_pos[:,:0:-1]
    rec_pos_side[:,1] -= np.min(rec_pos_side[:,1])
    plt.scatter(*zip(*rec_pos_side))
    plt.savefig('record.png')
    #os.system("xdg-open record.png")
    plt.close()
    rec_ground = [xy[0] for xy in rec_pos if xy[1]==0.]
    for d in np.diff(rec_ground):
        if d>1:
            print("Leap:", d)
    pos_deltas = [np.array(list(data['pos_delta'].values())) for data in rec]
    delta_abs = [np.linalg.norm(pos_delta[::2]) for pos_delta in pos_deltas]
    print(f"|pos_delta| 2D: min {min(delta_abs)} max {max(delta_abs)} avg {np.mean(delta_abs)}")
    print("Total avg speed:",
          np.linalg.norm(rec_pos[-1][::2] - rec_pos[0][::2]) / (rec[-1]['time'] - rec[0]['time']) * 1000)
    return

class BaseSkill:
    # Base class
    def __init__(self, data):
        self.position = np.array(list(data['pos'].values()))
        self.direction = get_dir_vec3(*data['dir'].values())
        self.blocks = np.array(data['blocks'])
        self.player_pos = np.floor(self.position).astype(int)  # player's block pos
        self.info_log = []

    def get_block(self, block_pos):
        return self.blocks[tuple(block_pos - self.player_pos + np.array(self.blocks.shape)//2)]

    def set_block(self, block_pos, value):
        self.blocks[tuple(block_pos - self.player_pos + np.array(self.blocks.shape)//2)] = value

    def cancel(self, cause=None):
        self.end_t = time.time()
        if cause is None:
            cause = shared.cancel_cause
        return

    def set_info(self, info):
        self.info_log.append(info)
        self.position = np.array(list(info['pos'].values()))
        # Do not update direction
        self.blocks = np.array(info['blocks'])
        self.player_pos = np.floor(self.position).astype(int)

    def wait_info(self):
        info = wait_info()
        if info is False:
            self.cancel()
            return False
        self.set_info(info)
        return True

    def last_info(self):
        # Return True if not cancelled
        info = last_info()
        if info is None:
            return True
        if info is False:
            return False
        self.set_info(info)
        return True



class BaseBridge(BaseSkill):

    def __init__(self, data):
        super().__init__(data)
        for s in ['ninja', 'god', 'telly']:
            if data['type'].startswith(s):
                self.tp = s
        self.get_dir_go()  # get self.dir_go, a 2D int array to go
        if self.get_block(self.player_pos - (0, 1, 0))=='Air':
            self.begin_pos = self.player_pos - (self.dir_go[0], 0, self.dir_go[1])
        else:
            self.begin_pos = self.player_pos.copy()
        self.start_y = self.position[1]
        self.count_dis = 0

    def set_info(self, info):
        self.info_log.append(info)
        position = list(info['pos'].values())
        #self.pos_y = position[1]
        if position[1] < self.start_y:
            self.cancel('fell')
            return False
        self.position = np.array(position[::2])

    def cancel(self, cause=None):
        print("Cancel")
        self.end_t = time.time()
        if cause is None:
            cause = shared.cancel_cause
        if cause=='manual':
            ri.keyDown(shift)
            ri.keyDown('a')
            ri.keyDown('w')
            ri.keyUp('s')
            ri.keyUp('d')
            time.sleep(0.1)
            ri.keyUp('a')
            ri.keyUp('w')
            ri.keyUp(shift)
            ri.keyUp('space')
        else:
            # 'hit' or fell
            ri.keyUp('s')
            ri.keyUp('d')
            ri.keyUp(shift)
            ri.keyUp('space')

    def get_dir_go(self):
        r'''
        Calculate bridge direction
        This works for NinjaBridge and GodBridge. As for Telly, the direction is always same as which player's pointing.

        The procedure is:
        1. If the player is pointing down (pitch>30), the direction will be just as the 
            player is starting to bridge (yaw+=135)
        2. If the player is not pointing down (pitch<=30), go to the direction the player is pointing.
            Sleep for 1s to avoid being banned.
        3. If the player is not standing on a block, change the starting block_pos.
        '''
        pitch, yaw = get_pitch_yaw(self.direction)
        position = self.player_pos

        # Store raw direction to go in _dir
        if self.tp!='telly' and pitch>30:
            yaw += 135
            _dir = get_dir_vec3(0., yaw)
        else:
            _dir = direction

        # Get dir_go
        if abs(_dir[0]) > abs(_dir[2]):
            self.dir_go = np.array([
                np.sign(_dir[0]),
                0,
                ], dtype=int)
        else:
            self.dir_go = np.array([
                0,
                np.sign(_dir[2]),
                ], dtype=int)

    def get_ninja_god_info(self):

        dir_go = self.dir_go
        position = self.player_pos
        # Check whether the player is reaching out edge
        if self.get_block(position - (0, 1, 0))=='Air':
            begin_pos = position - (dir_go[0], 0, dir_go[1])

        if self.tp=='ninja':
            conf = config.ninjabridge
        else:
            conf = config.godbridge

        # Get other info
        if dir_go[0]:
            self.dir_eye = np.array([  #  a 3D float array to look at
                conf.direction[0] * dir_go[0],
                conf.direction[1],
                conf.direction[2] * dir_go[0],
                ])
            self.pos_edge = np.array([  # a 2D float array to sneak to
                self.begin_pos[0] + 0.5 + conf.position[0] * dir_go[0],
                self.begin_pos[2] + 0.5 + conf.position[1] * dir_go[0],
                ])
            self.d_s = np.array([0, dir_go[0]])  # a 2D int array. dir when pressing 's'
        else:
            self.dir_eye = np.array([
                -conf.direction[2] * dir_go[1],
                conf.direction[1],
                conf.direction[0] * dir_go[1],
                ])
            self.pos_edge = np.array([
                self.begin_pos[0] + 0.5 - conf.position[1] * dir_go[1],
                self.begin_pos[2] + 0.5 + conf.position[0] * dir_go[1],
                ])
            self.d_s = np.array([-dir_go[1], 0])

    def ninja_god_adjust(self):
        # Enable hotkey, adjust position and direction

        ri.keyDown(shift)
        rotate_fast(self.dir_eye, self.direction)
        ri.keyDown('s')
        ri.keyDown('d')
        #breakpoint()

        # Wait until reaching the edge
        while True:
            if not self.wait_info():
                return False
            dist = self.get_dist()
            #print(dist)
            if dist<0.1: break
        print("Reached edge")

        # Move to the correct side-axis
        if manh_dist(self.position, self.pos_edge)>0.03:
            if np.dot(self.d_s, self.pos_edge - self.position)>0:
                # go s
                key = 'd'
            else:
                key = 's'
            ri.keyUp(key)
            #print("Key:", key)
            last_dist = 100.
            while True:
                if not self.wait_info():
                    return False
                dist = manh_dist(self.position, self.pos_edge)
                #print("Position:", position)
                #print("Pos2:", pos2)
                #print(dist)
                if dist<0.03 or dist>last_dist: break
                last_dist = dist
            ri.keyDown(key)
            time.sleep(0.01)
        print("Position corrected")
        return True

    def get_dist(self):
        # Get the (directed) distance between position and pos_edge
        return np.dot(self.pos_edge - self.position, self.dir_go)

    def analyze(self):
        print(f"Avg speed: {self.count_dis / (self.end_t - self.start_t)}")


class NinjaBridge(BaseBridge):
    step_forward = None
    left_upward = None
    left_forward = None

    def __init__(self, data):
        super().__init__(data)
        self.get_ninja_god_info()

    def ninja_1block(self):
        # 1 block forward

        # From standing at the edge, to the next edge
        ri.rightDown()
        ri.keyUp(shift)
        time.sleep(0.05)
        ri.rightUp()

        self.pos_edge += self.dir_go

        # Walk till near the edge
        #time_walk = np.random.randn() * 0.01 + 0.209
        #time_walk = np.clip(time_walk, 0.2, 0.21)
        time_walk = 0.18
        # Wait for the delay
        time.sleep(time_walk)
        #print("Start sneaking")

        # Sneak till the edge
        #time_sneak = np.random.randn() * 0.001 + 0.02
        #time_sneak = np.clip(time_sneak, 0.01, 0.01)
        time_sneak = 0.02
        ri.keyDown(shift)
        time.sleep(time_sneak)
        while True:
            if not self.wait_info():
                return False
            dist = self.get_dist()
            print(dist)
            if dist<0.05:
                break
            print("Not breaked")
        print("------------------------------------------------------------------")
        return True

    def ninja_1stair(self):
        # 1 block forward and upward

        # From standing at the edge, to the next edge
        ri.rightDown()
        ri.keyUp(shift)
        time.sleep(0.05)
        ri.rightUp()
        time.sleep(0.05)
        ri.keyDown(shift)

        ri.press('space', 0.05)
        time.sleep(0.06)
        ri.rightClick(0.01)
        ri.rightClick(0.01)

        self.pos_edge += self.dir_go

        # Sneak till the edge
        #time_sneak = np.random.randn() * 0.001 + 0.02
        #time_sneak = np.clip(time_sneak, 0.01, 0.022)
        time_sneak = 0.02
        time.sleep(time_sneak)
        while True:
            if not self.wait_info():
                return False
            dist = self.get_dist()
            print(dist)
            if dist<0.05:
                break
        print("Reached new edge")
        return True

    def ninja_bridge(self, style):
        if not self.ninja_god_adjust():
            return

        # Start ninja bridge
        if style=='ninjabridge':
            step_forward = None
            left_upward = 0
            left_forward = 10000
        elif style=='ninja_inc2':
            step_forward = 2
            left_upward = 10000
            left_forward = 2
        elif style=='ninja_inc3':
            step_forward = 3
            left_upward = 5
            left_forward = 3

        self.start_t = time.time()
        while True:
            if left_upward>0 and left_forward==1:
                if not self.ninja_1stair():
                    return
                left_forward = step_forward
                left_upward -= 1
            else:
                if not self.ninja_1block():
                    return
                left_forward -= 1

            with Lock():
                if NinjaBridge.step_forward is not None:
                    step_forward = NinjaBridge.step_forward
                    NinjaBridge.step_forward = None
                if NinjaBridge.left_forward is not None:
                    left_forward = NinjaBridge.left_forward
                    NinjaBridge.left_forward = None
                if NinjaBridge.left_upward is not None:
                    left_upward = NinjaBridge.left_upward
                    NinjaBridge.left_upward = None

            self.count_dis += 1


class GodBridge(BaseBridge):
    # God Bridge is not available

    def __init__(self, data):
        super().__init__(data)
        self.get_ninja_god_info()

    def god_1block(self):
        # Wait and place

        t1 = time.time()
        self.pos_edge += self.dir_go

        while True:
            if not self.wait_info():
                return False
            dist = self.get_dist()
            print(dist)
            if dist<0.7:
                break
            print("Not breaked")

        # Get speed
        ticks = 1
        speed = 0.
        for i in range(ticks):
            speed += np.dot(list(self.info_log[-1-i]['pos_delta'].values())[::2], self.dir_go)
        speed /= ticks
        speed *= 20
        dt = self.info_log[-1]['time'] / 1e3 + np.dot(self.pos_edge - self.position, self.dir_go) / speed
        dt -= time.time()
        print("Speed:", speed)
        print("Sleep for:", dt)
        dt += -0.05
        time.sleep(dt)
        ri.rightClick(0.001)
        time.sleep(0.05)
        ri.rightClick(0.001)
        time.sleep(0.05)
        ri.rightClick(0.001)
        time.sleep(0.05)

        t2 = time.time()
        print("Time:", t2-t1)
        return True

    def god_bridge(self):
        if not self.ninja_god_adjust():
            return
        ri.rightDown()
        ri.keyUp(shift)
        time.sleep(0.001)
        ri.rightUp()

        # Start god bridge
        left_forward = 8
        while True:
            if left_forward==1:
                break
            else:
                left_forward -= 1
            if not self.god_1block():
                return
            print("                                          --------------------")


def test_pointing(position, eye, direction, blocks):
    position = np.array(position)
    direction = get_dir_vec3(*direction)
    blocks = np.array(blocks)
    eye = np.array(eye)
    eye_blockpos = np.floor(eye).astype(np.int64)
    print('Eye', eye[0], eye[1], eye[2])
    print('Position', position[0], position[1], position[2])
    res = None
    res_name = None
    for (i, j, k), block in np.ndenumerate(blocks):
        if block=='Air': continue
        x = int(position[0]) + i - blocks.shape[0]//2
        y = int(position[1]) + j - blocks.shape[1]//2
        z = int(position[2]) + k - blocks.shape[2]//2
        if intersects(eye, direction, (x, y, z)):
            print("Found", block, "at", x, y, z)
            if res is None or manh_dist(res, eye_blockpos)>manh_dist((x, y, z), eye_blockpos):
                res = (x, y, z)
                res_name = block
    if res:
        print(res_name)
    else:
        print("Not pointing")
    return

def rotate_fast(dir2, dir1, duration=0.05):
    #print("Duration:", duration)
    t1 = time.time()
    _t1 = t1
    pixel_x, pixel_y = get_rotate_pixel(dir2, dir1)
    if max(abs(pixel_x), abs(pixel_y)) <= 1:
        return
    points = gen_line((0, 0), (pixel_x, pixel_y), (abs(pixel_x)+abs(pixel_y))//5+2)
    points = np.round(points).astype(int)
    #print("Pixel:", (0, 0), (pixel_x, pixel_y))
    #print("Smooth:", points[0], points[-1])
    m = min(100, len(points)-1)
    #print("m:", m)
    t2 = time.time()#; print("Gen line:", t2-t1); t1 = t2
    left_time = duration - (t2 - _t1)
    left_time *= 0.85
    for i in range(m):
        p1 = points[round((len(points)-1)*i/m)]
        p2 = points[round((len(points)-1)*(i+1)/m)]
        #print("From", p1, "to", p2)
        d = p2-p1
        if np.sum(np.abs(d))==0:
            continue
        #print("Move", *d)
        #mouse.move(*d)

        ri.move(d)

        time.sleep(left_time/m)

    t2 = time.time()#; print("Move mouse:", t2-t1); t1 = t2
    if t2 - _t1 < duration:
        time.sleep(duration - (t2 - _t1))
    #print("Total:", t2-_t1)
    return

class BlockIn(BaseSkill):

    def __init__(self, data):
        super().__init__(data)
        self.eye = np.array(list(data['eye'].values()))
        self.eye_blockpos = np.floor(self.eye).astype(int)
        self.placed_pos = []

    def set_info(self, info):
        # Basically only check whether cancelled
        self.info_log.append(info)

    def work(self):
        self.start_t = time.time()
        que = []
        for dy in [-1, 0, 1]:
            for dx, dz in [(0,1), (1,0), (0,-1), (-1,0)]:
                x = self.player_pos[0] + dx
                y = self.player_pos[1] + dy
                z = self.player_pos[2] + dz
                que.append(np.array([x, y, z]))
        x, y, z = self.player_pos
        que.append(np.array([x-1, y+2, z]))
        que.append(np.array([x+1, y+2, z]))
        que.append(np.array([x, y+2, z]))
        for pos in que:
            if not self.last_info():
                return
            if self.get_block(pos)!='Air':
                continue
            direction = self.get_place_direction(pos)
            if direction is not None:
                print("place block on", *pos)
                self.placed_pos.append(pos)
                rotate_fast(direction, self.direction, duration=0.03)
                self.direction = direction
                time.sleep(0.05)
                ri.rightClick(0.05)
                time.sleep(0.05)
                #time.sleep(1)
                self.set_block(pos, 'Wool')
            else:
                print("can't place on", *pos)

    def get_place_direction(self, pos):
        #breakpoint()
        for i in range(3):
            for j in [-1, 1]:
                base = pos.copy()  # base is the position of block to be clicked
                base[i] += j
                if self.get_block(base)=='Air':
                    continue
                if manh_dist(self.eye_blockpos, base) < manh_dist(self.eye_blockpos, pos):
                    continue
                for _ in range(30):
                    if _:
                        target = base.astype(float)
                        target[i] = max(base[i], pos[i])
                        for k in range(3):
                            if i==k: continue
                            target[k] += np.random.uniform(0.2, 0.8)
                        direction = target - self.eye
                        direction /= np.linalg.norm(direction)
                    else:
                        # If the direction is already satisfied
                        if not intersects(self.eye, self.direction, base, padding=-0.01):
                            continue
                        direction = self.direction.copy()
                    dist = manh_dist(self.eye_blockpos, base)
                    ex, ey, ez = self.eye_blockpos
                    ok = True
                    for x in range(ex-dist+1, ex+dist):
                        for y in range(ey-dist+abs(x-ex)+1, ey+dist-abs(x-ex)):
                            for z in range(ez-dist+abs(x-ex)+abs(y-ey)+1, ez+dist-abs(x-ex)-abs(y-ey)):
                                if self.get_block([x, y, z])=='Air':
                                    continue
                                if intersects(self.eye, direction, np.array([x, y, z]), padding=0.01):
                                    ok = False
                                    break
                            if not ok: break
                        if not ok: break
                    if ok:
                        return direction
        return None

def handle_skill(data):
    # Handle request (only skills) in background
    shared.cancelled = False

    tp = data['type']

    if tp=='sort':
        sort_items(data['hotbar'])

    elif tp=='test':
        test(data)

    elif tp=='record':
        record()

    elif tp=='blockin':
        blockin = BlockIn(data)
        blockin.work()

    elif tp.startswith('ninja'):
        ninja = NinjaBridge(data)
        ninja.ninja_bridge(tp)
        ninja.analyze()

    elif tp=='tellybridge':
        telly = TellyBridge(data)
        telly.telly_bridge()

    elif tp=='godbridge':
        god = GodBridge(data)
        god.god_bridge()

    elif tp=='playerlist':
        #print(data['names'])
        shared.players_que.put(data['names'])

    #print(data['type'])
    return

def update_skill(data):
    tp = data['type']
    if tp.startswith('ninja'):
        with Lock():
            if tp=='ninjabridge':
                NinjaBridge.left_forward = 10000
            elif tp=='ninja_inc2':
                NinjaBridge.step_forward = 2
                NinjaBridge.left_forward = 1
                NinjaBridge.left_upward = 10000
            elif tp=='ninja_inc3':
                NinjaBridge.step_forward = 3
                NinjaBridge.left_forward = 1
                NinjaBridge.left_upward = 10000
            else:
                print(tp)
    elif tp=='playerlist':
        shared.players_que.put(data['names'])

def main():

    Thread(target=receive_packets).start()
    Thread(target=stats_parser.update_stats).start()

    while True:
        data = shared.pack_que.get()
        tp = data['type']

        if tp=='info':
            print("You should break")

        elif tp=='cancel':
            print("Break")

        else:
            handle_skill(data)

