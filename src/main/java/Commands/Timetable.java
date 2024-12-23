package Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;
import Timetable.Request;

import java.io.IOException;

public class Timetable extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("timetable")) {
            JSONObject timetable = null;
            try {
                timetable = Request.getTimetable(event.getOption("station").getAsString(), event.getOption("customdate").getAsString(), event.getOption("customhour").getAsString());
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Embed
            EmbedBuilder timetableEmbed = new EmbedBuilder();
            timetableEmbed.setTitle(timetable.optString("station"));
            timetableEmbed.setDescription("Timetable Plugin V1");

            // Reply
            event.replyEmbeds(timetableEmbed.build()).queue();
        }
    }
}
