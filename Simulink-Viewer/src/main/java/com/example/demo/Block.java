package com.example.demo;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Block {
    private int ID;
    private String name;
    private double right;
    private double left;
    private double up;
    private double down;
    private boolean mirror;
    private int inputsNum;
    private int outputsNum;
    Label container;
    Label lName;

    public Block(int ID, String name, double left, double up, double right, double down, int inputsNum, int outputsNum, boolean mirror) {
        this.ID = ID;
        this.name = name;
        this.right = right;
        this.left = left;
        this.up = up;
        this.down = down;
        this.inputsNum = inputsNum;
        this.outputsNum = outputsNum;
        this.mirror = mirror;


        container = new Label();
        container.setId("LabelShape");
        container.setLayoutX(left);
        container.setLayoutY(up);
        container.setMinSize(right - left, down - up);


        lName = new Label(name);
        HBox h = new HBox();
        h.getChildren().add(lName);
        Scene s = new Scene(h);
        lName.applyCss();
        lName.setId("LabelName");
        lName.setLayoutX((left  + (right - left)/ 2.0) - (lName.prefWidth(-1) / 2.0));
        lName.setLayoutY(down);
    }

    public int getID() {
        return ID;
    }

    public String getName() {
        return name;
    }

    public double getLeft() {
        return left;
    }

    public double getUp() {
        return up;
    }

    public double getDown() {
        return down;
    }

    public double getWidth() {
        return (right - left);
    }

    public double getHeight() {
        return (down - up);
    }

    public int getInputsNum() {
        return inputsNum;
    }

    public int getOutputsNum() {
        return outputsNum;
    }

    public double getRight() {
        return right;
    }

    public boolean isMirror() {
        return mirror;
    }

    public Label getContainer() {
        return container;
    }

    public Label getlName() {
        return lName;
    }
}
