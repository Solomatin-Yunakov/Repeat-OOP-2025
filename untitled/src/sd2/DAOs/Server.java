package untitled.src.sd2.DAOs;

import untitled.src.sd2.DTOs.CurrentPlayers;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final int SERVER_PORT_NUMBER = 8080;
    private static final MySqlCurrentPlayersDao currentplayerDao = new MySqlCurrentPlayersDao();
    public static final String GET_ALL_ENTITIES_REQUEST = "GET_ALL_ENTITIES";
    public static final String DELETE_BY_ID = "DELETE_BY_ID";
    public static final String ADD_AN_ENTITY = "ADD_AN_ENTITY";
    private static final String IMAGE_DIRECTORY = "images/";
    public static final String CLIENT_EXIT = "CLIENT_EXIT";

    private static final int THREAD_POOL_SIZE = 10;


    public static void main(String[] args) {
        Server server = new Server();
        server.start();
    }

    public void start() {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT_NUMBER)) {
            System.out.println("Server has started on port " + SERVER_PORT_NUMBER);
            int clientNumber = 0;

            while (true) {
                System.out.println("Server: Listening/waiting for connections on port ..." + SERVER_PORT_NUMBER);
                try {
                    Socket clientSocket = serverSocket.accept();
                    final int currentClientNumber = ++clientNumber;
                    System.out.println("Server: Client " + clientNumber + " has connected.");
                    executor.submit(() -> handleClient(clientSocket, currentClientNumber));
                } catch (IOException e) {
                    System.out.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Could not start server: " + e.getMessage());
        } finally {
            executor.shutdown();
            System.out.println("Server: Server exiting, Goodbye!");
        }
    }

    private void handleClient(Socket clientSocket, int clientNumber) {
        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                OutputStream binaryOut = clientSocket.getOutputStream()
        ) {
            System.out.println("Server: Handling Client " + clientNumber);
            System.out.println("Server: Port number of remote client: " + clientSocket.getPort());
            System.out.println("Server: Port number of the socket used to talk with client " + clientSocket.getLocalPort());

            String request = in.readLine();
            System.out.println("Server: Received request from Client " + clientNumber + ": " + request);

//            if (GET_ALL_ENTITIES_REQUEST.equals(request)) {
//                String jsonResponse = getAllPlayers();
//                System.out.println("Server: Sending response to Client " + clientNumber + ": " + jsonResponse);
//                out.println(jsonResponse);
//            } else if (request.startsWith(DELETE_BY_ID)) {
//                String id = request.substring(DELETE_BY_ID.length() + 1);
//                String response = deleteById(id);
//                out.println(response);
//            } else if (request.startsWith(ADD_AN_ENTITY)) {
//                String JSONcurrentplayer = request.substring(ADD_AN_ENTITY.length() + 1);
//                String response = addcurrentplayer(JSONcurrentplayer);
//                out.println(response);
//            } else if (CLIENT_EXIT.equals(request)) {
//                System.out.println("Server: Client " + clientNumber + " has requested to exit.");
//                JSONObject response = new JSONObject();
//                response.put("success", true);
//                out.println(response.toString());
//            }
        } catch (IOException e) {
            System.out.println("Error handling Client " + clientNumber + ": " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing client socket for Client " + clientNumber + ": " + e.getMessage());
            }
        }
    }

    public static String convertToJson(List<CurrentPlayers> playerslist) {
        if (playerslist == null) {
            return new JSONArray().toString();
        }
        JSONArray jsonArray = new JSONArray();
        for (CurrentPlayers currentplayer : playerslist) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", currentplayer.getPlayerid());
            jsonObject.put("name", currentplayer.getPlayerName());
            jsonObject.put("height", currentplayer.getPlayerHeight());
            jsonObject.put("date of birth", currentplayer.getdateofbirth());
            jsonArray.put(jsonObject);
        }
        return jsonArray.toString();
    }
    private static String getAllEntitiesAsJson() {
        try {
            List<CurrentPlayers> playerslist = currentplayerDao.findAllPlayers();
            return convertToJson(playerslist);
        } catch (Exception e) {
            System.out.println("Error fetching materials: " + e.getMessage());
            return new JSONArray().toString();
        }
    }



//    private String getAllPlayers() {
//
//        return ;
//    }
//
//    private String deleteById(String id) {
//
//        return ;
//    }
//
//    private String addcurrentplayer(String jsoncurrentplayer) {
//
//        return ;
}
