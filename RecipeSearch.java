import java.util.*;

public class RecipeSearch {
    private List<Recipe> recipes;
    private Map<String, Integer> searchFrequency;
    private Trie trie;

    public RecipeSearch(List<Recipe> recipes) {
        this.recipes = recipes;
        this.searchFrequency = new HashMap<>();
        this.trie = new Trie();
        for (Recipe recipe : recipes) {
            trie.insert(recipe.getTitle());
        }
    }

    // Performs basic keyword search over title, sides, and meal type.
    public List<Recipe> search(String query) {
        List<Recipe> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }
        String lowerQuery = query.toLowerCase();
        for (Recipe recipe : recipes) {
            if (recipe.getTitle().toLowerCase().contains(lowerQuery) ||
                    recipe.getSides().toLowerCase().contains(lowerQuery) ||
                    recipe.getMealType().toLowerCase().contains(lowerQuery)) {
                results.add(recipe);
            }
        }
        searchFrequency.put(query, searchFrequency.getOrDefault(query, 0) + 1);
        return results;
    }

    public List<String> getAutoCompletionSuggestions(String prefix) {
        return trie.getWordsWithPrefix(prefix);
    }

    public int getSearchFrequency(String query) {
        return searchFrequency.getOrDefault(query, 0);
    }

    public List<Recipe> rankRecipes(List<Recipe> recipesToRank, String query) {
        recipesToRank.sort((r1, r2) -> {
            int count1 = countOccurrences(r1, query);
            int count2 = countOccurrences(r2, query);
            return Integer.compare(count2, count1); // Descending order.
        });
        return recipesToRank;
    }

    private int countOccurrences(Recipe recipe, String query) {
        int count = 0;
        String lowerQuery = query.toLowerCase();
        count += countMatches(recipe.getTitle().toLowerCase(), lowerQuery);
        count += countMatches(recipe.getSides().toLowerCase(), lowerQuery);
        count += countMatches(recipe.getMealType().toLowerCase(), lowerQuery);
        return count;
    }

    private int countMatches(String text, String query) {
        int count = 0;
        int idx = text.indexOf(query);
        while (idx != -1) {
            count++;
            idx = text.indexOf(query, idx + query.length());
        }
        return count;
    }
}
