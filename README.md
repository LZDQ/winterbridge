# winterbridge

This is a bedwars assistant developed by LZDQ. Currently it requires Minecraft 1.19.X and forge.

## functions

1. Ninja bridge
2. Ninja bridge with increase
3. Ninja diagonal bridge (with increase)
4. Sort hotbar
5. Spam-clicking (both left and right)
6. Block clutch
7. //God bridge (caution with this! it will get banned)
8. Auto switching tools
9. Quick switching to (and use) items (fireball, tnt, gapple, and so on)
10. Quick switching to hard blocks
11. Block-in

## Usage

This mod will probably only be used by myself lol, so no video tutorial

#### bridge methods

For straight line bridges (like ninja bridge), first face to the direction of which you would point towards in ninja bridge. The direction to go is calculated as your current pitch + 135, rounded to one of the four directions. Then press the key binded to your ninja bridge method and it should start bridging. Please do not touch any keys or move your mouse.

To cancel bridging, press \` (grave accent, next to your `1`).

Sometimes you would like to go inc(rease) while bridging. When you are ninja bridging invoked by `F6` of `F7`, you can press `F6` or `F7` to change the inc mode. By default, pressing `F6` goes flat and `F7` goes inc 5 times, each separated for 3 blocks.



For diagonal bridges, also face to the direction of which you would point towards in diagonal bridge (opposite to which you would go) and press `Y` or `U`. The direction is calculated as your current pitch + 180. You can change the mode of inc. `Y` is for flat and `U` is for inc for inf times, each separated 2 steps.



If you see your screen lagging when adjusting position, don't worry! It's supposed to be like that because I'm directly changing player's view rotation every tick. I also added some anti-anti-cheat mechanisms of drawing a smooth function to avoid being detected.



Please do not use godbridge cause I can't reproduce it legitmately. My godbridge is actually sending packets before I can, because simulating click usually fails. It will get banned even on BlockMS so don't try it on servers.

--------------

#### spam clicking

For swords, I strongly suggest binding your hotbar switch button of `1` to your mouse side button (Button 4). When holding down your side button, you start spam clicking your left mouse button every random 35~50 ms. So you can instantly switch to your sword and spam-click. To avoid being banned, there is a 50ms delay if you are not holding the sword at the moment. You can't change this behavior.

For blocks, I slightly suggest binding your hotbar switch button of your block slot to your mouse side button (Button 5). This mod has a key of switching to blocks and start spam right clicking. The logic of spam right clicking is as follows:

* When you are not selecting block, switch to the slot with most blocks and do nothing until you release.
* When you are holding down your block switching button and you are already selecting a block slot, start spam right clicking every client tick (50ms).

------------

#### sort

Pressing `F4` automatically sorts your hotbar slots. By default, it will place the best sword at slot 1, pickaxe at slot 2, shears at slot 3, axe at slot 4, and blocks with the most count at slot 5. This function swap two items every ~0.5s, you can configure the time interval. It doesn't open up your inventory, and you can move around when sorting if the server is not checking this subtle hack.

You can change the slots config in the mod's config file, but the items to sort are fixed. The list of them is: `sword`, `pickaxe`, `shears`, `axe`, `blocks`, `golden apple`, `fireball`, `ladder`, `bridge egg`, `pop-up tower`. No milk or potion because you always have plenty of time to prepare if you buy them.

In the configure file, change the `slot_*` to whatever slot you want. `-1` means you don't want to sort it, and `[0,8]` means slot from 1 to 9.

However, this feature is useless with the help of automatic switches.

-----------------

#### block clutch

When holding block, not on the ground, and clicking left button, the mod will try to block clutch. First, it will try to place a block under you. If success, second it will try to place a block based on the first block in the direction of you. So normally when side clutching on a bridge, it will prevent you from falling. However, be caution to use this because it is sending packets of placing blocks instead of rotating and clicking. It won't get banned on BlocksMC for now, but I think it gets banned on Jartex.

-----

#### auto selecting hardest block

Press a key to select the hardest block. Saves a lot time for block-in, cause when you are digging downwards, you will want to use the hardest block (which you gathered from their bed defense) to block other players. This also saves some time when doing your bed defense. Default order: `Obsidian` -> `End Stone` -> `Wood` -> `Clay` -> `Wool`. I realize that there is a better way to decide the order but I don't want to bother to change it.

-----

#### block-in

Press a key to cover your nearby area. Instant block-in, and it works.

-----

#### auto login / auto who

Tired of typing your password again and again? Now configure your password in this mod and press a key to login! Another key is for auto /who.

## Other features

* Selecting cheat mode. There are 3 different modes: absolute, relative, slightly (0, 1, 2). The names are just to make sure they are all 8-letter long and look better, like username and password. absolute means there is no anti anti-cheat. slightly means there is only slightly cheats. The key differences between these modes are precision and bridging speed. Using absolute mode 99% makes other players spot you hacking, while using slightly is even a lot slower than myself bridging. By default, absolute, relative, slightly mode wait for 0, 1, 2 ticks after each step. Also, some behaviors like block clutching are disabled in slightly mode. Press `n` to cycle these mode. You can also change the config of default cheat mode.
* Custom config in `.minecraft/config/winterbridge-config.toml`. You can change slot to place items, default cheat mode, color of bridging prompts, and so on.
* Auto cancelling bridging. When hitted or fell, automatically cancel bridging.

## Future work

#### Modify keybindings.

The (partial) list of items and functions:

1. Gapple. Frequently used, middle latency.
2. Auto switch to tool. Not frequently used, minimal latency.
3. Fireball. Frequently used, minimal latency.
4. Golem and silverfish. Only used in late game, not frequently, small latency. Interferes with PVP.
5. Hardest block. Seldom used, but could be useful when defending and block-in. Small latency.
6. TNT. Frequently used, small latency. Interferes with parkour.
7. Bridge egg. Not frequently used, but needs minimal latency when fleeing and void-clutching.
8. Pearl. Minimal latency. Requires extra precision (no misclick).
9. Jump. Not frequently used, middle latency.
10. Invis, milk, speed. Not frequently used, middle latency.
11. Ladder. Could be frequently used, middle latency. Interferes with parkour.
12. KB-stick. Seldom used in late game but requires minimal latency.
13. Pop-up tower. Seldom used in block-in, middle latency.
14. Block clutch. Not frequently used but requires minimal latency. Interferes with parkour.
15. Water. Large latency.
16. Bow. Large latency. Interferes with PVP.
17. Other rotational items

Keys available: q, e, r, f, x, 2, 3, t, g, button4, button5, button_thumb=middle, button_extra, button_mid2, button_mid3



Minimal latency: tools, fireball, egg (when fleeing), pearl, KB-stick (late game), block clutch

Small latency: golem and silverfish, hardest block, TNT

Middle latency: gapple (but frequent), jump, invis and milk and speed, ladder, pop-up tower

Large latency: bow, water.



#### New features:

1. Put all money into chest
2. Put all useful items back to slots
3. Two modes: normal, rushing.
4. Double-click
5. Drop all money
6. Block + Ladder clutch
7. Send 'inc' message



#### Bug fixes:

1. - [ ] Distinguish between TNT and blocks.
2. - [ ] Disable some functions when just finished typing and mistyped the hotkey.
3. - [x] Increase Ninja bridge pitch.

   

#### Specific functions of each key:

|         | Normal         | Rushing                | Chest                 | Inventory             |
| ------- | -------------- | ---------------------- | --------------------- | --------------------- |
| q       | ladder         | ladder                 | store money           |                       |
| e       | tool, KB-stick | tool, KB-stick         | get money             |                       |
| r       | fireball       | fireball               | tidy slots            | tidy slots            |
| f       | inventory      | tnt                    | close                 | close                 |
| x       | gapple         | gapple                 | set custom g          | set custom g          |
| 1       | cancel         | cancel                 | unset custom g        | unset custom g        |
| 2       | egg            | egg                    | swap with second slot | swap with second slot |
| 3       | potions        | potions                | swap with last slot   | swap with last slot   |
| t       | drop money     | block in               |                       | drop money            |
| g       | drop / custom  | hardest block / custom | drop                  | drop                  |
| thumb   | pearl          | pearl                  |                       |                       |
| extra   | bow            | bow                    |                       |                       |
| alt     | change mode    | change mode            |                       |                       |
| middle2 | freecam        | freecam                |                       |                       |
| middle3 | tnt            |                        |                       |                       |



## TODO

- [ ] Modify and add keys
- [ ] Optimize block-in initialization time
- [ ] Bind double-click with block clutch
- [ ] Block + ladder clutch (when falling and pressing space)
- [ ] Fix bugs
