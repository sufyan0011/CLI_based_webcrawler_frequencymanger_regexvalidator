import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class CSVLoader {

    public static List<Recipe> loadRecipes(String csvFilePath) {
        List<Recipe> recipes = new ArrayList<>();
        File file = new File(csvFilePath);

        if (!file.exists()) {
            System.err.println("File not found: " + csvFilePath);
            return recipes;
        }

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] tokens;
            int lineNumber = 0;

            tokens = reader.readNext(); // Skip header
            if (tokens == null) {
                System.err.println("Empty CSV file: " + csvFilePath);
                return recipes;
            }

            while ((tokens = reader.readNext()) != null) {
                lineNumber++;
                if (tokens.length < 10) {
                    System.err.println("Skipping malformed line " + lineNumber);
                    continue;
                }

                try {
                    String title = tokens[0].trim();
                    String sides = tokens[1].trim();
                    String mealType = tokens[2].trim();
                    int calories = Integer.parseInt(tokens[3].trim());
                    String preparationTime = tokens[4].trim();
                    String website = tokens[5].trim();
                    String weblink = tokens[6].trim();
                    String order = tokens[7].trim();
                    String email = tokens[8].trim();   // Can be empty or junk
                    String contact = tokens[9].trim();

                    recipes.add(new Recipe(title, sides, mealType, calories, preparationTime,
                            website, weblink, order, email, contact));

                } catch (NumberFormatException e) {
                    System.err.println("Skipping line " + lineNumber + ": invalid number format");
                }
            }

        } catch (IOException | CsvValidationException e) {
            System.err.println("Error reading CSV file: " + csvFilePath);
            e.printStackTrace();
        }

        return recipes;
    }

    // Loads MealsKitCanada CSV file
    public static List<String[]> loadGenericCSV(String csvFilePath) {
        List<String[]> rows = new ArrayList<>();
        File file = new File(csvFilePath);

        if (!file.exists()) {
            System.err.println("File not found: " + csvFilePath);
            return rows;
        }

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] tokens;
            reader.readNext(); // Skip header
            while ((tokens = reader.readNext()) != null) {
                rows.add(tokens);
            }

        } catch (IOException | CsvValidationException e) {
            System.err.println("Error reading CSV file: " + csvFilePath);
            e.printStackTrace();
        }

        return rows;
    }

    // ➤ Regex search for email by website across recipe data
    public static String findEmailByWebsiteRegex(List<Recipe> recipes, String websiteName) {
        for (Recipe recipe : recipes) {
            if (recipe.getWebsite().equalsIgnoreCase(websiteName)) {

                // Search both email + contact + weblink + order fields (or more)
                String[] fieldsToSearch = {
                        recipe.getEmail(),
                        recipe.getContact(),
                        recipe.getWeblink(),
                        recipe.getOrder()
                };

                for (String field : fieldsToSearch) {
                    String email = RegexValidator.extractEmail(field);
                    if (email != null) {
                        return email;
                    }
                }
            }
        }
        return "No Email Found";
    }

    public static String findContactByWebsite(List<Recipe> recipes, String websiteName) {
        for (Recipe recipe : recipes) {
            if (recipe.getWebsite().equalsIgnoreCase(websiteName)) {
                return recipe.getContact();
            }
        }
        return "Contact Not Found";
    }
}
