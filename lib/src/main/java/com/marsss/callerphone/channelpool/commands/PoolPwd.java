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

public class PoolPwd implements ICommand {

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

        final String PWD = args[1];

        e.getMessage().reply(poolPwd(e.getChannel().getId(), PWD)).queue();
    }

    @Override
    public void runSlash(SlashCommandEvent e) {
        final Member MEMBER = e.getMember();

        if (ChannelPool.permissionCheck(MEMBER, e)) {
            return;
        }

        e.reply(poolPwd(e.getChannel().getId(), e.getOption("password").getAsString())).setEphemeral(true).queue();
    }

    private String poolPwd(String id, String pwd) {
        if (pwd.equals("none"))
            pwd = "";

        PoolStatus stat = ChannelPool.setPassword(id, pwd);

        if (stat == PoolStatus.SUCCESS) {

            return ToolSet.CP_EMJ + String.format(PoolResponse.POOL_PWD.toString(), pwd);

        } else if (stat == PoolStatus.NOT_FOUND) {

            return ToolSet.CP_EMJ + PoolResponse.NOT_HOSTING.toString();

        }

        return ToolSet.CP_ERR + Response.ERROR.toString();
    }

    @Override
    public String getHelp() {
        return "`" + Callerphone.config.getPrefix() + "poolpwd <password | \"none\" for no password>` - Set channel pool password.";
    }

    @Override
    public String[] getTriggers() {
        return "password,pass,pwd,poolpass,poolpwd,poolpassword".split(",");
    }
}
