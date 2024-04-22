package Commands;

import SpookBot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Rules extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.getName().equals("verification")) {

            String channelId = event.getOption("channelid").getAsString();

            if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {

                EmbedBuilder rules = new EmbedBuilder();

                rules.setTitle("Verification");
                rules.setDescription("Please verify yourself to get access to the rest of this Server!");
                rules.setColor(new Color(34,188,157,255));

                //rules.addField("Rule 1 - Be Civil", "Don't insult, troll, witch hunt, dox, or threaten people. Criticizing someone's ideas is different from attacking them as a person and we have to share this space with each other. Being overly cruel or disrespectful is not conducive of the environment we wish to cultivate.", false);
                //rules.addField("Rule 2 - NSFW", "Per discords rules NSFW content is only permitted in channels flagged for such content. As this discord server has no such channels, NSFW content is forbidden server wide.", false);
                //rules.addField("Rule 3 - No Spam", "Please use #bot-spam for bot commands and do not spam channels.", false);
                //rules.addField("Rule 4 - No Advertisements", "If you wish to advertise another discord, gofundme (or other such site), or promote another project you must seek permission from the moderators. Promoting or attempting to promote known scams will result in your removal from the server.", false);
                //rules.addField("Other Considerations", "Failure to read the rules or refusing to read them when directed can result in your removal from the server. All rules are enforced at moderator discretion and as such equal application is not always a guarantee. It should go without saying but all rules set forth by the discord ToS also apply here.", false);

                rules.setFooter("This Message was send by SpookBot");

                Button btnAccept = Button.success("accept", "Verify");
                Button btnRefuse = Button.danger("refuse", "Refuse");

                TextChannel rulesChannel = event.getGuild().getTextChannelById(channelId);
                rulesChannel.sendMessageEmbeds(rules.build()).setActionRow(btnAccept, btnRefuse).queue();

                if (Main.spookOS != null) {
                    Main.spookOS.writeToConsole(event.getMember().getEffectiveName() + " requested a embedded rules message in: " + rulesChannel);
                } else {
                    Main.loggingService.info(event.getMember().getEffectiveName() + " requested a embedded rules message in: " + rulesChannel);
                }

                event.deferReply().queue();
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                event.getHook().sendMessage("Verification Message should be setup!").queue();

            }

        }

    }

    public void onButtonInteraction (ButtonInteractionEvent event) {

        if (event.getButton().getId().equals("accept")) {

            Role memberRole = event.getGuild().getRoleById("987703002477502564");

            event.getGuild().addRoleToMember(event.getMember(), memberRole).queue();
            event.reply("You have now access to our Server!").setEphemeral(true).queue();

            if (Main.spookOS != null) {
                Main.spookOS.writeToConsole(event.getMember().getEffectiveName() + " has accepted the rules and got the role: " + memberRole);
            } else {
                Main.loggingService.info(event.getMember().getEffectiveName() + " has accepted the rules and got the role: " + memberRole);
            }

        }

        if (event.getButton().getId().equals("refuse")) {

            event.reply("Sadly, you did not accept our rules. Therefor we won't grant you access to our Server! If you changed your mind, we will always welcome you to our Server! :)").setEphemeral(true).queue();

            if (Main.spookOS != null) {
                Main.spookOS.writeToConsole(event.getMember().getEffectiveName() + " did not accept the rules!");
            } else {
                Main.loggingService.info(event.getMember().getEffectiveName() + " did not accept the rules!");
            }

        }

    }
}
