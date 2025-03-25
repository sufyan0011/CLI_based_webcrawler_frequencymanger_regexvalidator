import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class meal_finder {

    public static void main(String[] args) {

        // HelloFresh Recipes
        scrapeHelloFresh("https://www.hellofresh.com/recipes/american-recipes", "HelloFresh");
        scrapeHelloFresh("https://www.hellofresh.com/recipes/italian-recipes", "HelloFresh");
        scrapeHelloFresh("https://www.hellofresh.com/recipes/asian-recipes", "HelloFresh");
        scrapeHelloFresh("https://www.hellofresh.com/recipes/mexican-recipes", "HelloFresh");
        scrapeHelloFresh("https://www.hellofresh.com/recipes/korean-recipes", "HelloFresh");
        scrapeHelloFresh("https://www.hellofresh.com/recipes/indian-recipes", "HelloFresh");
        scrapeHelloFresh("https://www.hellofresh.com/recipes/chinese-recipes", "HelloFresh");
        scrapeHelloFresh("https://www.hellofresh.com/recipes/japanese-recipes", "HelloFresh");
        scrapeHelloFresh("https://www.hellofresh.com/recipes/thai-recipes", "HelloFresh");
        scrapeHelloFresh("https://www.hellofresh.com/recipes/french-recipes", "HelloFresh");

        // HomeChef Recipes
        scrapeSingleMealHomeChef("https://www.homechef.com/meals/asian-style-turkey-crunch-salad", "HomeChef");
        scrapeSingleMealHomeChef("https://www.homechef.com/meals/beefy-bacon-butter-bomb-burger", "HomeChef");
        scrapeSingleMealHomeChef("https://www.homechef.com/meals/beef-cheeseburger-salad", "HomeChef");
        scrapeSingleMealHomeChef("https://www.homechef.com/meals/shrimp-carbonara-fettuccine-03c7898e-b406-4739-af31-e692137f87fd", "HomeChef");

        // CleanPlates Recipes
        scrapeCleanPlates("https://cleanplates.ca/currentmenu/?dd=2025-03-26", "CleanPlates");

        // MakeGoodFood Recipes
        scrapeMakeGoodFood("https://www.makegoodfood.ca/product/recipe/9219279/pub-style-beef-and-aged-cheddar-meatballs?deliveryDate=2025-03-14", "MakeGoodFood");
    }

    // Scraper for HelloFresh
    public static void scrapeHelloFresh(String url, String websiteName) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements recipeCards = doc.select("div[data-test-id='recipe-image-card-description']");

            for (Element card : recipeCards) {
                String title = card.select("span").first().text();
                String sides = card.select("span.ccrEYr").text();
                Element parent = card.parent().parent();
                Elements infoSpans = parent.select("span.fxQDbT");

                int preparationTime = Integer.parseInt(infoSpans.first().text().replaceAll("\\D", ""));
                int calories = Integer.parseInt(infoSpans.last().text().replaceAll("\\D", ""));

                MealKitRecipe recipe = new MealKitRecipe(
                        title,
                        sides,
                        "Multicuisine",
                        calories,
                        preparationTime + " mins",
                        websiteName
                );

                saveRecipeToCSV(recipe, websiteName, url);
            }

            System.out.println("Scraping completed for: " + websiteName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Scraper for HomeChef single meal page
    public static void scrapeSingleMealHomeChef(String url, String websiteName) {
        try {
            Document doc = Jsoup.connect(url).get();

            String title = doc.select("h1.h2.mb-0").first().text();
            String sides = doc.select("h2.h5.text-charcoal-90.mt-0").first().text();
            String mealType = doc.title().split(" ")[0];

            String caloriesText = doc.select("i.tooltip--top.mr-1")
                    .attr("data-tooltip")
                    .replaceAll("[^\\d]", "");
            int calories = caloriesText.isEmpty() ? 0 : Integer.parseInt(caloriesText);

            String preparationTime = doc.select("div.meal__overviewItem span.inline-block.bp2\\:inline").text();

            MealKitRecipe recipe = new MealKitRecipe(
                    title,
                    sides,
                    mealType,
                    calories,
                    preparationTime,
                    websiteName
            );

            saveRecipeToCSV(recipe, websiteName, url);
            System.out.println("Scraping completed for: " + websiteName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Scraper for CleanPlates
    public static void scrapeCleanPlates(String url, String websiteName) {
        try {
            Document doc = Jsoup.connect(url).get();

            Elements menuItems = doc.select("p.menu_title");
            Elements descriptions = doc.select("p.menu_description");
            Elements calorieElements = doc.select("span b");

            int totalItems = Math.min(menuItems.size(),
                    Math.min(descriptions.size(), calorieElements.size()));

            for (int i = 0; i < totalItems; i++) {
                String title = menuItems.get(i).text().trim();
                String sides = descriptions.get(i).text().trim();
                int calories = Integer.parseInt(
                        calorieElements.get(i).text().replaceAll("[^\\d]", "")
                );

                String preparationTime = "20 mins";

                MealKitRecipe recipe = new MealKitRecipe(
                        title,
                        sides,
                        "Multicuisine",
                        calories,
                        preparationTime,
                        websiteName
                );

                saveRecipeToCSV(recipe, websiteName, url);
            }

            System.out.println("Scraping completed for: " + websiteName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Scraper for MakeGoodFood
    public static void scrapeMakeGoodFood(String url, String websiteName) {
        try {
            Document doc = Jsoup.connect(url).get();

            Element titleElement = doc.select("title").first();
            String title = (titleElement != null)
                    ? titleElement.text().split("\\|")[0].trim()
                    : "Unknown Title";

            Element sidesElement = doc.select("h2[data-testid='text'].mt-2.mb-2").first();
            String sides = (sidesElement != null && !sidesElement.text().trim().isEmpty())
                    ? sidesElement.text().trim()
                    : "with Mashed Potatoes & Roasted Brussels Sprouts";

            Element caloriesElement = doc.select("p[data-testid='product-nutritional-info-calories-text'].ml-2").first();
            int calories = 0;
            if (caloriesElement != null) {
                String caloriesText = caloriesElement.text().replaceAll("[^\\d]", "").trim();
                if (!caloriesText.isEmpty()) {
                    calories = Integer.parseInt(caloriesText);
                }
            }
            if (calories == 0) {
                calories = 650;
            }

            Element prepTimeElement = doc.select("p[data-testid='product-nutritional-info-prep-time-text'].ml-2").first();
            String preparationTime = (prepTimeElement != null && !prepTimeElement.text().trim().isEmpty())
                    ? prepTimeElement.text().trim()
                    : "20 mins";

            MealKitRecipe recipe = new MealKitRecipe(
                    title,
                    sides,
                    "Multicuisine",
                    calories,
                    preparationTime,
                    websiteName
            );

            saveRecipeToCSV(recipe, websiteName, url);
            System.out.println("Scraping completed for: " + websiteName);
        } catch (IOException e) {
            System.out.println("Error fetching data from URL: " + url);
            e.printStackTrace();
        }
    }

    private static Set<String> loadExistingRecipeTitles(String fileName) {
        Set<String> existingTitles = new HashSet<>();
        File file = new File(fileName);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                reader.readLine(); // skip header
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length > 0) {
                        String title = tokens[0].replaceAll("^\"|\"$", "");
                        existingTitles.add(title);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return existingTitles;
    }

    private static String getDefaultWeblink(String websiteName) {
        return switch (websiteName.toLowerCase()) {
            case "hellofresh" -> "www.hellofresh.com";
            case "homechef" -> "www.homechef.com";
            case "makegoodfood" -> "www.makegoodfood.ca";
            case "cleanplates" -> "https://cleanplates.ca";
            default -> "";
        };
    }

    private static String getDefaultEmail(String websiteName) {
        return switch (websiteName.toLowerCase()) {
            case "hellofresh" -> "hello@hellofresh.com";
            case "homechef" -> "support@homechef.com";
            case "makegoodfood" -> "chef@makegoodfood.ca";
            case "cleanplates" -> "info@cleanplates.ca";
            default -> "";
        };
    }

    private static String getDefaultContact(String websiteName) {
        return switch (websiteName.toLowerCase()) {
            case "hellofresh" -> "1-905-636-8888";
            case "homechef" -> "1-872-225-2433";
            case "makegoodfood" -> "N/A";
            case "cleanplates" -> "1-905-636-8888";
            default -> "";
        };
    }

    private static void saveRecipeToCSV(MealKitRecipe recipe, String websiteName, String moduleUrl) {
        if (recipe.website.equalsIgnoreCase("Factor")) {
            System.out.println("Discarding recipe with website 'Factor': " + recipe.title);
            return;
        }

        // ✅ Save to IntelliJ project root
        String directoryPath = ""; // Save in root directory
        String fileName = directoryPath + websiteName + ".csv";

        Set<String> existingTitles = loadExistingRecipeTitles(fileName);
        if (existingTitles.contains(recipe.title)) {
            System.out.println("Duplicate recipe skipped: " + recipe.title);
            return;
        }

        boolean fileExists = new File(fileName).exists();

        try (PrintWriter writer = new PrintWriter(new FileOutputStream(fileName, true))) {
            if (!fileExists) {
                writer.println("Title,Sides,MealType,Calories,PreparationTime,Website,Weblink,Order,Email,Contact");
            }

            String weblink = getDefaultWeblink(websiteName);
            String email = getDefaultEmail(websiteName);
            String contact = getDefaultContact(websiteName);

            writer.printf("\"%s\",\"%s\",%s,%d,\"%s\",%s,\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    recipe.title,
                    recipe.sides,
                    recipe.mealType,
                    recipe.calories,
                    recipe.preparationTime,
                    recipe.website,
                    weblink,
                    moduleUrl,
                    email,
                    contact
            );

            System.out.println("Saved recipe: " + recipe.title + " to " + fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
