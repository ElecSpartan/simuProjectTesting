package com.example.demo;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
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
            container.setId("container");
            container.setLayoutX(left);
            container.setLayoutY(up);
            container.setMinSize(width, height);

            // for the name
            Label name = new Label(b.getName());
            name.setId("name");
            name.setLayoutX(left);
            name.setLayoutY(down + 10);   // 10 (trial and error)
            name.setMinSize(width, height);

            root.getChildren().addAll(container, name);
        }
    }
    public static void drawArrows() {

    }
}