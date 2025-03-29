package examples.com;

// Import required Java I/O and networking classes
import java.io.BufferedReader;       // Reads text from a character-input stream
import java.io.InputStreamReader;    // Bridge from byte streams to character streams
import java.net.HttpURLConnection;   // For making HTTP connections
import java.net.URL;                 // Represents a Uniform Resource Locator
import org.json.JSONObject;          // For parsing and working with JSON data

public class Main {
    // Main method - entry point of the application
    public static void main(String[] args) throws Exception {
        // 1. Make HTTP GET request to a URL
        // Create a URL object for the target endpoint
        URL url = new URL("https://hello-welcome-to-my-site.netlify.app/about/");

        // Open a connection to the URL and cast it to HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Set the HTTP request method to GET
        conn.setRequestMethod("GET");

        // 2. Get response metadata
        // Retrieve the HTTP status code (200 = OK, 404 = Not Found, etc.)
        int responseCode = conn.getResponseCode();
        // Get the content type header to verify response format
        String contentType = conn.getContentType();
        System.out.println("Status code: " + responseCode);
        System.out.println("Content type: " + contentType);

        // Process only successful responses (status code 200-299)
        if (responseCode >= 200 && responseCode < 300) {
            // 3. Verify content type before JSON parsing
            if (contentType != null && contentType.contains("application/json")) {
                // Create a BufferedReader to read the response from the server
                // InputStreamReader converts the byte stream to characters
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                // Variables to store each line of response and build complete response
                String inputLine;
                StringBuilder response = new StringBuilder();

                // Read the response line by line and append to the StringBuilder
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);  // Add each line to the response
                }

                // Close the BufferedReader to release resources
                in.close();

                // 4. Attempt to parse the JSON response
                try {
                    // Convert the response string to a JSONObject
                    JSONObject obj = new JSONObject(response.toString());
                    String key = "answer"; // The JSON key we want to extract

                    // Safely check if the key exists before accessing it
                    if (obj.has(key)) {
                        System.out.println("API Response: " + obj.getString(key));
                    } else {
                        System.out.println("Key '" + key + "' not found in JSON");
                        System.out.println("Full response: " + response.toString());
                    }
                } catch (Exception e) {
                    // Handle JSON parsing errors
                    System.out.println("JSON parsing error: " + e.getMessage());
                    System.out.println("Response might not be valid JSON");
                }
            } else {
                // Handle non-JSON responses
                System.out.println("Unexpected content type. Expected JSON but got: " + contentType);

                // Read response anyway for debugging purposes
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                // Concatenate all lines of the response
                String debugResponse = in.lines().reduce("", String::concat);
                in.close();
                System.out.println("Response content: " + debugResponse);
            }
        } else {
            // Handle failed requests
            System.out.println("Request failed with status: " + responseCode);
        }
    }
}