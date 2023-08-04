package es.thalesalv.chatrpg.domain.model.bot;

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
public class Bump {

    public String role;
    public String content;
    public Integer frequency;
    public final static Predicate<Bump> isValid = bump -> null != bump.frequency && bump.frequency > 1
            && !StringUtils.isEmpty(bump.role) && !StringUtils.isEmpty(bump.content);
}
