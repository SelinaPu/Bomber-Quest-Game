package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.screen.CountdownTimer;
import de.tum.cit.ase.bomberquest.texture.Drawable;
import de.tum.cit.ase.bomberquest.texture.Textures;
import de.tum.cit.ase.bomberquest.map.Player;

import java.util.Timer;

/**
 * Represents a Power-Up item in the game.
 * Power-ups are hidden under destructible walls and provide enhancements to the player when collected.
 */
public class PowerUp implements Drawable{
    private final float x;
    private final float y;
    /** The type of Power-Up (e.g., increased blast radius or additional bomb placement). */
    private PowerUpType type;

    public PowerUp(float x, float y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }
    @Override
    public float getWidth() {
        return 1;
    }

    @Override
    public float getHeight() {
        return 1;
    }


    /**
     * Applies the Power-Up effect to the player.
     * When the player collects the Power-Up, it grants a permanent upgrade.
     *
     * @param player The player who collects the Power-Up.
     */
    public void applyEffect(Player player) {
        MusicTrack.COLLECT.play();// Play collection sound effect

        switch (type) {
            case BLAST_RADIUS:
                player.increaseBlastRadius();// Increase the player's bomb blast radius
                break;
            case CONCURRENT_BOMBS:
                player.increaseConcurrentBombs();// Increase the number of bombs the player can place at once
                break;
//            case TIME_EXTENSION:
//                if (timer != null) {
//                    timer.addTime(10);
//                    System.out.println("Time extended by 10 seconds!");
//                }// Increase 10 seconds
//                break;

        }
    }

    public PowerUpType getType() {
        return type;
    }

    @Override
    public TextureRegion getCurrentAppearance() {
        switch (this.type) {
            case BLAST_RADIUS:
                return Textures.INCREASEDRADIUS;
            case CONCURRENT_BOMBS:
                return Textures.ADDITIONALBOMB;
//            case TIME_EXTENSION:
//                return Textures.TIMEEXTENSION;
            default:
                return null; // or some default texture
        }
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }
}
