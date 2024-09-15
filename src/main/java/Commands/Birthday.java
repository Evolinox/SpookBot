package Commands;

import SpookBot.Main;
import SpookBot.Utils;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class Birthday extends ListenerAdapter {
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
            event.reply("removebirthdaylololo").queue();
        }

        // User wants a list of upcoming Birthdays
        else if (event.getName().equals("get_birthday")) {
            event.reply("getbirthdaylololo").queue();
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
    }
}
