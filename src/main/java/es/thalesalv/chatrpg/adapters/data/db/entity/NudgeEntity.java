package es.thalesalv.chatrpg.adapters.data.db.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NudgeEntity {

    public String role;
    public String content;
}
