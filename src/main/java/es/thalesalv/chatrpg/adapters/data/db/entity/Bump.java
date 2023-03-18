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
public class Bump {

    public String role;
    public String content;
    public Integer frequency;
    public final static Predicate<Bump> isValid = bump -> null != bump.frequency && bump.frequency > 1 
            && !StringUtils.isEmpty(bump.role) && ! StringUtils.isEmpty(bump.content);
}
