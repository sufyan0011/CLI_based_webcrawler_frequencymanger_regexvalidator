import java.util.*;

public class IntegratedMain {

    // === Centered Text Helper ===
    public static void printCentered(String text, int width) {
        int padding = (width - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padding; i++) sb.append(" ");
        
        sb.append(text);
        System.out.println(sb.toString());
    }

    // === Burger Banner ===
    public static void printBurgerBanner() {
        final int TERMINAL_WIDTH = 80;
        printCentered("  \\o8Oo./", TERMINAL_WIDTH);
        printCentered("  _o8o8o8Oo_.", TERMINAL_WIDTH);
        printCentered(" \\========/", TERMINAL_WIDTH);
        printCentered("  \\------/'", TERMINAL_WIDTH);
        System.out.println();
    }

    // === Welcome Banner ===
    public static void printWelcomeBanner(String userEmail) {
        String[] banner = {
                "**************************************************",
                "*                                                *",
                String.format("*      Welcome back, %-22s*", userEmail),
                "*      Your Food Hunting Begins =)               *",
                "*                                                *",
                "**************************************************"
        };
        for (String line : banner) System.out.println(line);
    }

    // === Exit ===
    public static void exitProgram() {
        System.out.println("Exiting program. Goodbye!");
        System.exit(0);
    }

    // === MAIN ===
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        final String EXIT_CMD = "\\quit";
        final int TERMINAL_WIDTH = 80;

        printBurgerBanner();
        printCentered("Welcome to FindYourMeal – Your Culinary Adventure Awaits!", TERMINAL_WIDTH);
        System.out.println();
        System.out.println("Type '" + EXIT_CMD + "' at any prompt to exit.");

        boolean exitProgram = false;
        boolean loggedIn = false;
        String currentUser = "";

        // === USER AUTHENTICATION ===
        while (!loggedIn) {
            System.out.print("Do you have an account? (y/n): ");
            String hasAccount = scanner.nextLine().trim().toLowerCase();
            if (hasAccount.equalsIgnoreCase(EXIT_CMD)) exitProgram();

            if (!hasAccount.equals("y") && !hasAccount.equals("n")) {
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
                continue;
            }

            if (hasAccount.equals("n")) {
                System.out.println("Let's register a new account.");

                String email = promptForEmail(scanner, EXIT_CMD);
                String password = promptForPassword(scanner, EXIT_CMD);
                String phone = promptForPhone(scanner, EXIT_CMD);
                String postalCode = promptForPostalCode(scanner, EXIT_CMD);

                if (LoginManager.registerUser(email, password, phone, postalCode)) {
                    System.out.println("Registration successful! Please log in.");
                } else {
                    System.out.println("Registration failed. Try logging in.");
                }
            }

            String loginEmail = promptForEmail(scanner, EXIT_CMD);
            System.out.print("Password: ");
            String loginPassword = scanner.nextLine().trim();

            if (loginPassword.equalsIgnoreCase(EXIT_CMD)) exitProgram();

            if (LoginManager.loginUser(loginEmail, loginPassword)) {
                System.out.println("Welcome back, " + loginEmail + "!");
                loggedIn = true;
                currentUser = loginEmail;
            } else {
                System.out.println("Login failed. Please check your credentials.");
            }
        }

        // === MAIN MENU LOOP ===
        while (!exitProgram) {
            printWelcomeBanner(currentUser);
            System.out.println("\nMain Menu:");
            System.out.println("1. Search by Keywords");
            System.out.println("2. Search by Calories");
            System.out.println("3. Trending");
            System.out.println("4. Subscription Plans");
            System.out.println("Type 'exit' or '\\quit' to quit.");

            String menuOption = scanner.nextLine().trim().toLowerCase();
            if (menuOption.equals("exit") || menuOption.equals(EXIT_CMD)) exitProgram();

            switch (menuOption) {
                case "1": keywordSearch(scanner, currentUser, EXIT_CMD); break;
                case "2": calorieFilter(scanner, EXIT_CMD); break;
                case "3": displayTrendingSearches(scanner); break;
                case "4": displaySubscriptionPlans(scanner); break;
                default: System.out.println("Invalid option. Please try again."); break;
            }
        }

        scanner.close();
    }

    // === PROMPT HELPERS ===
    private static String promptForEmail(Scanner scanner, String exitCmd) {
        String email;
        while (true) {
            System.out.print("Enter your email: ");
            email = scanner.nextLine().trim();
            if (email.equalsIgnoreCase(exitCmd)) exitProgram();
            if (!RegexValidator.isValidEmail(email)) {
                System.out.println("Invalid email format. Please try again.");
                continue;
            }
            break;
        }
        return email;
    }

    private static String promptForPassword(Scanner scanner, String exitCmd) {
        String password;
        while (true) {
            System.out.print("Enter your password (min 6 characters): ");
            password = scanner.nextLine().trim();
            if (password.equalsIgnoreCase(exitCmd)) exitProgram();
            if (!RegexValidator.isValidPassword(password)) {
                System.out.println("Invalid password. Please try again.");
                continue;
            }
            break;
        }
        return password;
    }

    private static String promptForPhone(Scanner scanner, String exitCmd) {
        String phone;
        while (true) {
            System.out.print("Enter your phone number (e.g., +1 123-456-7890): ");
            phone = scanner.nextLine().trim();
            if (phone.equalsIgnoreCase(exitCmd)) exitProgram();
            if (!RegexValidator.isValidPhoneNumber(phone)) {
                System.out.println("Invalid phone number. Please try again.");
                continue;
            }
            break;
        }
        return phone;
    }

    private static String promptForPostalCode(Scanner scanner, String exitCmd) {
        String postalCode;
        while (true) {
            System.out.print("Enter your postal code (e.g., A1A 1A1): ");
            postalCode = scanner.nextLine().trim();
            if (postalCode.equalsIgnoreCase(exitCmd)) exitProgram();
            if (!RegexValidator.isValidPostalCode(postalCode)) {
                System.out.println("Invalid postal code. Please include a space.");
                continue;
            }
            break;
        }
        return postalCode;
    }

    // === LOAD RECIPES ===
    private static List<Recipe> loadAllRecipes() {
        List<Recipe> recipesList = new ArrayList<>();
        recipesList.addAll(CSVLoader.loadRecipes("HelloFresh.csv"));
        recipesList.addAll(CSVLoader.loadRecipes("CleanPlates.csv"));
        recipesList.addAll(CSVLoader.loadRecipes("HomeChef.csv"));
        recipesList.addAll(CSVLoader.loadRecipes("MakeGoodFood.csv"));
        return recipesList;
    }

    // === CALORIE FILTER ===
    private static void calorieFilter(Scanner scanner, String exitCmd) {
        boolean continueSearch = true;
        while (continueSearch) {
            List<Recipe> recipesList = loadAllRecipes();

            try {
                System.out.print("Enter minimum calories: ");
                String minStr = scanner.nextLine().trim();
                if (minStr.equalsIgnoreCase(exitCmd)) exitProgram();
                int min = Integer.parseInt(minStr);

                System.out.print("Enter maximum calories: ");
                String maxStr = scanner.nextLine().trim();
                if (maxStr.equalsIgnoreCase(exitCmd)) exitProgram();
                int max = Integer.parseInt(maxStr);

                List<Recipe> filtered = filterByCalories(recipesList, min, max);

                if (filtered.isEmpty()) {
                    System.out.println("No recipes found.");
                } else {
                    RecipeSorter.sortByCalories(filtered);
                    displayRecipesPaginated(scanner, filtered);
                }

                System.out.print("Search again? (y/n): ");
                String again = scanner.nextLine().trim().toLowerCase();
                if (!again.equals("y")) continueSearch = false;

            } catch (NumberFormatException e) {
                System.out.println("Please enter valid numbers.");
            }
        }
    }

    private static List<Recipe> filterByCalories(List<Recipe> recipes, int min, int max) {
        List<Recipe> filtered = new ArrayList<>();
        for (Recipe r : recipes) {
            if (r.getCalories() >= min && r.getCalories() <= max) filtered.add(r);
        }
        return filtered;
    }

    // === KEYWORD SEARCH ===
    private static void keywordSearch(Scanner scanner, String currentUser, String exitCmd) {
        List<Recipe> recipesList = loadAllRecipes();
        Trie autoCompleteTrie = new Trie();
        TrieInvertedIndex trieIndex = new TrieInvertedIndex();
        RecipeRanker recipeRanker = new RecipeRanker();

        for (Recipe r : recipesList) autoCompleteTrie.insert(r.getTitle());
        trieIndex.buildIndex(recipesList);

        Set<String> vocabulary = new HashSet<>();
        for (Recipe r : recipesList) Collections.addAll(vocabulary, r.getTitle().toLowerCase().split("\\W+"));

        SpellChecker spellChecker = new SpellChecker(vocabulary);
        SearchTracker searchTracker = new SearchTracker("searchData.csv");
        WebsiteFrequencyCounter frequencyCounter = new WebsiteFrequencyCounter(recipesList);

        while (true) {
            System.out.print("\nSearch > ");
            String query = scanner.nextLine().trim();
            if (query.equalsIgnoreCase(exitCmd)) exitProgram();
            if (!RegexValidator.isValidSearchQuery(query)) {
                System.out.println("Letters and spaces only!");
                continue;
            }

            List<String> suggestions = autoCompleteTrie.getWordsWithPrefix(query);
            if (!suggestions.isEmpty()) {
                System.out.println("\nYou might be interested in:");
                for (int i = 0; i < suggestions.size(); i++) System.out.println((i + 1) + ". " + suggestions.get(i));

                System.out.print("Select number or press Enter to continue: ");
                String selection = scanner.nextLine().trim();
                if (selection.equalsIgnoreCase(exitCmd)) exitProgram();
                if (!selection.isEmpty()) {
                    try {
                        int index = Integer.parseInt(selection);
                        if (index >= 1 && index <= suggestions.size()) query = suggestions.get(index - 1);
                    } catch (NumberFormatException e) { }
                }
            }

            searchTracker.recordSearch(query, currentUser);
            List<Recipe> results = trieIndex.searchByTokens(query);

            if (results.isEmpty()) {
                System.out.println("No recipes found for \"" + query + "\".");
                List<String> spellSuggestions = spellChecker.suggest(query, 3, 2);
                if (!spellSuggestions.isEmpty()) {
                    System.out.println("\nDid you mean:");
                    for (int i = 0; i < spellSuggestions.size(); i++) System.out.println((i + 1) + ". " + spellSuggestions.get(i));

                    System.out.print("Select number or press Enter to search again: ");
                    String choice = scanner.nextLine().trim();
                    if (choice.equalsIgnoreCase(exitCmd)) exitProgram();

                    if (!choice.isEmpty()) {
                        int index = Integer.parseInt(choice);
                        if (index >= 1 && index <= spellSuggestions.size()) {
                            query = spellSuggestions.get(index - 1);
                            searchTracker.recordSearch(query, currentUser);
                            results = trieIndex.searchByTokens(query);
                        }
                    } else continue;
                } else continue;
            }

            results = recipeRanker.rank(results, query);
            int total = searchTracker.getTotalSearchCount(query);
            int unique = searchTracker.getUniqueUserCount(query);

            String funFact = unique <= 1
                    ? String.format("Fun fact: \"%s\" has been searched %d times by you.", query, total)
                    : String.format("Fun fact: \"%s\" has been searched by you and %d others, %d times!", query, unique - 1, total);

            System.out.println("\n" + funFact);

            Map<String, Integer> websiteFrequencies = frequencyCounter.getFrequencyByWebsite(query);
            List<Map.Entry<String, Integer>> sortedWebsites = new ArrayList<>(websiteFrequencies.entrySet());
            sortedWebsites.sort((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue()));

            if (!sortedWebsites.isEmpty()) {
                System.out.println("\nPages that Might Be Relevant to You:");
                int rank = 1;
                for (Map.Entry<String, Integer> e : sortedWebsites) {
                    System.out.printf("%d. %s - \"%s\" appears %d times\n", rank++, e.getKey(), query, e.getValue());
                }
            }

            System.out.print("\nPress Enter to view recipes...");
            scanner.nextLine();

            boolean returnToMenu = displayRecipeTitles(scanner, results);
            if (returnToMenu) return;
        }
    }

    private static boolean displayRecipeTitles(Scanner scanner, List<Recipe> results) {
        while (true) {
            System.out.println("\nFound " + results.size() + " recipes. Choose one:");
            for (int i = 0; i < results.size(); i++) System.out.println((i + 1) + ". " + results.get(i).getTitle());
            System.out.print("Enter number, 'back' to return, or '\\quit': ");
            String selection = scanner.nextLine().trim();
            if (selection.equalsIgnoreCase("\\quit")) exitProgram();
            if (selection.equalsIgnoreCase("back")) return false;

            try {
                int choice = Integer.parseInt(selection);
                if (choice >= 1 && choice <= results.size()) {
                    boolean goToMainMenu = displayRecipeDetails(scanner, results.get(choice - 1));
                    if (goToMainMenu) return true;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
            }
        }
    }

    private static boolean displayRecipeDetails(Scanner scanner, Recipe recipe) {
        System.out.println("\n🍽️ Recipe Details:");
        System.out.println("Title: " + recipe.getTitle());
        System.out.println("Sides: " + recipe.getSides());
        System.out.println("Meal Type: " + recipe.getMealType());
        System.out.println("Calories: " + recipe.getCalories());
        System.out.println("Preparation Time: " + recipe.getPreparationTime());
        System.out.println("Website: " + recipe.getWebsite());
        System.out.println("🌐 Link: " + recipe.getWeblink());
        System.out.println("🛒 Order: " + recipe.getOrder());

        System.out.print("\n1. Back to results\n2. Main menu\nChoice: ");
        String choice = scanner.nextLine().trim();
        if (choice.equalsIgnoreCase("\\quit")) exitProgram();
        if (choice.equals("2")) {
            System.out.println("Returning to main menu...");
            return true;
        }

        return false;
    }

    private static void displayRecipesPaginated(Scanner scanner, List<Recipe> recipes) {
        int perPage = 5;
        int i = 0;
        while (i < recipes.size()) {
            int end = Math.min(i + perPage, recipes.size());
            System.out.println("\nRecipes " + (i + 1) + " to " + end + " of " + recipes.size() + ":");
            for (int j = i; j < end; j++) {
                System.out.println(recipes.get(j));
                System.out.println("----------------------------------");
            }
            i = end;
            if (i < recipes.size()) {
                System.out.print("See more? (y/n): ");
                String input = scanner.nextLine().trim();
                if (!input.equalsIgnoreCase("y")) break;
            }
        }
    }

    private static void displayTrendingSearches(Scanner scanner) {
        SearchTracker tracker = new SearchTracker("searchData.csv");
        Map<String, Integer> counts = tracker.getAllSearchCounts();

        if (counts.isEmpty()) {
            System.out.println("No trending searches yet.");
            return;
        }

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(counts.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        System.out.println("\n🔥 Trending Searches:");
        int rank = 1;
        for (Map.Entry<String, Integer> e : sorted) {
            System.out.printf("%d. %s - %d searches\n", rank++, e.getKey(), e.getValue());
            if (rank > 10) break;
        }

        System.out.print("\nPress Enter to return to menu...");
        scanner.nextLine();
    }

    private static void displaySubscriptionPlans(Scanner scanner) {
        List<SubscriptionPlan> plans = loadSubscriptionPlans();
        if (plans.isEmpty()) {
            System.out.println("No subscription plans available.");
            return;
        }

        System.out.println("\n📦 Subscription Plans and Contact Details:");
        for (SubscriptionPlan plan : plans) {
            System.out.println(plan);
        }

        System.out.print("\nPress Enter to return to menu...");
        scanner.nextLine();
    }

    private static List<SubscriptionPlan> loadSubscriptionPlans() {
        List<SubscriptionPlan> plans = new ArrayList<>();

        Set<String> workingWith = Set.of("hellofresh", "homechef", "makegoodfood", "cleanplates");

        List<String[]> mealData = CSVLoader.loadGenericCSV("MealKitsCanada.csv");
        List<Recipe> recipes = loadAllRecipes();

        for (String[] row : mealData) {
            if (row.length < 3) continue;

            String rating = row[0].trim();
            String price = row[1].trim();
            String site = row[2].trim();
            String normalizedSite = site.toLowerCase().replaceAll("\\s+", "");

            if (!workingWith.contains(normalizedSite)) continue;

            String email = CSVLoader.findEmailByWebsiteRegex(recipes, site);
            String contact = CSVLoader.findContactByWebsite(recipes, site);

            plans.add(new SubscriptionPlan(site, price, contact, email, rating));
        }

        return plans;
    }
}
