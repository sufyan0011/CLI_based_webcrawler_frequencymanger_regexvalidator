import java.util.*;

public class SpellChecker {
    private Set<String> vocabulary;

    public SpellChecker(Set<String> vocabulary) {
        this.vocabulary = vocabulary;
    }

    public int computeEditDistance(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j;
        }
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1],
                            Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[a.length()][b.length()];
    }

    public List<String> suggest(String query, int maxSuggestions, int threshold) {
        List<String> suggestions = new ArrayList<>();
        for (String word : vocabulary) {
            int distance = computeEditDistance(query.toLowerCase(), word.toLowerCase());
            if (distance <= threshold) {
                suggestions.add(word);
            }
        }
        suggestions.sort(Comparator.comparingInt(s -> computeEditDistance(query.toLowerCase(), s.toLowerCase())));
        if (suggestions.size() > maxSuggestions) {
            return suggestions.subList(0, maxSuggestions);
        }
        return suggestions;
    }
}
