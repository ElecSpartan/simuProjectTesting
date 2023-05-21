package com.example.demo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Scanner;

public class Test {
        public static void main(String[] args) {
            try {

                // this part handles that the mdl file has more than xml file so we just take the <system>.....</system> information.
                File file = new File("Example.mdl");
                FileInputStream input = new FileInputStream(file);
                StringBuilder s = new StringBuilder();
                int x;
                while ((x = input.read()) != -1) {
                    s.append((char) x);
                }
                String code = s.toString();
                Scanner scanner = new Scanner(code);
                StringBuilder a = new StringBuilder();
                String before = "-1";
                boolean now = false;
                while(scanner.hasNextLine()) {
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
                String ss = a.toString();

                String outputFileName = "one.mdl";
                FileOutputStream outputStream = new FileOutputStream(outputFileName);
                outputStream.write(ss.getBytes());


                file = new File("one.mdl");
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(file);
                Element rootElement = doc.getDocumentElement();
                // Optional: normalize the XML structure
                doc.getDocumentElement().normalize();
                if(rootElement.getTagName().equals("System")){
                    NodeList lineList = doc.getElementsByTagName("Line");
                    for (int i = 0; i < lineList.getLength(); i++) {
                        Node lineNode = lineList.item(i);
                        //NodeList childNodes = lineNode.getChildNodes();
                        if (lineNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element lineElement = (Element) lineNode;

                            // looping on the tags to find srcID srcPlace
                            int srcId=0,srcPlace=0;
                            for (int j = 0; j < lineElement.getElementsByTagName("P").getLength(); j++) {
                                if(lineElement.getElementsByTagName("P").item(j).getAttributes().item(0).getTextContent().equals("Src")){
                                    /*srcId = Integer.parseInt*/
                                    String srcInfo=(lineElement.getElementsByTagName("P").item(j).getTextContent());
                                    srcId=srcInfo.charAt(0)-'0';
                                    srcPlace=srcInfo.charAt(6)-'0';
                                    System.out.println(srcId+" "+srcPlace);
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
}
