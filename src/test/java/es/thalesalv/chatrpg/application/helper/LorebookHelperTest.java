package es.thalesalv.chatrpg.application.helper;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import es.thalesalv.chatrpg.domain.model.bot.LorebookEntry;
import es.thalesalv.chatrpg.domain.model.bot.World;
import es.thalesalv.chatrpg.testutils.TextMessageUtils;
import es.thalesalv.chatrpg.testutils.WorldTestUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Mentions;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.restaction.CacheRestAction;

@ExtendWith(MockitoExtension.class)
public class LorebookHelperTest {

    @InjectMocks
    private LorebookHelper lorebookHelper;

    @Test
    public void testChatModeLorebookEntries() {

        final World world = WorldTestUtils.buildSimplePublicWorld();
        final List<LorebookEntry> entries = world.getLorebook();
        final List<String> messages = TextMessageUtils.createChat();
        final List<String> processedMessages = lorebookHelper.chatModeLorebookEntries(entries, messages);
        Assertions.assertEquals("Test entry description: Test description", processedMessages.get(0));
    }

    @Test
    public void testRpgModeLorebookEntries_simpleEntry() {

        final JDA jda = mock(JDA.class);
        final World world = WorldTestUtils.buildSimplePublicWorld();
        final List<LorebookEntry> entries = world.getLorebook();
        final List<String> messages = TextMessageUtils.createChat();

        final List<String> processedMessages = lorebookHelper.rpgModeLorebookEntries(entries, messages, jda);
        assertEquals("Test entry description: Test description", processedMessages.get(0));
    }

    @Test
    @SuppressWarnings("all")
    public void testRpgModeLorebookEntries_playerEntry() {

        final JDA jda = mock(JDA.class);
        final CacheRestAction cacheRestAction = mock(CacheRestAction.class);
        final User user = mock(User.class);

        final List<LorebookEntry> entries = new ArrayList<>();
        final List<String> messages = TextMessageUtils.createChat();
        entries.add(LorebookEntry.builder()
                .name("Test entry")
                .description("Test description")
                .playerDiscordId("4723847234")
                .regex("test")
                .build());

        when(jda.retrieveUserById(anyString())).thenReturn(cacheRestAction);
        when(cacheRestAction.complete()).thenReturn(user);
        when(user.getAsTag()).thenReturn("ChatRPG");
        when(user.getName()).thenReturn("ChatRPG");

        final List<String> processedMessages = lorebookHelper.rpgModeLorebookEntries(entries, messages, jda);
        assertEquals("Test entry description: Test description", processedMessages.get(0));
    }

    @Test
    public void testHandlePlayerCharacterEntries_isPlayerSelf() {

        final User user = mock(User.class);
        final Mentions mentions = mock(Mentions.class);

        final List<LorebookEntry> entries = new ArrayList<>();
        entries.add(LorebookEntry.builder()
                .name("Narildon")
                .description("Test description")
                .playerDiscordId("4723847234")
                .regex("test")
                .build());

        final List<String> messages = TextMessageUtils.createChat();
        final World world = WorldTestUtils.buildSimplePublicWorld();
        world.setLorebook(entries);

        when(user.getId()).thenReturn("4723847234");
        when(user.getAsTag()).thenReturn("John");
        when(user.getName()).thenReturn("John");

        final List<String> result = lorebookHelper.handlePlayerCharacterEntries(entries, messages, user, mentions,
                world);

        assertTrue(result.get(0)
                .startsWith("Narildon said:"));
    }

    @Test
    public void testHandlePlayerCharacterEntries_isPlayerMentioned() {

        final User user = mock(User.class);
        final Mentions mentions = mock(Mentions.class);

        final List<LorebookEntry> entries = new ArrayList<>();
        entries.add(LorebookEntry.builder()
                .name("Narildon")
                .description("Test description")
                .playerDiscordId("4723847234")
                .regex("test")
                .build());

        List<User> users = new ArrayList<>();
        users.add(user);

        final List<String> messages = TextMessageUtils.createChat();
        messages.add("Martha said: @John, do you see this?");

        final World world = WorldTestUtils.buildSimplePublicWorld();
        world.setLorebook(entries);

        when(mentions.getUsers()).thenReturn(users);

        when(user.getId()).thenReturn("4723847234");
        when(user.getAsTag()).thenReturn("John");
        when(user.getName()).thenReturn("John");

        final List<String> result = lorebookHelper.handlePlayerCharacterEntries(entries, messages, user, mentions,
                world);

        assertTrue(result.get(0)
                .startsWith("Narildon said:"));

        assertTrue(result.get(6)
                .contains("@Narildon, do you see this?"));
    }

    @Test
    public void test() {

        final List<LorebookEntry> entries = new ArrayList<>();
        entries.add(LorebookEntry.builder()
                .name("Narildon")
                .description("Test description")
                .playerDiscordId("4723847234")
                .regex("Narildon")
                .build());

        entries.add(LorebookEntry.builder()
                .name("Bostildon")
                .description("Test description")
                .regex("Bostildon")
                .build());

        entries.add(LorebookEntry.builder()
                .name("Xirildon")
                .description("Test description")
                .regex("Xirildon")
                .build());

        final World world = WorldTestUtils.buildSimplePublicWorld();
        world.setLorebook(entries);

        final List<String> messages = TextMessageUtils.createChat();
        messages.add("Xirildon said: Does Bostildon like chocolate?");

        final List<LorebookEntry> result = lorebookHelper.handleEntriesMentioned(messages, world);
        assertTrue(result.size() == 2);
        result.forEach(entry -> {
            assertTrue(!entry.getName()
                    .equals("Narildon"));
        });
    }
}
