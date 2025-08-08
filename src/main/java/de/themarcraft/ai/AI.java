/**
 * themarcraft AI
 * A Simple way to integrate AI in your Project using Gemini
 * @author Marvin Niermann <me@themarcraft.de>
 * @version 08.08.2025
 * @copyright 2025 themarcraft.de
 */
package de.themarcraft.ai;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AI {
    String token;
    URL url;

    /**
     * Initialize your AI
     * @param token Gemini Token
     */
    public AI(String token) {
        this.token = token;
        try {
            this.url = new URL("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent");;
        }catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Make a new Query to Gemini
     * @param query Ask your Question
     * @return the Response of the Query
     */
    public String newQuery(String query) {
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("x-goog-api-key", token);
            conn.setDoOutput(true);

            String jsonInputString = "{\n" +
                    "    \"contents\": [\n" +
                    "      {\n" +
                    "        \"parts\": [\n" +
                    "          {\n" +
                    "            \"text\": \""+query+"\"\n" +
                    "          }\n" +
                    "        ]\n" +
                    "      }\n" +
                    "    ]\n" +
                    "  }";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Read response
            int status = conn.getResponseCode();
            InputStream is = (status < HttpURLConnection.HTTP_BAD_REQUEST)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }
                return extractText(response.toString());
            }catch (IOException e) {
                e.printStackTrace();
            }

            conn.disconnect();
        }catch (Exception e) {
            return e.getMessage();
        }
        return "Fehler beim Verarbeiten der Nachricht";
    }

    /**
     * Extract the Text Message from the response String of the AI
     * @param json the whole String
     * @return Only the Text Message String
     */
    private static String extractText(String json) {
        String key = "{\"candidates\": [{\"content\": {\"parts\": [{\"text\": \"";
        int start = json.indexOf(key);
        if (start == -1) return null;

        start += key.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;

        return json.substring(start, end);
    }

    public static void main(String[] args) {
        AI test = new AI("AIzaSyAG_RILmYHA1yiD1klkQ3xrAXdmMNYOQ0c");
        System.out.println(test.newQuery("Ich mag Zuege"));
    }
}