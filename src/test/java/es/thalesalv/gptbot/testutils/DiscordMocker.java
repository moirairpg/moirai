package es.thalesalv.gptbot.testutils;

import java.util.EnumSet;
import java.util.List;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.attribute.IThreadContainer;
import net.dv8tion.jda.api.entities.channel.concrete.NewsChannel;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.managers.AccountManager;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;

public class DiscordMocker {

    public static User buildUser() {

        return new User() {
            @Override
            public String getName() {
                return "Malaquias";
            }

            @Override
            public String getAsMention() {
                throw new UnsupportedOperationException("Unimplemented method 'getAsMention'");
            }

            @Override
            public long getIdLong() {
                throw new UnsupportedOperationException("Unimplemented method 'getIdLong'");
            }

            @Override
            public String getDiscriminator() {
                throw new UnsupportedOperationException("Unimplemented method 'getDiscriminator'");
            }

            @Override
            public String getAvatarId() {
                throw new UnsupportedOperationException("Unimplemented method 'getAvatarId'");
            }

            @Override
            public String getDefaultAvatarId() {
                throw new UnsupportedOperationException("Unimplemented method 'getDefaultAvatarId'");
            }

            @Override
            public CacheRestAction<Profile> retrieveProfile() {
                throw new UnsupportedOperationException("Unimplemented method 'retrieveProfile'");
            }

            @Override
            public String getAsTag() {
                throw new UnsupportedOperationException("Unimplemented method 'getAsTag'");
            }

            @Override
            public boolean hasPrivateChannel() {
                throw new UnsupportedOperationException("Unimplemented method 'hasPrivateChannel'");
            }

            @Override
            public CacheRestAction<PrivateChannel> openPrivateChannel() {
                throw new UnsupportedOperationException("Unimplemented method 'openPrivateChannel'");
            }

            @Override
            public List<Guild> getMutualGuilds() {
                throw new UnsupportedOperationException("Unimplemented method 'getMutualGuilds'");
            }

            @Override
            public boolean isBot() {
                throw new UnsupportedOperationException("Unimplemented method 'isBot'");
            }

            @Override
            public boolean isSystem() {
                throw new UnsupportedOperationException("Unimplemented method 'isSystem'");
            }

            @Override
            public JDA getJDA() {
                throw new UnsupportedOperationException("Unimplemented method 'getJDA'");
            }

            @Override
            public EnumSet<UserFlag> getFlags() {
                throw new UnsupportedOperationException("Unimplemented method 'getFlags'");
            }

            @Override
            public int getFlagsRaw() {
                throw new UnsupportedOperationException("Unimplemented method 'getFlagsRaw'");
            }
        };
    }

    public static SelfUser buildSelfUser() {

        return new SelfUser() {

            @Override
            public String getName() {
                return "Malaquias";
            }

            @Override
            public String getDiscriminator() {
                throw new UnsupportedOperationException("Unimplemented method 'getDiscriminator'");
            }

            @Override
            public String getAvatarId() {
                throw new UnsupportedOperationException("Unimplemented method 'getAvatarId'");
            }

            @Override
            public String getDefaultAvatarId() {
                throw new UnsupportedOperationException("Unimplemented method 'getDefaultAvatarId'");
            }

            @Override
            public CacheRestAction<Profile> retrieveProfile() {
                throw new UnsupportedOperationException("Unimplemented method 'retrieveProfile'");
            }

            @Override
            public String getAsTag() {
                throw new UnsupportedOperationException("Unimplemented method 'getAsTag'");
            }

            @Override
            public boolean hasPrivateChannel() {
                throw new UnsupportedOperationException("Unimplemented method 'hasPrivateChannel'");
            }

            @Override
            public CacheRestAction<PrivateChannel> openPrivateChannel() {
                throw new UnsupportedOperationException("Unimplemented method 'openPrivateChannel'");
            }

            @Override
            public List<Guild> getMutualGuilds() {
                throw new UnsupportedOperationException("Unimplemented method 'getMutualGuilds'");
            }

            @Override
            public boolean isBot() {
                throw new UnsupportedOperationException("Unimplemented method 'isBot'");
            }

            @Override
            public boolean isSystem() {
                throw new UnsupportedOperationException("Unimplemented method 'isSystem'");
            }

            @Override
            public JDA getJDA() {
                throw new UnsupportedOperationException("Unimplemented method 'getJDA'");
            }

            @Override
            public EnumSet<UserFlag> getFlags() {
                throw new UnsupportedOperationException("Unimplemented method 'getFlags'");
            }

            @Override
            public int getFlagsRaw() {
                throw new UnsupportedOperationException("Unimplemented method 'getFlagsRaw'");
            }

            @Override
            public String getAsMention() {
                throw new UnsupportedOperationException("Unimplemented method 'getAsMention'");
            }

            @Override
            public long getIdLong() {
                throw new UnsupportedOperationException("Unimplemented method 'getIdLong'");
            }

            @Override
            public long getApplicationIdLong() {
                throw new UnsupportedOperationException("Unimplemented method 'getApplicationIdLong'");
            }

            @Override
            public boolean isVerified() {
                throw new UnsupportedOperationException("Unimplemented method 'isVerified'");
            }

            @Override
            public boolean isMfaEnabled() {
                throw new UnsupportedOperationException("Unimplemented method 'isMfaEnabled'");
            }

            @Override
            public long getAllowedFileSize() {
                throw new UnsupportedOperationException("Unimplemented method 'getAllowedFileSize'");
            }

            @Override
            public AccountManager getManager() {
                throw new UnsupportedOperationException("Unimplemented method 'getManager'");
            }

        };
    }

    public static MessageChannelUnion buildChannel() {

        return new MessageChannelUnion() {

            @Override
            public long getIdLong() {
                return 1212;
            }

            @Override
            public String getName() {
                return "ChannelName";
            }

            @Override
            public long getLatestMessageIdLong() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getLatestMessageIdLong'");
            }

            @Override
            public boolean canTalk() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'canTalk'");
            }

            @Override
            public ChannelType getType() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getType'");
            }

            @Override
            public JDA getJDA() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getJDA'");
            }

            @Override
            public RestAction<Void> delete() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'delete'");
            }

            @Override
            public PrivateChannel asPrivateChannel() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'asPrivateChannel'");
            }

            @Override
            public TextChannel asTextChannel() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'asTextChannel'");
            }

            @Override
            public NewsChannel asNewsChannel() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'asNewsChannel'");
            }

            @Override
            public ThreadChannel asThreadChannel() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'asThreadChannel'");
            }

            @Override
            public VoiceChannel asVoiceChannel() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'asVoiceChannel'");
            }

            @Override
            public IThreadContainer asThreadContainer() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'asThreadContainer'");
            }

            @Override
            public GuildMessageChannel asGuildMessageChannel() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'asGuildMessageChannel'");
            }
        };
    }
}
