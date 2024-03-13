package es.thalesalv.chatrpg.infrastructure.outbound.persistence.persona;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Embeddable
@Builder(builderClassName = "Builder")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class BumpEntity {

    @Column(name = "bump_content")
    private String content;

    @Column(name = "bump_frequency")
    private int frequency;

    @Column(name = "bump_role")
    private String role;
}
