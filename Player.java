package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.screen.CountdownTimer;
import de.tum.cit.ase.bomberquest.screen.VictoryAndGameOverScreen;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

/**
 * Represents the player character in the game.
 * The player has a hitbox, so it can collide with other objects in the game.
 * This class extends GameObject, inheriting its basic properties and functionalities.
 */
public class Player  extends  GameObject {

    /** Total time elapsed since the game started. We use this for calculating the player movement and animating it. */
    private float elapsedTime;
    /** The Box2D hitbox of the player, used for position and collision detection. */
    private Body hitbox;
    /** Reference to the game map, contains information about the environment. */
    private final GameMap map;
    /** Maximum number of bombs the player can place at once. */
    private int bombLimit = 1;
    /** Number of bombs currently placed by the player. */
    private int bombsPlaced = 0;
    /** Boolean flag to check if the player is still alive. */
    private boolean alive = true;
//    private Animation<TextureRegion> currentAppearance = Animations.CHARACTER_WALK_DOWN;


    // Reset player velocities.
    float yVelocity;
    float xVelocity;
    // The physics world where the player exists.
    World world;

    /**
     * Constructor for creating a Player object.
     * @param world The Box2D world where the player will interact.
     * @param x The initial x position of the player.
     * @param y The initial y position of the player.
     * @param map The game map which contains the game environment.
     */
    public Player(World world, float x, float y, GameMap map) {
        super(x, y);  // Calls the superclass constructor to set the x, y position.
        Vector2 entrance = map.getEntrance(); // Retrieves the entrance location from the map.
        this.world = world; // Sets the world where the player interacts.
        this.hitbox = createHitbox(world, x, y); // Creates a hitbox for the player for collisions.
        this.map = map;// Sets the game map for the player.
    }

    /**
     * Creates a Box2D body for the player.
     * This is what the physics engine uses to move the player around and detect collisions with other bodies.
     * @param world The Box2D world to add the body to.
     * @param startX The initial X position.
     * @param startY The initial Y position.
     * @return The created body.
     */
    private Body createHitbox(World world, float startX, float startY) {
        // BodyDef is like a blueprint for the movement properties of the body.
        BodyDef bodyDef = new BodyDef();
        // Dynamic bodies are affected by forces and collisions.
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // Set the initial position of the body.
        bodyDef.position.set(startX, startY);
        // Create the body in the world using the body definition.
        Body body = world.createBody(bodyDef);
        // Now we need to give the body a shape so the physics engine knows how to collide with it.
        // We'll use a circle shape for the player.
        CircleShape circle = new CircleShape();
        circle.setRadius(0.3f);
        //Add a fixture to the body of the object using the circle created earlier and density 1.0.
        // Fixtures are used to define the physical properties of an object, such as shape, density, coefficient of friction, etc.
        body.createFixture(circle, 1.0f);

        body.setUserData(this);// Links the player object to the body for retrieval in collisions.
        return body; // Returns the created body.
    }

    /**
     * Retrieves the current position of the player's hitbox.
     * This position is used for various game logic operations, such as collision detection.
     * @return Vector2 containing the x and y coordinates of the hitbox.
     */
    public Vector2 getPosition() {
        return hitbox.getPosition();//The hitbox belongs to the Body class, and the Body class provides getPosition().

    }
    /**
     * Move the player around in a circle by updating the linear velocity of its hitbox every frame.
     * This doesn't actually move the player, but it tells the physics engine how the player should move next frame.
     * @param frameTime the time since the last frame.
     */
    public void tick(float frameTime) {//Updates the player's position and animation based on the elapsed time.
        this.elapsedTime += frameTime;// Update total elapsed time.
        float inputSpeed = 2.0f;// Player movement speed.

        // Reset player velocities.
         xVelocity = 0.0f;
         yVelocity = 0.0f;

        // Check input and adjust velocities.
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            xVelocity = -inputSpeed;
//            setCurrentAppearance(Animations.CHARACTER_WALK_LEFT);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            xVelocity = inputSpeed;
//            setCurrentAppearance(Animations.CHARACTER_WALK_RIGHT);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            yVelocity = inputSpeed;
//            setCurrentAppearance(Animations.CHARACTER_WALK_UP);
        } else if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            yVelocity = -inputSpeed;
//            setCurrentAppearance(Animations.CHARACTER_WALK_DOWN);
        }

       // Only update the speed when at least one directional key is pressed
        if (!Gdx.input.isKeyPressed(Input.Keys.LEFT) && !Gdx.input.isKeyPressed(Input.Keys.RIGHT) &&
                !Gdx.input.isKeyPressed(Input.Keys.UP) && !Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            xVelocity = 0;
            yVelocity = 0;
        }

        // Calculate target position for the player based on current position and velocity.
        frameTime = 0.2f;// Set the frame time interval.
        float targetX = this.getX() + xVelocity * frameTime;// Target x-coordinate after moving.
        float targetY = this.getY() + yVelocity * frameTime;// Target y-coordinate after moving.

        // Check if the target position is passable on the map.
        if (map.isPassablePlayer(targetX, targetY)) {
            // If the position is passable, set the hitbox's linear velocity to continue movement.
            hitbox.setLinearVelocity(xVelocity, yVelocity);
        } else {
            // If the position is not passable, stop the player's movement by setting velocity to zero.
            this.hitbox.setLinearVelocity(0, 0);
        }
    }

    // Determines animation frame based on velocity comparisons, using absolute values for direction.
    @Override
    public TextureRegion getCurrentAppearance() {
        TextureRegion textureRegion = null;

        if (Math.abs(yVelocity) > Math.abs(xVelocity)) {
            if (yVelocity > 0) {
                textureRegion = Animations.CHARACTER_WALK_UP.getKeyFrame(this.elapsedTime, true);// Walking up animation.
            } else {
                textureRegion = Animations.CHARACTER_WALK_DOWN.getKeyFrame(this.elapsedTime, true);// Walking down animation.
            }
        } else {
            if (xVelocity > 0) {
                textureRegion = Animations.CHARACTER_WALK_RIGHT.getKeyFrame(this.elapsedTime, true);// Walking right animation.
            } else if (xVelocity < 0) {
                textureRegion = Animations.CHARACTER_WALK_LEFT.getKeyFrame(this.elapsedTime, true);// Walking left animation.
            } else {
                // Standing still animation when no movement detected.
                textureRegion = Animations.CHARACTER_WALK_DOWN.getKeyFrame(this.elapsedTime, true);
            }
        }

        TextureRegion changed = new TextureRegion();
        changed.setRegion(textureRegion, 0, 6, 16, 20);// Sets the texture region to be drawn.

        return  changed;// Returns the final selected frame for rendering.
//        return currentAppearance.getKeyFrame(elapsedTime, true);
    }

//    public void setCurrentAppearance(Animation<TextureRegion> currentAppearance) {
//        this.currentAppearance = currentAppearance;
//    }

    @Override
    public float getX() {
        // The x-coordinate of the player is the x-coordinate of the hitbox (this can change every frame).
        return hitbox.getPosition().x;
    }

    @Override
    public float getY() {
        // The y-coordinate of the player is the y-coordinate of the hitbox (this can change every frame).
        return hitbox.getPosition().y;
    }

    @Override
    public float getWidth() {
        return 0.5f;
    }//set the width of player as 0.5f.

    @Override
    public float getHeight() {
        return 0.5f;
    }//set the height of player as 0.5f.

    /**
     * Sets the player's alive status to false, indicating they have been killed in the game.
     */
    public void kill(){
        if(alive){
            alive = false;
        }
    }


    //Bomb
    public int getBombLimit() {
        return bombLimit;
    }
    public int getBlastRadius() {
        return blastRadius;
    }


    public void placeBomb() {
        try {// Calculate the integer position where the bomb will be placed based on the player's current position.
            int bombX = (int)  Math.floor(hitbox.getPosition().x);
            int bombY = (int) Math.floor (hitbox.getPosition().y);

            // Print statement for debugging, indicating where the bomb is being attempted to place.
            System.out.println("Attempting to place bomb at: " + bombX + ", " + bombY + ", bombsPlaced is " + bombsPlaced);

            // Check if the bomb can be placed at the calculated position and if the number of placed bombs is within the limit.
            if (bombsPlaced < bombLimit && !map.hasBombAt(bombX, bombY)) {

            // Create a new bomb object at the specified position.
                Bomb bomb = new Bomb(
                        bombX,// X coordinate of the bomb placement
                        bombY, // Y coordinate of the bomb placement
                        Textures.BOMB,// Texture for the bomb
                        map, // The map where the bomb is placed
                        blastRadius// Blast radius of the bomb
                );
                // Add the bomb to the map.
                map.addBomb(bomb);
                // Increment the counter for the number of bombs placed.
                bombsPlaced++;
                // Print statement for debugging, confirming the bomb placement.
                System.out.println("Bomb placed successfully!");
                MusicTrack.DROP.play();// Play the bomb drop sound effect.
            }
        }catch (Exception e) {
            // Print stack trace if there is an exception during bomb placement.
            e.printStackTrace();
        }
    }
    /**
     * Method called when a bomb placed by this player explodes.
     * Reduces the count of placed bombs.
     */
    public void bombExploded(){
        if(bombsPlaced > 0){
            bombsPlaced--;// Decrease the count of bombs placed by the player
        }
        System.out.println("Attempting to remove bomb, bombsPlaced is " + bombsPlaced);// Debugging output
    }

    /**
     * Handles input from the player, specifically checking for the press of the space key to place a bomb.
     */
    public void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            System.out.println("SPACE key pressed!");// Debugging output
            placeBomb(); // Calls the placeBomb method to attempt to place a bomb
        }
    }


    // Power-up functionalities for the player.
    private int blastRadius = 1;// Initial blast radius for bombs placed by the player.

    /**
     * Increases the blast radius of bombs placed by the player.
     * Ensures the blast radius does not exceed a maximum value.
     */
    public void increaseBlastRadius() {
        if (blastRadius < 8)// Check if the current blast radius is less than the maximum allowed value.
            blastRadius++;// Increment the blast radius.
    }

    /**
     * Increases the maximum number of bombs the player can place concurrently.
     * Ensures the bomb limit does not exceed a maximum value.
     */
    public void increaseConcurrentBombs() {
        if (bombLimit < 8)// Check if the current bomb limit is less than the maximum allowed value.
            bombLimit++;// Increment the bomb limit.
    }
    /**
     * Increases the remaining game time when the player collects a Time Extension Power-Up.
     * @param seconds The number of seconds to add to the game timer.
     */
//    public void applyTimeExtension(int seconds, CountdownTimer timer) {
//        if (timer != null) {
//            timer.addTime(seconds);
//            System.out.println("Time extended by " + seconds + " seconds!");
//        }
//    }


}
