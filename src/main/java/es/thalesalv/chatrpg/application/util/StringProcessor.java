package es.thalesalv.chatrpg.application.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class StringProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass().getCanonicalName());
    private final List<UnaryOperator<String>> rules = new ArrayList<>();

    public void addRule(UnaryOperator<String> rule) {
        rules.add(rule);
    }

    public String process(final String input) {
        String output = input;
        int count = 0;
        for(UnaryOperator<String> rule : rules) {
            String o = output;
            output = rule.apply(output);
            if (!o.equals(output)) count++;
        }
        LOGGER.info("Replacements: " + count);
        return output;
    }
}
