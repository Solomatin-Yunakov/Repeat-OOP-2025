

/** OOP Feb 2024
 *
 * Data Access Object (DAO) for User table with MySQL-specific code
 * This 'concrete' class implements the 'UserDaoInterface'.
 *
 * The DAO will contain the SQL query code to interact with the database,
 * so, the code here is specific to a MySql database.
 * No SQL queries will be used in the Business logic layer of code, thus, it
 * will be independent of the database specifics. Changes to code related to
 * the database are all contained withing the DAO code base.
 *
 *
 * The Business Logic layer is only permitted to access the database by calling
 * methods provided in the Data Access Layer - i.e. by calling the DAO methods.
 * In this way, the Business Logic layer is seperated from the database specific code
 * in the DAO layer.
 */


import Exceptions.DaoException;
import DTOs.CurrentPlayers;
import DTOs.PerspectivePlayers;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;
import DAOs.MySqlDao;
public class MySqlUserDao extends MySqlDao implements UserDaoInterface
{
     public Connection getConnection() throws DaoException {
            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://localhost:3306/ca_repeat";
            String username = "root";
            String password = "";
            Connection connection = null;

            try {
                Class.forName(driver);
                connection = DriverManager.getConnection(url, username, password);
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
     * @return List of User objects
     * @throws DaoException
     */
    @Override
    public List<CurrentPlayers> findAllPlayers() throws DaoException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<CurrentPlayers> currentplayersList = new ArrayList<>();

        try
        {
            //Get connection object using the getConnection() method inherited
            // from the super class (MySqlDao.java)
            connection = this.getConnection();

            String query = "SELECT * FROM current_players";
            preparedStatement = connection.prepareStatement(query);

            //Using a PreparedStatement to execute SQL...
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                int playerId = resultSet.getInt("CurrentPlayerID");
                String playername = resultSet.getString("Name");
                double height = resultSet.getDouble("Height");
                Date dateofbirth = resultSet.getDate("DOB");
                CurrentPlayers cp = new CurrentPlayers(playerId,playername,height,dateofbirth);
                currentplayersList.add(cp);
            }
            double total = 0.0;
                        for (CurrentPlayers cp : currentplayersList) {
                            total = total + cp.getAmount();
                        }
                        System.out.println("Total number of current players: " + total);
        } catch (SQLException e)
        {
            throw new DaoException("findAllCurrentPlayers() " + e.getMessage());
        } finally
        {
            try
            {
                if (resultSet != null)
                {
                    resultSet.close();
                }
                if (preparedStatement != null)
                {
                    preparedStatement.close();
                }
                if (connection != null)
                {
                    freeConnection(connection);
                }
            } catch (SQLException e)
            {
                throw new DaoException("findAllCurrentPlayers() " + e.getMessage());
            }
        }
        return currentplayersList;     // may be empty
    }
    public Earnings getCurrentPlayerByID(int dateofbirth) throws DaoException {
            Connection connection = null;
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            CurrentPlayers currentplayersbyid = null;
            try {
                connection = this.getConnection();
                String query = "SELECT * FROM current_players WHERE CURRENTPLAYERID = ? ";
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setDate(1, date);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                   int playerId = resultSet.getInt("CurrentPlayerID");
                   String playername = resultSet.getString("Name");
                   double height = resultSet.getDouble("Height");
                   Date dateofbirth = resultSet.getDate("DOB");

                    currentplayersbyid = new CurrentPlayers(playerId,playername,height,dateofbirth);
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

                    String query = "DELETE FROM current_players WHERE CurrentPlayerID = ?";
                    preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, id);

                    int rowsAffected = preparedStatement.executeUpdate();

                    return rowsAffected > 0; // Returns true if at least one row was deleted

                } catch (SQLException e) {
                    throw new DaoException("Error deleting CurrentPlayer with ID " + id + ": " + e.getMessage());
                }
        }
        public boolean AddCurrentPlayer(String Name,  double height, Date DOB) throws DaoException {
                Connection connection = null;
                PreparedStatement preparedStatement = null;
                ResultSet resultSet = null;

                try {
                    connection = this.getConnection();

                    // Get the highest EXPENSE_ID to generate a new ID
                    String idQuery = "SELECT MAX(CurrentPlayerID) FROM current_players";
                    PreparedStatement idStatement = connection.prepareStatement(idQuery);
                    ResultSet idResult = idStatement.executeQuery();
                    int getid = 1;
                    if (idResult.next()) {
                        getid = idResult.getInt(1) + 1;
                    }


                    // Corrected SQL insert query
                    String query2 = "INSERT INTO current_players (CurrentPlayerID, Name, height, DOB) VALUES (?, ?, ?, ?)";
                    preparedStatement = connection.prepareStatement(query2);
                    preparedStatement.setInt(1, getid);
                    preparedStatement.setString(2, Name);
                    preparedStatement.setDouble(3, height);
                    preparedStatement.setDate(4, DOB);

                    return preparedStatement.executeUpdate() > 0;

                } catch (SQLException e) {
                    throw new DaoException("Failed to add current_players: " + e.getMessage());
                }

            }


    /**
     * Given a username and password, find the corresponding User
     * @param user_name
     * @param password
     * @return User object if found, or null otherwise
     * @throws DaoException
     */
    @Override

}

