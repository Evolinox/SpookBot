package Commands;

import SpookBot.main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class reddit extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.getName().equals("reddit")) {

            String subreddit;

            if (event.getOption("subreddit") == null) {

                subreddit = "all";

                event.reply("r/all").queue();

                main.spookOS.writeToConsole("Requesting a Reddit Post from r/all");

            }

            else {

                subreddit = event.getOption("subreddit").getAsString();

                event.reply("r/" + subreddit).queue();

                main.spookOS.writeToConsole("Requesting a Reddit Post from r/" + subreddit);

            }

        }

    }

}
