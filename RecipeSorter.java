import java.util.List;

public class RecipeSorter {

    // Public method to sort recipes by calories (ascending order).
    public static void sortByCalories(List<Recipe> recipes) {
        if (recipes == null || recipes.size() <= 1) {
            return;
        }
        quickSort(recipes, 0, recipes.size() - 1);
    }

    private static void quickSort(List<Recipe> recipes, int low, int high) {
        if (low < high) {
            int pivotIndex = partition(recipes, low, high);
            quickSort(recipes, low, pivotIndex - 1);
            quickSort(recipes, pivotIndex + 1, high);
        }
    }

    private static int partition(List<Recipe> recipes, int low, int high) {
        Recipe pivot = recipes.get(high);
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (recipes.get(j).getCalories() <= pivot.getCalories()) {
                i++;
                swap(recipes, i, j);
            }
        }
        swap(recipes, i + 1, high);
        return i + 1;
    }

    private static void swap(List<Recipe> recipes, int i, int j) {
        Recipe temp = recipes.get(i);
        recipes.set(i, recipes.get(j));
        recipes.set(j, temp);
    }
}
