package constants;

public class RegexConstants {
    public static final String UPPERCASE_PATTERN = ".*[A-Z].*";
    public static final String LOWERCASE_PATTERN = ".*[a-z].*";
    public static final String DIGIT_PATTERN = ".*\\d.*";
    public static final String SYMBOL_PATTERN = ".*[~`!@#$%^&*()_,.?/\"':;{}|<>\\[\\]].*";
    public static final String COMPLEX_PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[~`!@#$%^&*()_,.?/\"':;{}|<>\\[\\]]).+$";
}