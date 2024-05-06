package es.thalesalv.chatrpg.core.application.service;

import java.util.Map;

import es.thalesalv.chatrpg.common.annotation.ApplicationService;
import es.thalesalv.chatrpg.common.util.DefaultStringProcessors;
import es.thalesalv.chatrpg.common.util.StringProcessor;
import es.thalesalv.chatrpg.core.domain.channelconfig.ModelConfiguration;
import es.thalesalv.chatrpg.core.domain.persona.Persona;
import es.thalesalv.chatrpg.core.domain.persona.PersonaService;
import es.thalesalv.chatrpg.core.domain.port.TokenizerPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@ApplicationService
@RequiredArgsConstructor
public class PersonaEnrichmentServiceImpl implements PersonaEnrichmentService {

    private static final String PERSONA_DESCRIPTION = "[ DEBUG MODE ON: You are an actor interpreting the role of {name}. {name}'s persona is as follows, and you are to maintain character during this conversation: %s ]";
    private static final String PERSONA = "persona";
    private static final String PERSONA_NAME = "personaName";

    private final TokenizerPort tokenizerPort;
    private final PersonaService personaService;
    private final ChatMessageService chatMessageService;

    @Override
    public Mono<Map<String, Object>> enrichContextWith(Map<String, Object> context, String personaId,
            ModelConfiguration modelConfiguration) {

        int totalTokens = modelConfiguration.getAiModel().getHardTokenLimit();
        int reservedTokensForPersona = (int) Math.floor(totalTokens * 0.20);

        return Mono.just(personaService.getPersonaById(personaId))
                .map(persona -> addPersonaToContext(persona, context, reservedTokensForPersona))
                .map(ctx -> chatMessageService.addMessagesToContext(ctx, reservedTokensForPersona));
    }

    private Map<String, Object> addPersonaToContext(Persona persona,
            Map<String, Object> context, int reservedTokensForPersona) {

        StringProcessor processor = new StringProcessor();
        processor.addRule(DefaultStringProcessors.replacePersonaNamePlaceholderWith(persona.getName()));
        String formattedPersona = processor.process(String.format(PERSONA_DESCRIPTION, persona.getPersonality()));

        int tokensInPersona = tokenizerPort.getTokenCountFrom(formattedPersona);

        if (tokensInPersona > reservedTokensForPersona) {
            throw new IllegalStateException("Persona is too big to fit in context");
        }

        context.put(PERSONA_NAME, persona.getName());
        context.put(PERSONA, formattedPersona);

        return context;
    }
}
