package es.thalesalv.chatrpg.application.util;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class NanoIdIdentifierGenerator implements IdentifierGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object obj) throws HibernateException {

        return NanoId.randomNanoId();
    }
}