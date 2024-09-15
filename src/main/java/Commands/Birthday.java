package Commands;

import SpookBot.Main;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Birthday extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // User wants to add Birthday
        if (event.getName().equals("set_birthday")) {
            // Input Variables
            Integer day = event.getOption("day").getAsInt();
            Integer month = event.getOption("month").getAsInt();
            // Add day and month for Logging and Response
            String date = day + "." + month;

            // Check Variables
            if (month > 12 || month < 1 || day > 31 || day < 1) {
                event.reply("Please enter a valid Date! " + date + " is not a valid Day").queue();
            }

            // Only continue, when the entered Day is valid (Check above isn't fully Done yet :) )
            else {
                // Doing a bit of Logging
                Main.loggingService.info(event.getMember().getEffectiveName() + " has added a Birthday: " + date);

                // Logic
                event.reply("I've set your Birthday to " + date).queue();
            }

        // User wants to remove Birthday
        } else if (event.getName().equals("remove_birthday")) {
            event.reply("removebirthdaylololo").queue();

        // User wants a list of upcoming Birthdays
        } else if (event.getName().equals("get_birthday")) {
            event.reply("getbirthdaylololo").queue();
        }
    }
}
