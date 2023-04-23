package es.thalesalv.chatrpg.application.mapper;

import java.util.function.Function;

import org.springframework.stereotype.Component;

import es.thalesalv.chatrpg.domain.model.DiscordUserData;
import net.dv8tion.jda.api.entities.User;

@Component
public class JDAUserToDiscordUser implements Function<User, DiscordUserData> {

    @Override
    public DiscordUserData apply(User user) {

        return DiscordUserData.builder()
                .id(user.getId())
                .username(user.getName())
                .discriminator(user.getDiscriminator())
                .avatar(user.getAvatarUrl())
                .build();
    }
}
