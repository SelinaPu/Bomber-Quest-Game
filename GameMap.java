package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.map.PowerUp;
import de.tum.cit.ase.bomberquest.map.PowerUpType;
import de.tum.cit.ase.bomberquest.screen.CountdownTimer;
import de.tum.cit.ase.bomberquest.screen.Hud;
import de.tum.cit.ase.bomberquest.texture.Drawable;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 * Represents the game map.
 * The map holds all objects, including walls, enemies, bombs, power-ups, and the player.
 * It manages physics, collision detection, and game logic updates.
 */
public class GameMap {

    // A static block is executed once when the class is referenced for the first time.
    static {
        // Initialize the Box2D physics engine.
        com.badlogic.gdx.physics.box2d.Box2D.init();
    }

    // Box2D physics simulation parameters (you can experiment with these if you want, but they work well as they are)
    /**
     * The time step for the physics simulation.
     * This is the amount of time that the physics simulation advances by in each frame.
     * It is set to 1/refreshRate, where refreshRate is the refresh rate of the monitor, e.g., 1/60 for 60 Hz.
     */
    private static final float TIME_STEP = 1f / Gdx.graphics.getDisplayMode().refreshRate;
    /** The number of velocity iterations for the physics simulation. */
    private static final int VELOCITY_ITERATIONS = 6;
    /** The number of position iterations for the physics simulation. */
    private static final int POSITION_ITERATIONS = 2;
    /**
     * The accumulated time since the last physics step.
     * We use this to keep the physics simulation at a constant rate even if the frame rate is variable.
     */
    private float physicsTime = 0;

    /** The game, in case the map needs to access it. */
    private final BomberQuestGame game;
    /** The Box2D world for physics simulation. */
    private final World world;


    private Player player;

    private final Flowers[][] flowers;
    private final Wall[][] walls;

    private List<Enemy> enemies = new ArrayList<>();
    private List<Bomb> bombs = new ArrayList<>();
    private List<PowerUp> powerUps = new ArrayList<>();

    private Vector2 entrance;
    private Exit exit;
    private boolean exitRevealed = false;
    private int totalEnemies;

    public GameMap(BomberQuestGame game, String mapFilePath) throws IOException {
        this.game = game;
        this.world = new World( new Vector2(0,0), true);//initialize physical world

        System.out.println("GameMap: " + mapFilePath);

        this.exitRevealed = false;

        // Create flowers
        this.flowers = new Flowers[Gdx.graphics.getWidth()][Gdx.graphics.getHeight()];
        for (int i = 0; i < flowers.length; i++) {
            for (int j = 0; j < flowers[i].length; j++) {
                this.flowers[i][j] = new Flowers(i, j);
            }
        }
        //Load the map configuration file
        Properties properties = new Properties();
        properties.load(Gdx.files.internal(mapFilePath).reader());

        // Determine the maximum X and Y dimensions of the map
        int maxX = 0, maxY = 0;

        //Explain each rows in the map file
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith("#")) continue; //skip comment lines

            String[] coordinates = key.split(",");

            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);

            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
        }
        //create walls，
        this.walls = new Wall[maxY + 1][maxX + 1];

        //Parse map file to initialize game objects
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith("#")) continue;
            String[] coordinates = key.split(",");
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            int type = Integer.parseInt(properties.getProperty(key));
            switch (type) {
                case 0: // indestructible walls
                    walls[y][x] = new IndestructibleWall(x, y);
                    break;
                case 1: // destructible walls
                    walls[y][x] = new DestructibleWall(x, y);
                    break;
                case 2: // entrance(player's origin point)
                    this.entrance = new Vector2(x, y);
                    this.player = new Player(this.world, entrance.x, entrance.y, this);
                    break;
                case 3: // Enemy
                    Enemy enemy = new Enemy(world,x,y,this);
                    enemies.add(enemy);
                    break;
                case 4: // exit(hidden behind destructible wall)
                    this.exit = new Exit(x, y);
                    walls[y][x] = new DestructibleWall(x, y);
                    break;
                case 5: // Power-up: Increases bomb count
                    powerUps.add(new PowerUp(x, y, PowerUpType.CONCURRENT_BOMBS));
                    walls[y][x] = new DestructibleWall(x, y);
                    break;
                case 6: // Power-up: Increases blast radius
                    powerUps.add(new PowerUp(x, y, PowerUpType.BLAST_RADIUS));
                    walls[y][x] = new DestructibleWall(x, y);
                    break;
//                case 7: //Power-up: Increase countdown time
//                    powerUps.add(new PowerUp(x,y,PowerUpType.TIME_EXTENSION));
//                    walls[y][x] = new DestructibleWall(x, y);
//                    break;
            }
        }

        // Create a player with initial position (1, 3)
        this.player = new Player(this.world, entrance.x, entrance.y, this);//入口位置

        // If no exit exists, place one randomly behind a destructible wall
        if (this.exit == null) {
            System.out.println("No Exit");
            addRandomExit();
        }

        totalEnemies = enemies.size();
    }

    public int getTotalEnemies() {
        return totalEnemies;
    }

    /**
     * Adds a random exit location if no exit was defined in the map file.
     */
    private void addRandomExit(){
        List<Vector2> destructibleWalls = new ArrayList<>();
        for (int y = 0; y < walls.length; y++) {
            for (int x = 0; x < walls[y].length; x++) {
                Wall wall = walls[y][x];
                if (wall != null && wall.isDestructible()) {
                    destructibleWalls.add(new Vector2(x, y));//Add destructible walls to the Vector2 destructibleWalls list
                }
            }
        }
        if (!destructibleWalls.isEmpty()) {
            Vector2 randomWall = destructibleWalls.get((int) (Math.random() * destructibleWalls.size()));
            this.exit = new Exit((int)randomWall.x, (int)randomWall.y);
        }
    }

    public boolean isReveal(Drawable d){
        return walls[(int)d.getY()][(int)d.getX()] == null;
    }

    /**
     * Updates the game state (called every frame).
     * Handles bomb updates, enemy movement, and collision detection.
     *
     * @param frameTime Time passed since last update.
     */
    public void tick(float frameTime) {
        this.player.tick(frameTime);
        doPhysicsStep(frameTime);
        updateBombs(frameTime);
        updateEnemies(frameTime);

        // Handle power-up collection
        for(PowerUp powerUp : new ArrayList<>(powerUps)){
            if(isReveal(powerUp) && isCollision(player, powerUp)){
                powerUp.applyEffect(player);
                powerUps.remove(powerUp);
            }
        }

        // Check for player-enemy collisions (Game Over scenario)
        for(Enemy enemy : enemies){
            if(isCollision(player, enemy)){
                getGame().setDeathReason("Player killed by enemy."); // Set the death reason
                getPlayer().kill(); // Player dies
                getGame().goToVictoryAndGameOver(false); // Go to Game Over screen
                return;
            }

        // Check for enemy-enemy collisions
        for(Enemy enemy2 : enemies){
                if(enemy != enemy2 && isCollision(enemy, enemy2)){//make sure there are two different enemies
                    enemy.reverseVelocity(enemy2);//make the first enemy turn around
                    enemy2.reverseVelocity(enemy);//the second one also turns around
                    break;
                }
            }
        }

        // Check if the player reaches the exit
        if(exit.isUnlocked() && isCollision(player, exit)){
            getGame().goToVictoryAndGameOver(true);
            return;
        }
    }

    /**
     * Performs physics updates to maintain consistent game behavior.
     * @param frameTime Time passed since the last frame.
     */
    private void doPhysicsStep(float frameTime) {
        this.physicsTime += frameTime;
        while (this.physicsTime >= TIME_STEP) {
            this.world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            this.physicsTime -= TIME_STEP;
        }
    }

    //Reveals the exit when a player reaches the corresponding position.
    public void revealExitIfNecessary(int x, int y){
        if(!exitRevealed && exit.getX() == x && exit.getY() ==y){
            exitRevealed = true;
        }
    }

    /**
     * Checks if a tile at a given position is passable (walkable).
     * A tile is passable if it is within the map's boundaries and is either:
     * - Empty (no walls)
     * - A destructible wall that has been destroyed.
     * @param x The x-coordinate of the tile.
     * @param y The y-coordinate of the tile.
     * @return True if the tile is passable, false otherwise.
     */
    public boolean isPassable(int x, int y){
        // Ensure the position is within the map bounds
        if (x < 0 || y < 0 || y >= walls.length || x >= walls[0].length) {
            return false;
        }
        Wall wall = walls[y][x];
        if (wall == null) {
            return true;
        }
        return wall.isDestructible() && wall.isDestroyed(); // Passable if a destructible wall has been destroyed
    }

    /**
     * Checks if two drawable objects collide based on their positions and sizes.
     *
     * @param a The first drawable object.
     * @param b The second drawable object.
     * @return True if the objects collide, false otherwise.
     */
    public boolean isCollision(Drawable a, Drawable b){
        float x1 = a.getX();
        float y1 = a.getY();
        float w1 = a.getWidth();
        float h1 = a.getHeight() ;

        float x2 = b.getX();
        float y2 = b.getY();
        float w2 = b.getWidth();
        float h2 = b.getHeight();

        // Create bounding boxes for both objects
        Rectangle r1 = new Rectangle((int)(x1*64), (int)(y1*64)-(int)   64, (int)(w1 * 64),(int)(h1 * 64));
        Rectangle r2 = new Rectangle((int)(x2*64), (int)(y2*64)-(int)   64, (int)(w2 * 64),(int)(h2 * 64));

        return r1.intersects(r2);// Check if the rectangles overlap
    }

    //Checks if the player can move to a given position.
    // The player cannot pass through walls that are not destroyed.
    public boolean isPassablePlayer(float targetX, float targetY){
        for(Wall[] row : walls){
            for(Wall wall : row){
                if(wall != null && !wall.isDestroyed()){// If the wall exists and is not destroyed
                    Vector2 p1 = new Vector2(targetX, targetY);
                    Vector2 p2 = wall.getPosition();
                    // Create collision rectangles
                    Rectangle r1 = new Rectangle((int)(p1.x*64), (int)(p1.y*64 ), (int)(player.getWidth()*64),(int)(player.getHeight()*64));
                    Rectangle r2 = new Rectangle((int)(p2.x*64), (int)(p2.y*64) , 64,64);

                    if(r1.intersects(r2)){
                        return false;// Player is blocked
                    }
                }
            }
        }

        return true;// Player can move
    }

    //Checks if an enemy can move to a given position.
    //  Enemies cannot pass through walls that are not destroyed.
    public boolean isPassableEnemy(Enemy enemy, float targetX, float targetY){
        for(Wall[] row : walls){
            for(Wall wall : row){
                if(wall != null && !wall.isDestroyed()){
                    Vector2 p1 = new Vector2(targetX, targetY);
                    Vector2 p2 = wall.getPosition();

                    Rectangle r1 = new Rectangle((int)(p1.x*64), (int)(p1.y*64 ), (int)(enemy.getWidth()*64),(int)(enemy.getHeight()*64));
                    Rectangle r2 = new Rectangle((int)(p2.x*64), (int)(p2.y*64) , 64,64);

                    if(r1.intersects(r2)){
                        return false;
                    }
                }
            }
        }

        return true;
    }


    /** Returns the player on the map. */
    public Player getPlayer() {
        return this.player;
    }


    /** Returns the flowers on the map. */
    public List<Flowers> getFlowers() {
        return Arrays.stream(flowers).flatMap(Arrays::stream).toList();
    }

    public List<PowerUp> getPowerUps() {
        return new ArrayList<>(powerUps);
    }

    public Wall[][] getWalls() {
        return walls;
    }
    public Wall getWallAt(int x, int y) {
        if (x < 0 || y < 0 || y >= walls.length || x >= walls[y].length) {
            return null;
        }
        return walls[y][x];
    }

    public Wall getWallContains(int x, int y) {
        for(int i=0;i<walls.length;i++){
            for(int j=0;j<walls[i].length;j++){
                Wall wall = walls[i][j];
                if(i == 9 && j== 0){
                }
                if(wall != null && !wall.isDestroyed() && contains(wall, x, y)){
                    return wall;
                }
            }
        }
        return null;
    }

    public boolean contains(Drawable wall, float x, float y){
        return x >= wall.getX()*64 && x < wall.getX()*64 + wall.getWidth()*64 && y >= wall.getY()*64 && y < wall.getY()*64 + wall.getHeight()*64;
    }


    public void destroyWall(Wall wall) {
        System.out.println("destroyWall: " + wall.getPosition());

        for(int i=0;i<walls.length;i++){
            for(int j=0;j<walls[i].length;j++){
                if(walls[i][j] == wall){
                    walls[i][j].destroy();
                    walls[i][j] = null;
                }
            }
        }
    }

    public int getWidth() {
        if (walls == null || walls.length == 0) return 0;
        return walls[0].length;
    }
    public int getHeight() {
        if (walls == null) return 0;
        return walls.length;
    }

    public Vector2 getEntrance() {
        return entrance;
    }

    public Exit getExit() {
        return exit;
    }

    public boolean isExitRevealed() {
        return exitRevealed;
    }

    public World getWorld() {
        return world;
    }

    public BomberQuestGame getGame() {
        return game;
    }

    public float getPhysicsTime() {
        return physicsTime;
    }

    public List<Enemy> getEnemies() {
        return enemies;
    }

    public List<Bomb> getBombs() {
        return bombs;
    }

    //Checks if there is an enemy at a specific position.
    public Enemy getEnemyAt2(int x, int y) {
        for (Enemy enemy : enemies) {
            if (isCollision(enemy, new Flowers(x, y))) {
                return enemy;
            }
        }
        return null;
    }


    public void killEnemy(Enemy enemy) {
        enemies.remove(enemy);

        if(enemies.size() == 0){
            exit.setUnlocked(true);
        }
    }

    public void updateEnemies(float delta) {
        for (Enemy enemy : enemies) {
            enemy.update(delta);
        }
    }

    /**
     * Updates all active bombs.
     * Removes exploded bombs and resets the player's bomb placement limit.
     */

    public void addBomb(Bomb bomb){
        bombs.add(bomb);
        //setBombAt((int)bomb.getX(), (int)bomb.getY());
        System.out.println("Bomb added to map at: " + bomb.getX() + ", " + bomb.getY());
    }

    public void updateBombs(float delta) {
        Iterator<Bomb> iterator = bombs.iterator();
        while (iterator.hasNext()) {
            Bomb bomb = iterator.next();
            bomb.update(delta);

            if (bomb.isExploded()) {
                iterator.remove();
                getPlayer().bombExploded(); // Notify the player that they can place a new bomb
            }
        }
    }

    public boolean hasBombAt(int x, int y) {
        for(Bomb bomb: new ArrayList<>(bombs)){
            if(bomb.getX() == x && bomb.getY() == y){
                return true;
            }
        }

        return false;
    }



}
