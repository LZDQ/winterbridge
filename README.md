# winterbridge

This is a bedwars assistant developed by LZDQ. Currently it requires Minecraft 1.19.X and forge.

## functions

1. Ninja bridge
2. Ninja bridge with inc
3. Ninja diagonal bridge (with inc)
4. Sort hotbar
5. Spam-clicking (both left and right)
6. Block clutch
7. //God bridge (caution with this! it will get banned)

## Usage

#### bridge methods

For straight line bridges (like ninja bridge), first face to the direction of which you would point towards in ninja bridge. The direction to go is calculated as your current pitch + 135, rounded to one of the four directions. Then press the key binded to your ninja bridge method and it should start bridging. Please do not touch any keys or move your mouse.

To cancel bridging, press \` (grave accent, next to your `1`).

Sometimes you would like to go inc(rease) while bridging. When you are ninja bridging invoked by `F6` of `F7`, you can press `F6` or `F7` to change the inc mode. By default, pressing `F6` goes flat and `F7` goes inc 5 times, each separated for 3 blocks.



For diagonal bridges, also face to the direction of which you would point towards in diagonal bridge (opposite to which you would go) and press `Y` or `U`. The direction is calculated as your current pitch + 180. You can change the mode of inc. `Y` is for flat and `U` is for inc for inf times, each separated 2 steps.



If you see your screen lagging when adjusting position, don't worry! It's supposed to be like that because I'm directly changing player's view rotation every tick. I also added some anti-anti-cheat mechanisms of drawing a smooth function to avoid being detected.



Please do not use godbridge cause I can't reproduce it legitmately. My godbridge is actually sending packets before I can, because simulating click usually fails. It will get banned even on BlockMS so don't try it on servers.

--------------

#### spam clicking

For swords, I strongly suggest binding your hotbar switch button of `1` to your mouse side button (Button 4). When holding down your side button, you start spam clicking your left mouse button every random 30~50 ms. So you can instantly switch to your sword and spam-click.

For blocks, I strongly suggest binding your hotbar switch button of your block slot to your mouse side button (Button 5). The logic of spam right clicking is as follows:

* When you actually switch from another slot to your block slot, do nothing until you release the button.
* When you are holding down your block slot switching button but you are already holding that slot, start spam right clicking every client tick (50ms).

------------

#### sort

Pressing `F4` automatically sorts your hotbar slots. By default, it will place the best sword at slot 1, pickaxe at slot 2, shears at slot 3, axe at slot 4, and blocks with the msot count at slot 5. This function swap two items every 0.5s. It doesn't open up your inventory, and you can move around when sorting if the server is not checking this subtle hack.

You can change the slots config in the mod's config file, but the items to sort are fixed.

-----------------

#### block clutch

When holding block, not on the ground, and clicking left button, the mod will try to block clutch. First, it will try to place a block under you. If success, second it will try to place a block based on the first block in the direction of you. So normally when side clutching on a bridge, it will prevent you from falling. However, be caution to use this because it is sending packets of placing blocks instead of rotating and clicking. It won't get banned on BlocksMC for now.

## Other features

* Selecting cheat mode. There are 3 different modes: absolute, relative, slightly (0, 1, 2). absolute means there is no anti anti-cheat. slightly means there is only slightly cheats. The key differences between these modes are precision and bridging speed. Using absolute mode 99% makes other players spot you hacking, while using slightly is even a little slower than myself bridging. By default, absolute, relative, slightly mode wait for 0, 1, 2 ticks after each step. Also, block clutching is disabled in slightly mode. Press `n` to cycle these mode. You can also change the config of default cheat mode.
* Custom config in `.minecraft/config/winterbridge-config.toml`. You can change slot to place sword, pickaxe, shears, axe, and blocks, default cheat mode, color of bridging prompts, and so on.
* Auto cancelling bridging. When hitted or fell, automatically cancel bridging.

## Future work

I would add Block-In and ladder clutch in the future. I also considered other functions such as location players, anti-invis, but these would spoil game experience. The purpose of this mod is to reduce accident rate of bridging, not to win by hacking.

Maybe release a video of how to use this.
