1. intercept all packets (done)
2. get entity or block pointing at (done, `mc.hitResult`)
3. spam-click-left (done)

4. spam-click-right (done)

5. sort (done)

6. Ninja bridge (done, wait for auto cancel)

7. God bridge (queued for Packet Handle)

8. Diagonal bridge (done)

9. Side clutch (done)

10. Ladder clutch


## Deobfuscate packets sent

First of all, Serverbound means its from client to server.



Place block:

`ServerboundUseItemOnPacket`



Swap item (sort):

AbstractContainerMenu -   

```this.minecraft.gameMode.handleInventoryMouseClick(this.menu.containerId, p_97779_, p_97780_, p_97781_, this.minecraft.player);```



## Utils

`Direction`: utility for NORTH, SOUTH, WEST, EAST (,UP, DOWN)

`ensureHasSentCarriedItem`: private function to send set carried item
