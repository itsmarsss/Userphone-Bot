package com.marsss.callerphone.tccallerphone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.marsss.callerphone.Callerphone;
import com.marsss.callerphone.tccallerphone.ConvoStorage.Convo;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

public class TCCallPairer {
    private static final String cpEmj = Callerphone.Callerphone;

    public static void onCallCommand(TextChannel tcchannel, Message message, boolean cens, boolean anon) {
        final Logger logger = LoggerFactory.getLogger(TCCallPairer.class);
        final String CHANNELID = tcchannel.getId();
        final JDA jda = com.marsss.callerphone.Callerphone.jda;
        for (int i = 0; i < ConvoStorage.convo.length; i++) {
            final Convo convo = ConvoStorage.convo[i];
            if (!convo.getConnected()) {
                if (!convo.getCallerTCID().equals("empty")) {
                    if (!cens) {
                        tcchannel.sendMessage("This chat will be uncensored, if you do not wish to proceed please run `" + Callerphone.prefix + "endchat`").queue();
                    }
                    convo.setRFF(cens);
                    convo.setRAnon(anon);
                    convo.setReceiverTCID(CHANNELID);
                    convo.setConnected(true);

                    jda.getTextChannelById(convo.getCallerTCID()).sendMessage(cpEmj + "Someone picked up the phone!").queue();

                    message.reply(cpEmj + "Calling...").queue();
                    tcchannel.sendMessage(cpEmj + "Someone picked up the phone!").queue();

                    logger.info("From TC: " + convo.getCallerTCID() + " - To TC: " + convo.getReceiverTCID());
                    logger.info("From Guild: " + jda.getTextChannelById(convo.getCallerTCID()).getGuild().getId() + " - To Guild: " + jda.getTextChannelById(convo.getReceiverTCID()).getGuild().getId());
                    convo.setLastMessage(System.currentTimeMillis());
                    return;
                } else if (convo.getCallerTCID().equals("empty")) {
                    if (!cens) {
                        tcchannel.sendMessage("This chat will be uncensored, if you do not wish to proceed please run `" + Callerphone.prefix + "endchat`").queue();
                    }
                    convo.setCFF(cens);
                    convo.setCAnon(anon);
                    convo.setCallerTCID(CHANNELID);
                    message.reply(cpEmj + "Calling...").queue();
                    return;
                }
            }
        }
        message.reply(cpEmj + "Hmmm, I was unable to find an open port!").queue();
        logger.warn("Port not found");
    }

    public static String callHelp() {
        return "`" + Callerphone.Prefix + "chat <anon/empty>` - Chat with someone from another server with text.";

    }

    public static String uncenscallHelp() {
        return "`" + Callerphone.Prefix + "chatuncens <anon/empty>` - Chat with someone from another server with text. (uncensored)";

    }

    public static String hangupHelp() {
        return "`" + Callerphone.Prefix + "endchat` - Hangup a pending or existing chat.";

    }

    public static String reportHelp() {
        return "`" + Callerphone.Prefix + "reportchat` - Report a chat, make sure to report during a call.";
    }

}
