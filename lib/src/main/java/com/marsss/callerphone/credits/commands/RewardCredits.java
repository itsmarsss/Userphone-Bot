package com.marsss.callerphone.credits.commands;

import com.marsss.ICommand;
import com.marsss.callerphone.Callerphone;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public class RewardCredits implements ICommand {

    private final String CP_EMJ = Callerphone.Callerphone;

    @Override
    public void runCommand(GuildMessageReceivedEvent e) {
        if (!e.getAuthor().getId().equals(Callerphone.owner)) {
            e.getMessage().reply(CP_EMJ + "Run this command once you own this bot...").queue();
            return;
        }
        final String[] ARGS = e.getMessage().getContentRaw().split("\\s+");
        final List<User> MENTIONS = e.getMessage().getMentionedUsers();
        final User USER = MENTIONS.size() > 0 ? MENTIONS.get(0) : e.getAuthor();
        int amount;
        try {
            amount = Integer.parseInt(ARGS[1]);
        } catch (Exception ex) {
            ex.printStackTrace();
            e.getMessage().reply(CP_EMJ+"`c?rewardcreds <amount> <@user>`").queue();
            return;
        }
        e.getMessage().reply(rewardCredits(USER, amount)).queue();
    }

    @Override
    public void runSlash(SlashCommandEvent e) {}

    private String rewardCredits(User user, int amount) {
        Callerphone.reward(user, amount);
        return CP_EMJ + "Rewarded `\u00A9 " + amount + "` to " + user.getAsMention();
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
