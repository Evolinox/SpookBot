package Commands;

import SpookBot.Main;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Messages extends ListenerAdapter {

    public void onMessageReceived (MessageReceivedEvent event) {

        if (event.isFromGuild()) {

            if (event.getMessage().getContentStripped().contains("SpookBot")) {

                event.getChannel().sendMessage("Hallo").queue();
                //event.getMessage().addReaction("\uD83D\uDC4B").queue();
                if (Main.spookOS != null) {
                    Main.spookOS.writeToConsole(event.getMember().getEffectiveName() + " mentioned me in his Message! :O");
                } else {
                    Main.loggingService.info(event.getMember().getEffectiveName() + " mentioned me in his Message! :O");
                }

            }

        }

    }

}
