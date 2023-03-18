package es.thalesalv.chatrpg.adapters.data.db.entity;

import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Nudge {

    public String role;
    public String content;
    public final static Predicate<Nudge> isValid = nudge -> !StringUtils.isEmpty(nudge.role)
            && !StringUtils.isEmpty(nudge.content);
}
