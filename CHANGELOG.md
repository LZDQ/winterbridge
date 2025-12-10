# CHANGELOG

## v2.4

* Fix bug in v2.3 that double block still works when the player is on ground.
* Switching to ladder or hardest block now has a new feature: after some seconds of block-in (default 15s, configurable at `blockin_post_time`), the switch is directly to hardest block, without preferring ladder.

## v2.3

* Disable block clutch if has jump boost.
* Double block now only works when the player is not on the ground.

## v2.2

* Add ice bridge keybinding and treat it as non-block.
* Send different inc messages ("inc" then "incc" then "inc", ...) to avoid being intercepted by Jartex.
* Update terracotta texture pack to sanrio.
* Update logs and planks texture pack.
* Add "configured" as a dependency and tool to modify configuration.
* Remove all hotbar sorting related things.
* Misc changes on code, config, gradle version, etc.
