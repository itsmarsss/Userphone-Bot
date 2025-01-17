package com.marsss.callerphone.users.commands;

import com.marsss.callerphone.Callerphone;
import com.marsss.callerphone.ToolSet;
import com.marsss.commandType.ITextCommand;
import com.marsss.database.categories.Users;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.util.List;

public class RewardCredits implements ITextCommand {

    @Override
    public void runCommand(MessageReceivedEvent e) {
        if (!e.getAuthor().getId().equals(Callerphone.config.getOwnerID())) {
            e.getMessage().reply(ToolSet.CP_EMJ + "Run this command once you own this bot...").queue();
            return;
        }
        try {
            final String[] ARGS = e.getMessage().getContentRaw().split("\\s+");
            final List<User> MENTIONS = e.getMessage().getMentions().getUsers();
            final User USER = !MENTIONS.isEmpty() ? MENTIONS.get(0) : e.getAuthor();
            int amount;
            amount = Integer.parseInt(ARGS[1]);
            e.getMessage().reply(rewardCredits(USER, amount)).queue();
        } catch (Exception ex) {
            ex.printStackTrace();
            e.getMessage().reply(ToolSet.CP_EMJ + "`/rewardcreds <amount> <@user>`").queue();
        }
    }

    private String rewardCredits(User user, int amount) {
        Users.reward(user.getId(), amount);
        return ToolSet.CP_EMJ + "Rewarded `\u23E3 " + amount + "` to " + user.getAsMention();
    }

    @Override
    public String getHelp() {
        return "Admin command";
    }

    @Override
    public String[] getTriggers() {
        return "reward,givecreds,rewardcreds".split(",");
    }
}
