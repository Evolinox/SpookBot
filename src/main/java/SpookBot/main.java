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
import java.util.Arrays;
import java.util.Scanner;

public class main {

    static Scanner scanner = new Scanner(System.in);

    //set basic JDA Variables
    private static JDA spookBot = null;
    public static SpookOS spookOS = null;

    //set global Version String
    public static String version = "v0.3.0-alpha";

    //main class, basic code for the Bot
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

        if (Arrays.asList(args).contains("-g")) {
            //Create a Window with Title and x/y Size
            Integer xSize = 500, ySize = 400;
            spookOS = new SpookOS("SpookOS " + version, xSize, ySize);
        }

        if (Arrays.asList(args).contains("-s")) {
            //Code for Headless Setup
            headlessSetup();
        }

        startSpookBot();

    }

    public static void setActivity(Boolean Custom, String activity) {
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

        String base = document.getElementsByTagName("botActivity").item(0).getTextContent();

        if (Custom) {
            spookBot.getPresence().setActivity(Activity.playing(activity));
            spookOS.writeToConsole("Bot Activity set to " + activity);
        }
        else {
            spookBot.getPresence().setActivity(Activity.playing(base));
        }
    }

    public static void headlessSetup() {
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

        //CLI Stuff
        System.out.println("Enter your preferred Prefix ( Something like !s for example ) : ");
        String prefix = scanner.nextLine();
        System.out.println("Enter your Discord Bot Token ( From Developer Page ) : ");
        String token = scanner.nextLine();
        System.out.println("Enter your preferred Activity ( Something like SpookOS :P ) : ");
        String activity = scanner.nextLine();

        //Confirmation
        System.out.println("Please Confirm your Input");
        System.out.println("Prefix: " + prefix);
        System.out.println("Token: " + token);
        System.out.println("Activity: " + activity);
        System.out.println("Are your Input's correct? (Y/N)");

        handleConfirmation();
    }

    public static void handleConfirmation() {
        if (scanner.nextLine().equals("Y")) {
            System.out.println("Setup Complete! SpookBot will now start...");
        } else if (scanner.nextLine().equals("N")) {
            System.out.println("Setup will start again...");
            headlessSetup();
        } else {
            System.out.println("Please enter either Y or N... Setup will restart...");
            handleConfirmation();
        }
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
        bot.addEventListeners(new reporting());

        JDA SpookBot = bot.build();

        spookBot = SpookBot;
        spookOS.writeToConsole("SpookBot is running!");

    }

}
