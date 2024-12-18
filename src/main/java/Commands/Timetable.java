package Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import Timetable.Request;

import java.awt.*;
import java.io.IOException;

public class Timetable extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("timetable")) {
            JSONObject timetable = null;
            try {
                timetable = Request.getTimetable(event.getOption("station").getAsString(), event.getOption("customdate").getAsString(), event.getOption("customhour").getAsString());
                System.out.println(timetable.toString(1));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Embed
            EmbedBuilder timetableEmbed = new EmbedBuilder();
            timetableEmbed.setTitle(timetable.optString("station"));
            timetableEmbed.setFooter("Timetable Plugin V1 · Lokalen Fahrplan beachten!");
            timetableEmbed.setColor(Color.red);

            // Read Services
            JSONArray services = timetable.getJSONArray("services");
            for (int i = 0; i < services.length(); i++) {
                JSONObject service = services.getJSONObject(i);
                String title = service.getString("type") + " " + service.getString("line") + " (" + service.getString("number") + ") nach " + service.getString("destination");
                String data = "Abfahrt von Gleis " + service.getString("track") + "\num 17:42 Uhr";
                timetableEmbed.addField(title, data, false);
            }

            // Reply
            event.replyEmbeds(timetableEmbed.build()).queue();
        }
    }
}
