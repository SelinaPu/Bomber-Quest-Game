package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.Random;

/**
 * Represents an enemy character in the game.
 * The enemy has a hitbox for collisions and can interact with the player and the environment.
 * This class extends GameObject, inheriting its basic properties and functionalities.
 */
public class Enemy extends GameObject {

    // The physical body used for collision detection.
    private final Body hitbox;
    // Reference to the game map.
    private GameMap map;
    // Movement speed of the enemy.
    private float speed = 0.4f;
    // Status to check if the enemy is alive.
    private boolean alive = true;
    // For generating random movement.
    private Random random = new Random();
    // Current animation based on the enemy's movement.
    private Animation<TextureRegion> currentAnimation;
    // Timer for managing animation states.
    private float stateTime = 0;
    // Initial direction of the enemy.
    private Direction currentDirection = Direction.DOWN;
    // Current velocity of the enemy.
    private Vector2 velocity = new Vector2();

    /**
     * Constructor to create an enemy.
     * @param world The Box2D world where the enemy will exist.
     * @param x Initial x-coordinate of the enemy.
     * @param y Initial y-coordinate of the enemy.
     * @param map Reference to the game map for navigation.
     */
    public Enemy(World world, float x, float y, GameMap map) {
        super(x, y);
        this.map = map;
        this.hitbox = createHitbox(world, x, y);// Set up the physical body.
        randomVelocity();// Assign initial random velocity.
    }

    /**
     * Creates the physical body (hitbox) for the enemy.
     * @param world The Box2D world.
     * @param x Initial x-coordinate.
     * @param y Initial y-coordinate.
     * @return Body the created Box2D body.
     */
    private Body createHitbox(World world, float x, float y) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(x, y);
        bodyDef.type = BodyDef.BodyType.DynamicBody; // Allows the body to move and interact physically.
        Body body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(0.3f);
        body.createFixture(circle, 1.0f); // Density set to 1.0 for physical properties.
        circle.dispose();// Cleanup after creating the fixture.
        body.setUserData(this);
        return body;
    }

    /**
     * Updates the enemy's position and state.
     * @param deltaTime Time since the last frame.
     */
    public void update(float deltaTime) {
        if (!alive) return;// Do nothing if the enemy is dead.

        stateTime += deltaTime; // Update the state time for animations.

        // Calculate the next position based on current velocity.
        float targetX = getX() + velocity.x;
        float targetY = getY() + velocity.y;

        // enemy randomly moving
        if(map.isPassableEnemy(this, targetX, targetY)) {

            hitbox.setLinearVelocity(velocity);
        }else {

            hitbox.setLinearVelocity(0, 0);
            randomVelocity();
        }
    }


    /**
     * Reverses the current velocity and changes direction randomly.
     */
    // Prevent two enemies from sticking together when they collide diagonally.
    public void reverseVelocity(Enemy others) {
        Vector2 directionToOther = new Vector2(others.getX() - this.getX(), others.getY() - this.getY());
        float angle = directionToOther.angleDeg();//Calculate the angle of the vector directionToOther from the current enemy to another enemy.

        // change direction
        if (angle >= 45 && angle < 135) { //another enemy is above the current enemy.
            this.velocity.set(0, -speed);
            currentAnimation = Animations.ENEMY_WALK_DOWN;
        } else if (angle >= 225 && angle < 315) { //another enemy is below the current enemy

            this.velocity.set(0, speed);
            currentAnimation = Animations.ENEMY_WALK_UP;
        } else if (angle >= 135 && angle < 225) { //another enemy is to the left of current enemy

            this.velocity.set(speed, 0);
            currentAnimation = Animations.ENEMY_WALK_RIGHT;
        } else {//another enemy is to the right of current enemy
            this.velocity.set(-speed, 0);
            currentAnimation = Animations.ENEMY_WALK_LEFT;
        }
    }

    /**
     * Generates a random direction and updates the enemy's velocity accordingly.
     * This method helps in simulating unpredictable enemy movements.
     */
    public void randomVelocity(){
        // Randomly choose a direction.
        int moveDirection = random.nextInt(4);//Randomly generate a random integer between 0 and 3
        updateDirection(moveDirection); // Update the current direction based on 4 random choice.
        switch (currentDirection) {
            case DOWN:
                velocity.set(0, -speed);// Set velocity to move down.
                currentAnimation = Animations.ENEMY_WALK_DOWN;// Set animation to walking down.
                break;
            case UP:
                velocity.set(0, speed); // Set velocity to move up.
                currentAnimation = Animations.ENEMY_WALK_UP;// Set animation to walking up.
                break;
            case LEFT:
                velocity.set(-speed, 0);// Set velocity to move left.
                currentAnimation = Animations.ENEMY_WALK_LEFT;// Set animation to walking left.
                break;
            case RIGHT:
                velocity.set(speed, 0);// Set velocity to move right.
                currentAnimation = Animations.ENEMY_WALK_RIGHT;// Set animation to walking right.
                break;
        }
    }

    /**
     * Updates the direction of the enemy based on the provided direction index.
     * Sets the corresponding velocity and animation based on the new direction.
     */
    private void updateDirection(int moveDirection) {
        switch (moveDirection) {
            case 0:// DOWN
                currentDirection = Direction.DOWN;
                break;
            case 1: // RIGHT
                currentDirection = Direction.RIGHT;
                break;
            case 2:// UP
                currentDirection = Direction.UP;
                break;
            case 3:// LEFT
                currentDirection = Direction.LEFT;
                break;
        }
    }

    /**
     * Retrieves the current appearance of the enemy based on the animation's state time.
     * This method is useful for rendering the enemy in the game with the correct frame of the animation.
     * @return TextureRegion representing the current frame of the animation to be rendered.
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        return currentAnimation.getKeyFrame(stateTime, true);
    }

    /**
     * Retrieves the x-coordinate of the enemy's hitbox.
     * This method is essential for position-based logic in the game, such as collision detection.
     * @return float representing the x-coordinate of the hitbox.
     */
    @Override
    public float getX() {
        return  hitbox.getPosition().x;
    }

    /**
     * Retrieves the y-coordinate of the enemy's hitbox.
     * This method is used in various game logic, especially in position and movement calculations.
     * @return float representing the y-coordinate of the hitbox.
     */
    @Override
    public float getY() {
        return hitbox.getPosition().y;
    }
}
