import java.util.HashMap;
import java.util.Map;

public class BoyerMoore {
    private Map<Character, Integer> right;
    private String pat;

    public BoyerMoore(String pat) {
        this.pat = pat;
        right = new HashMap<>();
        for (int j = 0; j < pat.length(); j++) {
            right.put(pat.charAt(j), j);
        }
    }

    public int searchCount(String txt) {
        int m = pat.length();
        int n = txt.length();
        int count = 0;
        int i = 0;
        while (i <= n - m) {
            int j;
            for (j = m - 1; j >= 0; j--) {
                if (pat.charAt(j) != txt.charAt(i + j)) {
                    break;
                }
            }
            if (j < 0) {
                count++;
                i++; // Move forward one position after match
            } else {
                int r = right.getOrDefault(txt.charAt(i + j), -1);
                int skip = Math.max(1, j - r);
                i += skip;
            }
        }
        return count;
    }
}
