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
            boolean includeEndingServices;
            JSONObject timetable = null;

            try {
                includeEndingServices = event.getOption("includeendingtrains").getAsBoolean();
            } catch (NullPointerException e) {
                includeEndingServices = false;
            }

            try {
                timetable = Request.getTimetable(event.getOption("station").getAsString(), event.getOption("customdate").getAsString(), event.getOption("customhour").getAsString());
                System.out.println(timetable.toString(1));
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Embed
            EmbedBuilder timetableEmbed = new EmbedBuilder();
            timetableEmbed.setTitle(timetable.optString("station"));
            timetableEmbed.setDescription("Fahrplan von " + event.getOption("customhour").getAsString() + " bis " + (event.getOption("customhour").getAsInt() + 1) + " Uhr");
            timetableEmbed.setFooter("Timetable Plugin V1 · Lokalen Fahrplan beachten!");
            timetableEmbed.setColor(Color.red);

            // Read Services
            JSONArray services = timetable.getJSONArray("services");
            for (int i = 0; i < services.length(); i++) {
                JSONObject service = services.getJSONObject(i);
                String time = service.getString("departure");
                String timeFormatted = time.substring(0, time.length() - 2) + ":" + time.substring(time.length() - 2);
                if (includeEndingServices) {
                    if (service.getBoolean("ending")) {
                        String title = service.getString("type") + " " + service.getString("line") + " (" + service.getString("number") + ")";
                        String data = "Ankunft auf Gleis " + service.getString("track") + "\num " + timeFormatted + " Uhr";
                        timetableEmbed.addField(title, data, false);
                    } else {
                        String title = service.getString("type") + " " + service.getString("line") + " (" + service.getString("number") + ") nach " + service.getString("destination");
                        String data = "Abfahrt von Gleis " + service.getString("track") + "\num " + timeFormatted + " Uhr";
                        timetableEmbed.addField(title, data, false);
                    }
                } else {
                    if (!service.getBoolean("ending")) {
                        String title = service.getString("type") + " " + service.getString("line") + " (" + service.getString("number") + ") nach " + service.getString("destination");
                        String data = "Abfahrt von Gleis " + service.getString("track") + "\num " + timeFormatted + " Uhr";
                        timetableEmbed.addField(title, data, false);
                    }
                }
            }

            // Reply
            event.replyEmbeds(timetableEmbed.build()).queue();
        }
    }
}
