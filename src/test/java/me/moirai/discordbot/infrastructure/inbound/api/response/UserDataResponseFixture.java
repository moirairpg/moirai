package me.moirai.discordbot.infrastructure.inbound.api.response;

public class UserDataResponseFixture {

    public static UserDataResponse.Builder create() {

        return UserDataResponse.builder()
                .id("1234")
                .email("user@email.com")
                .globalNickname("nickname")
                .username("username")
                .avatar("https://img.com/avatar.jpg");
    }
}
