package es.thalesalv.chatrpg.application.util;

import java.util.Optional;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class NanoIdIdentifierGenerator implements IdentifierGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {

        final String id = (String) session.getEntityPersister(null, object).getIdentifier(object, session);
        return Optional.ofNullable(id).orElse(NanoId.randomNanoId());
    }
}