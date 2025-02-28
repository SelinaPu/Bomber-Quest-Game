package de.tum.cit.ase.bomberquest.screen;

import com.badlogic.gdx.utils.Timer;
import de.tum.cit.ase.bomberquest.BomberQuestGame;
/**
 * The CountdownTimer class manages the in-game countdown timer.
 * It decreases the time every second and triggers a game over when time runs out.
 */

public class CountdownTimer {
    private float timeLeft; // Remaining time in seconds
    private Timer.Task timerTask;//Timer task responsible for countdown execution
    BomberQuestGame game;
    private boolean isGameOver = false;//Flag to track if the game is over due to time running out
    private boolean pause;//Flag to indicate if the timer is paused

    public CountdownTimer(float initialTime, BomberQuestGame game) {
        this.timeLeft = initialTime;
        this.game = game;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
    }

    /**
     * Starts the countdown timer.
     * Decreases the time by 1 second every second until time runs out.
     */
    public void start(){
        timerTask = new Timer.Task() {
            public void run() {
                if(pause){ // If the game is paused, do nothing
                    return;
                }

                if(timeLeft > 0) {
                    timeLeft -= 1; // Decrease time by 1 second
                }else{
                    endGame();// If time reaches zero, trigger game over
                }
            }
        } ;
        // Schedule the timer to execute every second (1s delay, 1s interval)
        Timer.schedule(timerTask,1,1);
    }

    /**
     * Increases the remaining time.
     *
     * @param extraTime The amount of time (in seconds) to add to the timer.
     */
//    public float addTime(float extraTime) {
//        this.timeLeft += extraTime;  // This modifies the instance variable directly
//        System.out.println("Add " + extraTime + " second(s), timeLeft now: " + this.timeLeft + " second(s)");
//        return this.timeLeft;
//    }

    /**
     * Stops the countdown timer.
     * Prevents further execution of the countdown task.
     */

    public void stop(){
        if(timerTask != null){
            timerTask.cancel();
        }
    }

    /**
     * Triggers the game over event when the countdown reaches zero.
     */
    private void endGame(){
        isGameOver = true;
        stop();//stop the timer
        game.setDeathReason("Time ran out."); // Set the death reason
        game.goToVictoryAndGameOver(false); // Go to Game Over screen
    }

    /**
     * Retrieves the remaining time in seconds.
     *
     * @return The current remaining time.
     */
    public float getTimeLeft() {
        return timeLeft;
    }

    public Timer.Task getTimerTask() {
        return timerTask;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

}
