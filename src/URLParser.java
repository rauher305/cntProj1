import java.io.*;
import java.net.*;

public class URLParser {

    public static void main(String[] args) {
        try {
            // we need to change the file to accept inline txt.file argument ie: "java monitor urls-file" in the terminal
            BufferedReader reader = new BufferedReader(new FileReader("src/urls-file.txt"));
            String urlString;
            // call function on given url string line
            while ((urlString = reader.readLine()) != null) {
                fetchURL(urlString,0);
                System.out.println();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void fetchURL(String urlString, int flag) { // flag == 0 for default, 1 for redirect, 2 for reference
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


                //trying to figure out how to find referenced url




                //System.out.println("URL: " + urlString);
                if(flag == 0)
                    System.out.print("URL: " + urlString);
                else if (flag == 1)
                    System.out.print("Redirected URL: " + urlString);
                else if (flag == 2)
                    System.out.print("Referenced URL: " + urlString);



                System.out.println("\nStatus: " + responseLine.substring(9));

                // redirection if not 301 or 302
                if ((responseLine != null) && (responseLine.contains("301") || responseLine.contains("302"))) {
                    String location = "";
                    String line;

                    // Read headers to find the Location header
                    while ((line = in.readLine()) != null && !line.isEmpty()) {

                        if (line.startsWith("Location:")) {
                            location = line.substring(10).trim(); // Extract the new URL
                        }
                    }

                    // Print redirect information

                    // Follow the redirect if a new location is provided
                    if (!location.isEmpty()) {
                        // Create a new URL object for the redirected URL
                        URL redirectUrl = new URL(location);
                        // Print status for the redirected URL
                        fetchURL(location,1 );
                    }
                }else if (urlString.contains(".html")) {
                    String imgTAG;
                    String line;
                    while ((line = in.readLine()) != null){
                        if(line.startsWith("<img ")){
                            line = line.substring(10);
                            String[] parts = line.split("\\\"");
                            line = parts[0];
                            if(line.charAt(0) == 'h')
                                fetchURL(line,2);
                            else {
                                int urlEndInt = urlString.lastIndexOf('/');
                                String urlStart = urlString.substring(0,urlEndInt);
                                String fullUrl = urlStart + line;
                                fetchURL(fullUrl,2);
                            }

                        }
                    }


                }/*else {
                    // Handle other status codes

                    System.out.println("Final response received for " + urlString + ": not a redirect.");
                }*/

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

