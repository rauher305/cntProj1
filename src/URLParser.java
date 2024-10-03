import java.io.*;
import java.net.*;
public class URLParser {
    // using java.net.url class
    // step by step is to read file containing URL's


    // make a helper function that prints the protocol, host, port, and path
    // function input is protocol, host, port, path

    public static void main(String[] args) {
        try { // replace temp file with file name
            BufferedReader reader = new BufferedReader(new FileReader("MUSTREPLACETOWORK"));
            String urlString;
            // parse individual urls line by line
            while ((urlString = reader.readLine()) != null) {

                URL url = new URL(urlString);
                String protocol = url.getProtocol();
                String host = url.getHost();
                String path = url.getPath();
                int port = url.getPort();
                try {
                    // parse url and set variables to different parts of the url
                    

                    // begin testing / checking
                    if (port == -1) {
                        // http = 80
                        if ("http".equalsIgnoreCase(protocol)) {
                            port = 80;
                            // https = 443
                        } else if ("https".equalsIgnoreCase(protocol)) {
                            port = 443;
                        }
                    }
                    // step 2 | tcp connection
                    Socket socket = new Socket(host, port);
                    System.out.println("Verified connection to " + host);

                    // parse information
                    System.out.println("Protocol: " + protocol);
                    System.out.println("Host: " + host);
                    System.out.println("Port: " + port);
                    System.out.println("Path: " + path);
                    socket.close();
                    System.out.println("Connection closed");

                } catch (MalformedURLException e) {
                    // invalid the parsed information
                    System.out.println("invalid url" + urlString);

                    // step 3
                } catch (IOException e) {
                    // invalid socket catch
                    System.out.println("URL: " + protocol + host);
                    System.out.println("Status: Network Error");
                }
            }


            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
