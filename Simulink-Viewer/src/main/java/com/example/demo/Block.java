package com.example.demo;
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

    public Block() {

    }
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
}
