package me.moirai.discordbot.common.dbutil;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

public class NanoIdIdentifierGenerator implements IdentifierGenerator {

    @Override
    public Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {

        final String id = (String) session.getEntityPersister(null, object)
                .getIdentifier(object, session);

        return Optional.ofNullable(id)
                .filter(StringUtils::isNotBlank)
                .orElse(NanoId.randomNanoId());
    }
}