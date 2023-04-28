package es.thalesalv.chatrpg.domain.model.chconf;

import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Nudge {

    public String role;
    public String content;
    public final static Predicate<Nudge> isValid = nudge -> !StringUtils.isEmpty(nudge.role)
            && !StringUtils.isEmpty(nudge.content);
}
