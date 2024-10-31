package Commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Echo extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // Echo
        if (event.getName().equals("echo")) {
            // Acknowledge
            event.deferReply(true).queue();

            // Input Variables
            String msg = event.getOption("message").getAsString();

            // Send Message to Channel
            event.getChannel().sendMessage(msg).queue();

            // Acknowledge User
            event.getHook().sendMessage("successfully send message!").queue();
        }
    }
}
