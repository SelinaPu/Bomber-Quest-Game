package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;

import java.io.IOException;

import static com.badlogic.gdx.utils.JsonValue.ValueType.object;

/**
 * The MenuScreen class represents the main menu of the game.
 * It sets up the user interface, handles button interactions, and allows the player
 * to start a new game, continue a game, or exit.
 */
public class MenuScreen implements Screen {
    /** The stage that holds UI elements for the menu. */
    private final Stage stage;
    /** The main game instance, used for navigation and accessing global resources. */
    private final BomberQuestGame game;


    /**
     * Constructor for MenuScreen. Sets up the camera, viewport, stage, and UI elements.
     *
     * @param game The main game class, used to access global resources and methods.
     */
    public MenuScreen(BomberQuestGame game) {
        this.game = game;
        var camera = new OrthographicCamera();
        camera.zoom = 1.5f; // Set camera zoom for a closer view

        Viewport viewport = new ScreenViewport(camera); // Create a viewport with the camera
        stage = new Stage(viewport, game.getSpriteBatch()); // Create a stage for UI elements

        Table table = new Table(); // Table layout for arranging UI components
        table.setFillParent(true); // The table fills the entire screen
        stage.addActor(table); // Add the table to the stage

        // Add a label as a title
        table.add(new Label("Hello World from the Menu!", game.getSkin(), "title")).padBottom(80).row();


        // Create and add a button to go to the game screen
        TextButton goToGameButton = new TextButton("Go To Game", game.getSkin());
        table.row().padTop(10);
        table.add(goToGameButton).fillX().uniformX();
        goToGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.goToGame(); // Change to the game screen when button is pressed
            }
        });

        // Create buttons for additional menu options
        TextButton continueButton = new TextButton("Continue the game", game.getSkin());
        TextButton loadNewGameButton = new TextButton("Load a new map file and start a new game", game.getSkin());
        TextButton exitButton = new TextButton("Exit the game", game.getSkin());
        // Add the buttons to the table layout
        table.row().padTop(10);
        table.add(continueButton).fillX().uniformX();
        table.row().padTop(10);
        table.add(loadNewGameButton).fillX().uniformX();
        table.row().padTop(10);
        table.add(exitButton).fillX().uniformX();

        // Set up event listener for "Load New Game" button
        loadNewGameButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Display the map selection dialog
                game.loadNewMap();
            }
        });
        // Set up event listener for "Continue Game" button
        continueButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.continueGame();
            }
        });
        // Set up event listener for "Exit Game" button
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    /**
     * Displays a map selection dialog where the player can choose a map to load.
     */
    private void showMapSelectionDialog() {
        Dialog dialog = new Dialog("Select Map", game.getSkin()) {
            @Override
            protected void result(Object object) {
                if (object instanceof String) {
                    String selectedMap = (String) object;  // Get the selected map file
                    try {
                        game.loadMap("maps/" + selectedMap); // Load the selected map
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        // Set up dialog buttons for selecting different maps
        dialog.text("Choose a map to load:");
        dialog.button("Map 1", "map-1.properties");
        dialog.button("Map 2", "map-2.properties");
        dialog.show(stage); // Display the dialog on the current stage
    }

    /**
     * The render method is called every frame to render the menu screen.
     * It clears the screen and draws the stage.
     * @param deltaTime The time in seconds since the last render.
     */
    @Override
    public void render(float deltaTime) {
        float frameTime = Math.min(deltaTime, 0.250f); // Cap frame time to 250ms to prevent spiral of death        ScreenUtils.clear(Color.BLACK);
        ScreenUtils.clear(Color.BLACK);
        stage.act(frameTime); // Update the stage
        stage.draw(); // Draw the stage
    }

    /**
     * Resize the stage when the screen is resized.
     * @param width The new width of the screen.
     * @param height The new height of the screen.
     */
    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // Update the stage viewport on resize
    }

    @Override
    public void dispose() {
        // Dispose of the stage when screen is disposed
        stage.dispose();
    }

    @Override
    public void show() {
        // Set the input processor so the stage can receive input events
        Gdx.input.setInputProcessor(stage);
    }


    // The following methods are part of the Screen interface but are not used in this screen.
    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }
}
