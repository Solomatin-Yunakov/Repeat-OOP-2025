

/**
 * OOP Feb 2024
 * <p>
 * Data Access Object (DAO) for User table with MySQL-specific code
 * This 'concrete' class implements the 'UserDaoInterface'.
 * <p>
 * The DAO will contain the SQL query code to interact with the database,
 * so, the code here is specific to a MySql database.
 * No SQL queries will be used in the Business logic layer of code, thus, it
 * will be independent of the database specifics. Changes to code related to
 * the database are all contained withing the DAO code base.
 * <p>
 * <p>
 * The Business Logic layer is only permitted to access the database by calling
 * methods provided in the Data Access Layer - i.e. by calling the DAO methods.
 * In this way, the Business Logic layer is seperated from the database specific code
 * in the DAO layer.
 */


package untitled.src.sd2.DAOs;

import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.sql.Date;
import java.sql.DriverManager;


import untitled.src.sd2.DTOs.CurrentPlayers;
import untitled.src.sd2.Exceptions.DaoException;

import java.util.HashSet;

public class MySqlCurrentPlayersDao extends MySqlDao implements CurrentPlayersDaoInterface {
    HashSet<Integer> playeridcache = new HashSet<Integer>();
    boolean isplayeridcacheloaded = false;
    private void loadcache(Connection connection) throws DaoException {

        if (isplayeridcacheloaded) {
            return;
        }

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {

            String query = "SELECT CurrentPlayerID FROM current_players";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int playerId = resultSet.getInt("CurrentPlayerID");
                playeridcache.add(playerId);
            }
            isplayeridcacheloaded = true;

        } catch (SQLException e) {
            throw new DaoException("loadCache() " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    freeConnection(connection);
                }
            } catch (SQLException e) {
                throw new DaoException("loadCache() " + e.getMessage());
            }
        }
    }
    public Connection getConnection() throws DaoException {
        String driver = "com.mysql.cj.jdbc.Driver";
        String url = "jdbc:mysql://localhost:3306/ca_repeat";
        String username = "root";
        String password = "";
        Connection connection = null;

        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, username, password);
            this.loadcache(connection); // Load the cache after establishing the connection
        } catch (ClassNotFoundException e) {
            System.out.println("Failed to find driver class " + e.getMessage());
            System.exit(1);
        } catch (SQLException e) {
            System.out.println("Connection failed " + e.getMessage());
            System.exit(2);
        }
        return connection;
    }

    /**
     * Will access and return a List of all users in User database table
     *
     * @return List of User objects
     * @throws DaoException
     */


    @Override
    public List<CurrentPlayers> findAllPlayers() throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<CurrentPlayers> currentplayersList = new ArrayList<>();

        try {
            //Get connection object using the getConnection() method inherited
            // from the super class (MySqlDao.java)
            connection = this.getConnection();

            String query = "SELECT * FROM current_players";
            preparedStatement = connection.prepareStatement(query);

            //Using a PreparedStatement to execute SQL...
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int playerId = resultSet.getInt("CurrentPlayerID");
                String playername = resultSet.getString("Name");
                double height = resultSet.getDouble("Height");
                Date dateofbirth = resultSet.getDate("DOB");
                CurrentPlayers cp = new CurrentPlayers(playerId, playername, height, dateofbirth);
                currentplayersList.add(cp);
            }


        } catch (SQLException e) {
            throw new DaoException("findAllCurrentPlayers() " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    freeConnection(connection);
                }
            } catch (SQLException e) {
                throw new DaoException("findAllCurrentPlayers() " + e.getMessage());
            }
        }
        return currentplayersList;     // may be empty
    }

    private boolean isPlayerIdExists(int playerId) {
        return playeridcache.contains(playerId);
    }


    public CurrentPlayers getCurrentPlayerByID(int id) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        CurrentPlayers currentplayersbyid = null;
        try {
            connection = this.getConnection();

            if (isPlayerIdExists(id)) {

                String query = "SELECT * FROM current_players WHERE CURRENTPLAYERID = ? ";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setInt(1, id);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    int playerId = resultSet.getInt("CurrentPlayerID");
                    String playername = resultSet.getString("Name");
                    double height = resultSet.getDouble("Height");
                    Date dateofbirth = resultSet.getDate("DOB");

                    currentplayersbyid = new CurrentPlayers(playerId, playername, height, dateofbirth);
                }
            } else {
                throw new DaoException("Player ID does not exist in the database.");
            }
        } catch (SQLException e) {
            throw new DaoException("getCurrentPlayerByID() " + e.getMessage());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    freeConnection(connection);
                }
            } catch (SQLException e) {
                throw new DaoException("getCurrentPlayerByID() " + e.getMessage());
            }
        }
        return currentplayersbyid;     // reference to User object, or null value
    }

    public boolean DeleteCurrentPlayerByID(int id) throws DaoException {

        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = this.getConnection();  // Initialize connection

            if (!isPlayerIdExists(id)){
                throw new DaoException("Player ID does not exist in the database.");
            }
            String query = "DELETE FROM current_players WHERE CurrentPlayerID = ?";
            playeridcache.remove(id); // Remove the ID from the cache
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);

            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected > 0; // Returns true if at least one row was deleted

        } catch (SQLException e) {
            throw new DaoException("Error deleting CurrentPlayer with ID " + id + ": " + e.getMessage());
        }
    }

    public boolean AddCurrentPlayer(String Name, double height, Date DOB) throws DaoException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = this.getConnection();
            // Corrected SQL insert query
            String query2 = "INSERT INTO current_players ( Name, height, DOB) VALUES ( ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query2, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, Name);
            preparedStatement.setDouble(2, height);
            preparedStatement.setDate(3, DOB);

            int result = preparedStatement.executeUpdate();
            int idforplayer = 0;
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                idforplayer = rs.getInt(1);
            }
            playeridcache.add(idforplayer);
            return result > 0;

        } catch (SQLException e) {
            throw new DaoException("Error a CurrentPlayer with ID " + ": " + e.getMessage());
        }
    }

    public List<CurrentPlayers> findMaterialByFilter(Comparator<CurrentPlayers> comparator) throws DaoException {
        List<CurrentPlayers> currentPlayers = new ArrayList<>();
        String query = "SELECT * FROM current_players"; // Fixed from PLAYERS

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Add material to list
                CurrentPlayers currentplayer = new CurrentPlayers(
                        rs.getInt("CurrentPlayerID"),
                        rs.getString("Name"),
                        rs.getDouble("Height"),
                        rs.getDate("DOB")
                        );
                currentPlayers.add(currentplayer);
            }
            currentPlayers.sort(comparator); // Sort list
        } catch (SQLException e) {
            throw new DaoException("Error finding currentPlayers"); // Fixed message
        }
        return currentPlayers;
    }


    /**
     * Given a username and password, find the corresponding User
     * @param user_name
     * @param password
     * @return User object if found, or null otherwise
     * @throws DaoException
     */


}

