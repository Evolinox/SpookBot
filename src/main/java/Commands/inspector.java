package Commands;

import SpookBot.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class Inspector extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.getName().equals("inspect")) {

            if (event.isFromGuild()) {

                Member member = event.getOption("user").getAsMember();

                Integer numReports = 0;
                Integer numTimeouts = 0;

                Button btnKick = Button.secondary("kick" + member.getId(), "Kick");
                Button btnTimeout = Button.secondary("timeout" + member.getId(), "Timeout");
                Button btnBan = Button.danger("ban" + member.getId(), "Ban");
                Button btnAvatar = Button.link(member.getEffectiveAvatarUrl(), "View Avatar");

                EmbedBuilder inspector = new EmbedBuilder();

                inspector.setTitle("Inspector");
                inspector.setDescription("Userinformations for " + member.getEffectiveName());
                inspector.setThumbnail(member.getEffectiveAvatarUrl());

                if (member.getNickname() != null) {

                    inspector.addField("Nickname", member.getNickname(), true);

                }

                inspector.addField("Reports", numReports + " Reports", true);
                inspector.addField("Timouts", numTimeouts + " total Timeouts", true);

                if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {

                    event.replyEmbeds(inspector.build()).addActionRow(btnAvatar, btnKick, btnTimeout, btnBan).queue();

                    if (Main.spookOS != null) {
                        Main.spookOS.writeToConsole(event.getMember().getEffectiveName() + " inspected " + member.getNickname() + " with admin rights");
                    } else {
                        Main.loggingService.info(event.getMember().getEffectiveName() + " inspected " + member.getNickname() + " with admin rights");
                    }

                }

                else {

                    event.replyEmbeds(inspector.build()).addActionRow(btnAvatar).queue();

                    if (Main.spookOS != null) {
                        Main.spookOS.writeToConsole(event.getMember().getEffectiveName() + " inspected " + member.getNickname());
                    } else {
                        Main.loggingService.info(event.getMember().getEffectiveName() + " inspected " + member.getNickname());
                    }

                }

            }

        }

    }

    public void onButtonInteraction (ButtonInteractionEvent event) {

        if (event.getButton().getId().startsWith("kick")) {

            if (event.getMember().getPermissions().contains(Permission.ADMINISTRATOR)) {

            }

        }

    }

}
