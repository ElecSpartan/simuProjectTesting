package com.example.demo;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
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
        //  for experiment (blocks only)
        Block one = new Block(5, "Constant", 780, 200, 810, 230, 1, 1);
        blocks.add(one);
        Block two = new Block(1, "Saturation", 935, 200, 965, 230, 1, 1);
        blocks.add(two);
        Block three = new Block(3, "ADD", 1040, 209, 1070, 241, 3, 1);
        blocks.add(three);
        Block four = new Block(7, "Scope", 1130, 209, 1160, 241, 1, 0);
        blocks.add(four);
        Block five = new Block(4, "Unit delay", 1040, 283, 1075, 317, 1, 1);
        blocks.add(five);
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
            System.out.println(left + "  " + width/2 + "  " + name.getMinWidth()/2.0);
            name.setLayoutX((left + width/2.0) - (name.getMinWidth()/2.0) );
            name.setLayoutY(down);   // 10 (trial and error)

        }
    }
    public static void drawArrows() {

    }
}