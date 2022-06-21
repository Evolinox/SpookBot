package Commands;

import SpookBot.main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class inspector extends ListenerAdapter {

    public void onMessageReceived (MessageReceivedEvent event) {

        String path = System.getProperty("user.home") + File.separator + "Documents" + File.separator + "SpookBotSettings";
        File file = new File(path + File.separator + "config.xml");

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        Document document = null;

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (Exception d) {
            d.printStackTrace();
        }

        try {
            document = documentBuilder.parse(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String currentPrefix = document.getElementsByTagName("commandPrefix").item(0).getTextContent();

        if (event.isFromGuild()) {

            if (event.getMessage().getContentStripped().startsWith(currentPrefix + "inspect")) {

                Member member = event.getMessage().getMentions().getMembers().get(0);

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

                    event.getChannel().sendMessageEmbeds(inspector.build()).setActionRow(btnAvatar, btnKick, btnTimeout, btnBan).queue();
                    main.spookOS.writeToConsole(event.getMember().getEffectiveName() + " inspected " + member.getNickname() + " with admin rights");

                }

                else {

                    event.getChannel().sendMessageEmbeds(inspector.build()).setActionRow(btnAvatar).queue();
                    main.spookOS.writeToConsole(event.getMember().getEffectiveName() + " inspected " + member.getNickname());

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
