package Timetable;

import SpookBot.Main;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.*;

import javax.xml.parsers.*;
import java.io.IOException;
import java.io.StringReader;
import java.net.http.*;

public class XmlToJson {
    public static JSONObject ToJSON(HttpResponse<String> response) {
        // Debug, will be removed, when done.
        Main.loggingService.info(response.body());

        // Document Builder
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            Main.loggingService.severe(String.valueOf(e));
        }

        // Parse XML
        InputSource inStream = new InputSource();
        inStream.setCharacterStream(new StringReader(response.body()));
        Document document = null;
        try {
            document = builder.parse(inStream);
        } catch (SAXException | IOException e) {
            Main.loggingService.severe(String.valueOf(e));
        }

        // Get Root Element of XML
        Element root = document.getDocumentElement();

        // Convert XML to a JSON Object
        JSONObject timetable = new JSONObject();
        timetable.put("station", root.getAttribute("station"));
        // Array für Services
        JSONArray servicesArray = new JSONArray();

        // Get all Services
        NodeList servicesNodeList = root.getChildNodes();
        for (int i = 0; i < servicesNodeList.getLength(); i++) {
            Node serviceNode = servicesNodeList.item(i);
            String serviceId = "null";
            String serviceNumber = "null";
            String serviceLine = "null";
            String serviceType = "null";
            String serviceDestination = "null";

            if (serviceNode.getNodeType() == Node.ELEMENT_NODE) {
                Element serviceElement = (Element) serviceNode;
                serviceId = serviceElement.getAttribute("id");

                NodeList serviceDataNodeList = serviceElement.getChildNodes();

                for (int j = 0; j < serviceDataNodeList.getLength(); j++) {
                    Node serviceDataNode = serviceDataNodeList.item(j);

                    if (serviceDataNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element serviceDataElement = (Element) serviceDataNode;

                        if (!serviceDataElement.getAttribute("n").isEmpty()) {
                            serviceNumber = serviceDataElement.getAttribute("n");
                        }
                        if (!serviceDataElement.getAttribute("l").isEmpty()) {
                            serviceLine = serviceDataElement.getAttribute("l");
                        }
                        if (!serviceDataElement.getAttribute("c").isEmpty()) {
                            serviceType = serviceDataElement.getAttribute("c");
                        }
                    }
                }
            }
            // Create JSON Object
            JSONObject service = new JSONObject();
            service.put("id", serviceId);
            service.put("number", serviceNumber);
            service.put("line", serviceLine);
            service.put("type", serviceType);
            service.put("destination", serviceDestination);
            service.put("track", "2");
            servicesArray.put(service);
        }

        timetable.put("services", servicesArray);

        // Return JSON Object
        return timetable;
    }
}
