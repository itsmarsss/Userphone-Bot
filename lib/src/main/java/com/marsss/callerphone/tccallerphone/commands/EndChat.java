package com.marsss.callerphone.tccallerphone.commands;

import com.marsss.ICommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class EndChat implements ICommand {
    @Override
    public void runCommand(GuildMessageReceivedEvent e) {

    }

    @Override
    public void runSlash(SlashCommandEvent e) {

    }

    @Override
    public String getHelpF() {
        return null;
    }

    @Override
    public String[] getTriggers() {
        return new String[0];
    }
}
