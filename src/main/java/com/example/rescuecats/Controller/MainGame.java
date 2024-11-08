package com.example.rescuecats.Controller;

import com.example.rescuecats.Model.Bomb;
import javafx.animation.AnimationTimer;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;


import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

public class MainGame implements Initializable {


    public ImageView puzzleImage;
    public Label highScore;
    public Label score;
    public Canvas gameCanvas;
    private GraphicsContext gc;


    private ArrayList<Integer> catXPosition = new ArrayList<>();
    private ArrayList<Integer> bombSpawnPoints = new ArrayList<>();
    private ArrayList<Image> Frames = new ArrayList<>();
    private ArrayList<Bomb> bombs = new ArrayList<>();

    private Image bombImage;

    private final Random random = new Random();
    private long lastUpdate=0;
    private long lastAnimationUpdate=0;
    private int currentFrame=0;
    private int bombCount=0;
    private int bombSpawnCooldown=0;
    private final int BOMB_SPAWN_DELAY=10;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        gc=gameCanvas.getGraphicsContext2D();

        // Load cat and bomb images
        bombImage = new Image(getClass().getResourceAsStream("/images/bomb.png"));

        // Load all the tail animation frames into tailFrames
        for (int i = 1; i <= 3; i++) {
            Frames.add(new Image(getClass().getResourceAsStream("/images/cat" + i + ".png")));
        }
        initializeCats();
        startGameLoop();

    }

    private void initializeCats() {
//code refrenced from https://docs.oracle.com/javase/8/javafx/api/javafx/scene/canvas/GraphicsContext.html#drawImage-javafx.scene.image.Image-double-double-double-double-
        int catYPos= (int)gameCanvas.getHeight()-100;
        System.out.println("first"+catYPos+"second"+gameCanvas.getHeight());
        for (int i = 0; i < 4; i++) {
            int catXPos = 20 + i * 110;
            catXPosition.add(catXPos); // Store each cat's X position
            gc.drawImage(Frames.get(0), catXPos, catYPos, 100, 100); // Draw the first frame initially
        }

        for (int i = 0; i <catXPosition.size() ; i++) {
            bombSpawnPoints.add(catXPosition.get(i)+10);
        }
    }

    public void startGameLoop() {
        AnimationTimer gameLoop = new AnimationTimer() { // code obtained from https://code.tutsplus.com/introduction-to-javafx-for-game-development--cms-23835t
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 16_000_000) { // Roughly 5 FPS
                    updateBomb();
                    lastUpdate = now;
                }

                if (now - lastAnimationUpdate >= 200_000_000) { // Roughly 5 FPS
                    updateGame();
                    lastAnimationUpdate = now;
                }
                renderGame();
            }
        };
        gameLoop.start();
    }

    private void updateGame() {
        currentFrame = (currentFrame + 1) % Frames.size(); // Cycle through the frames
    }

    private void updateBomb()
    {
        if(bombCount!=4)
            spawnBomb();

        // move bombs
        for(Bomb bomb:bombs)
        {
            bomb.incrementY();
        }
    }


    private void renderGame() {
        int catYPos = (int) gameCanvas.getHeight() - 100;

        // Clear the canvas to redraw the next frame
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());

        // Draw each cat with the current tail frame
        for (int i = 0; i < catXPosition.size(); i++) {
            int catXPos = catXPosition.get(i);
            gc.drawImage(Frames.get(currentFrame), catXPos, catYPos, 100, 100);
        }

        for(Bomb bomb:bombs)
        {
            gc.drawImage(bombImage, bomb.getX(), bomb.getY(),80,80);
        }

    }

    private void spawnBomb()
    {
        if(bombSpawnCooldown>0)
        {
            --bombSpawnCooldown;
        }
        else
        {
            int chosenSpawnPoint= random.nextInt(4);
            System.out.println(chosenSpawnPoint);
            bombs.add(new Bomb(bombSpawnPoints.get(chosenSpawnPoint), 0));
            ++bombCount;
            bombSpawnCooldown=BOMB_SPAWN_DELAY;
        }

    }

}
