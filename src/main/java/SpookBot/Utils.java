package SpookBot;

import org.w3c.dom.Document;

import javax.security.auth.login.LoginException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Logger;

import static SpookBot.Main.loggingService;

public class Utils {
    private static String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "SpookBotSettings";
    private static File file = new File(path + File.separator + "config.xml");

    public static Document getConfiguration() {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        Document document = null;

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (Exception d) {
            loggingService.severe(d.getMessage());
        }

        try {
            document = documentBuilder.parse(file);
        } catch (Exception e) {
            loggingService.severe(e.getMessage());
        }
        return document;
    }
    public static boolean setConfiguration(Document document) {
        boolean success = false;
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            //transformer.setOutputProperties(OutputKeys.INDENT, "yes");

            transformer.transform(new DOMSource(document), new StreamResult(new FileOutputStream(file)));
            success = true;
        } catch (TransformerException te) {
            loggingService.severe(te.getMessage());
        } catch (IOException ioe) {
            loggingService.severe(ioe.getMessage());
        }
        return success;
    }
}
