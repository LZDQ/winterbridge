# winterbridge

This is a bedwars assistant developed by LZDQ. Currently it requires Minecraft 1.19.X and forge.

## Requirement

[configured-2.1.1-1.19.4.jar](https://www.curseforge.com/minecraft/mc-mods/configured/files/4462894)

## Functions

1. Ninja bridge
2. Ninja bridge with increase
3. Ninja diagonal bridge (with increase)
4. Spam-clicking (both left and right)
5. Block clutch
6. Switch tools
7. Quick switching to (and use) items (fireball, tnt, gapple, and so on)
8. Quick switching to hard blocks
9. Block-in
10. Put money into chest
11. Double-click
12. Drop money
13. Block + Ladder clutch
14. Send 'inc', 'incc' message

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

#### block clutch

When holding block, not on the ground, and clicking left button, the mod will try to block clutch. First, it will try to place a block under you. If success, second it will try to place a block based on the first block in the direction of you. So normally when side clutching on a bridge, it will prevent you from falling. However, be caution to use this because it is sending packets of placing blocks instead of rotating and clicking. It won't get banned on BlocksMC for now, but I think it gets banned on Jartex.

-----

#### select hardest block

Press a key to select the hardest block. Saves a lot time for block-in, cause when you are digging downwards, you will want to use the hardest block (which you gathered from their bed defense) to block other players. This also saves some time when doing your bed defense. Default order: `Obsidian` -> `End Stone` -> `Wood` -> `Clay` -> `Wool`. I realize that there is a better way to decide the order but I don't want to bother to change it.

-----

#### block-in

Press a key to cover your nearby area. Instant block-in, and it works.

-----

#### auto login / auto who

Tired of typing your password again and again? Now configure your password in this mod and press a key to login! Another key is for auto /who.

## Other features

* Selecting cheat mode. There are 3 different modes: absolute, relative, slightly (0, 1, 2). The names are just to make sure they are all 8-letter long and look better, like username and password. absolute means there is no anti anti-cheat. slightly means there is only slightly cheats. The key differences between these modes are precision and bridging speed. Using absolute mode 99% makes other players spot you hacking, while using slightly is even a lot slower than myself bridging. By default, absolute, relative, slightly mode wait for 0, 1, 2 ticks after each step. Also, some behaviors like block clutching are disabled in slightly mode. Press `n` to cycle these mode. You can also change the config of default cheat mode.
* Configurable, powered by the mod "configured". Go to "Mod" -> "winterbridge" and click "Config".
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



#### Bug fixes:

1. - [x] Distinguish between TNT and blocks.
2. - [ ] Disable some functions when just finished typing and mistyped the hotkey.
3. - [x] Increase Ninja bridge pitch.

4. - [ ] When jump potion, disable auto block and ladder clutch

#### Specific functions of each key:

|         | IN_GAME                | GUI (Chest)           | GUI (Inventory)          |
| ------- | ---------------------- | --------------------- | ------------------------ |
| q       | ladder / hardest block | store money           | place money to inventory |
| e       | tool, KB-stick         | get money             | place money to hotbar    |
| r       | fireball               |                       |                          |
| f       | inventory              |                       |                          |
| x       | gapple                 |                       |                          |
| 1       | cancel                 |                       |                          |
| 2       | egg                    | swap with second slot | swap with second slot    |
| 3       | potions                | swap with last slot   | swap with last slot      |
| t       | drop money             |                       | drop money               |
| b       | block in               |                       |                          |
| thumb   | pearl                  |                       |                          |
| extra   | tnt                    |                       |                          |
| middle2 | reserved               |                       |                          |
| middle3 | reserved               |                       |                          |



## TODO

- [x] Modify and add keys
- [ ] Optimize block-in initialization time
- [x] Bind double-click (double-jump) with block clutch
- [x] Block + ladder clutch (when falling and pressing space)
- [x] Remove rushing mode and rearrange keys
- [ ] ~~Distinguish between mining and double-click when holding blocks~~
- [x] Auto tool switch to empty or sword if holding block
- [ ] Reduce packets sent when spam right click (if collide with player)
- [x] Sword/stick right click = left click + left click in next frame (if hitObject is entity)
- [x] Sword left spam only start spam if hitObject is entity (only when switching)
- [x] Sword left spam if hitObject is not player, do not click with a probability
- [ ] Optimize store money

## Resourcepacks

[cute sanrio terracotta](https://www.curseforge.com/minecraft/texture-packs/clovers-sanrio-glazed-terracotta) change all blazed terracotta to just terracotta (remove all `_blazed` in filename); TODO: change this because some colors are not covered

[wool overlay 1.8.9 manually upgraded to 1.19.4](http://www.mediafire.com/file/bjplir0mtieakng/#3+Wool+Overlay.zip/file), see [here](https://minecraft.wiki/w/Item_tag_(Java_Edition)#wool) for item tag list

[fullbright](https://www.curseforge.com/minecraft/texture-packs/fullbright)

[planks-reimagined](https://www.planetminecraft.com/texture-pack/planks-reimagined/)

[improved-logs](https://www.curseforge.com/minecraft/texture-packs/improved-logs)

Aimz - PVP Crosshair

## CHANGELOG

See [`CHANGELOG.md`](CHANGELOG.md).

## Configs

FOV: 100
