package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;

/**
 * Represents a destructible wall in the game.
 * These walls can be destroyed by bomb explosions to create new paths.
 */

public class DestructibleWall extends Wall {

    protected boolean destroyed;// Flag indicating whether the wall has been destroyed

    /**
     * Determines if this wall is destructible.
     * Always returns true for destructible walls.
     * @return true, as this wall can be destroyed.
     */
    public boolean isDestructible() {
        return true;
    }

    /**
     * Constructs a destructible wall at a specific (x, y) coordinate.
     * @param x X-coordinate of the wall.
     * @param y Y-coordinate of the wall.
     */
    public DestructibleWall(int x, int y ) {
        super(x, y);
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        if(destroyed){
            return null;// Wall disappears after being destroyed
        }
        return Textures.DESTRUCTIBLE_WALL;// Default texture for destructible walls
    }

    public void destroy(){
        destroyed = true;
    }//Destroys the wall, marking it as removed from the game.


    public boolean isDestroyed(){//check whether the destructible wall is destroyed or not
        return destroyed;
    }

}