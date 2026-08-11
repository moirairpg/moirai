package me.moirai.storyengine.core.application.query.adventure;

import java.util.List;

import me.moirai.storyengine.common.annotation.QueryHandler;
import me.moirai.storyengine.common.cqs.query.AbstractQueryHandler;
import me.moirai.storyengine.core.port.inbound.AssetMember;
import me.moirai.storyengine.core.port.inbound.adventure.GetAdventureMembers;
import me.moirai.storyengine.core.port.outbound.adventure.AdventurePermissionReader;

@QueryHandler
public class GetAdventureMembersHandler extends AbstractQueryHandler<GetAdventureMembers, List<AssetMember>> {

    private final AdventurePermissionReader reader;

    public GetAdventureMembersHandler(AdventurePermissionReader reader) {
        this.reader = reader;
    }

    @Override
    public List<AssetMember> execute(GetAdventureMembers query) {

        return reader.getAllByAdventurePublicId(query.adventureId());
    }
}
