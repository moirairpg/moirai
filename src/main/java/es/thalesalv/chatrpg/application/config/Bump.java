package es.thalesalv.chatrpg.application.config;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.apache.commons.lang3.StringUtils;

import java.util.function.Predicate;

@Value
@RequiredArgsConstructor
public class Bump {
    public String role;
    public String content;
    public Integer frequency;
    public final static Predicate<Bump> isValid = bump -> null != bump.frequency && bump.frequency > 1 && !StringUtils.isEmpty(bump.role) && ! StringUtils.isEmpty(bump.content);
}
