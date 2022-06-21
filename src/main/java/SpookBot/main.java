package SpookBot;

import Commands.inspector;
import Commands.messages;
import Commands.music;
import Commands.rules;

import Swing.SpookOS;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.w3c.dom.Document;

import javax.security.auth.login.LoginException;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class main {

    private static JDA spookBot = null;

    public static void main(String[] args) throws LoginException {

        JFrame frame = new SpookOS("SpookOS", 500, 400);
        frame.setVisible(true);

        startSpookBot();

    }

    public static void setActivity(String activity) {
        spookBot.getPresence().setActivity(Activity.playing(activity));
    }

    public static void startSpookBot() throws LoginException {

        String token = null;
        String activity = null;

        File file = new File("src/main/java/SpookBot/app.xml");

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        Document document = null;

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (Exception d) {
            d.printStackTrace();
        }

        try {
            document = documentBuilder.parse(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        token = document.getElementsByTagName("botToken").item(0).getTextContent();
        activity = document.getElementsByTagName("botActivity").item(0).getTextContent();

        JDABuilder bot = JDABuilder.createDefault(token);
        bot.setStatus(OnlineStatus.ONLINE);
        bot.setActivity(Activity.playing(activity));
        bot.enableCache(CacheFlag.VOICE_STATE);

        bot.addEventListeners(new messages());
        bot.addEventListeners(new rules());
        bot.addEventListeners(new inspector());
        bot.addEventListeners(new music());

        JDA SpookBot = bot.build();

        spookBot = SpookBot;

    }



}
