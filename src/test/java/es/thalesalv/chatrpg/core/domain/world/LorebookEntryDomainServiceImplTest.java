package es.thalesalv.chatrpg.core.domain.world;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import es.thalesalv.chatrpg.common.exception.BusinessException;
import es.thalesalv.chatrpg.core.application.port.TokenizerPort;

@ExtendWith(MockitoExtension.class)
public class LorebookEntryDomainServiceImplTest {

    @Mock
    private TokenizerPort tokenizerPort;

    @InjectMocks
    private LorebookEntryDomainServiceImpl service;

    @Test
    public void createLorebookEntrySuccesfully() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a kingdom in an empire";
        String regex = "[Ee]ldrida";

        LorebookEntry.Builder lorebookEntryBuilder = LorebookEntryFixture.sampleLorebookEntry()
                .name(name)
                .description(description)
                .regex(regex);

        LorebookEntry expectedLorebookEntry = lorebookEntryBuilder.build();

        // When
        LorebookEntry createdLorebookEntry = service.createLorebookEntry(name, description, regex, null);

        // Then
        assertThat(createdLorebookEntry.getName()).isEqualTo(expectedLorebookEntry.getName());
        assertThat(createdLorebookEntry.getDescription()).isEqualTo(expectedLorebookEntry.getDescription());
        assertThat(createdLorebookEntry.getRegex()).isEqualTo(expectedLorebookEntry.getRegex());
    }

    @Test
    public void errorWhenEntryNameTokenLimitIsSurpassed() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a kingdom in an empire";
        String regex = "[Ee]ldrida";

        ReflectionTestUtils.setField(service, "lorebookEntryNameTokenLimit", 2);
        ReflectionTestUtils.setField(service, "lorebookEntryDescriptionTokenLimit", 20);
        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        // Then
        assertThrows(BusinessException.class, () -> service.createLorebookEntry(name, description, regex, null));
    }

    @Test
    public void errorWhenEntryDescriptionTokenLimitIsSurpassed() {

        // Given
        String name = "Eldrida";
        String description = "Eldrida is a kingdom in an empire";
        String regex = "[Ee]ldrida";

        ReflectionTestUtils.setField(service, "lorebookEntryNameTokenLimit", 20);
        ReflectionTestUtils.setField(service, "lorebookEntryDescriptionTokenLimit", 2);
        when(tokenizerPort.getTokenCountFrom(anyString())).thenReturn(10);

        // Then
        assertThrows(BusinessException.class, () -> service.createLorebookEntry(name, description, regex, null));
    }
}
