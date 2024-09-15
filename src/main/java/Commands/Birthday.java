package Commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Birthday extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("set_birthday")) {
            event.reply("setbirthdaylololo").queue();
        } else if (event.getName().equals("remove_birthday")) {
            event.reply("removebirthdaylololo").queue();
        } else if (event.getName().equals("get_birthday")) {
            event.reply("getbirthdaylololo").queue();
        }
    }
}
