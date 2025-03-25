import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexValidator {

    private static final String EMAIL_REGEX = "([A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,})";
    private static final String PASSWORD_REGEX = ".{6,}";
    private static final String PHONE_REGEX = "^(?:\\+1\\s*|1\\s*)?(?:\\(\\d{3}\\)|\\d{3})[-\\s]\\d{3}[-\\s]\\d{4}";
    private static final String POSTAL_CODE_REGEX = "^[A-Za-z]\\d[A-Za-z] \\d[A-Za-z]\\d$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    public static boolean isValidEmail(String email) {
        return email.matches(EMAIL_REGEX);
    }

    public static boolean isValidPassword(String password) {
        return password.matches(PASSWORD_REGEX);
    }

    public static boolean isValidSearchQuery(String query) {
        return query.matches("^[a-zA-Z\\s]+$");
    }

    public static boolean isValidYesNo(String input) {
        return input.matches("^[yn]$");
    }

    public static boolean isValidPhoneNumber(String phone) {
        return phone.matches(PHONE_REGEX);
    }

    public static boolean isValidPostalCode(String postalCode) {
        return postalCode.matches(POSTAL_CODE_REGEX);
    }

    // NEW ➤ Extract first email found in any string
    public static String extractEmail(String input) {
        Matcher matcher = EMAIL_PATTERN.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
