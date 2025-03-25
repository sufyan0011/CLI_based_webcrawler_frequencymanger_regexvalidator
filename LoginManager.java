import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;

public class LoginManager {

    private static final String USER_CSV = "users.csv";

    public static boolean registerUser(String email, String password, String phoneNumber, String postalCode) {
        File file = new File(USER_CSV);

        // Check if user already exists
        if (file.exists()) {
            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                String[] line;
                while ((line = reader.readNext()) != null) {
                    if (line.length > 0 && line[0].trim().equalsIgnoreCase(email)) {
                        System.out.println("User with this email already exists.");
                        return false;
                    }
                }
            } catch (IOException | CsvValidationException e) {
                System.err.println("Error reading user file.");
                e.printStackTrace();
                return false;
            }
        }

        // Append the new user to the CSV file
        try (CSVWriter writer = new CSVWriter(new FileWriter(file, true))) {
            String[] record = { email, password, phoneNumber, postalCode };
            writer.writeNext(record);
        } catch (IOException e) {
            System.err.println("Error writing to user file.");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean loginUser(String email, String password) {
        File file = new File(USER_CSV);

        if (!file.exists()) {
            System.out.println("No registered users found. Please register first.");
            return false;
        }

        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] line;

            while ((line = reader.readNext()) != null) {
                if (line.length >= 2 &&
                        line[0].trim().equalsIgnoreCase(email) &&
                        line[1].trim().equals(password)) {
                    return true;
                }
            }

        } catch (IOException | CsvValidationException e) {
            System.err.println("Error reading user file during login.");
            e.printStackTrace();
        }

        return false;
    }
}
