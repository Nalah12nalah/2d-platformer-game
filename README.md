## Description

A 2D platformer where a domestic cat navigates the streets of Al-Balad,
Jeddah, overcoming obstacles and enemies to find its way home.



## Project Structure

- src/game/characters/ → Main characters (Cat, Thief Cat, Pigeons)

- src/game/levels/     → Level1…Level5

- src/game/pickups/    → Collectibles (Food, Nubella, Rug)

- src/game/projectiles/→ Rock, Nubella

- src/game/props/      → Balila carts, Paint buckets

- src/game/sounds/     → Background music, SFX

- src/game/gui/        → Main Menu, Pause Menu

- src/game/obstacles/  → Moving platforms, Boxes

- src/game/winobjects/ → Fish Bucket (level completion)

- src/game/utils/      → Helpers & interfaces

- data/                → Images, sounds, fonts


## APIs Used
- **city.cs.engine**: Physics engine classes for bodies, fixtures, step listeners, and collision handling.  
- **org.jbox2d.common**: Vector math and shapes via JBox2D integration.  
- **javax.swing**: GUI components, panels, timers, and event handling (MainMenu, PauseMenu, LevelTransitionPanel).  
- **java.awt**: Rendering, fonts, custom drawing in GameView and UI overlays.  
- **javax.sound.sampled**: Loading, playing, and looping audio clips for background music and sound effects.  
- **java.io**: File input/output for saving and loading game state.  
- **java.util**: Collections (List, ArrayList), Scanner for reading save files, and Timer for animation and periodic checks.


## Installation & Setup

1. Clone this repository:

2. git clone https://github.com/Nalah12nalah/2d-platformer-game.git

3. In IntelliJ IDEA, add the City University Physics Engine as a Global Library.

4. Run game.Game (main) to start.
   

## Usage

**Arrow keys** to move/jump

**Spacebar** to throw rocks

**E** to eat collected food

**P** to pause

**Mouse Click** to destroy destructables 

**Mouse Hold** to charge Nubella weapon


## License & Disclaimer

Academic Project Part of IN1007 Java Programming, City, University of London.
Uses the City University Physics Engine (university-provided). Not for commercial distribution.