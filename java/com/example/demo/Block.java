package com.example.demo;
public class Block {
    private int ID;
    private String name;
    private double right;
    private double left;
    private double up;
    private double down;
    int inputsNum;
    int outputsNum;

    public Block(int ID, String name, double left, double up, double right, double down, int inputsNum, int outputsNum) {
        this.ID = ID;
        this.name = name;
        this.right = right;
        this.left = left;
        this.up = up;
        this.down = down;
        this.inputsNum = inputsNum;
        this.outputsNum = outputsNum;
    }
}
