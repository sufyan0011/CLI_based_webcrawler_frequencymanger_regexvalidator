import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SearchTracker {

    private Map<String, Integer> totalSearchCount;
    private Map<String, Set<String>> uniqueUsers;
    private String csvFilePath;

    public SearchTracker(String csvFilePath) {
        this.csvFilePath = csvFilePath;
        totalSearchCount = new HashMap<>();
        uniqueUsers = new HashMap<>();
        loadFromCSV();
    }

    private String normalize(String query) {
        return query.trim().toLowerCase();
    }

    public void recordSearch(String query, String userEmail) {
        String normalizedQuery = normalize(query);
        int currentCount = totalSearchCount.getOrDefault(normalizedQuery, 0);
        totalSearchCount.put(normalizedQuery, currentCount + 1);

        uniqueUsers.putIfAbsent(normalizedQuery, new HashSet<>());
        uniqueUsers.get(normalizedQuery).add(userEmail);

        saveToCSV();
    }

    public int getTotalSearchCount(String query) {
        int count = totalSearchCount.getOrDefault(normalize(query), 0);
        return (count == 0) ? 1 : count;
    }

    public int getUniqueUserCount(String query) {
        return uniqueUsers.containsKey(normalize(query))
                ? uniqueUsers.get(normalize(query)).size()
                : 0;
    }

    // ✅ ✅ ✅ Add This Method ✅ ✅ ✅
    public Map<String, Integer> getAllSearchCounts() {
        return new HashMap<>(totalSearchCount);
    }

    private void loadFromCSV() {
        File file = new File(csvFilePath);
        if (!file.exists()) {
            return;
        }

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] nextLine;
            reader.readNext(); // skip header

            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length < 3) continue;

                String query = normalize(nextLine[0]);
                int count = Integer.parseInt(nextLine[1].trim());
                if (count <= 0) continue;

                String[] users = nextLine[2].trim().split(";");
                totalSearchCount.put(query, count);
                uniqueUsers.put(query, new HashSet<>(Arrays.asList(users)));
            }

        } catch (IOException | CsvValidationException e) {
            System.err.println("Error loading search data from CSV: " + e.getMessage());
        }
    }

    private void saveToCSV() {
        try (CSVWriter writer = new CSVWriter(new FileWriter(csvFilePath))) {
            String[] header = {"query", "totalCount", "uniqueUsers"};
            writer.writeNext(header);

            for (String query : totalSearchCount.keySet()) {
                int count = totalSearchCount.get(query);
                Set<String> usersSet = uniqueUsers.getOrDefault(query, new HashSet<>());

                String users = String.join(";", usersSet);
                String[] line = {query, String.valueOf(count), users};

                writer.writeNext(line);
            }

        } catch (IOException e) {
            System.err.println("Error saving search data to CSV: " + e.getMessage());
        }
    }
}
