package de.tum.cit.ase.bomberquest.map;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.screen.VictoryAndGameOverScreen;
import de.tum.cit.ase.bomberquest.texture.Animations;
import de.tum.cit.ase.bomberquest.texture.Textures;

import java.util.HashMap;
import java.util.Map;

/**
 * The Bomb class represents a bomb that a player can place on the game map.
 * The bomb has a timer and explodes after a fixed duration, creating an explosion effect.
 * It interacts with the game map and can destroy destructible walls, damage players/enemies, and trigger chain reactions.
 */

public class Bomb extends GameObject {

    private float bombTimer = 3.0f;//the time after bomb is placed and before it explodes
    private final float explosionDuration = 0.5f;//duration of the explosion animation
    private float explosionTimer;
    private Animation<TextureRegion> explosionAnimation;
    private boolean exploded;
    private final TextureRegion bombTexture;
    private final GameMap map;// Reference to the game map where the bomb exists
    public static VictoryAndGameOverScreen victoryAndGameOverScreen;
    private Player player;// Reference to the player who placed the bomb
    // static attribute for death reason
    public static String deathReason = "";


    public float getExplosionTimer() {
        return bombTimer;
    }//returns remaining time until the explosion

    public GameMap getMap() {
        return map;
    }

    public Bomb(float x, float y, TextureRegion bombTexture, GameMap map, int explosionRadius) {
        super(x, y);//call the position from GameObject Class
        this.map = map;
        this.bombTexture = Textures.BOMB;
        this.exploded = false;
        player = map.getPlayer();//get the player instance from the map
    }

    public void render(SpriteBatch spriteBatch) {//render the bomb or explosion animation based on timer
        float x = getX();
        float y = getY();

        if (bombTimer < 0.8f) {
            TextureRegion frame = explosionAnimation.getKeyFrame(explosionDuration - explosionTimer, false);//This expression determines which frame of the animation should be shown at a given moment.
            spriteBatch.draw(frame, x - 16 * 2.5f, y, 1, 1); // Adjust explosion size accordingly
        } else {
            spriteBatch.draw(bombTexture, x, y, 1, 1);
        }
    }

    public void update(float delta) {
        stateTime += delta;//updated state time

        if (!exploded) {
            bombTimer -= delta;
            if (bombTimer <= 0.0f) {// If timer reaches zero, trigger explosion
                explode();
            }
        } else {
            explosionTimer -= delta;
            if (explosionTimer <= 0.0f) {
                setExploded(true);
            }
        }
    }

    private void explode() {//Triggers the bomb explosion, playing sound and animation, and generating blast effects.
        if (!exploded) {

            MusicTrack.EXPLODE.play();

            this.exploded = true;
            playExplosionAniation();
            generateBlast();// Generate blast waves affecting walls, enemies, and the player
            setExploded(true);
        }
    }

    private void playExplosionAniation() {
        explosionAnimation = Animations.BOMB_EXPLOSION;
        explosionTimer = explosionDuration;
    }

    private void generateBlast() {
        for (Direction direction : Direction.values()) {// Loop through all four directions (UP, DOWN, LEFT, RIGHT)
            propagateBlast(direction);
        }
    }

    public Map<Direction, Integer> getBlastRadius() {
        Map<Direction, Integer> directionIntegerMap = new HashMap<>();

        for (Direction direction : Direction.values()) {
            directionIntegerMap.put(direction, propagateBlast2(direction));
        }

        return directionIntegerMap;
    }

    private int propagateBlast(Direction direction) {
        float x = getX();
        float y = getY();

        for (int i = 0; i <= player.getBlastRadius(); i++) {
            int targetX = (int) (x + direction.getOffsetX() * i);// Calculate X position in explosion path
            int targetY = (int) (y + direction.getOffsetY() * i); // Calculate Y position in explosion path

            if (!map.isPassablePlayer(targetX, targetY)) {// Boundaries and indestructible walls block explosion
            }


            Wall wall = map.getWallContains(targetX * 64, targetY * 64);// Check if a destructible wall exists at the explosion's position

            if (wall != null) {
                if (!wall.isDestructible()) {
                    return i;// Stop explosion if an indestructible wall is hit
                }

                if (wall.isDestructible() && !wall.isDestroyed()) {
                    map.destroyWall(wall); // Destroy destructible wall
                }
            }

            // Check if the explosion hits the player
            map.getPlayer();//player is killed by bomb.
            if (map.isCollision(map.getPlayer(), new Bomb(targetX, targetY, Textures.BOMB, map, 1))) {
                deathReason = "Player killed by bomb."; // Set the death reason
                map.getPlayer().kill();
                map.getGame().setScreen(new VictoryAndGameOverScreen(map.getGame(), false, Bomb.deathReason)); // Pass the reason to game over screen
            }



            Enemy enemy = map.getEnemyAt2(targetX, targetY);
            if (enemy != null) {
                map.killEnemy(enemy);
            }
        }

        return player.getBlastRadius();
    }

    private int propagateBlast2(Direction direction) {
        float x = getX();
        float y = getY();

        for (int i = 0; i <= player.getBlastRadius(); i++) {
            int targetX = (int) (x + direction.getOffsetX() * i);
            int targetY = (int) (y + direction.getOffsetY() * i);


            if (!map.isPassablePlayer(targetX, targetY)) {
            }

            Wall wall = map.getWallContains(targetX * 64, targetY * 64);

            if (wall != null) {
                if (!wall.isDestructible()) {
                    return i-1;
                }
            }
        }

        return player.getBlastRadius();
    }


    private float stateTime = 0;

    @Override
    public TextureRegion getCurrentAppearance() {
        return bombTimer < 0.8f ? Animations.BOMB_EXPLOSION.getKeyFrame(stateTime, true) : Animations.BOMB_DISPLAY.getKeyFrame(stateTime, true);
    }

    public boolean isExploded() {
        return exploded;
    }

    public void setExploded(boolean exploded) {
        this.exploded = exploded;
    }
}
