package es.thalesalv.chatrpg.domain.model.chconf;

import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Nudge {

    public String role;
    public String content;
    public final static Predicate<Nudge> isValid = nudge -> !StringUtils.isEmpty(nudge.role)
            && !StringUtils.isEmpty(nudge.content);
}
