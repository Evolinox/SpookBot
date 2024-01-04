package Commands;

import SpookBot.main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.concurrent.TimeUnit;

public class ask extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.getName().equals("ask")) {

            String question = event.getOption("question").getAsString();

            event.deferReply().queue();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            event.getHook().sendMessage("This feature won't be maintained anymore, please use <@823237843685081088>'s /prompt Command instead!").queue();

        }

    }

}
