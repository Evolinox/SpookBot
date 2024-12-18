package Commands;

import SpookBot.Main;
import SpookBot.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Birthday extends ListenerAdapter {
    private final ScheduledExecutorService scheduler;
    private final Set<LocalTime> target;
    private final ZoneId zoneId = ZoneId.of("Europe/Berlin");

    public Birthday() {
        this.scheduler = Executors.newScheduledThreadPool(1);
        this.target = Set.of(
                LocalTime.of(6, 0)
        );
    }
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        // User wants to add Birthday
        if (event.getName().equals("set_birthday")) {
            // Input Variables
            Integer day = event.getOption("day").getAsInt();
            Integer month = event.getOption("month").getAsInt();
            // Add day and month for Logging and Response
            String date = day + "." + month;
            String dateClean = day.toString() + month;
            // Get Eventdata
            String guildId = event.getGuild().getId();
            String userId = event.getMember().getId();

            // Check Variables
            if (month > 12 || month < 1 || day > 31 || day < 1) {
                event.reply("Please enter a valid Date! " + date + " is not a valid Day").queue();
            }

            // Only continue, when the entered Day is valid (Check above isn't fully Done yet :) )
            else {
                // Doing a bit of Logging
                Main.loggingService.info(event.getMember().getEffectiveName() + " has added a Birthday: " + date);

                // Get config.xml
                Document config = Utils.getConfiguration();
                config.getDocumentElement().normalize();

                // Check, if Server is already in Birthday List
                NodeList birthdayConfigList = config.getElementsByTagName("birthday");
                if (birthdayConfigList.getLength() > 0) {
                    Element birthdayElement = (Element) birthdayConfigList.item(0);
                    NodeList serverElements = birthdayElement.getElementsByTagName("s" + guildId);

                    // Check
                    if (serverElements.getLength() == 0) {
                        Element serverBirthdayConfig = config.createElement("s" + guildId);
                        serverBirthdayConfig.setAttribute("enabled", "false");
                        serverBirthdayConfig.setAttribute("broadcastId", "null");
                        birthdayElement.appendChild(serverBirthdayConfig);
                    }

                    if (serverElements.getLength() > 0){
                        Element serverBirthdayConfig = (Element) serverElements.item(0);

                        // Check, if User is already in config.xml or not and update the Date
                        NodeList userNodes = serverBirthdayConfig.getElementsByTagName("u" + userId);
                        if (userNodes.getLength() == 0) {
                            Element userElement = config.createElement("u" + userId);
                            userElement.setTextContent(date);
                            serverBirthdayConfig.appendChild(userElement);
                        } else {
                            serverBirthdayConfig.getElementsByTagName("u" + userId).item(0).setTextContent(date);
                        }
                    }
                }

                // Set config.xml and respond to User
                if (Utils.setConfiguration(config)) {
                    Main.loggingService.info("succesfully updated config.xml");
                    event.reply("I've set your Birthday to " + date).queue();
                } else {
                    Main.loggingService.severe("could not update config.xml");
                    event.reply("There was an Issue setting your Birthday. Please try again later or contact my Creator!").queue();
                }
            }
        }

        // User wants to remove Birthday
        else if (event.getName().equals("remove_birthday")) {
            event.reply("This Feature is currently not implemented, please ask my owner for this").queue();
        }

        // User wants a list of upcoming Birthdays
        else if (event.getName().equals("get_birthday")) {
            // Get Guild ID
            String guildId = event.getGuild().getId();

            // Check, if user has birthday
            Document config = Utils.getConfiguration();

            EmbedBuilder birthdayCollectionEmbed = new EmbedBuilder();
            birthdayCollectionEmbed.setTitle("Upcoming Birthdays");
            birthdayCollectionEmbed.setFooter("SpookBot v1.1");

            NodeList serverBirthdayConfig = config.getElementsByTagName("s" + guildId);
            // Durchlaufe alle gefundenen <s123> Elemente
            for (int i = 0; i < serverBirthdayConfig.getLength(); i++) {
                Node guildNode = serverBirthdayConfig.item(i);

                // Stelle sicher, dass es sich um ein Element handelt
                if (guildNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element guildElement = (Element) guildNode;

                    // Alle Kind-Knoten des <s123> Elements finden
                    NodeList childNodes = guildElement.getChildNodes();

                    // Maximal fünf User sollen auf die Liste
                    int maxAmountUsers = 5;
                    if (childNodes.getLength() < 5) {
                        maxAmountUsers = childNodes.getLength();
                    }

                    // Durchlaufe alle Kind-Knoten und prüfe den Inhalt
                    for (int j = 0; j <= maxAmountUsers; j++) {
                        Node childNode = childNodes.item(j);

                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element childElement = (Element) childNode;
                            String content = childElement.getTextContent().trim();
                            String userId = childElement.getTagName().replace("u", "");
                            User user = event.getJDA().retrieveUserById(userId).complete();
                            String userName = user.getEffectiveName();

                            String[] dateSplit = content.split("\\.");
                            String birthDay = dateSplit[0]; // Day
                            String birthMonth = dateSplit[1]; // Month
                            birthdayCollectionEmbed.addField(userName, "hat am " + birthDay + "." + birthMonth + ". Geburtstag!", false);
                        }
                    }
                }
            }
            event.replyEmbeds(birthdayCollectionEmbed.build()).queue();
        }

        // User wants to set the Broadcast Channel
        else if (event.getName().equals("set_broadcast_channel")) {
            // Input Variables
            String channelId = event.getOption("broadcastchannel").getAsString();

            // Get Eventdata
            String guildId = event.getGuild().getId();

            // Doing a bit of Logging
            Main.loggingService.info(event.getMember().getEffectiveName() + " has set the Broadcast ID for " + guildId + " to: " + channelId);

            // Get config.xml
            Document config = Utils.getConfiguration();
            config.getDocumentElement().normalize();

            // Check, if Server is already in Birthday List
            NodeList birthdayConfigList = config.getElementsByTagName("birthday");
            if (birthdayConfigList.getLength() > 0) {
                Element birthdayElement = (Element) birthdayConfigList.item(0);
                NodeList serverElements = birthdayElement.getElementsByTagName("s" + guildId);

                // Check
                if (serverElements.getLength() == 0) {
                    Element serverBirthdayConfig = config.createElement("s" + guildId);
                    serverBirthdayConfig.setAttribute("enabled", "true");
                    serverBirthdayConfig.setAttribute("broadcastId", channelId);
                    birthdayElement.appendChild(serverBirthdayConfig);
                } else if (serverElements.getLength() > 0) {
                    Element serverBirthdayConfig = (Element) serverElements.item(0);
                    serverBirthdayConfig.setAttribute("enabled", "true");
                    serverBirthdayConfig.setAttribute("broadcastId", channelId);
                }
            }

            // Set config.xml and respond to User
            if (Utils.setConfiguration(config)) {
                Main.loggingService.info("succesfully updated config.xml");
                event.reply("I've set the Broadcast ID to " + channelId).queue();
            } else {
                Main.loggingService.severe("could not update config.xml");
                event.reply("There was an Issue setting the Broadcast ID. Please try again later or contact my Creator!").queue();
            }
        }

        // User wants to set the Broadcast Channel
        else if (event.getName().equals("set_birthday_role")) {
            // Input Variables
            String roleId = event.getOption("birthdayrole").getAsString();

            // Get Eventdata
            String guildId = event.getGuild().getId();

            // Doing a bit of Logging
            Main.loggingService.info(event.getMember().getEffectiveName() + " has set the Birthdayrole for " + guildId + " to: " + roleId);

            // Get config.xml
            Document config = Utils.getConfiguration();
            config.getDocumentElement().normalize();

            // Check, if Server is already in Birthday List
            NodeList birthdayConfigList = config.getElementsByTagName("birthday");
            if (birthdayConfigList.getLength() > 0) {
                Element birthdayElement = (Element) birthdayConfigList.item(0);
                NodeList serverElements = birthdayElement.getElementsByTagName("s" + guildId);

                // Check
                if (serverElements.getLength() == 0) {
                    Element serverBirthdayConfig = config.createElement("s" + guildId);
                    serverBirthdayConfig.setAttribute("birthdayRoleId", roleId);
                    birthdayElement.appendChild(serverBirthdayConfig);
                } else if (serverElements.getLength() > 0) {
                    Element serverBirthdayConfig = (Element) serverElements.item(0);
                    serverBirthdayConfig.setAttribute("birthdayRoleId", roleId);
                }
            }

            // Set config.xml and respond to User
            if (Utils.setConfiguration(config)) {
                Main.loggingService.info("succesfully updated config.xml");
                event.reply("I've set the Birthdayrole to " + roleId).queue();
            } else {
                Main.loggingService.severe("could not update config.xml");
                event.reply("There was an Issue setting the Birthdayrole. Please try again later or contact my Creator!").queue();
            }
        }
    }

    @Override
    public void onGuildReady(GuildReadyEvent event) {
        Runnable runnable = () -> {
            // Today's Date
            LocalTime now = LocalTime.now(zoneId);
            LocalDate today = LocalDate.now(zoneId);
            Integer day = today.getDayOfMonth();
            Integer month = today.getMonthValue();

            // Get Guild ID
            String guildId = event.getGuild().getId();

            // Prepare User ID List
            ArrayList<String> userIds = new ArrayList<String>();

            // Prepare Broadcast Channel
            NewsChannel broadcastChannel = null;

            // Prepare Birthday Role
            Role birthdayRole = null;

            // Check, if user has birthday
            Document config = Utils.getConfiguration();

            NodeList serverBirthdayConfig = config.getElementsByTagName("s" + guildId);

            // Durchlaufe alle gefundenen <s123> Elemente
            for (int i = 0; i < serverBirthdayConfig.getLength(); i++) {
                Node guildNode = serverBirthdayConfig.item(i);

                // Stelle sicher, dass es sich um ein Element handelt
                if (guildNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element guildElement = (Element) guildNode;
                    String channelId = guildElement.getAttribute("broadcastId");
                    broadcastChannel = event.getGuild().getNewsChannelById(channelId);
                    String roleId = guildElement.getAttribute("birthdayRoleId");
                    birthdayRole = event.getGuild().getRoleById(roleId);

                    // Alle Kind-Knoten des <s123> Elements finden
                    NodeList childNodes = guildElement.getChildNodes();

                    // Durchlaufe alle Kind-Knoten und prüfe den Inhalt
                    for (int j = 0; j < childNodes.getLength(); j++) {
                        Node childNode = childNodes.item(j);

                        if (childNode.getNodeType() == Node.ELEMENT_NODE) {
                            Element childElement = (Element) childNode;
                            String content = childElement.getTextContent().trim();

                            // Vergleich des Inhalts
                            String[] dateSplit = content.split("\\.");
                            String birthDay = dateSplit[0]; // Day
                            String birthMonth = dateSplit[1]; // Month
                            if (birthDay.equals(day.toString()) && birthMonth.equals(month.toString())) {
                                String userId = childElement.getTagName().replace("u", "");
                                userIds.add(userId);
                            }
                        }
                    }
                }
            }

            for (LocalTime time : target) {
                if (time.getHour() == now.getHour() && time.getMinute() == now.getMinute()) {
                    // Remove Birthday Role from everyone, that has it
                    if (birthdayRole != null) {
                        for (Member member : event.getGuild().getMembersWithRoles(birthdayRole)) {
                            event.getGuild().removeRoleFromMember(member, birthdayRole).queue();
                        }
                    }

                    // Check for Birthdays
                    for (String userId : userIds) {
                        User user = event.getJDA().getUserById(userId);

                        if (birthdayRole != null && user != null) {
                            event.getGuild().addRoleToMember(user, birthdayRole).queue();
                        }

                        EmbedBuilder birthdayMessage = new EmbedBuilder();

                        birthdayMessage.setTitle("Happy Birthday!");
                        birthdayMessage.setDescription("Heute hat <@" + userId + "> Geburtstag! Alles gute :)");
                        birthdayMessage.setFooter("SpookBot v1.1");

                        broadcastChannel.sendMessageEmbeds(birthdayMessage.build()).queue();
                        userIds.remove(userId);
                    }
                }
            }
        };

        scheduler.scheduleAtFixedRate(runnable, 0, 1, TimeUnit.MINUTES);
    }
}
