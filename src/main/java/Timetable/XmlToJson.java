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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
            String servicePlatform = "null";
            String serviceDepartureTime = "null";
            Boolean serviceEnding = true;

            if (serviceNode.getNodeType() == Node.ELEMENT_NODE) {
                Element serviceElement = (Element) serviceNode;
                serviceId = serviceElement.getAttribute("id");

                NodeList serviceDataNodeList = serviceElement.getChildNodes();

                for (int j = 0; j < serviceDataNodeList.getLength(); j++) {
                    Node serviceDataNode = serviceDataNodeList.item(j);

                    if (serviceDataNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element serviceDataElement = (Element) serviceDataNode;

                        // Check, if service is ending here
                        if (Objects.equals(serviceDataElement.getTagName(), "dp")) {
                            serviceEnding = false;
                            String[] stops = serviceDataElement.getAttribute("ppth").split("\\|");
                            serviceDestination = stops[stops.length - 1];
                        }

                        if (!serviceDataElement.getAttribute("n").isEmpty()) {
                            serviceNumber = serviceDataElement.getAttribute("n");
                        }
                        if (!serviceDataElement.getAttribute("l").isEmpty()) {
                            serviceLine = serviceDataElement.getAttribute("l");
                        }
                        if (!serviceDataElement.getAttribute("c").isEmpty()) {
                            serviceType = serviceDataElement.getAttribute("c");
                        }
                        if (!serviceDataElement.getAttribute("pp").isEmpty()) {
                            servicePlatform = serviceDataElement.getAttribute("pp");
                        }
                        if (!serviceDataElement.getAttribute("pt").isEmpty()) {
                            serviceDepartureTime = serviceDataElement.getAttribute("pt").substring(6);
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
            service.put("track", servicePlatform);
            service.put("departure", serviceDepartureTime);
            service.put("ending", serviceEnding);
            servicesArray.put(service);
        }

        // Sort JSONArray
        JSONArray sortedArray = sortJSONArrayByKey(servicesArray, "departure");
        timetable.put("services", sortedArray);

        // Return JSON Object
        return timetable;
    }

    public static JSONArray sortJSONArrayByKey(JSONArray array, String key) {
        // Convert JSONArray to a List of JSONObjects for sorting
        List<JSONObject> jsonObjects = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            jsonObjects.add(array.getJSONObject(i));
        }

        // Sort the List by the specified key
        jsonObjects.sort(Comparator.comparing(o -> o.optString(key)));

        // Convert back to JSONArray
        JSONArray sortedArray = new JSONArray();
        for (JSONObject jsonObject : jsonObjects) {
            sortedArray.put(jsonObject);
        }

        return sortedArray;
    }
}
