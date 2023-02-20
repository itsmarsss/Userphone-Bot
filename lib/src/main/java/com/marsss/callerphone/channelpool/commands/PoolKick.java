package com.marsss.callerphone.channelpool.commands;

import com.marsss.ICommand;
import com.marsss.callerphone.Callerphone;
import com.marsss.callerphone.Response;
import com.marsss.callerphone.ToolSet;
import com.marsss.callerphone.channelpool.ChannelPool;
import com.marsss.callerphone.channelpool.PoolResponse;
import com.marsss.callerphone.channelpool.PoolStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class PoolKick implements ICommand {

    @Override
    public void runCommand(GuildMessageReceivedEvent e) {
        final Member MEMBER = e.getMember();

        if (ChannelPool.permissionCheck(MEMBER, e.getMessage())) {
            return;
        }

        String[] args = e.getMessage().getContentRaw().split("\\s+");

        if (args.length == 1) {
            e.getMessage().reply(String.format(ToolSet.CP_ERR + Response.MISSING_PARAM.toString(), Callerphone.config.getPrefix())).queue();
            return;
        }

        final String CHANNELID = args[1];

        e.getMessage().reply(poolKick(e.getChannel().getId(), CHANNELID)).queue();
    }

    @Override
    public void runSlash(SlashCommandEvent e) {
        final Member MEMBER = e.getMember();

        if (ChannelPool.permissionCheck(MEMBER, e)) {
            return;
        }

        e.reply(poolKick(e.getChannel().getId(), e.getOption("target").getAsString())).queue();
    }

    private String poolKick(String requestID, String kickID) {
        PoolStatus stat = ChannelPool.removeChild(requestID, kickID);

        if (stat == PoolStatus.IS_CHILD) {

            return ToolSet.CP_EMJ + PoolResponse.NOT_HOSTING.toString();

        } else if (stat == PoolStatus.SUCCESS) {

            final TextChannel CHILD_CHANNEL = ToolSet.getTextChannel(kickID);
            if (CHILD_CHANNEL != null) {
                CHILD_CHANNEL.sendMessage(ToolSet.CP_EMJ + PoolResponse.KICKED_FROM_POOL.toString()).queue();
            }
            return String.format(ToolSet.CP_EMJ + PoolResponse.KICK_POOL_SUCCESS.toString(), kickID);

        } else if (stat == PoolStatus.NOT_FOUND) {

            return ToolSet.CP_EMJ + PoolResponse.REQUESTED_NOT_FOUND.toString();

        }

        return ToolSet.CP_ERR + Response.ERROR.toString();
    }

    @Override
    public String getHelp() {
        return "`" + Callerphone.config.getPrefix() + "kickchan <channel ID>` - Kick channel from pool.";
    }

    @Override
    public String[] getTriggers() {
        return "poolkick,kick,kickchannel,kickchan".split(",");
    }
}