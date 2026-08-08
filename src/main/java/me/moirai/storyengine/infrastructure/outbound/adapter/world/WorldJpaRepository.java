package me.moirai.storyengine.infrastructure.outbound.adapter.world;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import me.moirai.storyengine.common.dbutil.PaginationRepository;
import me.moirai.storyengine.core.domain.world.World;

public interface WorldJpaRepository
        extends JpaRepository<World, Long>, PaginationRepository<World, Long> {

    Optional<World> findByPublicId(UUID publicId);

    void deleteByPublicId(UUID publicId);

    @Query("""
            SELECT w
              FROM World w
              JOIN w.permissions p
             WHERE p.userId = :userId
               AND p.level = me.moirai.storyengine.common.enums.PermissionLevel.OWNER
            """)
    List<World> findAllOwnedBy(Long userId);

    @Query("""
            SELECT DISTINCT w
              FROM World w
              JOIN w.permissions p
             WHERE p.userId = :userId
            """)
    List<World> findAllInvolving(Long userId);
}
