package com.marsss.callerphone.channelpool.commands;

import com.marsss.ICommand;
import com.marsss.callerphone.Callerphone;
import com.marsss.callerphone.Response;
import com.marsss.callerphone.ToolSet;
import com.marsss.callerphone.channelpool.ChannelPool;
import com.marsss.callerphone.channelpool.PoolResponse;
import com.marsss.callerphone.channelpool.PoolStatus;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class LeavePool implements ICommand {
    @Override
    public void runCommand(GuildMessageReceivedEvent e) {
        final Member MEMBER = e.getMember();

        if (ChannelPool.permissionCheck(MEMBER, e.getMessage())) {
            return;
        }

        e.getMessage().reply(leavePool(e.getChannel().getId())).queue();
    }

    @Override
    public void runSlash(SlashCommandEvent e) {
        final Member MEMBER = e.getMember();

        if (ChannelPool.permissionCheck(MEMBER, e)) {
            return;
        }

        e.reply(leavePool(e.getChannel().getId())).queue();
    }

    private String leavePool(String id) {
        PoolStatus stat = ChannelPool.leavePool(id);

        if (stat == PoolStatus.IS_HOST) {

            return ToolSet.CP_EMJ + PoolResponse.ALREADY_HOSTING +
                    String.format(PoolResponse.POOL_ID.toString(), id) + "\n" +
                    (ChannelPool.hasPassword(id)
                            ? String.format(PoolResponse.POOL_PWD.toString(), ChannelPool.getPassword(id))
                            : String.format(PoolResponse.POOL_SET_PWD.toString(), Callerphone.config.getPrefix())) + "\n" +
                    String.format(PoolResponse.POOL_END_WITH.toString(), Callerphone.config.getPrefix());

        } else if (stat == PoolStatus.SUCCESS) {

            return ToolSet.CP_EMJ + PoolResponse.LEAVE_POOL_SUCCESS.toString();

        } else if (stat == PoolStatus.NOT_FOUND) {

            return ToolSet.CP_EMJ + PoolResponse.NOT_IN_POOL.toString();

        }

        return ToolSet.CP_ERR + Response.ERROR.toString();
    }

    @Override
    public String getHelp() {
        return "`" + Callerphone.config.getPrefix() + "leavepool` - Leave a channel pool.";
    }

    @Override
    public String[] getTriggers() {
        return "leave,leavepool,exitpool".split(",");
    }
}
