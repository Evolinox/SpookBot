package Commands;

import SpookBot.Main;
import com.fasterxml.jackson.core.JsonParser;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

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
            // Get Question
            final String question = event.getOption("question").getAsString();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(ollamaApiUrl))
                    .header("Accept", "*/*")
                    .header("User-Agent", "SpookBot")
                    .header("Content-Type", "application/json")
                    .method("POST", HttpRequest.BodyPublishers.ofString(String.format("{\n  \"model\": \"%s\",\n  \"prompt\": \"%s\",\n  \"stream\": false\n}", model, question)))
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
            JSONObject responseBody = new JSONObject(response.body());
            String answer = responseBody.getString("response");

            // Output response
            event.getHook().editOriginal(answer).queue();
        }
    }
}
