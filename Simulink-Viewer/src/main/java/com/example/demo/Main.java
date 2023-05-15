package com.example.demo;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
    static private Pane root = new Pane();

    @Override
    public void start(Stage stage) throws IOException, ParserConfigurationException, SAXException {
        Scene scene = new Scene(root, 1500, 790);
        stage.setTitle("Simulink viewer");
        Image image = new Image("1.png");
        ImageView imageView = new ImageView(image);
        stage.getIcons().add(imageView.getImage());
        mdlParsing();
        drawBlocks();
        drawArrows();

        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    public static void mdlParsing() throws IOException, ParserConfigurationException, SAXException {

        // taked the needed part for gui in a seperate file
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
                    int ID = (Integer.parseInt(blockElement.getAttribute("SID")));

                    //this part parse the position string to extract the 4 coordinates of the block and handles weather it's on index 0 or index 1
                    String Position = blockElement.getElementsByTagName("P").item(0).getTextContent();
                    Position = Position.replace("[", "").replace("]", ""); // Remove square brackets
                    if (Position.length() <= 5) {
                        inputs_ports_position_flag = true;
                        Position = blockElement.getElementsByTagName("P").item(1).getTextContent();
                        Position = Position.replace("[", "").replace("]", ""); // Remove square brackets
                    }
                    String[] strValues = Position.split(","); // Split by comma
                    double left = Double.parseDouble(strValues[0]);
                    double up = Double.parseDouble(strValues[1]);
                    double right = Double.parseDouble(strValues[2]);
                    double down = Double.parseDouble(strValues[3]);

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

                    Block b = new Block(ID, Name, left, up, right, down, NumInputPorts, NumOutputPorts, blockMirror);
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