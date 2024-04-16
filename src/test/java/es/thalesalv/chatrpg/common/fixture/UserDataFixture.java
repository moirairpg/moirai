package es.thalesalv.chatrpg.common.fixture;

import discord4j.discordjson.json.ImmutableUserData;
import discord4j.discordjson.json.UserData;

public class UserDataFixture {

    @SuppressWarnings("deprecation")
    public static ImmutableUserData.Builder humanUser() {

        return UserData.builder()
                .id(123123)
                .bot(false)
                .emailOrNull("email@email.com")
                .globalName("TestUser")
                .username("test_user")
                .system(false)
                .verified(false)
                .mfaEnabled(false)
                .premiumType(1)
                .discriminator("test_user#1234");
    }

    public static ImmutableUserData.Builder botUser() {

        return humanUser()
                .bot(true);
    }
}
