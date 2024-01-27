package es.thalesalv.chatrpg.core.domain.model;

import java.util.ArrayList;
import java.util.List;

public class PermissionFixture {

    private static final String OWNER_DISCORD_ID = "586678721356875";

    public static Permissions.Builder samplePermissions() {

        List<String> userList = new ArrayList<>();
        userList.add("613226587696519");
        userList.add("910602820805797");
        userList.add("643337806686791");
        userList.add("559802401039646");

        return Permissions.builder().ownerDiscordId(OWNER_DISCORD_ID)
                .usersAllowedToRead(userList)
                .usersAllowedToWrite(userList);
    }
}
