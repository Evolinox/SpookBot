package SpookBot;

import Commands.inspector;
import Commands.messages;
import Commands.music;
import Commands.rules;

import Swing.SpookOS;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import javax.swing.*;

import java.io.IOException;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

public class main {

    public static void main(String[] args) throws LoginException {

        String token = new String();
        File file = new File("src/main/java/SpookBot/data.json");
        if (!file.exists()) {

            try {

                file.createNewFile();

            } catch (IOException e) {

                e.printStackTrace();

            }

        }
        else {

            try {

                String jsonPath = new String(Files.readAllBytes(Paths.get(file.toURI())));

                JSONObject json = new JSONObject(jsonPath);
                token = json.getString("Token");

            } catch (IOException e) {

                e.printStackTrace();

            }

        }

        JFrame frame = new SpookOS("SpookOS", 600, 500);
        frame.setVisible(true);

        JDABuilder bot = JDABuilder.createDefault(token);

        bot.setStatus(OnlineStatus.ONLINE);
        bot.setActivity(Activity.playing("SpookOS"));
        bot.enableCache(CacheFlag.VOICE_STATE);

        bot.addEventListeners(new messages());
        bot.addEventListeners(new rules());
        bot.addEventListeners(new inspector());
        bot.addEventListeners(new music());

        JDA SpookBot = bot.build();

    }

}
