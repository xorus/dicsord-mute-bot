package fr.xorus.discord.mutebot;

import java.util.Arrays;

/**
 * Configuration data class for the discord channel mute bot
 */
public class DiscordConfiguration {
    /**
     * Login token
     */
    private String token;

    /**
     * Bot id
     */
    private String botId;

    /**
     * Target server id
     */
    private String targetGuild;

    /**
     * List of user ids that should not be muted
     */
    private String[] excludedUsers;

    /**
     * Target channel id
     */
    private String targetChannel;

    public DiscordConfiguration() {
    }

    public String getToken() {
        return token;
    }

    public DiscordConfiguration setToken(String token) {
        this.token = token;
        return this;
    }

    public String getTargetGuild() {
        return targetGuild;
    }

    public DiscordConfiguration setTargetGuild(String targetGuild) {
        this.targetGuild = targetGuild;
        return this;
    }

    public String[] getExcludedUsers() {
        return excludedUsers;
    }

    public DiscordConfiguration setExcludedUsers(String[] excludedUsers) {
        this.excludedUsers = excludedUsers;
        return this;
    }

    public String getTargetChannel() {
        return targetChannel;
    }

    public DiscordConfiguration setTargetChannel(String targetChannel) {
        this.targetChannel = targetChannel;
        return this;
    }

    public String getBotId() {
        return botId;
    }

    public DiscordConfiguration setBotId(String botId) {
        this.botId = botId;
        return this;
    }

    @Override
    public String toString() {
        return "DiscordConfiguration{" +
                "token='" + token + '\'' +
                ", targetGuild='" + targetGuild + '\'' +
                ", excludedUsers=" + Arrays.toString(excludedUsers) +
                ", targetChannel='" + targetChannel + '\'' +
                '}';
    }
}
