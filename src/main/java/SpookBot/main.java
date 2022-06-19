package SpookBot;

import Commands.inspector;
import Commands.messages;
import Commands.music;
import Commands.rules;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class main {

    public static void main(String[] args) throws LoginException {

        //SpookBot Token, soll später in eine Json
        String token = "Bot Token Here";

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
