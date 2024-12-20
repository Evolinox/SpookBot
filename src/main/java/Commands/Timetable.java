package Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import Timetable.Request;

import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Timetable extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("timetable")) {
            boolean includeEndingServices;
            JSONObject timetable = null;
            String date, hour;
            if (event.getOption("customdate") == null) {
                date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
            } else {
                date = event.getOption("customdate").getAsString();
            }
            if (event.getOption("customhour") == null) {
                hour = LocalTime.now().format(DateTimeFormatter.ofPattern("HH"));
            } else {
                hour = event.getOption("customhour").getAsString();
            }

            try {
                includeEndingServices = event.getOption("includeendingtrains").getAsBoolean();
            } catch (NullPointerException e) {
                includeEndingServices = false;
            }

            try {
                timetable = Request.getTimetable(event.getOption("station").getAsString(), date, hour);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Prepare Description
            String hourFormatted = hour + " bis " + (Integer.parseInt(hour) + 1);
            String dateFormatted = date.substring(4) + "." + date.substring(2, date.length() - 2) + "." + date.substring(0, date.length() - 4);

            // Embed
            EmbedBuilder timetableEmbed = new EmbedBuilder();
            timetableEmbed.setTitle(timetable.optString("station"));
            timetableEmbed.setDescription("Fahrplan von " + hourFormatted + " Uhr am " + dateFormatted);
            timetableEmbed.setFooter("Timetable Plugin V1 · Lokalen Fahrplan beachten!");
            timetableEmbed.setColor(Color.red);

            // Read Services
            JSONArray services = timetable.getJSONArray("services");
            for (int i = 0; i < services.length(); i++) {
                JSONObject service = services.getJSONObject(i);

                String trainNumber;
                if (service.getString("line") == service.getString("number")) {
                    trainNumber = "";
                } else {
                    trainNumber = " (" + service.getString("number") + ")";
                }
                String time = service.getString("departure");
                String timeFormatted = time.substring(0, time.length() - 2) + ":" + time.substring(time.length() - 2);

                if (includeEndingServices) {
                    if (service.getBoolean("ending")) {
                        String title = service.getString("type") + " " + service.getString("line") + trainNumber;
                        String data = "Ankunft auf Gleis " + service.getString("track") + "\num " + timeFormatted + " Uhr";
                        timetableEmbed.addField(title, data, false);
                    } else {
                        String title = service.getString("type") + " " + service.getString("line") + trainNumber + " nach " + service.getString("destination");
                        String data = "Abfahrt von Gleis " + service.getString("track") + "\num " + timeFormatted + " Uhr";
                        timetableEmbed.addField(title, data, false);
                    }
                } else {
                    if (!service.getBoolean("ending")) {
                        String title = service.getString("type") + " " + service.getString("line") + trainNumber + " nach " + service.getString("destination");
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
