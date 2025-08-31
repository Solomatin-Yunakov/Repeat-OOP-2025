package sd2.GUI;


import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.Socket;
import java.sql.Date;

import sd2.DTOs.CurrentPlayers;
import sd2.DAOs.Server;
public class GUIController {

        private static final String SERVER_ADDRESS = "localhost";
        private static final int SERVER_PORT = 8080;

        private static final String GET_ALL_ENTITIES_REQUEST = Server.GET_ALL_ENTITIES_REQUEST;
        private static final String GET_BY_ID = "GET_BY_ID";
        private static final String DELETE_BY_ID = Server.DELETE_BY_ID;
        private static final String CLIENT_EXIT = Server.CLIENT_EXIT;
        private static final String ADD_AN_ENTITY = Server.ADD_AN_ENTITY;


        @FXML private TextField idTextField;
        @FXML private TextField idTextField1;
        @FXML private Button displayByIdButton;
        @FXML private Button displayAllButton;
        @FXML private Button deleteByIdButton;

        @FXML private TextField nameTextField;
        @FXML private TextField heightTextField;
        @FXML private TextField dobTextField;
        @FXML private Button addEntityButton;

        @FXML private TableView<CurrentPlayers> playersTable;
        @FXML private TableColumn<CurrentPlayers, Integer> idColumn;
        @FXML private TableColumn<CurrentPlayers, String> nameColumn;
        @FXML private TableColumn<CurrentPlayers, Double> heightColumn;
        @FXML private TableColumn<CurrentPlayers, java.sql.Date> dobColumn;

        @FXML private Label statusLabel;

        public GUIController() { }

        @FXML
        public void initialize() {

            idColumn.setCellValueFactory(new PropertyValueFactory<>("playerid"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("playerName"));
            heightColumn.setCellValueFactory(new PropertyValueFactory<>("playerHeight"));
            dobColumn.setCellValueFactory(cell ->
                    new ReadOnlyObjectWrapper<>(cell.getValue().getdateofbirth())
            );

            displayAllButton.setOnAction(e -> handleDisplayAll());
            displayByIdButton.setOnAction(e -> handleSearchById());
            deleteByIdButton.setOnAction(e -> handleDeleteById());
            addEntityButton.setOnAction(e -> handleAddEntity());
        }

        private void handleDisplayAll() {
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println(GET_ALL_ENTITIES_REQUEST);
                String response = in.readLine();
                if (response == null) { showAlert("Server Error", "No response from server"); return; }

                JSONArray arr = new JSONArray(response);
                ObservableList<CurrentPlayers> data = FXCollections.observableArrayList();
                for (int i = 0; i < arr.length(); i++) {
                    data.add(parsePlayer(arr.getJSONObject(i)));
                }
                playersTable.setItems(data);
                updateStatus("Displayed all players (" + data.size() + ")");
            } catch (IOException ex) {
                showAlert("Server Error", "Error connecting to server: " + ex.getMessage());
            } catch (Exception ex) {
                showAlert("Parse Error", ex.getMessage());
            }
        }

        private void handleSearchById() {
            String idText = idTextField.getText().trim();
            if (idText.isEmpty()) { showAlert("Input Error", "Please enter a Player ID"); return; }

            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                out.println(GET_BY_ID + " " + idText);
                String response = in.readLine();
                if (response == null) { showAlert("Server Error", "No response from server"); return; }

                JSONObject obj = new JSONObject(response);
                if (!obj.optBoolean("success", false)) {
                    showAlert("Search Error", obj.optString("message", "Unknown error"));
                    return;
                }
                JSONObject data = obj.getJSONObject("data");
                ObservableList<CurrentPlayers> list = FXCollections.observableArrayList(parsePlayer(data));
                playersTable.setItems(list);
                updateStatus("Found player with ID: " + idText);
            } catch (IOException ex) {
                showAlert("Server Error", "Error connecting to server: " + ex.getMessage());
            } catch (Exception ex) {
                showAlert("Parse Error", ex.getMessage());
            }
        }
    private void handleDeleteById() {
        try {
            String idText = idTextField1.getText().trim();
            if (idText.isEmpty()) {
                showAlert("Input Error", "Please enter a Material ID to delete");
                return;
            }
            int id = Integer.parseInt(idText);

            try (
                    Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
            ) {
                out.println(DELETE_BY_ID + " " + id);
                String response = in.readLine();

                JSONObject jsonResponse = new JSONObject(response);
                boolean success = jsonResponse.getBoolean("success");
                String message = jsonResponse.getString("message");

                if (success) {
                    updateStatus("Successfully deleted material with ID: " + id);
                    handleDisplayAll();
                } else {
                    showAlert("Delete Error", message);
                }
            } catch (IOException e) {
                showAlert("Server Error", "Error connecting to server: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            showAlert("Input Error", "Please enter a valid numeric ID");
        }
    }
    private void handleAddEntity() {
        String name = nameTextField.getText().trim();
        String heightStr = heightTextField.getText().trim();
        String dobStr = dobTextField.getText().trim();

        if (name.isEmpty() || heightStr.isEmpty() || dobStr.isEmpty()) {
            showAlert("Input Error", "Please fill name, height, and date (YYYY-MM-DD).");
            return;
        }

        double height;
        try {
            height = Double.parseDouble(heightStr);
        } catch (NumberFormatException nfe) {
            showAlert("Input Error", "Height must be a number.");
            return;
        }

        JSONObject payload = new JSONObject()
                .put("name", name)
                .put("height", height)
                .put("date_of_birth", dobStr);

        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            out.println(ADD_AN_ENTITY + " " + payload.toString());
            String response = in.readLine();
            if (response == null) { showAlert("Server Error", "No response from server"); return; }

            JSONObject res = new JSONObject(response);
            if (res.optBoolean("success", false)) {
                updateStatus("Added player: " + name);
                handleDisplayAll();
            } else {
                showAlert("Add Error", res.optString("message", "Unknown error"));
            }
        } catch (Exception ex) {
            showAlert("Add Error", ex.getMessage());
        }
    }

        private CurrentPlayers parsePlayer(JSONObject json) {
            int id = json.getInt("id");
            String name = json.getString("name");
            double height = json.getDouble("height");
            Date dob = Date.valueOf(json.getString("date_of_birth"));
            return new CurrentPlayers(id, name, height, dob);
        }

        private void showAlert(String title, String message) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(title);
                alert.setHeaderText(null);
                alert.setContentText(message);
                alert.showAndWait();
            });
        }

        private void updateStatus(String message) {
            if (statusLabel != null) statusLabel.setText(message);
            System.out.println("Status: " + message);
        }
}
