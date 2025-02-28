package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
import de.tum.cit.ase.bomberquest.audio.MusicTrack;

/**
 * The VictoryAndGameOverScreen class is responsible for displaying the screen menu when the game ends with different texts when success or failure.
 * It extends the LibGDX Screen class and sets up the UI components for the menu.
 */

public class VictoryAndGameOverScreen implements Screen{
    private BomberQuestGame game;
    private Stage stage;
    private boolean won; // a private boolean
    private String deathReason;

    public VictoryAndGameOverScreen(BomberQuestGame game, boolean won, String deathReason) {
        this.game = game;
        this.won = won;
        this.deathReason = deathReason;
    }

    @Override
    public void show() {

        stage = new Stage(new ScreenViewport()); // initialize a new Stage, use ScreenViewport to make UI adapt to all screen size without zooming
        Gdx.input.setInputProcessor(stage); // Input Processor is stage. User can input with mouse or keyboard

        Table table = new Table(); // new a Table to organize elements
        table.setFillParent(true); // tale fills the stage
        stage.addActor(table);  // add table to stage

        Label.LabelStyle labelStyle = new Label.LabelStyle(); // new a LabelStyle to define the appearance of the text
        labelStyle.font = new BitmapFont();// Bitmapfont is the font here
        labelStyle.font.getData().setScale(2f);//set the font size to twice as big as before

        String message = won ? "Victory! You won the game!" : "Game Over! You lost!";
        Label messageLabel = new Label(message, labelStyle); // new a Label to show the message of game over
        // Check if the game was lost and there is a death reason
        if (!won && deathReason != null && !deathReason.isEmpty()) {
            Label reasonLabel = new Label(deathReason, labelStyle); // Create a label for death reason
            table.add(reasonLabel).pad(10); // Add death reason above the "Game Over" text
            table.row(); // Move to the next row
        }
        table.add(messageLabel).pad(20);

        TextButton returnButton = new TextButton("Return to Main Menu", game.getSkin()); // new a Textbutton to show "Return to Main Menu"
        table.row(); // create a new row
        table.add(returnButton).pad(20);

        returnButton.addListener(new ChangeListener() { // 给返回按钮添加一个监听器。
            @Override
            public void changed(ChangeEvent event, com.badlogic.gdx.scenes.scene2d.Actor actor) {
                game.setScreen(new MenuScreen(game));

                MusicTrack.BACKGROUND_MENU.play(); // Play some background music
                MusicTrack.BACKGROUND.stop(); // Play some background music
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f)); // update stage performance
        stage.draw();
    }


    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true); // update stage viewpoint size
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        stage.dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public BomberQuestGame getGame() {
        return game;
    }

    //getter and setter
    public Stage getStage() {
        return stage;
    }

    public boolean isWon() {
        return won;
    }

    public void setGame(BomberQuestGame game) {
        this.game = game;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }


}
