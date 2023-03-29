package es.thalesalv.chatrpg.application.util.dbutils;

import java.util.Optional;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import es.thalesalv.chatrpg.application.util.NanoId;

public class NanoIdIdentifierGenerator implements IdentifierGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {

        final String id = (String) session.getEntityPersister(null, object)
                .getIdentifier(object, session);
        return Optional.ofNullable(id)
                .orElse(NanoId.randomNanoId());
    }
}