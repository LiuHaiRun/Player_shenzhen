package com.ghts.player.parse;

import com.ghts.player.utils.LogUtil;
import com.ghts.player.utils.VeDate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by lijingjing on 17-9-21.
 */
public class XmlUpdate {

    public static boolean UpdateXmlFile(Document document, String filename) {
        boolean flag = true;
        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(filename));
            transformer.transform(source, result);
        } catch (Exception ex) {
            flag = false;
            ex.printStackTrace();
        }
        return flag;
    }

    public static Document load(String filename) {
        Document document = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            File file = new File(filename);
            document = builder.parse(file);
            document.normalize();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return document;
    }

    //更新信息
    public void alertXml(String filename) {
        try{
            Document document = load(filename);
            Element root = document.getDocumentElement();
                 NodeList stuNodeList = root.getElementsByTagName("upgrade");
                for (int i = 0; i < stuNodeList.getLength(); i++) {
                    Element stuElement = (Element) stuNodeList.item(i);
                    NodeList stuInforList = stuElement.getChildNodes();
                    for (int j = 0; j < stuInforList.getLength(); j++) {
                        Node stuInforNode = stuInforList.item(j);
                        if (stuInforNode.getNodeName().equals("item")) {
                            Element element = (Element) stuInforNode;
                             String type = stuInforNode.getAttributes().getNamedItem("type").getNodeValue();
                            if (type != null && type.equals("apk")) {
                                String file = stuInforNode.getAttributes().getNamedItem("file").getNodeValue();
                                String install = stuInforNode.getAttributes().getNamedItem("install").getNodeValue();
                                String updatetime = stuInforNode.getAttributes().getNamedItem("updatetime").getNodeValue();
                                LogUtil.e(type + "--" + file, install + "-" + updatetime);
                                //<item type="apk" file="/sata/upgrade/mplay.apk" install="0" updatetime=""/>
                                Element ele = (Element) stuInforNode;
                                ele.setAttribute("install", "0");
                                ele.setAttribute("updatetime", VeDate.getStringDate());

                                UpdateXmlFile(document, filename);
                            }
                        }
                    }
                }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}