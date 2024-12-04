package com.example.rescuecats.Controller;

import com.example.rescuecats.Service.AuthenticationService;
import com.example.rescuecats.Model.Bomb;
import com.example.rescuecats.Model.Player;
import com.example.rescuecats.Model.Puzzle;
import com.example.rescuecats.Service.*;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MainGame implements Initializable {


    public ImageView puzzleImage;
    public Canvas gameCanvas;
    public Button zeroBtn;
    public Button oneBtn;
    public Button twoBtn;
    public Button threeBtn;
    public Button fourBtn;
    public Button fiveBtn;
    public Button sixBtn;
    public Button sevenBtn;
    public Button eightBtn;
    public Button nineBtn;
    public Label highScoreLabel;
    public Label currentScoreLabel;
    public Button BackToMenuBtn;
    public Button restartBtn;
    public Label numberOfPuzzlesSolvedLabel;

    private GraphicsContext gc; // object used to draw on the canvas element
    private Button[] answerButtons; //list holding the answer buttons
    private Image bombImage;

    private ArrayList<Integer> catXPosition = new ArrayList<>();     // list containing the X-coordinates where the cats should spawn
    private ArrayList<Integer> bombSpawnPoints = new ArrayList<>();  // list containing the X-coordinate at which the bombs should spawn
    private Map<Integer, Boolean> activeSpawnPoints = new HashMap<>(); // list responsible for tracking the points at which bombs have spawned
    private ArrayList<Image> catFrames = new ArrayList<>();            // an image list containing all the frames required to animate the cats
    private ArrayList<Image> explosionFrames = new ArrayList<>();     // an image list containing all the frames required to animate the bomb explosion
    private ArrayList<Bomb> bombs = new ArrayList<>();               // a list of bomb objects that have spawned
    private ArrayList<Bomb> collidedBombs = new ArrayList<>();      // a list bomb objects that need to explode as a result of collison or the player has given the correct answer


    private final Random random = new Random();
    private long lastUpdate=0;
    private int currentCatFrame=0;
    private int currentExplosionFrame=0;
    private int bombCount=0;
    private int bombsToBeOnScreen =1;
    private int bombSpawnCooldown=0;
    private final int BOMB_SPAWN_DELAY=150;      // a small delay before next bomb spawn
    private double bombSpeed=0.1;                // initially fall at slower rate
    private final double SPEED_INCREMENT = 0.05; // Speed increment per level
    private final double MAX_SPEED = 2.0;
    private boolean collided=false;
    private boolean foundAnswer=false;
    private int frameCounter;
    int radius=2;
    int bombFrame=6;
    private boolean isExploding = false;
    private Bomb bombToBeRemoved=null;
    private int currentScore=0;
    private int speedIncrementCounter=0;
    private boolean gameOver=false;

    private PuzzleService puzzleService;
    private CounterService counterService;
    private AchievementService achievementService;
    private MusicService musicService;

    private Player player;
    private Puzzle currentPuzzle;
    AnimationTimer gameLoop;

    //achievement stats
    private boolean firstGame=false;
    private int noOfDestroyedBombs=0;
    private int noOfSolvedPuzzles=0;


    @Override
    public void initialize(URL url,ResourceBundle resourceBundle) {

        gc=gameCanvas.getGraphicsContext2D();
        puzzleService=new PuzzleService();
        counterService=new CounterService();            /* initializing multiple service classes reponsible for making api calls ,starting background music and monitoring achievement criteria*/
        musicService=new MusicService();
        achievementService=AchievementService.getInstance();
        player= AuthenticationService.player;
        new Thread(() -> {                                           // aa thread had to be used to make api calls so as to not block the main ui thread while the game wait for a response from the api call
            currentPuzzle=puzzleService.fetchNewPuzzle();           // making api call to banana game api to load in the puzzle image
            puzzleImage.setImage(currentPuzzle.getPuzzleImage());
            counterService.ResetCounter();
            int count=counterService.createCounter();               // making api call to create a counter if this is a new player or to bring in the existing counter if the player is not new adn has a counter already made for them
            Platform.runLater(()-> numberOfPuzzlesSolvedLabel.setText(String.valueOf(count)));
        }).start();

        answerButtons=new Button[]{zeroBtn,oneBtn,twoBtn,threeBtn,fourBtn,fiveBtn,sixBtn,sevenBtn,eightBtn,nineBtn}; // initializing the answer buttons

        // Load bomb image
        bombImage = new Image(getClass().getResourceAsStream("/images/bomb.png"));

        // Load all the tail animation frames into catFrames
        for (int i = 1; i <= 3; i++) {
           catFrames.add(new Image(getClass().getResourceAsStream("/images/cat" + i + ".png")));
        }
        // Load all the explosion animation frames into explosionFrames
        for (int i = 1; i <= 3; i++) {
            explosionFrames.add(new Image(getClass().getResourceAsStream("/images/explosion" + i + ".png")));
        }
        initializeCats();
        startGameLoop();
        addListeners();
    }

    /** initializeCats() will draw cats at the specified X-coordinates and also get the X-coordinates at which the bomb should spawn**/
    private void initializeCats() {
        //code referenced from https://docs.oracle.com/javase/8/javafx/api/javafx/scene/canvas/GraphicsContext.html#drawImage-javafx.scene.image.Image-double-double-double-double-
        int catYPos= (int)gameCanvas.getHeight()-100;
        //draw the cats
        for (int i = 0; i < 4; i++) {
            int catXPos = 20 + i * 110;
            catXPosition.add(catXPos); // Store each cat's X position
            gc.drawImage(catFrames.get(0), catXPos, catYPos, 100, 100); // Draw the first frame initially
        }
        // copying same X-coordinates of cats onto bombSpawnPoints because the bombs need to fall accurately right on top of the cats head
        for (int i = 0; i <catXPosition.size() ; i++) {
            bombSpawnPoints.add(catXPosition.get(i)+10);
        }

        // Initialize activeSpawnPoints
        for (int x : bombSpawnPoints) {
            activeSpawnPoints.put(x, false); // Initially all are inactive
        }
        //set high score
        highScoreLabel.setText(String.valueOf(player.getHighscore()));
        firstGame=true;
    }
    /** startGameLoop() will update game logic and render graphics 60 times per second**/
    public void startGameLoop() {
     gameLoop = new AnimationTimer() { // code obtained from https://code.tutsplus.com/introduction-to-javafx-for-game-development--cms-23835t
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 16_000_000) { // Roughly 60 FPS
                    updateGame();
                    renderGame();
                    lastUpdate = now;
                }

            }
        };
        gameLoop.start();
    }
    /** update() will update the game logic such as incrementing bombs speed,moving the bomb forward, checking for collisions, checking if player gave
     * correct answer, updating the frame counter to indicate which animation frame should be shown next and continuously checking if an achievement criteria
     * has been met**/
    private void updateGame()
    {
        checkAchievements();   // check if player did an achievement
        if(catXPosition.isEmpty())  // if all cats are dead sop updating game logic ,exit from method and signal a game over event
        {
            gameOver=true;
            return;
        }
        ++frameCounter;   //counter to keep track of which cat animation frame to show next

        if(collided || foundAnswer ||isExploding)
        {
            if(bombFrame==6)          //to control bomb explosion animation
            {
                currentExplosionFrame = (currentExplosionFrame + 1) % explosionFrames.size();  // cycling through the bomb explosion frames when the bomb needs to explode
                bombFrame=0;
            }
            ++bombFrame;
        }

        if(frameCounter==10)  // to control cat animation
        {
            currentCatFrame = (currentCatFrame + 1) % catFrames.size(); // Cycle through the frames to animate the cat
            frameCounter=0;
        }

        if (activeSpawnPoints.size() == 2) {
            bombsToBeOnScreen = 1; // Allow only 1 bomb for 2 spawn points
        } else {
            bombsToBeOnScreen = Math.min(2, Math.max(1, activeSpawnPoints.size()));
        }
        if(bombCount!=bombsToBeOnScreen) {
            spawnBomb();
        }

        if (foundAnswer && !bombs.isEmpty()) {   // triggered if player found correct answer
            Bomb closestBomb = bombs.get(0);
            for (Bomb bomb : bombs) {                    // identify the closest bomb to the cats within this loop
                if (bomb.getY() > closestBomb.getY()) {
                    closestBomb = bomb;
                }
            }
            int spawnPointIndex = closestBomb.getX();
            activeSpawnPoints.put(spawnPointIndex, false);
            collidedBombs.add(closestBomb);
            bombs.remove(closestBomb);
            foundAnswer=false;
            isExploding=true;
            --bombCount;
            ++noOfDestroyedBombs;
            ++noOfSolvedPuzzles;
            ++speedIncrementCounter;
            incrementScore();
            new Thread(() -> {
                int count=counterService.incrementCounter();            //call to counter api to increment the players counter
                Platform.runLater(()->numberOfPuzzlesSolvedLabel.setText(String.valueOf(count)));
            }).start();

            if (speedIncrementCounter % 20 == 0 && bombSpeed < MAX_SPEED) {     // increase bomb falling speed every 20 puzzles
                bombSpeed += SPEED_INCREMENT;
                System.out.println("New Bomb Speed: " + bombSpeed);

            }
        }

        // move bombs forward
        for(Bomb bomb:bombs)
        {

            bomb.moveBombForward(bombSpeed);

            if(bomb.getY()>500)
            {
                collidedBombs.add(bomb);
                collided=true;
                isExploding=true;
                bombToBeRemoved=bomb;
                --bombCount;
            }
        }
        if(bombToBeRemoved!=null)   //remove the position of the cat that got collided and remove the spawn point for the bomb so that no more bombs will spawn on top of the place where the cat died
        {
            int i=bombSpawnPoints.indexOf(bombToBeRemoved.getX());
            int j= catXPosition.indexOf(bombToBeRemoved.getX()-10);
            bombSpawnPoints.remove(i);
            activeSpawnPoints.remove(bombToBeRemoved.getX());
            catXPosition.remove(j);
            bombs.remove(bombToBeRemoved);
            bombToBeRemoved=null;

        }

    }
    // rendering the graphics
    private void renderGame() {

        int catYPos = (int) gameCanvas.getHeight() - 100;

        // Clear the canvas to redraw the next frame
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Draw each cat with the current tail frame
        for (int catXPos : catXPosition) {
            gc.drawImage(catFrames.get(currentCatFrame), catXPos, catYPos, 100, 100);
        }

        if(isExploding)  // drawing the bomb explosion animation on screen
        {

            for(Bomb bomb:collidedBombs)
            {
                    gc.drawImage(explosionFrames.get(currentExplosionFrame), bomb.getX()-radius, bomb.getY(), 100+radius, 100+radius);
                    radius+=2;

            }
            if (currentExplosionFrame == explosionFrames.size() - 1) {
                // Explosion animation finished, reset collision flags
                currentExplosionFrame=0;
                collided = false;
                isExploding=false;
                radius=2;
                collidedBombs.clear();
            }
        }

        for(Bomb bomb:bombs)
        {
            gc.drawImage(bombImage, bomb.getX(), bomb.getY(),80,80);
        }
        if(gameOver)   // when game over detected draw the game over screen
        {
            System.out.println("GAME OVER");
            gameLoop.stop();
            gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 45));
            gc.fillText("😿 GAME OVER 💣", gameCanvas.getWidth() / 2 - 190, gameCanvas.getHeight() / 2 - 50);
            restartBtn.setVisible(true);
            BackToMenuBtn.setVisible(true);
            musicService.stopBackgroundMusic();
            restartBtn.setOnAction(actionevent -> {
                try {
                    endGame(actionevent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            BackToMenuBtn.setOnAction(actionevent -> {
                try {
                    endGame(actionevent);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

        }

    }

    /** spawnBomb() will randomnly choose a spawn point and drop a bomb, no of bomb falling will depend on the number of cats alive**/
    private void spawnBomb()
    {
        int chosenSpawnPoint;
        if(bombSpawnCooldown>0)
        {
            --bombSpawnCooldown;
        }
        else
        {
            do {
                chosenSpawnPoint = bombSpawnPoints.get(random.nextInt(bombSpawnPoints.size()));
            } while (activeSpawnPoints.get(chosenSpawnPoint)); // Ensure point is inactive

            bombs.add(new Bomb(chosenSpawnPoint, 0));
            activeSpawnPoints.put(chosenSpawnPoint, true); // Mark spawn point as active
            ++bombCount;
            bombSpawnCooldown=BOMB_SPAWN_DELAY;

        }
    }

    private void addListeners()
    {
        for (int i = 0; i < answerButtons.length ; i++)
        {
            answerButtons[i].setOnAction(actionEvent -> handleAnswerButtonClick(actionEvent)); // adding event listener for all answer buttons
        }
    }

    private void handleAnswerButtonClick(ActionEvent event)  // responding to answer button click
    {
        Button clickedButton = (Button) event.getSource();
        int number = Integer.parseInt(clickedButton.getText());
        if(currentPuzzle.checkAnswer(number))
        {
            new Thread(() -> {
                currentPuzzle=puzzleService.fetchNewPuzzle();           //call to banana game api to get next puzzle
                puzzleImage.setImage(currentPuzzle.getPuzzleImage());
            }).start();

            foundAnswer=true;
        }
        else
        {
            System.out.println("wrong answer");
        }

    }

    // simple method to increment score of player provided that the correct answer has been given
    private void incrementScore()
    {
        currentScore=Integer.parseInt(currentScoreLabel.getText());
        ++currentScore;
        currentScoreLabel.setText(String.valueOf(currentScore));
    }

    /** checkAchievements() will continuously check for any achievement criteria met and will unlock it if it has not been unlocked already **/
    private void checkAchievements() {

        if(firstGame)
        {
            if(achievementService.alreadyUnlocked(1))
            {
                achievementService.unlockAchievement(1);
            }
        }
        if(noOfDestroyedBombs==10)
        {
            if(achievementService.alreadyUnlocked(2))
            {
               achievementService.unlockAchievement(2);
            }
        }
        if(noOfDestroyedBombs==50)
        {
            if(achievementService.alreadyUnlocked(3))
            {
                achievementService.unlockAchievement(3);

            }
        }
        if(currentScore==20)
        {
            if(achievementService.alreadyUnlocked(4))
            {
                achievementService.unlockAchievement(4);

            }
        }
        if(noOfSolvedPuzzles==30)
        {
            if(achievementService.alreadyUnlocked(5))
            {
                achievementService.unlockAchievement(5);

            }
        }

    }
    /** endGame() will handle restarting of game and resetting all counters and variables **/
    private void endGame(ActionEvent event) throws IOException {
        Button clickedButton = (Button) event.getSource();
        String text= clickedButton.getText();
        if(text.equals("Restart"))
        {
            gameOver=false;
            restartBtn.setVisible(false);
            BackToMenuBtn.setVisible(false);
            gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
            new Thread(()->{
                int count=counterService.ResetCounter();
                Platform.runLater(()->numberOfPuzzlesSolvedLabel.setText(String.valueOf(count)));
            }).start();
            initializeCats();
            startGameLoop();
        }
        else {
            SceneController.control(BackToMenuBtn,"menu.fxml");

        }

        currentScore=0;
       currentScoreLabel.setText(String.valueOf(currentScore));
       highScoreLabel.setText(String.valueOf(player.updateHighScore(Integer.parseInt(currentScoreLabel.getText()))));
    }

}
