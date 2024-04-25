package Commands;

import Reddit.Client;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

            // Set HTTP Request Parameter
            final Map<String, String> params = new HashMap<>();
            params.put("model", model);
            params.put("prompt", question);

            // Building the Request Form
            final String form = params.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .collect(Collectors.joining("&"));

            // Building the HTTP Request
            final HttpRequest request = HttpRequest.newBuilder()
                    .POST(HttpRequest.BodyPublishers.ofString(form))
                    .uri(URI.create(ollamaApiUrl))
                    .build();

            final HttpClient client = Reddit.Client.getHttpClient();

            HttpResponse<String> response;
            try {
                response = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // Defer Reply
            event.deferReply().queue();
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            event.getHook().editOriginal(response.body()).queue();
        }
    }
}
