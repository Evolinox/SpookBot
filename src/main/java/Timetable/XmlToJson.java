package Timetable;

import SpookBot.Main;

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
        } catch (SAXException e) {
            Main.loggingService.severe(String.valueOf(e));
        } catch (IOException e) {
            Main.loggingService.severe(String.valueOf(e));
        }

        // Get Root Element of XML
        Element root = document.getDocumentElement();

        // Convert XML to a JSON Object
        JSONObject timetable = new JSONObject();
        timetable.put("station", root.getAttribute("station"));

        // Return JSON Object
        return timetable;
    }
}
