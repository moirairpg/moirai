package es.thalesalv.chatrpg.common.cqrs.query;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;

@SuppressWarnings("all")
public class QueryRunnerImpl implements QueryRunner {

    private static final String HANDLER_NOT_FOUND = "No query handler found for %s";
    private static final String HANDLER_CANNOT_BE_NULL = "Cannot register null handlers";
    private static final String HANDLER_ALREADY_REGISTERED = "Cannot register query handler for %s - there is a handler already registered";
    private static final String HANDLER_REGISTERED = "Handler {} registered for query {}";

    private static final Logger LOGGER = LoggerFactory.getLogger(QueryRunnerImpl.class);

    private final Map<Class<? extends Query<?>>, QueryHandler<?, ?>> handlersByQuery = new HashMap<>();

    @Override
    public <T> T run(Query<T> query) {

        QueryHandler<?, ?> handler = handlersByQuery.get(query.getClass());

        if (handler == null) {
            String errorMessage = String.format(HANDLER_NOT_FOUND, query.getClass().getSimpleName());
            throw new IllegalArgumentException(errorMessage);
        }

        return ((QueryHandler<Query<T>, T>) handler).execute(query);
    }

    @Override
    public <A extends Query<T>, T> void registerHandler(QueryHandler<A, T> handler) {

        if (Objects.isNull(handler)) {
            throw new IllegalArgumentException(HANDLER_CANNOT_BE_NULL);
        }

        Class<A> queryType = extractQueryType(handler);

        boolean isHandlerAlreadyRegisteredForQuery = handlersByQuery.containsKey(queryType);
        if (isHandlerAlreadyRegisteredForQuery) {
            throw new IllegalArgumentException(HANDLER_ALREADY_REGISTERED);
        }

        handlersByQuery.putIfAbsent(queryType, handler);

        LOGGER.info(HANDLER_REGISTERED, handler.getClass().getSimpleName(), queryType.getSimpleName());
    }

    private <A extends Query<T>, T> Class<A> extractQueryType(QueryHandler<A, T> handler) {

        Class<? extends QueryHandler<A, T>> unproxiedHandler =
                (Class<? extends QueryHandler<A, T>>) AopUtils.getTargetClass(handler);

        ParameterizedType parameterizedType = (ParameterizedType) unproxiedHandler.getGenericSuperclass();

        return (Class<A>) parameterizedType.getActualTypeArguments()[0];
    }
}
