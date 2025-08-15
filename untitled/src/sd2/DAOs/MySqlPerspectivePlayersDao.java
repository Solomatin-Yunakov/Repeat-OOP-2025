

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
import DTOs.PerspectivePlayers;
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
    public List<PerspectivePlayers> findAllPlayers() throws DaoException
    {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<PerspectivePlayers> perspectiveplayersList = new ArrayList<>();

        try
        {
            //Get connection object using the getConnection() method inherited
            // from the super class (MySqlDao.java)
            connection = this.getConnection();

            String query = "SELECT * FROM perspective_players";
            preparedStatement = connection.prepareStatement(query);

            //Using a PreparedStatement to execute SQL...
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                int userId = resultSet.getInt("PerspectivePlayerID");
                String username = resultSet.getString("Name");
                double amount = resultSet.getDouble("Height");
                Date dateIncurred = resultSet.getDate("DOB");
                PerspectivePlayers pp = new PerspectivePlayers(perspectiveplayerid,playerName,playerHeight,dateofbirth);
                perspectiveplayersList.add(pp);
            }
            double total = 0.0;
                        for (PerspectivePlayers pp : perspectiveplayersList) {
                            total = total + pp.getAmount();
                        }
                        System.out.println("Total number of perspective players: " + total);
        } catch (SQLException e)
        {
            throw new DaoException("findPerspectivePlayers() " + e.getMessage());
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
                throw new DaoException("findAllPerspectivePlayers() " + e.getMessage());
            }
        }
        return perspectiveplayersList;     // may be empty
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

