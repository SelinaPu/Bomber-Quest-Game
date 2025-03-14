package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.texture.Textures;

/**
 * Represents an indestructible wall within the game environment.
 * This class extends the abstract Wall class and provides implementations for an indestructible wall type.
 */
public class IndestructibleWall extends Wall {

    public boolean isDestructible() {
        return false;
    }

    public IndestructibleWall(int x, int y ) {
        super(x, y);
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        return Textures.INDESTRUCTIBLE_WALL;
    }

    //destructible walls are destructed
    public void destroy(){

    }

    //check whether the destructible wall is destroyed or not
    public boolean isDestroyed(){
        return false;
    }

}
