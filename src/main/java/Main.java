import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class Main {
    public static void main(String[] args) {
        DatabaseManager manager = DatabaseManager.getManager();
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(8000), 0);
            server.createContext("/signup", new SignupHandler());
            server.setExecutor(null);
            server.start();

        } catch (IOException e) {
            e.printStackTrace();
        }


//        manager.addUser(new User("vex","ali","yazdi","ali@gmail.com","","1234","ir","2004-11-21"));
        Thread clientHandler = new ClientHandler(manager.getUser("mmd"));
        clientHandler.start();
    }

    static class SignupHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {

            if(t.getResponseBody().equals("POST")){
                InputStream in = t.getRequestBody();
                byte[] request = in.readAllBytes();
                String requestBody = new String(request);

            }
            String response = "This is the response";
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
