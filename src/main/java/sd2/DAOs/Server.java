package sd2.DAOs;

import sd2.DTOs.CurrentPlayers;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Server {
    private static final int SERVER_PORT_NUMBER = 8080;
    private static final MySqlCurrentPlayersDao currentplayerDao = new MySqlCurrentPlayersDao();
    public static final String GET_ALL_ENTITIES_REQUEST = "GET_ALL_ENTITIES";
    public static final String GET_BY_ID = "GET_BY_ID";
    public static final String DELETE_BY_ID = "DELETE_BY_ID";
    public static final String ADD_AN_ENTITY = "ADD_AN_ENTITY";
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
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

            String request = in.readLine();
            if (request == null) return;

            if (GET_ALL_ENTITIES_REQUEST.equals(request)) {
                String jsonResponse = getAllEntitiesAsJson();
                out.println(jsonResponse);
            }else if (request.startsWith(GET_BY_ID)) {
                    String id = request.substring(GET_BY_ID.length()).trim();
                    out.println(getByIdResponse(id));
            } else if (request.startsWith(DELETE_BY_ID)) {
                String id = request.substring(DELETE_BY_ID.length()).trim();
                out.println(deleteByIdResponse(id));
            } else if (request.startsWith(ADD_AN_ENTITY)) {
                String holder = request.substring(ADD_AN_ENTITY.length()).trim();
                out.println(addEntityResponse(holder));
            } else{
                out.println(new JSONObject().put("success", false).put("message", "Unknown request").toString());
            }
        } catch (IOException e) {
            System.out.println("Error handling Client " + clientNumber + ": " + e.getMessage());
        }
    }

    public static String convertToJson(List<CurrentPlayers> players) {
        JSONArray jsonArray = new JSONArray();
        if (players != null) {
            for (CurrentPlayers p : players) {
                jsonArray.put(playerToJson(p));
            }
        }
        return jsonArray.toString();
    }

    private static JSONObject playerToJson(CurrentPlayers p) {
        return new JSONObject()
                .put("id", p.getPlayerid())
                .put("name", p.getPlayerName())
                .put("height", p.getPlayerHeight())
                .put("date_of_birth", p.getdateofbirth() == null ? JSONObject.NULL : p.getdateofbirth().toString());
    }

    private static String getAllEntitiesAsJson() {
        try {
            List<CurrentPlayers> list = currentplayerDao.findAllPlayers();
            return convertToJson(list);
        } catch (Exception e) {
            System.out.println("Error fetching players: " + e.getMessage());
            return new JSONArray().toString();
        }
    }

    private static String getByIdResponse(String idStr) {
        JSONObject res = new JSONObject();
        try {
            int id = Integer.parseInt(idStr);
            CurrentPlayers p = currentplayerDao.getCurrentPlayerByID(id);
            if (p == null) {
                res.put("success", false).put("message", "Not found");
            } else {
                res.put("success", true).put("message", "OK").put("data", playerToJson(p));
            }
        } catch (NumberFormatException nfe) {
            res.put("success", false).put("message", "Invalid id");
        } catch (Exception e) {
            res.put("success", false).put("message", e.getMessage());
        }
        return res.toString();
    }

    private static String deleteByIdResponse(String idStr) {
        JSONObject res = new JSONObject();
        try {
            int id = Integer.parseInt(idStr);
            boolean ok = currentplayerDao.DeleteCurrentPlayerByID(id);
            res.put("success", ok).put("message", ok ? "Deleted" : "No rows affected");
        } catch (NumberFormatException nfe) {
            res.put("success", false).put("message", "Invalid id");
        } catch (Exception e) {
            res.put("success", false).put("message", e.getMessage());
        }
        return res.toString();}


    private static String addEntityResponse(String holder) {
        JSONObject res = new JSONObject();
        try {
            if (holder == null || holder.isEmpty()) {
                res.put("success", false).put("message", "Missing JSON holder");
            } else {
                JSONObject obj = new JSONObject(holder);

                if (!obj.has("name") || !obj.has("height") || !obj.has("date_of_birth")) {
                    res.put("success", false).put("message", "Required: name, height, date_of_birth");
                } else {
                    String name = obj.getString("name").trim();
                    double height = obj.getDouble("height");
                    String dobStr = obj.getString("date_of_birth").trim();
                    Date dob = Date.valueOf(dobStr);

                    boolean ok = currentplayerDao.AddCurrentPlayer(name, height, dob);

                    res.put("success", ok)
                            .put("message", ok ? "Inserted" : "Insert failed")
                            .put("data", new JSONObject()
                                    .put("name", name)
                                    .put("height", height)
                                    .put("date_of_birth", dobStr));
                }
            }
        } catch (IllegalArgumentException badDate) {
            res.put("success", false).put("message", "date_of_birth must be YYYY-MM-DD");
        } catch (Exception e) {
            res.put("success", false).put("message", e.getMessage());
        }
        return res.toString();
    }

}
