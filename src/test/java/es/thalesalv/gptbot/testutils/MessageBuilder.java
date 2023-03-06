package es.thalesalv.gptbot.testutils;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.unions.GuildMessageChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.sticker.StickerItem;
import net.dv8tion.jda.api.interactions.components.LayoutComponent;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.AuditableRestAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.ThreadChannelAction;
import net.dv8tion.jda.api.requests.restaction.pagination.ReactionPaginationAction;
import net.dv8tion.jda.api.utils.AttachedFile;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Formatter;
import java.util.List;

public class MessageBuilder {
    public static Message getMessage() {
        return new Message() {
            @Override
            public long getIdLong() {
                return 0;
            }

            @Override
            public void formatTo(Formatter formatter, int flags, int width, int precision) {

            }

            @Override
            public MessageReference getMessageReference() {
                return null;
            }

            @Override
            public Mentions getMentions() {
                return null;
            }

            @Override
            public boolean isEdited() {
                return false;
            }

            @Override
            public OffsetDateTime getTimeEdited() {
                return null;
            }

            @Override
            public User getAuthor() {
                return null;
            }

            @Override
            public Member getMember() {
                return null;
            }

            @Override
            public int getApproximatePosition() {
                return 0;
            }

            @Override
            public String getJumpUrl() {
                return null;
            }

            @Override
            public String getContentDisplay() {
                return null;
            }

            @Override
            public String getContentRaw() {
                return null;
            }

            @Override
            public String getContentStripped() {
                return null;
            }

            @Override
            public List<String> getInvites() {
                return null;
            }

            @Override
            public String getNonce() {
                return null;
            }

            @Override
            public boolean isFromType(ChannelType type) {
                return false;
            }

            @Override
            public ChannelType getChannelType() {
                return null;
            }

            @Override
            public boolean isWebhookMessage() {
                return false;
            }

            @Override
            public long getApplicationIdLong() {
                return 0;
            }

            @Override
            public MessageChannelUnion getChannel() {
                return null;
            }

            @Override
            public GuildMessageChannelUnion getGuildChannel() {
                return null;
            }

            @Override
            public Category getCategory() {
                return null;
            }

            @Override
            public Guild getGuild() {
                return null;
            }

            @Override
            public List<Attachment> getAttachments() {
                return null;
            }

            @Override
            public List<MessageEmbed> getEmbeds() {
                return null;
            }

            @Override
            public List<LayoutComponent> getComponents() {
                return null;
            }

            @Override
            public List<MessageReaction> getReactions() {
                return null;
            }

            @Override
            public List<StickerItem> getStickers() {
                return null;
            }

            @Override
            public boolean isTTS() {
                return false;
            }

            @Override
            public MessageActivity getActivity() {
                return null;
            }

            @Override
            public MessageEditAction editMessage(CharSequence newContent) {
                return null;
            }

            @Override
            public MessageEditAction editMessage(MessageEditData data) {
                return null;
            }

            @Override
            public MessageEditAction editMessageEmbeds(Collection<? extends MessageEmbed> embeds) {
                return null;
            }

            @Override
            public MessageEditAction editMessageComponents(Collection<? extends LayoutComponent> components) {
                return null;
            }

            @Override
            public MessageEditAction editMessageFormat(String format, Object... args) {
                return null;
            }

            @Override
            public MessageEditAction editMessageAttachments(Collection<? extends AttachedFile> attachments) {
                return null;
            }

            @Override
            public AuditableRestAction<Void> delete() {
                return null;
            }

            @Override
            public JDA getJDA() {
                return null;
            }

            @Override
            public boolean isPinned() {
                return false;
            }

            @Override
            public RestAction<Void> pin() {
                return null;
            }

            @Override
            public RestAction<Void> unpin() {
                return null;
            }

            @Override
            public RestAction<Void> addReaction(Emoji emoji) {
                return null;
            }

            @Override
            public RestAction<Void> clearReactions() {
                return null;
            }

            @Override
            public RestAction<Void> clearReactions(Emoji emoji) {
                return null;
            }

            @Override
            public RestAction<Void> removeReaction(Emoji emoji) {
                return null;
            }

            @Override
            public RestAction<Void> removeReaction(Emoji emoji, User user) {
                return null;
            }

            @Override
            public ReactionPaginationAction retrieveReactionUsers(Emoji emoji) {
                return null;
            }

            @Override
            public MessageReaction getReaction(Emoji emoji) {
                return null;
            }

            @Override
            public AuditableRestAction<Void> suppressEmbeds(boolean suppressed) {
                return null;
            }

            @Override
            public RestAction<Message> crosspost() {
                return null;
            }

            @Override
            public boolean isSuppressedEmbeds() {
                return false;
            }

            @Override
            public EnumSet<MessageFlag> getFlags() {
                return null;
            }

            @Override
            public long getFlagsRaw() {
                return 0;
            }

            @Override
            public boolean isEphemeral() {
                return false;
            }

            @Override
            public boolean isSuppressedNotifications() {
                return false;
            }

            @Override
            public ThreadChannel getStartedThread() {
                return null;
            }

            @Override
            public MessageType getType() {
                return null;
            }

            @Override
            public Interaction getInteraction() {
                return null;
            }

            @Override
            public ThreadChannelAction createThreadChannel(String name) {
                return null;
            }
        };
    }
}
