import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.*;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Crawler {

    // Utility method to load existing composite keys from CSV
    private static Set<String> loadExistingEntries(String fileName) {
        Set<String> existingEntries = new HashSet<>();
        File file = new File(fileName);
        if (file.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                // Skip header
                String line = br.readLine();
                while ((line = br.readLine()) != null) {
                    // Assuming CSV is in the format: "OverallRating","Weekly Price","WebsiteName"
                    String[] tokens = line.split(",");
                    if (tokens.length >= 3) {
                        // Remove quotes and trim
                        String overallRating = tokens[0].replaceAll("^\"|\"$", "").trim();
                        String weeklyPrice = tokens[1].replaceAll("^\"|\"$", "").trim();
                        String websiteName = tokens[2].replaceAll("^\"|\"$", "").trim();
                        String key = overallRating + "||" + weeklyPrice + "||" + websiteName;
                        existingEntries.add(key);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return existingEntries;
    }

    public static void main(String[] args) {
        // Set the path to your ChromeDriver executable (update the path accordingly)
        //System.setProperty("webdriver.chrome.driver", "path/to/chromedriver");

// Define the directory and CSV file path
        String fileName = "MealKitsCanada.csv";
        File file = new File(fileName);
        boolean fileExists = file.exists(); //

// Create the file if it doesn't exist
        if (!fileExists) {
            try {
                file.createNewFile();
                System.out.println("File created: " + file.getAbsolutePath());
            } catch (IOException e) {
                System.out.println("An error occurred while creating the file.");
                e.printStackTrace();
            }
        }

        // Load existing entries from the CSV to avoid duplicates
        Set<String> existingEntries = loadExistingEntries(fileName);

        // Initialize ChromeDriver
        WebDriver driver = new ChromeDriver();

        try {
            // Navigate to MealKitsCanada website
            driver.get("https://mealkitscanada.ca/");

            // Create an explicit wait (up to 20 seconds)
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            // Wait for the container that holds the items to be visible
            String containerSelector = "body > div.layout > main > div > div > section.top-rating__wrapper > section > div";
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(containerSelector)));

            // Locate all similar items (each item is assumed to be a child div inside the container)
            List<WebElement> items = driver.findElements(By.cssSelector(containerSelector + " > div"));
            System.out.println("Found " + items.size() + " items.");

            // Open CSV file for writing in append mode
            try (PrintWriter writer = new PrintWriter(new FileOutputStream(fileName, true))) {
                // If the file is new, write the header first
                if (!fileExists) {
                    writer.println("OverallRating,Weekly Price,WebsiteName");
                }

                // Loop through each item and extract the required details using relative selectors
                for (WebElement item : items) {
                    String overallRating = "";
                    String weeklyPrice = "";
                    String websiteName = "";

                    // Extract overall rating using relative selector
                    try {
                        overallRating = item.findElement(By.cssSelector("div:nth-child(1) > div.top-rating__stars > span.num.color-secondary")).getText();
                    } catch (Exception e) {
                        overallRating = "Not found";
                    }

                    // Extract weekly price using relative selector
                    try {
                        weeklyPrice = item.findElement(By.cssSelector("div:nth-child(3) > div:nth-child(2)")).getText();
                    } catch (Exception e) {
                        weeklyPrice = "Not found";
                    }

                    // Extract website name using relative selector (from the image's alt attribute)
                    try {
                        WebElement websiteImage = item.findElement(By.cssSelector("div:nth-child(1) > a:nth-child(2) > figure > img"));
                        websiteName = websiteImage.getAttribute("alt");
                        if (websiteName == null || websiteName.isEmpty()) {
                            websiteName = websiteImage.getAttribute("src");
                        }
                    } catch (Exception e) {
                        websiteName = "Not found";
                    }

                    // Discard items where the Website Name equals "Factor"
                    if (websiteName.equalsIgnoreCase("Factor")) {
                        System.out.println("Skipping item with Website Name = Factor");
                        continue;
                    }

                    // Create a composite key for duplicate checking
                    String key = overallRating + "||" + weeklyPrice + "||" + websiteName;
                    if(existingEntries.contains(key)){
                        System.out.println("Skipping duplicate entry: " + key);
                        continue; // Skip duplicate
                    }

                    // Add new key to the set so duplicates in the same run are avoided
                    existingEntries.add(key);

                    // Print the scraped details to console
                    System.out.println("Overall Rating: " + overallRating);
                    System.out.println("Weekly Price: " + weeklyPrice);
                    System.out.println("Website Name: " + websiteName);
                    System.out.println("------------------------------");

                    // Save the scraped details to the CSV file
                    writer.printf("\"%s\",\"%s\",\"%s\"\n", overallRating, weeklyPrice, websiteName);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Close the browser
            driver.quit();
        }
    }
}
