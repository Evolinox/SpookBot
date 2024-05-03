package Commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.JSONObject;
import Timetable.Request;

import java.io.IOException;

public class Timetable extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("timetable")) {
            JSONObject tt = null;
            try {
                tt = Request.getTimetable(event.getOption("station").getAsString(), event.getOption("customdate").getAsString(), event.getOption("customhour").getAsString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            event.reply(tt.toString()).queue();
        }
    }
}
