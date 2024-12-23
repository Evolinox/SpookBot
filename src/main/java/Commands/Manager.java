package Commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Manager extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        List<CommandData> commandData = new ArrayList<>();

        // Verification Command
        OptionData channelId = new OptionData(OptionType.CHANNEL, "channelid", "Choose your Verification Channel", true);
        commandData.add(Commands.slash("verification", "Create a Verification Message!").addOptions(channelId));

        // Inspector Command
        OptionData userRequired = new OptionData(OptionType.USER, "user", "User to inspect", true);
        commandData.add(Commands.slash("inspect", "Inspect Userprofiles on your Server!").addOptions(userRequired));

        // Report Command
        OptionData reportedUser = new OptionData(OptionType.USER, "user", "User you want to report", true);
        OptionData reportDescription = new OptionData(OptionType.STRING, "reason", "Why do you want to report this User?", true);
        commandData.add(Commands.slash("report", "Report other Users on this Server to the Moderators!").addOptions(reportedUser, reportDescription));

        // Music Commands
        // Play
        OptionData musicText = new OptionData(OptionType.STRING, "link", "What music to play? (Sound Cloud Link or Songname)", true);
        commandData.add(Commands.slash("play", "Play some music for you and your Friends!").addOptions(musicText));

        // Stop
        commandData.add(Commands.slash("stop", "Done with some cool music? Let me leave!"));

        // Next
        commandData.add(Commands.slash("next", "Some ugly music right there? Just start the next one!"));

        //Reddit
        OptionData subredditName = new OptionData(OptionType.STRING, "subreddit", "Want a specific subreddit?", false);
        commandData.add(Commands.slash("reddit", "You want to see some reddit posts?").addOptions(subredditName));

        //Ollama
        OptionData llmModel = new OptionData(OptionType.STRING, "model", "What Model do you want to use?", true)
                .addChoice("Llama 3", "llama3")
                .addChoice("Llama 2", "llama2")
                .addChoice("Code-Llama", "codellama");
        OptionData llmQuestion = new OptionData(OptionType.STRING, "question", "What is your Question?", true);
        commandData.add(Commands.slash("ollama", "You have a Question and need an Answer? Try this").addOptions(llmModel, llmQuestion));

        //Timetable
        OptionData stationId = new OptionData(OptionType.STRING, "station", "Which Station?", true)
                .addChoice("Mosbach (Baden)", "8004094")
                .addChoice("Mosbach-Neckarelz", "8000264")
                .addChoice("Heilbronn Hbf", "8000157");
        OptionData customDate = new OptionData(OptionType.STRING, "customdate", "Enter a custom Date (YYMMDD)", false);
        OptionData customHour = new OptionData(OptionType.STRING, "customhour", "Enter a custom Hour (HH)", false);
        commandData.add(Commands.slash("timetable", "See all Arrivals and Departures from a Station (Germany Only)").addOptions(stationId, customDate, customHour));

        // Birthday Commands
        // Set
        OptionData birthdayDay = new OptionData(OptionType.INTEGER, "day", "Please enter the Day of your Birthday", true);
        OptionData birthdayMonth = new OptionData(OptionType.INTEGER, "month", "Please enter the Month of your Birthday", true);
        commandData.add(Commands.slash("set_birthday", "Set the Date of your Birthday, so you will get a special Role on this Day! (DDMM)").addOptions(birthdayDay, birthdayMonth));

        // Remove
        commandData.add(Commands.slash("remove_birthday", "Remove your Birthday from this Server's list!"));

        // Get
        commandData.add(Commands.slash("get_birthday", "See all the Birthdays!"));

        // Set Broadcast Channel
        OptionData broadcastId = new OptionData(OptionType.STRING, "broadcastchannel", "Please enter the Channel ID for a Broadcast Channel", true);
        commandData.add(Commands.slash("set_broadcast_channel", "The Broadcast Channel, where i can post Updates like Birthdays")
                .addOptions(broadcastId)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
        );

        // Set Birthday Role
        OptionData birthdayRoleId = new OptionData(OptionType.STRING, "birthdayrole", "Please enter the Birthdayrole ID", true);
        commandData.add(Commands.slash("set_birthday_role", "The Birthdayrole, which i will give the user, who has its birthday")
                .addOptions(birthdayRoleId)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
        );

        // Echo
        OptionData echoMessage = new OptionData(OptionType.STRING, "message", "Please enter a message i should sent", true);
        commandData.add(Commands.slash("echo", "Tell me, what i should send in this Channel")
                .addOptions(echoMessage)
                .setDefaultPermissions(DefaultMemberPermissions.enabledFor(Permission.ADMINISTRATOR))
        );

        event.getJDA().updateCommands().addCommands(commandData).queue();
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        event.getGuild().updateCommands().addCommands().queue();
    }

}