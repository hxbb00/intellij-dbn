package com.dbn.common.expression;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlToGroovyExpressionConverter {
    private static final Map<String, String> cache = new ConcurrentHashMap<>();

    public static String cachedSqlToGroovy(String expression) {
        return cache.computeIfAbsent(expression, e -> SqlToGroovyExpressionConverter.sqlToGroovy(e));
    }

    public static String sqlToGroovy(String expression) {
        expression = expression.replaceAll("(?i)\\bOR\\b", "||");
        expression = expression.replaceAll("(?i)\\bAND\\b", "&&");
        expression = expression.replaceAll("(?i)\\bIS\\s+NULL\\b", "== NULL");
        expression = expression.replaceAll("(?i)\\bIS\\s+NOT\\s+NULL\\b", "!= NULL");

        expression = replace_EQUALS(expression);
        expression = replace_LIKE(expression);
        expression = replace_IN(expression);

        return expression;
    }

    private static String replace_EQUALS(String expression) {
        Pattern pattern = Pattern.compile("(?<!([=<>!]))=(?!=)");
        Matcher matcher = pattern.matcher(expression);
        expression = matcher.replaceAll("==");
        return expression;
    }

    private static String replace_LIKE(String expression) {
        Pattern p = Pattern.compile("(\\w+)\\s+LIKE\\s+'(%?)([^%]+)%'");
        Matcher m = p.matcher(expression);
        StringBuffer result = new StringBuffer();
        while (m.find()) {
            String name = m.group(1);
            String suffix = m.group(2);
            String value = m.group(3);
            String transformed = String.format("%s.toLowerCase().indexOf('%s'.toLowerCase()) " + (suffix.isEmpty() ? "== 0" : "> 0"), name, value);
            m.appendReplacement(result, transformed);
        }
        m.appendTail(result);
        expression = result.toString();
        return expression;
    }

    private static String replace_IN(String expression) {
        Pattern p = Pattern.compile("(\\w+)\\s+IN\\s+\\((.*?)\\)");
        Matcher m = p.matcher(expression);
        StringBuffer result = new StringBuffer(); // Java 8 compatibility
        while (m.find()) {
            String name = m.group(1);
            String values = m.group(2);
            String transformed = String.format("[%s].contains(%s)", values, name);
            m.appendReplacement(result, transformed);
        }
        m.appendTail(result);
        expression = result.toString();
        return expression;
    }
}
