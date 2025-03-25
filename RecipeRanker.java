import java.util.*;

public class RecipeRanker {

    public List<Recipe> rank(List<Recipe> recipes, String query) {
        // Priority queue to sort recipes by keyword occurrence (descending)
        PriorityQueue<RecipeRank> heap = new PriorityQueue<>(Comparator.comparingInt(r -> -r.count));

        // Create a BoyerMoore instance for efficient search
        BoyerMoore bm = new BoyerMoore(query.toLowerCase());

        for (Recipe recipe : recipes) {
            int count = countOccurrences(recipe, bm);
            heap.offer(new RecipeRank(recipe, count));
        }

        List<Recipe> ranked = new ArrayList<>();
        while (!heap.isEmpty()) {
            ranked.add(heap.poll().recipe);
        }

        return ranked;
    }

    private int countOccurrences(Recipe recipe, BoyerMoore bm) {
        int count = 0;
        count += bm.searchCount(recipe.getTitle().toLowerCase());
        count += bm.searchCount(recipe.getSides().toLowerCase());
        count += bm.searchCount(recipe.getMealType().toLowerCase());
        return count;
    }

    private static class RecipeRank {
        Recipe recipe;
        int count;

        public RecipeRank(Recipe recipe, int count) {
            this.recipe = recipe;
            this.count = count;
        }
    }
}
