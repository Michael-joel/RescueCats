package com.example.rescuecats.Model;

import javafx.scene.image.Image;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class Puzzle {

    private Image puzzleImage;
    private int solution;
    CompletableFuture<HttpResponse<String>> response = null;


    public Puzzle(Image puzzleImage, int solution) {
        this.puzzleImage = puzzleImage;
        this.solution = solution;
    }

    public Image getPuzzleImage() {
        return puzzleImage;
    }

    public void setPuzzleImage(Image puzzleImage) {
        this.puzzleImage = puzzleImage;
    }

    public int getSolution() {
        return solution;
    }

    public void setSolution(int solution) {
        this.solution = solution;
    }

    public boolean checkAnswer(int givenAnswer)
    {
        return givenAnswer == solution;
    }

}
