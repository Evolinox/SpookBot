package Commands;

import net.dv8tion.jda.api.events.*;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class manager extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

        // Verification Command
        OptionData channelId = new OptionData(OptionType.CHANNEL, "channelid", "Choose your Verification Channel", true);
        commandData.add(Commands.slash("verification", "Create a Verification Message!").addOptions(channelId));

        // Inspector Command
        OptionData userRequired = new OptionData(OptionType.USER, "user", "User to inspect", true);
        commandData.add(Commands.slash("inspect", "Inspect Userprofiles on your Server!").addOptions(userRequired));

        // Music Commands
        // Play
        OptionData musicText = new OptionData(OptionType.STRING, "name", "What music to play? (URL or a Name)", true);
        commandData.add(Commands.slash("play", "Play some music for you and your Friends!").addOptions(musicText));

        // Stop
        commandData.add(Commands.slash("stop", "Done with some cool music? Let me leave!"));

        // Next
        commandData.add(Commands.slash("next", "Some ugly music right there? Just start the next one!"));

        //Ask
        OptionData questionText = new OptionData(OptionType.STRING, "question", "Type your question here!", true);
        commandData.add(Commands.slash("ask", "You have a question for me? I'll try to answer it!").addOptions(questionText));

        //Reddit
        OptionData subredditName = new OptionData(OptionType.STRING, "subreddit", "Want a specific subreddit?", false);
        commandData.add(Commands.slash("reddit", "You want to see some reddit posts?").addOptions(subredditName));

        event.getJDA().updateCommands().addCommands(commandData).queue();
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        event.getGuild().updateCommands().addCommands().queue();
    }

}
