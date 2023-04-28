package es.thalesalv.chatrpg.domain.model.chconf;

import java.util.Map;

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
public class ModerationSettings {

    private String id;
    private String owner;
    private boolean absolute;
    private Map<String, Double> thresholds;

    public static ModerationSettings defaultModerationSettings() {

        return ModerationSettings.builder()
                .id("0")
                .absolute(true)
                .build();
    }
}
