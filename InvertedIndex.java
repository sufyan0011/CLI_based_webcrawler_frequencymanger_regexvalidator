import java.util.*;

public class InvertedIndex {
    private Map<String, List<Recipe>> index;

    public InvertedIndex(List<Recipe> recipes) {
        index = new HashMap<>();
        buildIndex(recipes);
    }

    private void buildIndex(List<Recipe> recipes) {
        for (Recipe recipe : recipes) {
            String combined = recipe.getTitle() + " " + recipe.getSides() + " " + recipe.getMealType();
            String[] tokens = combined.toLowerCase().split("\\W+");
            for (String token : tokens) {
                if (token.isEmpty()) continue;
                index.putIfAbsent(token, new ArrayList<>());
                index.get(token).add(recipe);
            }
        }
    }

    public List<Recipe> search(String token) {
        return index.getOrDefault(token.toLowerCase(), new ArrayList<>());
    }

    public Map<String, List<Recipe>> getIndex() {
        return index;
    }
}
