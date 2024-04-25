package Commands;

import SpookBot.Main;
import com.fasterxml.jackson.core.JsonParser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

public class Ollama extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.getName().equals("ollama")) {
            // API Endpoint
            final String ollamaApiUrl = "http://127.0.0.1:11434/api/generate";
            // Get Model
            final String model = event.getOption("model").getAsString();
            ;
            // Get Question
            final String question = event.getOption("question").getAsString();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ollamaApiUrl))
                    .header("Accept", "*/*")
                    .header("User-Agent", "Thunder Client (https://www.thunderclient.com)")
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString("{\n  \"model\": \"llama2\",\n  \"prompt\": \"Hi\",\n  \"stream\": false\n}"))
                    .build();

            // Defer Reply
            event.deferReply().queue();
            HttpResponse<String> response = null;
            try {
                response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                TimeUnit.SECONDS.sleep(1);
            } catch (IOException e) {
                Main.loggingService.severe(e.getMessage());
            } catch (InterruptedException e) {
                Main.loggingService.severe(e.getMessage());
            }

            // Convert to a JSON object to print data


            event.getHook().editOriginal(response.body()).queue();
        }
    }
}
