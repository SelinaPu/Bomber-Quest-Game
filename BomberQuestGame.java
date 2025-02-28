package de.tum.cit.ase.bomberquest;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;
import de.tum.cit.ase.bomberquest.map.Bomb;
import de.tum.cit.ase.bomberquest.map.GameMap;
import de.tum.cit.ase.bomberquest.screen.CountdownTimer;
import de.tum.cit.ase.bomberquest.screen.GameScreen;
import de.tum.cit.ase.bomberquest.screen.MenuScreen;
import de.tum.cit.ase.bomberquest.screen.VictoryAndGameOverScreen;
import games.spooky.gdx.nativefilechooser.NativeFileChooser;
import games.spooky.gdx.nativefilechooser.NativeFileChooserCallback;
import games.spooky.gdx.nativefilechooser.NativeFileChooserConfiguration;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import static de.tum.cit.ase.bomberquest.screen.GameScreen.SCALE;
import static de.tum.cit.ase.bomberquest.screen.GameScreen.TILE_SIZE_PX;

/**
 * The BomberQuestGame class represents the core of the Bomber Quest game.
 * It manages the screens and global resources like SpriteBatch and Skin.
 */
public class BomberQuestGame extends Game {

    /**
     * Sprite Batch for rendering game elements.
     * This eats a lot of memory, so we only want one of these.
     */
    private SpriteBatch spriteBatch;

    /** The game's UI skin. This is used to style the game's UI elements. */
    private Skin skin;

    /**
     * The file chooser for loading map files from the user's computer.
     * This will give you access to a {@link com.badlogic.gdx.files.FileHandle} object,
     * which you can use to read the contents of the map file as a String, and then parse it into a {@link GameMap}.
     */
    private final NativeFileChooser fileChooser;

    /**
     * The map. This is where all the game objects are stored.
     * This is owned by {@link BomberQuestGame} and not by {@link GameScreen}
     * because the map should not be destroyed if we temporarily switch to another screen.
     */
    private GameMap map;

    private GameScreen currentGameScreen;

    /** Stores the reason for the player's death (used in the game over screen). */
    private String deathReason = "";

    /**
     * Resumes the game if it was paused.
     * Restores the game screen and resumes the countdown timer.
     */
    public void continueGame() {
        if (currentGameScreen != null) {
            // Switch back to the game screen
            setScreen(currentGameScreen);

            currentGameScreen.getTimer().setPause(false);//Resume the game timer

            MusicTrack.BACKGROUND_MENU.stop(); // Play some background music
            MusicTrack.BACKGROUND.play(); // Play some background music
        }
    }

    /**
     * Constructor for BomberQuestGame.
     *
     * @param fileChooser The file chooser for the game, typically used in desktop environment.
     */
    public BomberQuestGame(NativeFileChooser fileChooser) {
        this.fileChooser = fileChooser;


    }

    /**
     * Called when the game is created. Initializes the SpriteBatch and Skin.
     * During the class constructor, libGDX is not fully initialized yet.
     * Therefore this method serves as a second constructor for the game,
     * and we can use libGDX resources here.
     */
    @Override
    public void create() {
        Bomb.victoryAndGameOverScreen = new VictoryAndGameOverScreen(this, true,deathReason);

        this.spriteBatch = new SpriteBatch(); // Create SpriteBatch for rendering
        this.skin = new Skin(Gdx.files.internal("skin/craftacular/craftacular-ui.json")); // Load UI skin
        try {
            // Load the default map when the game starts
            this.map = new GameMap(this, "maps/map-1.properties"); // Create a new game map (you should change this to load the map from a file instead)
        } catch (IOException e) {
            e.printStackTrace();
            Gdx.app.exit();// Exit the game if the map fails to load
        }
        BitmapFont font = new BitmapFont();
        goToMenu(); // Navigate to the menu screen
    }

    /**
     * Loads a specified map and switches to the game screen.
     *
     * @param mapFilePath The file path of the new map to load.
     */
    public void loadMap(String mapFilePath) throws IOException {
        this.map = new GameMap(this, mapFilePath);
        this.currentGameScreen = new GameScreen(this, map);
        setScreen(currentGameScreen);
    }
    /**
     * Displays a file chooser to select and load a new map file.
     */
    public void loadNewMap() {
        System.out.println("loadNewMap"  );

        // Configure the file chooser
        NativeFileChooserConfiguration conf = new NativeFileChooserConfiguration();
        conf.directory = Gdx.files.local("maps");
        conf.mimeFilter = "map/*";

        // Filter to show only .properties files
        conf.nameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("properties");
            }
        };
        // Add a nice title
        conf.title = "Choose map file";

        fileChooser.chooseFile(conf, new NativeFileChooserCallback() {
            @Override
            public void onFileChosen(FileHandle file) {
                try {
                    System.out.println("Selected file: " + file.path());
                    map = new GameMap(BomberQuestGame.this, file.path()); // Create a new game map (you should change this to load the map from a file instead)
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                    Gdx.app.exit();
                }

                currentGameScreen = new GameScreen(BomberQuestGame.this, map);
                setScreen(currentGameScreen); // Set the current screen to GameScreen

                MusicTrack.BACKGROUND_MENU.stop(); // stop playing some background music
                MusicTrack.BACKGROUND.play(); // Play some background music
            }

            @Override
            public void onCancellation() {

            }

            @Override
            public void onError(Exception exception) {

            }
        });
    }

    /**
     * Switches to the menu screen.
     */
    public void goToMenu() {
        this.setScreen(new MenuScreen(this)); // Set the current screen to MenuScreen

        if(currentGameScreen != null)
            currentGameScreen.getTimer().setPause(true);

        MusicTrack.BACKGROUND.stop(); // Play some background music
        MusicTrack.BACKGROUND_MENU.play(); // Play some background music
    }
    /**
     * Switches to the game screen.
     */
    public void goToGame() {
        create();
        currentGameScreen = new GameScreen(this, this.map);
        this.setScreen(currentGameScreen); // Set the current screen to GameScreen

        MusicTrack.BACKGROUND_MENU.stop(); // Play some background music
        MusicTrack.BACKGROUND.play(); // Play some background music
    }

    /**
     * Switches to the VictoryAndGameOver screen
     */
    public void goToVictoryAndGameOver(boolean won){
        this.setScreen(new VictoryAndGameOverScreen(this, won, deathReason));

        currentGameScreen = null;

        if(won){
            MusicTrack.WIN.play();
            MusicTrack.BACKGROUND.stop();
        }else {
            MusicTrack.LOSE.play();
            MusicTrack.BACKGROUND.stop();
        }
    }

    /** Returns the skin for UI elements. */
    public Skin getSkin() {
        return skin;
    }

    /** Returns the main SpriteBatch for rendering. */
    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    /** Returns the current map, if there is one. */
    public GameMap getMap() {
        return map;
    }

    // Setter method for death reason
    public void setDeathReason(String reason) {
        this.deathReason = reason;
    }

    /**
     * Switches to the given screen and disposes of the previous screen.
     * @param screen the new screen
     */
    @Override
    public void setScreen(Screen screen) {
        Screen previousScreen = super.screen;
        super.setScreen(screen);
        if (previousScreen != null && previousScreen != screen) {
            // previousScreen.dispose();
        }
    }

    /** Cleans up resources when the game is disposed. */
    @Override
    public void dispose() {
        getScreen().hide(); // Hide the current screen
        getScreen().dispose(); // Dispose the current screen
        spriteBatch.dispose(); // Dispose the spriteBatch
        skin.dispose(); // Dispose the skin
    }
}
