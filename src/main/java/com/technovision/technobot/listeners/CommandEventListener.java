package com.technovision.technobot.listeners;

import com.technovision.technobot.TechnoBot;
import com.technovision.technobot.commands.Command;
import com.technovision.technobot.util.BotRegistry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.internal.requests.Route;

import javax.annotation.Nonnull;
import java.io.IOException;

public class CommandEventListener extends ListenerAdapter {

    public static final String PREFIX = "!";
    public static final int EMBED_COLOR = 0xd256e8;

    @Override
    public void onMessageReceived(@Nonnull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) { return; }
        String[] mArray = event.getMessage().getContentRaw().split(" ");
        String command = mArray[0];
        if (command.startsWith(PREFIX)) {
            String[] args = new String[mArray.length - 1];
            for (int i = 0; i < mArray.length; i++) {
                if (i > 0) args[i - 1] = mArray[i];
            }

            BotRegistry registry = TechnoBot.getInstance().getRegistry();

            for (Command cmd : registry.getCommands()) {
                if ((PREFIX + cmd.name).equalsIgnoreCase(command)) {
                    try {
                        if (!cmd.execute(event, args)) {
                            // do something, idk (the command failed to execute in this situation)
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    return;
                }
            }
            event.getChannel().sendMessage("Unknown Command!").queue();
        }
    }
}
