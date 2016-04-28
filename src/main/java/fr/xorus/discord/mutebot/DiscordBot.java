package fr.xorus.discord.mutebot;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.JDAInfo;
import net.dv8tion.jda.Permission;
import net.dv8tion.jda.entities.Channel;
import net.dv8tion.jda.entities.User;
import net.dv8tion.jda.entities.VoiceChannel;
import net.dv8tion.jda.entities.impl.JDAImpl;
import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.hooks.EventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

/**
 * Simple discord bot who connects to a guild then can server mute / unmute everyone in a specified room
 */
public class DiscordBot implements EventListener {
    /**
     * discord client
     */
    private JDA client;

    /**
     * Logger
     */
    private static Log logger = LogFactory.getLog(DiscordBot.class.getName());

    /**
     * discord target voice channel
     */
    private Channel targetChannel = null;

    private List<String> excludedUserIds;

    private DiscordConfiguration configuration;

    /**
     * Initializes the discord client
     */
    public DiscordBot(DiscordConfiguration configuration) {
        this.configuration = configuration;
        if (configuration.getToken().isEmpty()) {
            logger.info("discord bot token isn't set, disabling.");
            return;
        }
        JDABuilder builder = new JDABuilder();
        builder.setAudioEnabled(false);
        builder.setBotToken(configuration.getToken());
        builder.addListener(this);
        try {
            builder.buildBlocking();
        } catch (LoginException e) {
            logger.error("Could not create discord client : " + e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        excludedUserIds = Arrays.asList(configuration.getExcludedUsers());
    }

    public void onEvent(Event event) {
        if (event == null) {
            logger.error("empty event");
            return;
        }
        if (event instanceof ReadyEvent) {
            client = event.getJDA();
            logger.info("Connected to discord as " + event.getJDA().getSelfInfo().getUsername());

            VoiceChannel voiceChannel = client.getVoiceChannelById(configuration.getTargetChannel());
            if (voiceChannel == null) {
                logger.error("Could not connect to voice channel #" + configuration.getTargetChannel()
                + "\nDon't forget to register the bot at :" + this.getRegisterUrl());
                return;
            }
            targetChannel = voiceChannel;
        }
    }

    private void workaroundSetVoiceStatus(User user, boolean mute, boolean deaf) {
        JSONObject payload = new JSONObject();
        payload.put("mute", mute);
        payload.put("deaf", deaf);

        String uri = "https://discordapp.com/api/guilds/" + targetChannel.getGuild().getId() + "/members/" +
                user.getId();

        ((JDAImpl) client).getRequester().patch(uri, payload);
    }

    public void shutdown() {
        client.shutdown(true);
    }

    public boolean muteAll(boolean mute) {
        if (targetChannel == null) {
            logger.error("Channel not initialised");
            return false;
        }
        if (mute) {
            client.getAccountManager().setGame("audio interrupt");
        } else {
            client.getAccountManager().setGame(null);
        }

        for (User user : targetChannel.getUsers()) {
            if (!excludedUserIds.contains(user.getId())) {
                workaroundSetVoiceStatus(user, mute, false);
            }
        }
        return true;
    }

    public String getRegisterUrl() {
        int permission = 0x0400000; // MUTE_OTHERS

        return "https://discordapp.com/oauth2/authorize?&client_id=" + configuration.getBotId()
                + "&scope=bot&permissions=" + permission;
    }
}
