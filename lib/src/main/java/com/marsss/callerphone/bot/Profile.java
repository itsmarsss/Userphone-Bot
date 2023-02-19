package com.marsss.callerphone.bot;

import com.marsss.ICommand;
import com.marsss.callerphone.Callerphone;
import com.marsss.callerphone.Response;
import com.marsss.callerphone.ToolSet;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;
import java.time.Instant;
import java.util.List;

public class Profile implements ICommand {
    @Override
    public void runCommand(GuildMessageReceivedEvent e) {
        final List<User> MENTIONS = e.getMessage().getMentionedUsers();
        final User USER = MENTIONS.size() > 0 ? MENTIONS.get(0) : e.getAuthor();

        e.getMessage().replyEmbeds(profile(USER)).queue();
    }

    @Override
    public void runSlash(SlashCommandEvent e) {
        e.replyEmbeds(profile(e.getOption("target").getAsUser())).queue();
    }

    private MessageEmbed profile(User user) {
        final long EXECUTED = Callerphone.getExecuted(user);
        final long TRANSMITTED = Callerphone.getTransmitted(user);
        final long TOTAL = EXECUTED + TRANSMITTED;
        final int LVL = (int) TOTAL / 100;
        final int EXP = (int) TOTAL - 100 * LVL;
        final String PREFIX = Callerphone.prefix.getOrDefault(user.getId(), (LVL > 5 ? ":unlock: `" + Callerphone.Prefix + "prefix <prefix>`" : ":lock: `Level 50`"));

        String general = String.format(Response.PROFILE_GENERAL.toString(), LVL, EXP, PREFIX);
        String credits = String.format(Response.PROFILE_CREDITS.toString(), Callerphone.getCredits(user), 0, 0);
        String message = String.format(Response.PROFILE_MESSAGE.toString(), EXECUTED, TRANSMITTED, TOTAL);

        EmbedBuilder proEmd = new EmbedBuilder()
                .setTitle("**" + user.getName() + "'s Profile**")
                .setThumbnail(user.getAvatarUrl())
                .addField("**General**", general, true)
                .addField("**Credits**", credits, true)
                .addField("**Messages**", message, true)
                .addField("**Pool and Chat credit cooldowns**", ((System.currentTimeMillis() - Callerphone.getUserCooldown(user)) < ToolSet.CREDIT_COOLDOWN ? ":alarm_clock: " + ((ToolSet.CREDIT_COOLDOWN - (System.currentTimeMillis() - Callerphone.getUserCooldown(user))) / 1000) + " seconds" : ":white_check_mark: None"), true)
                .setFooter("Your Profile", Callerphone.jda.getSelfUser().getAvatarUrl())
                .setTimestamp(Instant.now())
                .setColor(new Color(114, 137, 218));

        return proEmd.build();
    }

    @Override
    public String getHelp() {
        return "`" + Callerphone.Prefix + "profile` - View your profile with Callerphone.";
    }

    @Override
    public String[] getTriggers() {
        return "profile,me,myself,aboutme,myprofile,stats".split(",");
    }
}
