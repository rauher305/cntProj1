import java.io.*;
import java.net.*;

public class URLParser {

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("src/urls-file.txt"));
            String urlString;
            // call function on given url string line
            while ((urlString = reader.readLine()) != null) {
                fetchURL(urlString);
                System.out.println();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void fetchURL(String urlString) {
        try {
            URL url = new URL(urlString);
            String protocol = url.getProtocol();
            String host = url.getHost();
            String path = url.getPath();
            int port = url.getPort();

            // Set the default port
            if (port == -1) { // if you are familiar with below, it is a ternary operator, value left = true, value right = False
                // 80 for http, 443 for otherwise (https)
                port = "http".equalsIgnoreCase(protocol) ? 80 : 443;
            }

            // Create socket and establish TCP connection
            try (Socket socket = new Socket(host, port);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                // Send an HTTP GET request
                String request = "GET " + path + " HTTP/1.1\r\n" + "Host: " + host + "\r\n" + "Connection: closed\r\n\r\n";

                out.println(request);

                // Read the response status line
                String responseLine = in.readLine();
                System.out.println("URL: " + urlString);
                System.out.println("Status: " + responseLine);

                // redirection if not 301 or 302
                if (responseLine != null && (responseLine.contains("301") || responseLine.contains("302"))) {
                    String location = "";
                    String line;

                    // Read headers to find the Location header
                    while ((line = in.readLine()) != null && !line.isEmpty()) {
                        if (line.startsWith("Location:")) {
                            location = line.substring(10).trim(); // Extract the new URL
                        }
                    }

                    // Print redirect information
                    System.out.println("Redirected URL: " + location);

                    // Follow the redirect if a new location is provided
                    if (!location.isEmpty()) {
                        // Create a new URL object for the redirected URL
                        URL redirectUrl = new URL(location);
                        // Print status for the redirected URL
                        fetchURL(location);
                    }
                } else {
                    // Handle other status codes

                    System.out.println("Final response received for " + urlString + ": not a redirect.");
                }

            } catch (IOException e) {
                //System.out.println("Network error when connecting to " + host + ": " + e.getMessage());
                System.out.println("URL: " + urlString);
                System.out.println("Status: Network Error");
            }

        } catch (MalformedURLException e) {
            System.out.println("Invalid URL: " + urlString);
        } catch (IOException e) {
            System.out.println("Error reading URL: " + e.getMessage());
        }
    }
}

