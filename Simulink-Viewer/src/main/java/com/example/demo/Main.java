package com.example.demo;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {
    static private List<Block> blocks = new ArrayList<Block>(); // for the blocks
    static private List<Arrow> connections = new ArrayList<Arrow>(); // for the connections
    static private Pane root = new Pane();

    @Override
    public void start(Stage stage) throws IOException {
        Scene scene = new Scene(root, 1500, 790);
        stage.setTitle("Simulink viewer");

        mdlParsing();
        drawBlocks();
        drawArrows();

        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void mdlParsing() {
         //  code for experiment only
        // place the real code for parsing here

        Block one = new Block(5, "Constant", 780, 200, 810, 230, 1, 1, false);
        blocks.add(one);
        Block two = new Block(1, "Saturation", 935, 200, 965, 230, 1, 1, false);
        blocks.add(two);
        Block three = new Block(3, "ADD", 1040, 209, 1070, 241, 3, 1, false);
        blocks.add(three);
        Block four = new Block(7, "Scope", 1130, 209, 1160, 241, 1, 0, false);
        blocks.add(four);
        Block five = new Block(4, "Unit delay", 1040, 283, 1075, 317, 1, 1, true);
        blocks.add(five);

        Arrow a1 = new Arrow(5, 1, 0);
        a1.addDest(1, 0);
        connections.add(a1);
        Arrow a2 = new Arrow(1, 1, 44);
        a2.addDest(3, 0);
        a2.addDest(3, 10);
        connections.add(a2);
        Arrow a3 = new Arrow(3,1,40);
        a3.addDest(4,75);
        a3.addDest(7,0);
        connections.add(a3);
        Arrow a4 = new Arrow(4,1,-8);
        a4.addDest(3,-65);
        connections.add(a4);
    }

    public static void drawBlocks() {
        for (Block b : blocks) {
            double width = b.getWidth();
            double height = b.getHeight();
            double left = b.getLeft();
            double up = b.getUp();
            double down = b.getDown();

            // for the container
            Label container = new Label();
            container.setId("LabelShape");
            container.setLayoutX(left);
            container.setLayoutY(up);
            container.setMinSize(width, height);

            // for the name
            Label name = new Label(b.getName());
            name.setMinWidth(1500);
            name.setId("LabelName");
            root.getChildren().addAll(container, name);
            name.setLayoutX((left + width / 2.0) - (name.getMinWidth() / 2.0));
            name.setLayoutY(down);
        }
    }

    public static void drawArrows() {
        double arrowSize = 5;
        for (Arrow a : connections) {
            Block b;
            b = getBlock(a.getScrId());
            double left = b.getLeft();
            double width = b.getWidth();
            double startX = left;
            if (!b.isMirror())
                startX = left + width;
            double div = b.getHeight() / (b.getOutputsNum() + 1);
            double startY = b.getUp() + a.getSrcPlace() * div;
            Line l = new Line();
            l.setStartX(startX);
            l.setStartY(startY);
            double endX = startX + a.getX();
            double endY = startY;
            l.setEndX(endX);
            l.setEndY(endY);
            root.getChildren().add(l);
            startX = endX;
            startY = endY;
            double sX = startX;
            double sY = startY;
            if (a.getDestsSize() > 1) {
                Circle c = new Circle(startX, startY, 3);
                root.getChildren().add(c);
            }

            for (int i = 0; i < a.getDestsSize(); i++) {
                endX = startX;
                endY = startY + a.getY(i);
                Line l2 = new Line(startX, startY, endX, endY);
                root.getChildren().add(l2);
                startX = endX;
                startY = endY;
                endY = startY;
                b = getBlock(a.getDestId(i));
                endX = b.getLeft();
                if (b.isMirror())
                    endX += b.getWidth();

                Line l3 = new Line(startX, startY, endX, endY);
                root.getChildren().add(l3);

                if (!b.isMirror())
                    root.getChildren().add(new Polygon(endX, endY, endX - arrowSize, endY + arrowSize, endX - arrowSize, endY - arrowSize));
                else
                    root.getChildren().add(new Polygon(endX, endY, endX + arrowSize, endY + arrowSize, endX + arrowSize, endY - arrowSize));

                startX = sX;
                startY = sY;
            }

        }
    }
    public static Block getBlock(int id) {
        for (Block b : blocks) {
            if (b.getID() == id)
                return b;
        }
        return null;
    }
}