package com.marsss.callerphone.bot;

import com.marsss.callerphone.Callerphone;
import com.marsss.callerphone.ToolSet;
import com.marsss.callerphone.channelpool.commands.*;
import com.marsss.callerphone.msginbottle.commands.FindBottle;
import com.marsss.callerphone.msginbottle.commands.SendBottle;
import com.marsss.callerphone.msginbottle.commands.ViewBottle;
import com.marsss.callerphone.tccallerphone.commands.Chat;
import com.marsss.callerphone.tccallerphone.commands.EndChat;
import com.marsss.callerphone.tccallerphone.commands.Prefix;
import com.marsss.callerphone.tccallerphone.commands.ReportChat;
import com.marsss.callerphone.users.commands.Profile;
import com.marsss.callerphone.utils.*;
import com.marsss.commandType.ISlashCommand;
import com.marsss.database.categories.Users;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

public class Help implements ISlashCommand {
    @Override
    public void runSlash(SlashCommandInteractionEvent e) {
        final boolean ADMIN = Users.isModerator(e.getUser().getId());
        final List<OptionMapping> PARAM = e.getOptions();
        if (PARAM.isEmpty()) {
            e.replyEmbeds(help("", ADMIN)).queue();
            return;
        }
        e.replyEmbeds(help(PARAM.get(0).getAsString(), ADMIN)).queue();
    }

    public MessageEmbed help(String name, boolean admin) {
        if (name.isEmpty()) {
            return helpCategories(admin);
        }

        String TITLE = "Sorry.";

        String DESC = "I don't recognize that category/command :(";
        name = name.toLowerCase();


        // Categories

        switch (name) {


            case "bot":
                TITLE = "Bot Commands";
                DESC = new About().getHelp() + "\n"
                        + new BotInfo().getHelp() + "\n"
                        + new Donate().getHelp() + "\n"
                        + new Help().getHelp() + "\n"
                        + new Invite().getHelp() + "\n"
                        + new Profile().getHelp();
                break;


            case "utils":
                TITLE = "Util Commands";
                DESC = new BotInfo().getHelp() + "\n"
                        + new ServerInfo().getHelp() + "\n"
                        + new ChannelInfo().getHelp() + "\n"
                        + new RoleInfo().getHelp() + "\n"
                        + new UserInfo().getHelp() + "\n"
                        + new Colour().getHelp() + "\n"
                        + new Search().getHelp();
                break;


            case "pooling":
                TITLE = "Channel Pooling Commands";
                DESC = new HostPool().getHelp() + "\n"
                        + new JoinPool().getHelp() + "\n"
                        + new EndPool().getHelp() + "\n"
                        + new LeavePool().getHelp() + "\n"
                        + new KickPool().getHelp() + "\n"
                        + new PoolParticipants().getHelp() + "\n"
                        + new PoolSettings().getHelp();
                break;


            case "tccall":
                TITLE = "TCCall Commands";
                DESC = new Chat().getHelp() + "\n"
                        + new EndChat().getHelp() + "\n"
                        + new ReportChat().getHelp() + "\n"
                        + new Prefix();
                break;

            case "msgbottle":
                TITLE = "Message In Bottle";
                DESC = new SendBottle().getHelp() + "\n"
                        + new FindBottle().getHelp() + "\n"
                        + new ViewBottle().getHelp();
                break;

            case "music":
                TITLE = "Music Commands";
                DESC = "Callerphone no longer can play music, however I've created a new bot called **Tunes**...\nJoin [this](" + Callerphone.config.getSupportServer() + ") server for more information!";
                break;


            case "creds":
                TITLE = "**EARN CREDITS**";
                DESC = "__Commands:__" +
                        "\n> Message ~ `\u23E3 1`" +
                        "\n> Slash ~ `\u23E3 2`" +
                        "\n\n__Messages:__" +
                        "\n> Channel Pool ~ `\u23E3 3`" +
                        "\n> Channel Chat ~ `\u23E3 5`" +
                        "\n\n__Other:__" +
                        "\n> Bug Report ~ `\u23E3 5,000`" +
                        "\n\n**NOTE:** Channel Pool/Chat can be earned a maximum of once per " + (ToolSet.CREDIT_COOLDOWN / 1000) + " seconds. *(Spam prevention)*";
                break;

            case "exp":
                TITLE = "**EARN EXPERIENCE**";
                DESC = "__**Temporary:**__" +
                        "\n> Each level required 100 exp, and each command/message transferred are worth 1 exp.";
                break;


        }


        if (Callerphone.cmdMap.containsKey(name)) {
            final String[] TRIGGERS = Callerphone.cmdMap.get(name).getTriggers();
            final StringBuilder TRIGGER = new StringBuilder();
            for (String trig : TRIGGERS) {
                TRIGGER.append(trig).append(", ");
            }

            TITLE = TRIGGER.substring(0, TRIGGER.length() - 2);
            DESC = Callerphone.cmdMap.get(name).getHelp();

            if (TRIGGER.toString().contains("search")) {
                DESC += "\nWe use Duckduckgo, so click [here](https://help.duckduckgo.com/duckduckgo-help-pages/results/syntax/) for searching syntax!";
            }
        }

        EmbedBuilder helpEmbed = new EmbedBuilder()
                .setTitle(TITLE)
                .setDescription(DESC)
                .setFooter("Hope you found this useful!", Callerphone.selfUser.getAvatarUrl())
                .setColor(ToolSet.COLOR);

        return helpEmbed.build();
    }

    private MessageEmbed helpCategories(boolean admin) {
        EmbedBuilder categoryEmbed = new EmbedBuilder()
                .setColor(ToolSet.COLOR)
                .setTitle("Categories")
                .addField("Bot", "all commands related to the bot will be here, do `/help bot` for more information", false)
                .addField("Utils", "all utility commands will be in this category, do `/help utils` for more information", false)
                .addField("Pooling", "all channel pooling commands will be in this category, do `/help pooling` for more information", false)
                .addField("TC Callerphone", "all text call commands will be in this category, do `/help tccall` for more information", false)
                .addField("Msg Bottles", "all message in bottle commands will be in this category, do `/help msgbottle` for more information", false)
                .addField("Music", "Callerphone no longer can play music", false)
                .setFooter("Type `/help <category name>` to see category commands");
        if (admin) {
            categoryEmbed.addField("Moderator only", "all moderator commands will be in this category, do `" + Callerphone.config.getPrefix() + "help mod` in dm for more information", false);
        }
        return categoryEmbed.build();
    }

    @Override
    public String getHelp() {
        return "</help:1075169172423720970> - help help help";
    }

    @Override
    public String[] getTriggers() {
        return "help,gethelp,helpmeahhh".split(",");
    }

    @Override
    public SlashCommandData getCommandData() {
        return Commands.slash(getTriggers()[0], getHelp().split(" - ")[1])
                .addOptions(
                        new OptionData(OptionType.STRING, "term", "Search term")
                );
    }
}
