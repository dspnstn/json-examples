package examples.com;

// Import required Java I/O and networking classes

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class Main {
    public static void main(String[] args) throws Exception {
            // 1. Make HTTP GET request to a JSON API
        // Create a URL object for the target API endpoint
        URL url = new URL("https://hello-welcome-to-my-site.netlify.app/about/");

        // Open a connection to the URL and cast it to HttpURLConnection
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        // Set the HTTP request method to GET
        conn.setRequestMethod("GET");

            // 2. Read the response
        // Get the HTTP response code - status code -
        // from the server (200 = OK, 404 = Not Found, etc.)
        int responseCode = conn.getResponseCode();
        System.out.println("Status code: " + responseCode);

        // Create a BufferedReader to read the response from the server
        // InputStreamReader converts the byte stream
            // from conn.getInputStream() to characters
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        // Variables to store each line of response and build the complete response
        String inputLine;
        StringBuilder response = new StringBuilder();

        // Read the response line by line and append to the StringBuilder
        while((inputLine = in.readLine()) != null) {
            response.append(inputLine); //add each line to the response
        }

        //Close the BufferedReader to release resources
        in.close();

        //Print the complete response as a String IF NEEDED
        //System.out.println(response.toString());

            // 3. Parse the JSON response
        JSONObject obj = new JSONObject(response.toString());
        String key = "answer"; // The JSON key you want to extract

        // 4. Safely check and get the value
        if (obj.has(key)) {
            System.out.println("API Response: " + obj.getString(key));
        } else {
            System.out.println("Key '" + key + "' not found in JSON");
        }

    }
}