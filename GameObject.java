package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

/**
 * Abstract class GameObject, representing any drawable object in the game.
 * This class provides the basic properties and methods that all game objects inherit.
 */

public abstract class GameObject implements Drawable {

    // X and Y coordinates representing the object's position in the game world.
    private final float x;
    private final float y;

    /**
     * Constructor for GameObject. Initializes the object with its position.
     * @param x The x-coordinate of the object.
     * @param y The y-coordinate of the object.
     */

    public GameObject(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Returns the position of the game object as a Vector2 containing x and y coordinates.
     * @return Vector2 representing the position of the object.
     */
    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    /**
     * Returns the width of the game object. Implemented by subclasses to provide specific dimensions.
     * @return float representing the width of the object.
     */
    @Override
    public float getWidth() {
        return 1;
    }

    /**
     * Returns the height of the game object. Implemented by subclasses to provide specific dimensions.
     * @return float representing the height of the object.
     */
    @Override
    public float getHeight() {
        return 1;
    }

    /**
     * Returns the x-coordinate of this game object.
     * @return float representing the x-coordinate.
     */
    @Override
    public float getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of this game object.
     * @return float representing the y-coordinate.
     */
    @Override
    public float getY() {
        return y;
    }
}
