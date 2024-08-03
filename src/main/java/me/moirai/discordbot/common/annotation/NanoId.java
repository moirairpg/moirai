package me.moirai.discordbot.common.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.hibernate.annotations.IdGeneratorType;

import me.moirai.discordbot.common.dbutil.NanoIdIdentifierGenerator;

@IdGeneratorType(NanoIdIdentifierGenerator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ FIELD, METHOD })
public @interface NanoId {

}
