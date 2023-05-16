package com.example.demo;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main extends Application {
    static private List<Block> blocks = new ArrayList<Block>(); // for the blocks
    static private List<Arrow> connections = new ArrayList<Arrow>(); // for the connections
    static private Group root = new Group();

    @Override
    public void start(Stage stage) throws IOException, ParserConfigurationException, SAXException {
        Scene scene = new Scene(root, 1500, 790);  // setting the width and height of the window
        stage.setTitle("Simulink viewer");               // setting title of the window

        Image image = new Image("1.png");
        ImageView imageView = new ImageView(image);
        stage.getIcons().add(imageView.getImage());     // setting icon of the program
        mdlParsing();                                   // parsing the mdl file
        drawBlocks();
        drawArrows();

        root.autosize();
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());   // setting the style of the window
        stage.setScene(scene);
        mouseEvents(scene, stage);
        stage.show();   // displaying
    }

    public static void mdlParsing() throws IOException, ParserConfigurationException, SAXException {

        File file = new File("Example.mdl");
        FileInputStream input = new FileInputStream(file);
        StringBuilder mdlFile = new StringBuilder();
        int x;
        while ((x = input.read()) != -1) {
            mdlFile.append((char) x);
        }
        String mdlFileS = mdlFile.toString();
        Scanner scanner = new Scanner(mdlFileS);
        StringBuilder a = new StringBuilder();
        String before = "-1";
        boolean now = false;
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.equals("<System>")) {
                now = true;
            } else if (line.equals("</System>")) {
                a.append(before + '\n');
                a.append(line);
                break;
            }
            if (now)
                a.append(before + '\n');
            before = line;
        }
        String newMdlFile = a.toString();
        String outputFileName = "neededFile.mdl";
        FileOutputStream outputStream = new FileOutputStream(outputFileName);
        outputStream.write(newMdlFile.getBytes());


        file = new File("neededFile.mdl");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        Element rootElement = doc.getDocumentElement();
        doc.getDocumentElement().normalize();


        addBlocks(rootElement, doc);
        addArrows();
    }

    public static void addBlocks(Element rootElement, Document doc) {
        if (rootElement.getTagName().equals("System")) {
            NodeList blockList = doc.getElementsByTagName("Block");
            for (int i = 0; i < blockList.getLength(); i++) {
                boolean inputs_ports_position_flag = false; // will be used to check if the block had a input ports number or not
                boolean blockMirror = false; // will be used to check if the block input and output will be mirrored or not
                Node blockNode = blockList.item(i);
                NodeList childNodes = blockNode.getChildNodes();
                if (blockNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element blockElement = (Element) blockNode;

                    //Extracting the information from the block tag
                    String Name = blockElement.getAttribute("Name");
                    String BlockType = blockElement.getAttribute("BlockType");
                    int ID = (Integer.parseInt(blockElement.getAttribute("SID")));

                    //this part parse the position string to extract the 4 coordinates of the block and handles weather it's on index 0 or index 1
                    double value1 = 0, value2 = 2, value3 = 0, value4 = 0;
                    for (int j = 0; j < blockElement.getElementsByTagName("P").getLength(); j++) {
                        if (blockElement.getElementsByTagName("P").item(j).getAttributes().item(0).getTextContent().equals("Position")) {
                            String Position = blockElement.getElementsByTagName("P").item(j).getTextContent();
                            Position = Position.replace("[", "").replace("]", ""); // Remove square brackets
                            String[] strValues = Position.split(","); // Split by comma
                            value1 = Double.parseDouble(strValues[0]);
                            value2 = Double.parseDouble(strValues[1]);
                            value3 = Double.parseDouble(strValues[2]);
                            value4 = Double.parseDouble(strValues[3]);
                        }
                    }


                    // this part extract the number of input and output ports and the flag is used to
                    int NumInputPorts = 1, NumOutputPorts = 1;
                    if (inputs_ports_position_flag) {
                        String ports = blockElement.getElementsByTagName("P").item(0).getTextContent();
                        if (ports.length() == 6) {
                            NumInputPorts = ports.charAt(1) - '0';
                            NumOutputPorts = ports.charAt(4) - '0';
                        } else if (ports.length() == 3) {
                            NumInputPorts = ports.charAt(1) - '0';
                            NumOutputPorts = 0;
                        }
                    }

                    for (int j = 0; j < blockElement.getElementsByTagName("P").getLength(); j++) {
                        if (blockElement.getElementsByTagName("P").item(j).getAttributes().item(0).getTextContent().equals("BlockMirror")) {
                            if (blockElement.getElementsByTagName("P").item(j).getTextContent().equals("on"))
                                blockMirror = true;
                        }
                    }
                    String inputs = ""; // for Add class ( signs )
                    String value = ""; // for constant class ( value )

                    Block b = switch (BlockType) {
                        case "Saturate" ->
                                new Saturation(ID, Name, value1, value2, value3, value4, NumInputPorts, NumOutputPorts, blockMirror);
                        case "UnitDelay" ->
                                new UnitDelay(ID, Name, value1, value2, value3, value4, NumInputPorts, NumOutputPorts, blockMirror);
                        case "Scope" ->
                                new Scope(ID, Name, value1, value2, value3, value4, NumInputPorts, NumOutputPorts, blockMirror);
                        case "Sum" ->
                                new Add(ID, Name, value1, value2, value3, value4, NumInputPorts, NumOutputPorts, blockMirror, inputs);
                        case "Constant" ->
                                new Constant(ID, Name, value1, value2, value3, value4, NumInputPorts, NumOutputPorts, blockMirror, value);
                        default ->
                                new Block(ID, Name, value1, value2, value3, value4, NumInputPorts, NumOutputPorts, blockMirror);
                    };

                    blocks.add(b);
                }
            }
        }
    }

    public static void addArrows() {
        Arrow a1 = new Arrow(5, 1, 0);
        a1.addDest(1, 0);
        connections.add(a1);
        Arrow a2 = new Arrow(1, 1, 44);
        a2.addDest(3, 0);
        a2.addDest(3, 10);
        connections.add(a2);
        Arrow a3 = new Arrow(3, 1, 40);
        a3.addDest(4, 75);
        a3.addDest(7, 0);
        connections.add(a3);
        Arrow a4 = new Arrow(4, 1, -8);
        a4.addDest(3, -65);
        connections.add(a4);
    }

    public static void drawBlocks() {
        for (Block b : blocks) {
            b.addBlock(root);
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

    public static void mouseEvents(Scene scene, Stage stage) {
        scene.setOnScroll(e -> {
            double zoomFactor = 1.05;
            double deltaY = e.getDeltaY();

            if (deltaY < 0) {
                zoomFactor = 0.95;
            }
            root.setScaleX(root.getScaleX() * zoomFactor);
            root.setScaleY(root.getScaleY() * zoomFactor);
        });
    }
}
