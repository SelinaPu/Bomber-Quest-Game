package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;
/**
 * Represents the exit in the game, which can be locked or unlocked.
 * This class extends GameObject and provides functionalities to manage the exit's state and appearance.
 */

public class Exit extends GameObject {

    private boolean unlocked;// Indicates if the exit is unlocked

    /**
     * Constructs an Exit at the specified coordinates.
     * This constructor initializes the exit's position within the game world.
     *
     * @param x the x-coordinate of the exit's position.
     * @param y the y-coordinate of the exit's position.
     */
    public Exit(int x, int y) {
        super(x, y);
    }// Calls the constructor of the superclass, GameObject, to set position.

    /**
     * Sets the unlocked state of the exit.
     * This method allows the game logic to unlock or lock the exit based on game progress.
     *
     * @param unlocked true to unlock the exit, false to lock it.
     */
    public void setUnlocked(boolean unlocked) {
        this.unlocked = unlocked;
    }

    /**
     * Returns the current appearance of the exit based on its lock state.
     * Overrides the getCurrentAppearance method from the GameObject class.
     *
     * @return a TextureRegion representing the current appearance of the exit.
     */
    @Override
    public TextureRegion getCurrentAppearance() {
        if(unlocked){
            return Textures.EXIT_UNLOCKED;
        }
        return Textures.EXIT;
    }

    /**
     * Checks if the exit is unlocked.
     * This method is used to query the lock state of the exit.
     *
     * @return true if the exit is unlocked, false otherwise.
     */
    public boolean isUnlocked() {
        return unlocked;
    }

}
