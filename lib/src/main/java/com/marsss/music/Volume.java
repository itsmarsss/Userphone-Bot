package com.marsss.music;

import com.marsss.Bot;
import com.marsss.music.lavaplayer.GuildMusicManager;
import com.marsss.music.lavaplayer.PlayerManager;
import com.marsss.utils.Help;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public class Volume {
	public static void volume(GuildMessageReceivedEvent event) {
		final Member self = event.getGuild().getSelfMember();
		final GuildVoiceState selfVoiceState = self.getVoiceState();
		final Message MESSAGE = event.getMessage();

		if (!selfVoiceState.inVoiceChannel()) {
			MESSAGE.reply("I need to be in a voice channel for this command to work").queue();
			return;
		}

		final Member member = event.getMember();
		final GuildVoiceState memberVoiceState = member.getVoiceState();

		if (!memberVoiceState.inVoiceChannel()) {
			MESSAGE.reply("You need to be in a voice channel for this command to work").queue();
			return;
		}

		if (!memberVoiceState.getChannel().equals(selfVoiceState.getChannel())) {
			MESSAGE.reply("You need to be in the same voice channel as me for this command to work").queue();
			return;
		}

		final GuildMusicManager musicManager = PlayerManager.getInstance().getMusicManager(event.getGuild());
		final AudioPlayer audioPlayer = musicManager.audioPlayer;
		
		int volume;
		try {
			volume = Integer.parseInt(event.getMessage().getContentRaw().split("\\s+")[1]);
		}catch(Exception e) {
			MESSAGE.replyEmbeds(Help.help("volume", false)).queue();
			return;
		}

		audioPlayer.setVolume(volume);

		MESSAGE.addReaction(Bot.ThumbsUp).queue();
		MESSAGE.reply("Set volume to `" + volume + "`").queue();
	}
	public static String getHelp() {
		return "`" + Bot.Prefix + "volume <volume>` - Sets volume of audio player.";
	}
}