package Commands;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
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
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

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

        event.getGuild().updateCommands().addCommands(commandData).queue();
    }

}
