package org.sunbird.util;

/**
 * Utility class for string formatting and joining operations.
 */
public class StringFormatter {

    public static final String DOT = ".";
    public static final String AND = " and ";
    public static final String OR = " or ";
    public static final String COMMA = ", ";

    private StringFormatter() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Joins strings with dot separator.
     *
     * @param params the strings to join
     * @return the dot-separated string
     */
    public static String joinByDot(String... params) {
        return String.join(DOT, params);
    }

    /**
     * Joins strings with "or" separator.
     *
     * @param params the strings to join
     * @return the "or"-separated string
     */
    public static String joinByOr(String... params) {
        return String.join(OR, params);
    }

    /**
     * Joins strings with "and" separator.
     *
     * @param params the strings to join
     * @return the "and"-separated string
     */
    public static String joinByAnd(String... params) {
        return String.join(AND, params);
    }

    /**
     * Joins strings with comma separator.
     *
     * @param params the strings to join
     * @return the comma-separated string
     */
    public static String joinByComma(String... params) {
        return String.join(COMMA, params);
    }
}

