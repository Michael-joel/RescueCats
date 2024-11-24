package com.example.rescuecats.Controller;

import com.example.rescuecats.Model.Authentication;
import com.example.rescuecats.Model.Bomb;
import com.example.rescuecats.Model.Player;
import com.example.rescuecats.Model.Puzzle;
import com.example.rescuecats.Service.CounterService;
import com.example.rescuecats.Service.PuzzleService;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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

    private GraphicsContext gc;


    private ArrayList<Integer> catXPosition = new ArrayList<>();
    private ArrayList<Integer> bombSpawnPoints = new ArrayList<>();
    private Map<Integer, Boolean> activeSpawnPoints = new HashMap<>();
    private ArrayList<Image> catFrames = new ArrayList<>();
    private ArrayList<Image> explosionFrames = new ArrayList<>();
    private ArrayList<Bomb> bombs = new ArrayList<>();
    private ArrayList<Bomb> collidedBombs = new ArrayList<>();
    private Button[] answerButtons;


    private Image bombImage;

    private final Random random = new Random();
    private long lastUpdate=0;
    private int currentCatFrame=0;
    private int currentExplosionFrame=0;
    private int bombCount=0;
    private int bombsToBeOnScreen =1;
    private int bombSpawnCooldown=0;
    private final int BOMB_SPAWN_DELAY=150;
    private boolean collided=false;
    private boolean foundAnswer=false;
    private int frameCounter;
    int radius=2;
    int bombFrame=6;
    private boolean isExploding = false;
    private Bomb bombToBeRemoved=null;
    private int currentScore=0;
    private boolean gameOver=false;
    private int puzzleCounter;

    private Player player;
    private PuzzleService puzzleService;
    private CounterService counterService;
    private Puzzle currentPuzzle;
    AnimationTimer gameLoop;

    @Override
    public void initialize(URL url,ResourceBundle resourceBundle) {

        gc=gameCanvas.getGraphicsContext2D();
        puzzleService=new PuzzleService();
        counterService=new CounterService();
        player=Authentication.player;
        new Thread(() -> {
            currentPuzzle=puzzleService.fetchNewPuzzle();
            puzzleImage.setImage(currentPuzzle.getPuzzleImage());
            int count=counterService.createCounter();
            Platform.runLater(()-> numberOfPuzzlesSolvedLabel.setText(String.valueOf(count)));
            //TODO: make sure counter starts at zero
        }).start();

        answerButtons=new Button[]{zeroBtn,oneBtn,twoBtn,threeBtn,fourBtn,fiveBtn,sixBtn,sevenBtn,eightBtn,nineBtn};

        // Load cat and bomb images
        bombImage = new Image(getClass().getResourceAsStream("/images/bomb.png"));

        // Load all the tail animation frames into tailFrames
        for (int i = 1; i <= 3; i++) {
           catFrames.add(new Image(getClass().getResourceAsStream("/images/cat" + i + ".png")));
        }
        for (int i = 1; i <= 3; i++) {
            explosionFrames.add(new Image(getClass().getResourceAsStream("/images/explosion" + i + ".png")));
        }
        initializeCats();
        startGameLoop();
        addListeners();
    }

    private void initializeCats() {
//code referenced from https://docs.oracle.com/javase/8/javafx/api/javafx/scene/canvas/GraphicsContext.html#drawImage-javafx.scene.image.Image-double-double-double-double-
        int catYPos= (int)gameCanvas.getHeight()-100;
        System.out.println("first"+catYPos+"second"+gameCanvas.getHeight());
        for (int i = 0; i < 4; i++) {
            int catXPos = 20 + i * 110;
            catXPosition.add(catXPos); // Store each cat's X position
            gc.drawImage(catFrames.get(0), catXPos, catYPos, 100, 100); // Draw the first frame initially
        }

        for (int i = 0; i <catXPosition.size() ; i++) {
            bombSpawnPoints.add(catXPosition.get(i)+10);
        }

        // Initialize activeSpawnPoints
        for (int x : bombSpawnPoints) {
            activeSpawnPoints.put(x, false); // Initially all are inactive
        }
        //set high score
        highScoreLabel.setText(String.valueOf(player.getHighscore()));
    }

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

    private void updateGame()
    {
        if(catXPosition.isEmpty())
        {
            gameOver=true;
            return;
        }
        ++frameCounter;
        if(collided || foundAnswer ||isExploding)
        {
            if(bombFrame==6)
            {
                currentExplosionFrame = (currentExplosionFrame + 1) % explosionFrames.size();
                bombFrame=0;
            }
            ++bombFrame;
        }
        if(frameCounter==10)
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
        if (foundAnswer && !bombs.isEmpty()) {
            Bomb closestBomb = bombs.get(0);
            for (Bomb bomb : bombs) {
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
            incrementScore();
            new Thread(() -> {
                int count=counterService.incrementCounter();
                Platform.runLater(()->numberOfPuzzlesSolvedLabel.setText(String.valueOf(count)));
            }).start();
        }

        // move bombs
        for(Bomb bomb:bombs)
        {

            bomb.incrementY();

            if(bomb.getY()>500)
            {
                collidedBombs.add(bomb);
                collided=true;
                isExploding=true;
                bombToBeRemoved=bomb;
                --bombCount;
            }
        }
        if(bombToBeRemoved!=null)
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


    private void renderGame() {

        int catYPos = (int) gameCanvas.getHeight() - 100;

        // Clear the canvas to redraw the next frame
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Draw each cat with the current tail frame
        for (int i = 0; i < catXPosition.size(); i++) {
            int catXPos = catXPosition.get(i);
            gc.drawImage(catFrames.get(currentCatFrame), catXPos, catYPos, 100, 100);
        }

        if(isExploding)
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
        if(gameOver)
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
            answerButtons[i].setOnAction(actionEvent -> handleAnswerButtonClick(actionEvent));
        }
    }

    private void handleAnswerButtonClick(ActionEvent event)
    {
        Button clickedButton = (Button) event.getSource();
        int number = Integer.parseInt(clickedButton.getText());
        if(currentPuzzle.checkAnswer(number))
        {
            new Thread(() -> {
                currentPuzzle=puzzleService.fetchNewPuzzle();
                puzzleImage.setImage(currentPuzzle.getPuzzleImage());
            }).start();

            foundAnswer=true;
        }
        else
        {
            System.out.println("wrong answer");
        }

    }

    private void incrementScore()
    {
        currentScore=Integer.parseInt(currentScoreLabel.getText());
        ++currentScore;
        currentScoreLabel.setText(String.valueOf(currentScore));
    }

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
    }

}
