package es.thalesalv.chatrpg.infrastructure.inbound.api.request;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(builderClassName = "Builder")
@AllArgsConstructor
public class CreateChannelConfigRequest {

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String name;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private final String worldId;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private final String personaId;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private String visibility;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private final String aiModel;

    @NotEmpty(message = "cannot be empty")
    @NotNull(message = "cannot be null")
    private final String moderation;

    @NotNull(message = "cannot be null")
    @Min(value = 100, message = "cannot be less than 100")
    private final Integer maxTokenLimit;

    @NotNull(message = "cannot be null")
    @Min(value = 10, message = "cannot be less than 10")
    @Max(value = 100, message = "cannot be greater than 100")
    private final Integer messageHistorySize;

    @NotNull(message = "cannot be null")
    @DecimalMin(value = "0.1", message = "cannot be less than 0.1")
    @DecimalMax(value = "2", message = "cannot be greater than 2")
    private final Double temperature;

    @DecimalMin(value = "-2", message = "cannot be less than -2")
    @DecimalMax(value = "2", message = "cannot be greater than 2")
    private final Double frequencyPenalty;

    @DecimalMin(value = "-2", message = "cannot be less than -2")
    @DecimalMax(value = "2", message = "cannot be greater than 2")
    private final Double presencePenalty;
    private final List<String> stopSequences;
    private final Map<String, Double> logitBias;
    private List<String> writerUsers;
    private List<String> readerUsers;
}
