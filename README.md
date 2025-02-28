# Bomber Quest

## Description
Bomber Quest is a strategy action game where you place
bombs on the map and use random Power-Ups hidden 
throughout the map 
(increase blast radius/increase bomb number) 
to destroy all enemies and find exits hidden 
under walls in order to win.
Players need to use strategy and speed to win.

## Project Structure

**assets/** : Contains all the resources of the game.
- audio/ : Holds the audio files used in the game, such as background music and sound effects.
- skin/ : Contains the skin and font files for the game.
- texture/ : Contains images of various textures.

**core/** : Contains the core source code for the game.

**src/** : root directory of the source code.

- audio/ : A class that deals with game sound effects. 
- map/ : Contains classes related to the map, such as the game objects that should appear on the map (e.g. player/enemy/wall, etc.) and manages the game map.
- screen/ : A management class that contains the various screens in the game, such as the game screen/Menu screen /victory and game over screen / hub / power-up.
- texture/: A directory of code that manages the texture of the game. It contains code that defines the images and animations of various game objects, which refer to the image resources in the assets folder.
- BomberQuestGame.java: The main class of the game, responsible for starting and managing the game flow.

**desktop/** : Contains desktop platform-specific startup and configuration code.

- DesktopLauncher.java: The startup class for the desktop version that sets up the game window and launches the game.

## Class Hierarchy
### Texture
- **SpriteSheet** （enum class): Defines all Sprite tables used in the game, and sets a standard grid size for each Sprite table, so that different game graphics resources can be easily referenced and used in game development.
- **Animation**: Defines the player /enemy/bomb (before and after the explosion)
- **Textures**: Defines the texture area constants (static game objects) of various objects in the game such as flowers, exits, treasure chests, walls, bombs, etc., for unified management and easy reference of graphical resources in the game.
- **Drawable**(Interface): Defines the methods that objects that can be drawn on the screen must implement, including obtaining their current texture area and position in the game world grid, allowing these objects to change their appearance over time and be accurately positioned in the game.

### Audio
- **MusicTrack**(enum class): Manages the music track in the game, defining the file name, volume and whether the loop is played for each song, which is convenient for unified management and effective control of the loading and playing of the game music.

### Map
#### Abstract Class
- **GameObject**: A base class that represents all Drawable objects in the game, providing the basic properties and methods of the object, such as position, size, and the ability to implement a 'drawable' interface, allowing all game objects to have a uniform way of drawing and position handling.
#### Enum Class
- **Direction**: Defines the basic directions used in the game (up, down, left, right), each associated with a specific x and y offset, which is used to calculate movement and determine the position of objects such as bombs and enemies in the game world.
####  Dynamic Object Class
- **Player**: Class representing the player character, extending from GameObject class.
- **Enemy**: Class represents the enemy character in the game, inherits from the GameObject class, and also calls the direction enumeration class to define the direction in which the enemy moves randomly.
#### Static Object Class
- **Bomb**: Class represents the bombs that players can place in the game, inherits from the GameObject class, and manages the bomb's timing, explosion, and interaction with the game map, affecting the environment, enemies, and players.
- **Chest**: Class represents a static object in a game that has a collision box that the player cannot pass through and is usually used to store items or rewards in the game.
- **Flower**: Class represents a purely decorative object in the game, which is inherited from the GameObject but does not have a collision box, so it does not affect the movement of the player, mainly used to add to the beauty of the game scene. (This is used to map the entire game and beautify the roads).
- **Exit**: Class represents an exit in the game, can be locked or unlocked, inherits from the GameObject class, and provides functionality to manage the state and appearance of the exit.
- **Wall**(abstract class): Represents a wall in the game, which can be destructible or indestructible, extends from the GameObject class and provides methods to check whether the wall is destructible and its destructible state.
1. **DestructibleWall**: Represents the walls that can be destroyed in the game, allowing players to change the map layout and path by placing bombs, this class is inherited from the Wall class, which realizes the function of the wall disappearing after being destroyed, and supports checking the damage status of the wall.
2. **IndestructibleWall**: Represents an indestructible Wall in the game, extending from the Wall class and always returns to an indestructible state, used to create permanent obstacles that block the path of players and explosions.
- **PowerUp**: Represents power-ups in the game and implements a "Drawable" interface, usually hidden behind a destructible wall, giving the player different ability enhancements when collecting, such as increasing the explosion range or allowing more bombs to be placed at the same time.
- **PowerUpType**(enum class): Defines the various power-up types available in the game, each providing unique effects to enhance the player's abilities, such as increasing the explosive range of bombs (' BLAST_RADIUS ') or allowing the player to place more bombs at once (' CONCURRENT_BOMBS ').
#### GameMap:
Class represents the map in the game, which contains all game objects, such as walls, enemies, bombs, powerups, and players.

### Screen
- **CountdownTimer**: Manages the countdown timer in the game (a total of 300 seconds), decrementing the time every second, and triggering the end of the game when time runs out. This class allows you to pause and start timing.
- **Hud**: Class is the overhead display (HUD) in the game, which is used to display real-time game-related information on the screen, including countdown, power-up status and enemy status.
- **MenuScreen**: Represents the main menu of the game and is responsible for setting up the user interface, allowing the player to start a new game, continue a previous game, or exit the game.
- **VictoryAndGameOverScreen**: Class is used to display the screen at the end of the game, displaying different information depending on whether the player won or lost, and allowing the player to return to the main menu or restart the game.
- **GameScreen**: Class is responsible for rendering the gameplay interface and managing the game state. This class provides a complete game interface and functionality by combining multiple components, such as HUD, GameMap, and CountdownTimer.

### BomberQuestGame:
The core management class is responsible for the resource management of the game, scene switching, and the management and control of various screens.

## How to Play
1. Click the "Go To Game" button to start the game from Map 1.
2. To select a different map, click the "Load a new map file and start a new game" button, and then select the required level file.
3. Game operation instructions:
   Use the **up, down, left and right keys** of the keyboard to control the direction of the player's movement.
   Press the **space** bar to place the bomb.
   Press the **Esc** key to switch between the menu screen and the game screen, and pause the game. To Continue the game, click the "Continue the Game" button from the menu screen.
4. The main screen of the game is surrounded by indestructible walls to ensure that players and enemies do not run off the map.
5. Bomb explosion effect:
   The bomb will explode in a cross direction.
   If a **destructible wall** is blown up, an item with a **heart** or **fire** icon may appear (increasing the number of bombs that can be placed and the bomb blast radius, respectively). Players simply walk over to collect items.
6. Game home screen information:
   The following is displayed in the upper left corner of the screen:
   - 300 seconds countdown
   - Current enemy numbers
   - The number of enemies defeated
   - The radius of the bomb blast
   - The number of bombs that can be placed simultaneously
   - Whether the exit is revealed

## Game Mechanics
- Win the game condition: blow up all enemies and blast the exit hidden under the destructible wall, escape through the exit.
- Possibility of game failure:
1. The player encounters an enemy
2. Players place bombs to blow themselves up
3. Game countdown ends (300 seconds)
- Collect power-ups hidden beneath destructible walls
1. Hearts increase the number of bombs a player can place at the same time
2. The flame indicates the increased radius of the bomb after detonation