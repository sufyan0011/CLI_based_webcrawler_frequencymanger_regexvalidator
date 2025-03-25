import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

// Represents a meal kit recipe with relevant attributes
class MealKitRecipe {
    String title;
    String sides;
    String mealType;
    int calories;
    String preparationTime;
    String website;

    public MealKitRecipe(String title, String sides, String mealType, int calories, String preparationTime, String website) {
        this.title = title;
        this.sides = sides;
        this.mealType = mealType;
        this.calories = calories;
        this.preparationTime = preparationTime;
        this.website = website;
    }
}

