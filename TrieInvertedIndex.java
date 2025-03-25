import java.util.*;

public class TrieInvertedIndex {
    private class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEndOfWord;
        Set<Recipe> recipes;  // Store recipes for the complete token

        TrieNode() {
            isEndOfWord = false;
            recipes = new HashSet<>();
        }
    }

    private TrieNode root;

    public TrieInvertedIndex() {
        root = new TrieNode();
    }

    // Insert a token along with its associated recipe into the Trie.
    public void insert(String word, Recipe recipe) {
        TrieNode node = root;
        for (char c : word.toLowerCase().toCharArray()) {
            node.children.putIfAbsent(c, new TrieNode());
            node = node.children.get(c);
        }
        node.isEndOfWord = true;
        node.recipes.add(recipe);
    }

    // Build the inverted index from the list of recipes.
    public void buildIndex(List<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            String combined = recipe.getTitle() + " " + recipe.getSides() + " " + recipe.getMealType();
            // Split into tokens using non-word characters as delimiters.
            String[] tokens = combined.split("\\W+");
            for (String token : tokens) {
                if (!token.isEmpty()) {
                    insert(token, recipe);
                }
            }
        }
    }

    /**
     * Searches for recipes using a substring method.
     * Traverses the Trie (via DFS) and collects recipes where the built token contains the query substring.
     */
    public List<Recipe> searchBySubstring(String substring) {
        List<Recipe> results = new ArrayList<>();
        String lowerSub = substring.toLowerCase();
        traverseAndCollect(root, new StringBuilder(), lowerSub, results);
        return new ArrayList<>(new HashSet<>(results)); // Remove duplicates.
    }

    private void traverseAndCollect(TrieNode node, StringBuilder prefix, String lowerSub, List<Recipe> results) {
        if (node.isEndOfWord && prefix.toString().contains(lowerSub)) {
            results.addAll(node.recipes);
        }
        for (Map.Entry<Character, TrieNode> entry : node.children.entrySet()) {
            prefix.append(entry.getKey());
            traverseAndCollect(entry.getValue(), prefix, lowerSub, results);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    /**
     * New method: splits the full query into tokens and returns the intersection
     * of recipes that contain every token.
     */
    public List<Recipe> searchByTokens(String query) {
        String[] tokens = query.split("\\W+");
        Set<Recipe> resultSet = null;
        for (String token : tokens) {
            if (token.isEmpty()) continue;
            List<Recipe> tokenResults = searchBySubstring(token);
            if (resultSet == null) {
                resultSet = new HashSet<>(tokenResults);
            } else {
                resultSet.retainAll(tokenResults);
            }
        }
        return resultSet == null ? new ArrayList<>() : new ArrayList<>(resultSet);
    }
}
