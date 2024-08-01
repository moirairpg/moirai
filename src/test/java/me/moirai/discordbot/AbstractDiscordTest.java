package me.moirai.discordbot;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public abstract class AbstractDiscordTest {

    @Mock
    protected SelfUser selfUser;

    @Mock
    protected User user;

    @Mock
    protected Member member;

    @Mock
    protected Guild guild;

    @Mock
    protected Message message;

    @Mock
    protected TextChannel textChannel;

    @Mock
    protected MessageChannelUnion channelUnion;

    @Mock
    protected JDA jda;
}
