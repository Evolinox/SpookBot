package SpookBot;

import Commands.*;

import Swing.SpookOS;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.w3c.dom.Document;

import javax.security.auth.login.LoginException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class main {

    private static JDA spookBot = null;
    public static SpookOS spookOS = null;

    public static String version = "v0.3.0-alpha";

    public static void main(String[] args) throws LoginException, IOException {

        String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "SpookBotSettings";
        File configPath = new File(path);
        File config = new File(path + File.separator + "config.xml");
        InputStream appSource = main.class.getClassLoader().getResourceAsStream("config.xml");

        if (configPath.exists()) {
            if (!config.isFile()) {
                Files.copy(appSource, Path.of(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "SpookBotSettings" + File.separator + "config.xml"));
            }

        } else if (configPath.mkdirs()) {
            Files.copy(appSource, Path.of(System.getProperty("user.home") + File.separator + "Documents" + File.separator + "SpookBotSettings" + File.separator + "config.xml"));
        } else {
            System.out.println("nope");
        }

        spookOS = new SpookOS("SpookOS " + version, 500, 400);

        startSpookBot();

    }

    public static void setActivity(String activity) {
        spookBot.getPresence().setActivity(Activity.playing(activity));
        spookOS.writeToConsole("Bot Activity set to " + activity);
    }

    public static void stopSpookBot() throws LoginException {

        spookBot = null;
        spookOS.writeToConsole("SpookBot exited");

        startSpookBot();

    }

    public static void startSpookBot() throws LoginException {

        String token = "base";
        String activity = "base";

        String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "SpookBotSettings";
        File file = new File(path + File.separator + "config.xml");

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        Document document = null;

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (Exception d) {
            spookOS.writeToConsole(d.getMessage());
        }

        try {
            document = documentBuilder.parse(file);
        } catch (Exception e) {
            spookOS.writeToConsole(e.getMessage());
        }

        token = document.getElementsByTagName("botToken").item(0).getTextContent();
        activity = document.getElementsByTagName("botActivity").item(0).getTextContent();

        JDABuilder bot = JDABuilder.createDefault(token);
        bot.setStatus(OnlineStatus.ONLINE);
        bot.setActivity(Activity.playing(activity));
        bot.enableCache(CacheFlag.VOICE_STATE);
        bot.enableIntents(GatewayIntent.MESSAGE_CONTENT);

        bot.addEventListeners(new messages());
        bot.addEventListeners(new rules());
        bot.addEventListeners(new inspector());
        bot.addEventListeners(new music());
        bot.addEventListeners(new manager());
        bot.addEventListeners(new ask());
        bot.addEventListeners(new reddit());

        JDA SpookBot = bot.build();

        spookBot = SpookBot;
        spookOS.writeToConsole("SpookBot is running!");

    }

}
