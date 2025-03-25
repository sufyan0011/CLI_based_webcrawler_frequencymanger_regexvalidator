import java.util.*;

public class WebsiteFrequencyCounter {
    private List<Recipe> recipes;

    public WebsiteFrequencyCounter(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public Map<String, Integer> getFrequencyByWebsite(String query) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        String lowerQuery = query.toLowerCase();
        for (Recipe recipe : recipes) {
            String website = recipe.getWebsite();
            if (website == null || website.isEmpty()) continue;
            int count = countOccurrencesInRecipe(recipe, lowerQuery);
            frequencyMap.put(website, frequencyMap.getOrDefault(website, 0) + count);
        }
        return frequencyMap;
    }

    private int countOccurrencesInRecipe(Recipe recipe, String query) {
        int count = 0;
        count += countMatches(recipe.getTitle().toLowerCase(), query);
        count += countMatches(recipe.getSides().toLowerCase(), query);
        count += countMatches(recipe.getMealType().toLowerCase(), query);
        return count;
    }

    private int countMatches(String text, String query) {
        BoyerMoore bm = new BoyerMoore(query);
        return bm.searchCount(text);
    }
}
