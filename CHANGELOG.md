# Changelog

## [26.1.2.19]

### Added

- Plunder (`ftb:axe_plunder`) skill attribute: when you kill with an axe in your main hand, its Fortune adds twice its level as Looting, up to your Plunder

### Changed

- Incendium mob and entity functions are run from one pass over loaded entities each tick instead of the datapack clock's `@e` scans; the pack's clock falls back to the scans when the companion is not loaded

### Fixed

- Modular Drones made in the Drone Assembler disappearing on save or relog, from the inventory and from the drone slot; the assembler now creates the drone from the registered item, so it saves (FTBTeam/FTB-Modpack-Issues#13553)
- EvilCraft Spiked Plates generating blood from entities in the `ftbevolutioncompanion:spiked_plate_blacklist` entity tag, such as training dummies that never die

## [26.1.2.18]

### Added

- Adamantite, Aeternium and Aurichalcum Turbine Coil blocks for Colossal Reactors turbines

### Fixed

- Mining Gadgets not giving Mining skill experience
- The LTXI drill's Vein Mine upgrade not giving Mining skill experience for the extra blocks it breaks
- Removed starcatcher payload fix (now fixed in the mod)

## [26.1.2.17]

### Changed

- Hats compat rolled back to Hats 1.2.0 for stability issues

## [26.1.2.16]

### Fixed

- Creative inventory search showing no results on servers
- Stopped structure-placed boss invaders rolling their gear with the server thread's random from worldgen threads (FTBTeam/FTB-Modpack-Issues#13558)
- edgecase crash (FTBTeam/FTB-Modpack-Issues#13557)
- Skill XP from stats worth less than half a point per award, such as swimming, rounding to nothing; those fractions now add up, and stats worth half a point or more pay what they did before
- Shift-clicking an item in a Colossal Reactors Turbine Resource Port screen duplicating it up to a full stack; (FTBTeam/FTB-Modpack-Issues#13573)
- Nutritional nutrients decaying and changing for Witchery vampires, who cannot eat normal food; a vampire's nutrients now stay at 50% (FTBTeam/FTB-Modpack-Issues#13549)
- Mining a Fabricator losing its stored energy and fluids; the dropped Fabricator now keeps its items, fluids and energy and puts them back when placed
- Biomass rate fix (FTBTeam/FTB-Modpack-Issues#13575)
- `/givehat` working with Hats 3.0.0; it now unlocks through Hats' own unlock store, so the toast, sync and collection advancements match a normal unlock

### Removed

- The Hats equip packet fix, which Hats 3.0.0 no longer needs

## [26.1.2.15]

### Added

- additional compat for dynamic resources
- Lancer cooldown reduction skill attribute, so spear skills can shorten the Lancer cooldown

### Changed

- Treasure Goblin escape window 10 s -> 20 s, and its run speed multiplier 1.4 -> 1.1

### Fixed

- Power Armor's Armor Modification Table no longer loses its armor piece when the chunk unloads (FTBTeam/FTB-Modpack-Issues#13502)
- JEI improved compat for jetpacks
- Crash with Stellaris 2.0.7 when a Stellaris fluid slot is used; Oritech oil is accepted by its fluid slots and machines again
- Full Agritech planters keep exporting into the inventory below (FTBTeam/FTB-Modpack-Issues#13499)
- Singleplayer worlds with characters like `?` in their name no longer disconnect on join (FTBTeam/FTB-Modpack-Issues#13506)

## [26.1.2.14]

### Fixed

- data remapping to internal and mc ids

## [26.1.2.13]

### Changed

- internally registered player attibutes for puffskill magic bonuses

### Fixed

- Players on worlds that had Thaumaturge no longer lose their saved attributes, such as Paraglider stamina vessels, after it is removed

## [26.1.2.12]

### Fixed

- Crash on startup when Ars Magica Legacy is not installed
- Warnings and errors in the log when Thaumaturge is not installed

## [26.1.2.11]

### Changed

- The XyCraft Flare Rod no longer needs XyCraft's experimental features, so new worlds no longer have them turned on and no longer show the experimental settings warning

### Fixed

- XyCraft Flares and the beams between them not showing with an Iris shaderpack on

## [26.1.2.9]

### Fixed

- Client crash when an Ars Magica Legacy altar uses up a dropped ingredient during spell crafting (FTBTesting/Testing-Issues#4627)
- Disconnect when opening a XyCraft tank valve with Sophisticated Inventory Interactions installed

## [26.1.2.8]

### Added

- Shift-clicking a Fabricator input tank empties it, so a wrong fluid no longer gets stuck (FTBTesting/Testing-Issues#4643)

## [26.1.2.7]

### Fixed

- Skill XP from dealing damage dropping the leftover fraction of every hit, so weak hits like shotgun pellets on the Ender Dragon gave no Gunnery XP at all

## [26.1.2.6]

### Removed

- The SG Economy coin display fix, which SG Economy now does itself and which would have crashed the game with the updated mod

## [26.1.2.5]

### Fixed

- Crash on launch with Thaumaturge 1.0.1
- Focus Power skills not boosting Thaumaturge spells, and casting Thaumaturge spells not giving Magic skill XP
- Magical Hephaestus tools not spawning aspect orbs when mining ores

## [26.1.2.4]

### Fixed

- Pipez item pipes not feeding the FTB Skyline

## [26.1.2.3]

### Fixed

- Lootr containers looking up and copying their loot data every tick, adding server lag and memory use
- Ars Magica Legacy resending the Life Ward state of every living entity to clients every tick

## [26.1.2.2]

### Fixed

- LTX Industries recipes in JEI no longer flicker to an empty fluid slot when the recipe accepts more than one fluid
- FTB Skyline bays now each feed their own kind of task in the selected quest, so items go in while an energy or fluid task is selected, and the other way round

## [26.1.2.1]

- Initial release for Evo Companion 2
