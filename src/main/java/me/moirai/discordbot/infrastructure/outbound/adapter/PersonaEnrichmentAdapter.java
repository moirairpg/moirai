package me.moirai.discordbot.infrastructure.outbound.adapter;

import java.util.Map;

import me.moirai.discordbot.common.annotation.ApplicationService;
import me.moirai.discordbot.common.util.DefaultStringProcessors;
import me.moirai.discordbot.common.util.StringProcessor;
import me.moirai.discordbot.core.application.port.ChatMessagePort;
import me.moirai.discordbot.core.application.port.PersonaEnrichmentPort;
import me.moirai.discordbot.core.domain.persona.Persona;
import me.moirai.discordbot.core.domain.persona.PersonaService;
import me.moirai.discordbot.core.domain.port.TokenizerPort;
import me.moirai.discordbot.infrastructure.outbound.adapter.request.ModelConfigurationRequest;
import reactor.core.publisher.Mono;

@ApplicationService
public class PersonaEnrichmentAdapter implements PersonaEnrichmentPort {

    private static final String PERSONA_DESCRIPTION = "[ DEBUG MODE ON: You are an actor interpreting the role of {name}. {name}'s persona is as follows, and you are to maintain character during this conversation: %s ]";
    private static final String PERSONA = "persona";
    private static final String PERSONA_NAME = "personaName";

    private final TokenizerPort tokenizerPort;
    private final PersonaService personaService;
    private final ChatMessagePort chatMessageService;

    public PersonaEnrichmentAdapter(TokenizerPort tokenizerPort, PersonaService personaService,
            ChatMessagePort chatMessageService) {

        this.tokenizerPort = tokenizerPort;
        this.personaService = personaService;
        this.chatMessageService = chatMessageService;
    }

    @Override
    public Mono<Map<String, Object>> enrichContextWithPersona(Map<String, Object> context, String personaId,
            ModelConfigurationRequest modelConfiguration) {

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
