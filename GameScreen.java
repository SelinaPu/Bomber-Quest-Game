package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.map.*;
import de.tum.cit.ase.bomberquest.texture.Drawable;

/**
 * The GameScreen class is responsible for rendering the gameplay screen.
 * It manages the game state, rendering objects, and handling input.
 */
public class GameScreen implements Screen {

    /**
     * The size of a grid cell in pixels.
     * This allows us to think of coordinates in terms of square grid tiles
     * (e.g. x=1, y=1 is the bottom left corner of the map)
     * rather than absolute pixel coordinates.
     */
    public static final int TILE_SIZE_PX = 16;

    /**
     * The scale of the game.
     * This is used to make everything in the game look bigger or smaller.
     */
    public static final int SCALE = 4;

    private final BomberQuestGame game; // Reference to the main game instance
    private SpriteBatch spriteBatch; // Sprite batch for rendering game objects
    private final GameMap map; // The game map containing all game elements
    private Hud hud; // Heads-up display (HUD) for game information
    private final OrthographicCamera mapCamera; // Camera for rendering the game world
    private CountdownTimer timer; // Countdown timer for game time limit
    private boolean isGameOver = false; // Flag to check if the game is over
    private Player player; // The player object
    private World world; // Physics simulation world


    /**
     * Constructor for GameScreen. Sets up the camera and font.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public GameScreen(BomberQuestGame game, GameMap map) {
        this.world = new World(new Vector2(0, 0), true);// Create a physics world with no gravity
        this.game = game;
        this.spriteBatch = game.getSpriteBatch();

        this.map = game.getMap();
        this.hud = new Hud(spriteBatch, game.getSkin().getFont("font"), timer, this.map);
        // Create and configure the camera for the game view
        this.mapCamera = new OrthographicCamera();
        this.mapCamera.setToOrtho(false);
        Vector2 entrance = map.getEntrance();
        this.player = map.getPlayer();

        timer = new CountdownTimer(300, game);/// Initialize countdown timer with 300 seconds
        timer.start(); // Start the countdown timer
    }

    /**
     * The render method is called every frame to render the game.
     *
     * @param deltaTime The time in seconds since the last render.
     */
    @Override
    public void render(float deltaTime) {

        // Render the HUD (game timer)
        spriteBatch.begin();
        drawHUD();
        spriteBatch.end();

        // If the timer runs out, trigger game over
        if (timer.isGameOver() && !isGameOver) {
            isGameOver = true;
            onGameOver();
        }
        // If the player presses the ESCAPE key, return to the menu
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.goToMenu();
        }

        // Clear the previous frame from the screen, or else the picture smears
        ScreenUtils.clear(Color.BLACK);

        // Cap frame time to 250ms to prevent spiral of death
        float frameTime = Math.min(deltaTime, 0.250f);

        // Update the map state
        map.tick(frameTime);

        // Update the camera
        updateCamera();

        // Handle player input
        player.handleInput();

        // Render the map on the screen
        renderMap();

        // Render the HUD on the screen
        hud.render(player.getX(), player.getY());

    }

    /**
     * Updates the camera to match the current state of the game.
     * Currently, this just centers the camera at the origin.
     */
    private void updateCamera() {
        Player player = map.getPlayer(); // get player instance
        float playerX = player.getX() * TILE_SIZE_PX * SCALE;
        float playerY = player.getY() * TILE_SIZE_PX * SCALE;
        // Keep the player centered on the screen
        mapCamera.position.set(playerX, playerY, 0);
        // Ensure the camera does not move outside the map boundaries
        float viewportHalfWidth = mapCamera.viewportWidth / 2;
        float viewportHalfHeight = mapCamera.viewportHeight / 2;

        mapCamera.setToOrtho(false);
        mapCamera.position.x = Math.max(viewportHalfWidth, Math.min(playerX, map.getWidth() * TILE_SIZE_PX * SCALE - viewportHalfWidth));
        mapCamera.position.y = Math.max(viewportHalfHeight, Math.min(playerY, map.getHeight() * TILE_SIZE_PX * SCALE - viewportHalfHeight));
        mapCamera.update(); // This is necessary to apply the changes
    }

    private void renderMap() {
        // This configures the spriteBatch to use the camera's perspective when rendering
        spriteBatch.setProjectionMatrix(mapCamera.combined);

        // Start drawing
        spriteBatch.begin();

        // Render everything in the map here, in order from lowest to highest (later things appear on top)
        // You may want to add a method to GameMap to return all the drawables in the correct order

        //flowers
        for (Flowers flowers : map.getFlowers()) {
            draw(spriteBatch, flowers);
        }

        //powerups
        for (PowerUp powerUp : map.getPowerUps()) {
            draw(spriteBatch, powerUp);
        }

        //exit
        draw(spriteBatch, map.getExit());

        //walls
        for (int y = 0; y < map.getWalls().length; y++) {
            for (int x = 0; x < map.getWalls()[y].length; x++) {
                Wall wall = map.getWallAt(x, y);
                if (wall != null) {
                    draw(spriteBatch, wall);
                }
            }
        }
        // enemies
        for (Enemy enemy : map.getEnemies()) {
            draw(spriteBatch, enemy);
        }
        //bombs
        for (Bomb bomb : map.getBombs()) {
            if (bomb.getExplosionTimer() < 0.8f) {
                draw2(spriteBatch, bomb);
            } else {
                draw(spriteBatch, bomb);
            }
        }

        draw(spriteBatch, map.getPlayer());
        // Finish drawing, i.e. send the drawn items to the graphics card
        spriteBatch.end();
    }

    /**
     * Draws this object on the screen.
     * The texture will be scaled by the game scale and the tile size.
     * This should only be called between spriteBatch.begin() and spriteBatch.end(), e.g. in the renderMap() method.
     *
     * @param spriteBatch The SpriteBatch to draw with.
     */
    private static void draw(SpriteBatch spriteBatch, Drawable drawable) {
        TextureRegion texture = drawable.getCurrentAppearance();
        // Drawable coordinates are in tiles, so we need to scale them to pixels
        float x = drawable.getX() * TILE_SIZE_PX * SCALE;
        float y = drawable.getY() * TILE_SIZE_PX * SCALE;
        // Additionally scale everything by the game scale
        float width = texture.getRegionWidth() * SCALE;
        float height = texture.getRegionHeight() * SCALE;

        if (drawable instanceof Player) {
            width = 8 * SCALE;
            height = 13 * SCALE;
        }
        spriteBatch.draw(texture, x, y, width, height);
    }
    /**
     * Draws the bomb explosion animation.
     * This method renders the explosion effect in all four cardinal directions (UP, DOWN, LEFT, RIGHT),
     * extending based on the bomb's blast radius.
     *
     * @param spriteBatch The sprite batch used for rendering.
     * @param bomb The bomb whose explosion needs to be drawn.
     */
    private static void draw2(SpriteBatch spriteBatch, Bomb bomb) {

        // Get the current appearance of the bomb (or explosion animation)
        TextureRegion texture = bomb.getCurrentAppearance();
        // Convert bomb's grid-based coordinates to pixel-based coordinates
        float x = bomb.getX() * TILE_SIZE_PX * SCALE;
        float y = bomb.getY() * TILE_SIZE_PX * SCALE;

        // Draw the center of the explosion at the bomb's position
        TextureRegion center = new TextureRegion(texture, 32, 32, 16, 16);
        spriteBatch.draw(center, x, y, 64, 64);

        // Render explosion in the UP direction
        int radius = bomb.getBlastRadius().get(Direction.UP);
        for (int i = 0; i < radius; i++) {
            TextureRegion top = new TextureRegion(texture, 32, 16, 16, 16);
            spriteBatch.draw(top, x, y + 64 * i + 64, 64, 64);
        }

        // Render explosion in the DOWN direction
        radius = bomb.getBlastRadius().get(Direction.DOWN);
        for (int i = 0; i < radius; i++) {
            TextureRegion down = new TextureRegion(texture, 32, 48, 16, 16);
            spriteBatch.draw(down, x, y - 64 * i - 64, 64, 64);
        }

        // Render explosion in the LEFT direction
        radius = bomb.getBlastRadius().get(Direction.LEFT);
        for (int i = 0; i < radius; i++) {
            TextureRegion left = new TextureRegion(texture, 16, 32, 16, 16);
            spriteBatch.draw(left, x- 64 * i - 64, y  , 64, 64);
        }

        // Render explosion in the RIGHT direction
        radius = bomb.getBlastRadius().get(Direction.RIGHT);
        for (int i = 0; i < radius; i++) {
            TextureRegion right = new TextureRegion(texture, 48, 32, 16, 16);
            spriteBatch.draw(right, x+ 64 * i + 64, y, 64, 64);
        }
    }

    /**
     * Draws the Heads-Up Display (HUD), such as the countdown timer.
     */
    private void drawHUD() {
        // Create a font for rendering text
        BitmapFont font = new BitmapFont();
    }

    /**
     * Handles game-over logic when the timer expires.
     */
    private void onGameOver() {
        System.out.println("Time out, game failed! ");
        game.goToVictoryAndGameOver(false);
    }

    /**
     * Called when the window is resized.
     * This is where the camera is updated to match the new window size.
     *
     * @param width  The new window width.
     * @param height The new window height.
     */
    @Override
    public void resize(int width, int height) {
        mapCamera.setToOrtho(false);// Ensure the camera is set to 2D mode
        mapCamera.viewportWidth = width / SCALE;// Adjust the viewport width
        mapCamera.viewportHeight = height / SCALE;// Adjust the viewport height
        mapCamera.update();// Apply the new camera settings
        hud.resize(width, height);// Resize the HUD accordingly
    }

    public CountdownTimer getTimer() {
        return timer;
    }

    // Unused methods from the Screen interface
    @Override
    public void pause() {
        timer.setPause(true);
    }

    @Override
    public void resume() {
        timer.setPause(false);
    }

    @Override
    public void show() {
        spriteBatch = new SpriteBatch();

        timer.setPause(false);
        BitmapFont font = new BitmapFont();

        //将计时器传递给hud
        hud = new Hud(spriteBatch, font, timer, map);
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        spriteBatch.dispose();
    }

}
