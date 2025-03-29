package examples.com;

// Import required Java I/O and networking classes
import java.io.BufferedReader;       // Reads text from a character-input stream
import java.io.InputStreamReader;    // Bridge from byte streams to character streams
import java.net.HttpURLConnection;   // For making HTTP connections
import java.net.URL;                 // Represents a Uniform Resource Locator
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONObject;          // For parsing and working with JSON data

public class Main {
    // Main method - entry point of the application
    public static void main(String[] args) throws Exception {
        // 1. Make HTTP GET request to a URL
        // Create a URL object for the target endpoint
        URL url = new URL("https://v2.jokeapi.dev/joke/Programming?type=single");

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
            if (contentType != null && contentType.contains("application/json")) {
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
            }  else {
                // 4. Simple HTML parsing using regex
                System.out.println("\nAttempting HTML content extraction:");

                // Extract title tag content
                extractHtmlTag(response.toString(), "<title>(.*?)</title>", "Title");

                // Extract h1 headings
                extractHtmlTag(response.toString(), "<h1.*?>(.*?)</h1>", "Heading 1");

                // Extract paragraph content
                extractHtmlTag(response.toString(), "<p.*?>(.*?)</p>", "Paragraph");

                // Show first 200 chars of raw response
                System.out.println("\nRaw content preview: " +
                        response.substring(0, Math.min(response.length(), 200)) + "...");
            }
        } else {
            // Handle failed requests
            System.out.println("Request failed with status: " + responseCode);
        }
    }

    /*
     * Helper method to extract content from HTML tags using regex
     * @param html The HTML content to parse
     * @param regex Pattern to match the HTML tag
     * @param label Description of what's being extracted
     */
    private static void extractHtmlTag(String html, String regex, String label) {
        // Compile the regex pattern with CASE_INSENSITIVE flag
        // to match tags regardless of uppercase/lowercase
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

        // Create a matcher that will scan the HTML string for matches
        Matcher matcher = pattern.matcher(html);

        // Loop through all matches found in the HTML string
        while (matcher.find()) {
            // Extract the content between the tags (group 1 in the regex match)
            String content = matcher.group(1)
                    // Remove any nested HTML tags from the extracted content
                    // by replacing anything between < and > with empty string
                    .replaceAll("<[^>]*>", "")
                    // Trim whitespace from both ends of the string
                    .trim();

            // Only print if the extracted content isn't empty
            if (!content.isEmpty()) {
                // Print the label (e.g., "Heading 1") followed by the cleaned content
                System.out.println(label + ": " + content);
            }
        }
    }
}