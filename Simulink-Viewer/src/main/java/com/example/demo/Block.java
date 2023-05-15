package com.example.demo;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

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
        lName.setLayoutX((left + (right - left) / 2.0) - (lName.prefWidth(-1) / 2.0));
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

    public void addBlock(Pane root) {
        root.getChildren().addAll(container,lName);
    }
}

class Saturation extends Block {
    static Image image = new Image("saturate.png");
    ImageView imgview;


    public Saturation(int ID, String name, double left, double up, double right, double down, int inputsNum, int outputsNum, boolean mirror) {
        super(ID, name, left, up, right, down, inputsNum, outputsNum, mirror);
        imgview = new ImageView(image);
    }

    @Override
    public void addBlock(Pane root) {
        super.addBlock(root);
        imgview.setX(super.getLeft() + 4);
        imgview.setY(super.getUp() + 4);
        imgview.setFitWidth(getWidth() - 8);
        imgview.setFitHeight(getHeight() - 8);

        root.getChildren().add(imgview);
    }
}
class UnitDelay extends Block {
    static Image image = new Image("ud.png");
    ImageView imgview;


    public UnitDelay(int ID, String name, double left, double up, double right, double down, int inputsNum, int outputsNum, boolean mirror) {
        super(ID, name, left, up, right, down, inputsNum, outputsNum, mirror);
        imgview = new ImageView(image);
    }

    @Override
    public void addBlock(Pane root) {
        super.addBlock(root);
        imgview.setX(super.getLeft() + 4);
        imgview.setY(super.getUp() + 4);
        imgview.setFitWidth(getWidth() - 8);
        imgview.setFitHeight(getHeight() - 8);
        root.getChildren().add(imgview);
    }
}
class Scope extends Block {
    static Image image = new Image("scope.png");
    ImageView imgview;

    public Scope(int ID, String name, double left, double up, double right, double down, int inputsNum, int outputsNum, boolean mirror) {
        super(ID, name, left, up, right, down, inputsNum, outputsNum, mirror);
        imgview = new ImageView(image);
    }

    @Override
    public void addBlock(Pane root) {
        super.addBlock(root);
        imgview.setX(super.getLeft() + 4);
        imgview.setY(super.getUp() + 4);
        imgview.setFitWidth(getWidth() - 8);
        imgview.setFitHeight(getHeight() - 8);
        root.getChildren().add(imgview);
    }
}

