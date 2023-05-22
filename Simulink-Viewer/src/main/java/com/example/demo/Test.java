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
                int q;
                while ((q = input.read()) != -1) {
                    s.append((char) q);
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
                            NodeList branchList = lineElement.getElementsByTagName("Branch");
                            // looping on the tags to find srcID srcPlace
                            int srcId=0,srcPlace=0;
                            for (int j = 0; j < lineElement.getElementsByTagName("P").getLength(); j++) {
                                if(lineElement.getElementsByTagName("P").item(j).getAttributes().item(0).getTextContent().equals("Src")){
                                    String src=(lineElement.getElementsByTagName("P").item(j).getTextContent());
                                    String srcIdInfo=""; String srcPlaceInfo="";
                                    for(int k=0;k<src.length();k++){
                                        if(src.charAt(k)=='#')
                                            break;
                                        else
                                            srcIdInfo+=src.charAt(k);
                                    }
                                    int idx=-1;
                                    for(int k=0;k<src.length();k++){
                                        if(src.charAt(k)==':') {
                                            idx=k+1;
                                            break;
                                        }
                                    }
                                    for(int k=idx;k<src.length();k++)
                                        srcPlaceInfo+=src.charAt(k);
                                    srcId = Integer.parseInt(srcIdInfo);
                                    srcPlace = Integer.parseInt(srcPlaceInfo);
                                    System.out.println(srcId+" "+srcPlace);
                                }
                            }

                            // dstID if there is no branches
                            if(branchList.getLength()==0){
                                int destId=0 ;
                                for (int j = 0; j < lineElement.getElementsByTagName("P").getLength(); j++) {
                                    if(lineElement.getElementsByTagName("P").item(j).getAttributes().item(0).getTextContent().equals("Dst")){
                                        String dst=(lineElement.getElementsByTagName("P").item(j).getTextContent());
                                        String dstIdInfo="";
                                        for(int k=0;k<dst.length();k++){
                                            if(dst.charAt(k)=='#')
                                                break;
                                            else
                                                dstIdInfo+=dst.charAt(k);
                                        }
                                        destId = Integer.parseInt(dstIdInfo);
                                        //System.out.println(destId);
                                    }
                                }

                                //getting x and y of the line if there is no branches
                                double x=0,y=0;
                                String points="";
                                if(getPIndexByName(lineElement, "Points")!=-1)
                                    points = lineElement.getElementsByTagName("P").item(getPIndexByName(lineElement, "Points")).getTextContent();
                                if(points.length()>0) {
                                    String pointInfo="";
                                    for(int k=1;k<points.length();k++){
                                        if(points.charAt(k)==',')
                                            break;
                                        else
                                            pointInfo+=points.charAt(k);
                                    }
                                    x=Double.parseDouble(pointInfo);
                                    Arrow arrow = new Arrow(srcId,srcPlace,x);
                                    //to check if the string has y coordinations
                                    boolean yFlag=false;
                                    for(int k=0;k<points.length();k++){
                                        if(points.charAt(k)==';') yFlag=true;
                                    }
                                    if(yFlag){
                                        int index=-1;
                                        String yString = "";
                                        for(int k=points.length()-2;k>=0;k--){
                                            if(points.charAt(k)==' ') {
                                                index=k+1;
                                                break;
                                            }
                                        }
                                        for(int k=index;k<points.length()-1;k++)  yString+=points.charAt(k);
                                        y=Double.parseDouble(yString);
                                        arrow.addDest(destId,y);
                                    }
                                    else
                                        arrow.addDest(destId,y);
                                }
                                System.out.println(destId);
                                System.out.println(x+" "+y);
                            }

                        }
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    private static int getPIndexByName(Element element, String name) {
        NodeList pList = element.getElementsByTagName("P");
        for (int i = 0; i < pList.getLength(); i++) {
            Element pElement = (Element) pList.item(i);
            if (pElement.getAttribute("Name").equals(name)) {
                return i;
            }
        }
        return -1; // Not found
    }
}
