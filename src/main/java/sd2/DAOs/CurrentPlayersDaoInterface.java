package sd2.DAOs;

/** OOP Feb 2022
 * UserDaoInterface
 *
 * Declares the methods that all UserDAO types must implement,
 * be they MySql User DAOs or Oracle User DAOs etc...
 *
 * Classes from the Business Layer (users of this DAO interface)
 * should use reference variables of this interface type to avoid
 * dependencies on the underlying concrete classes (e.g. MySqlUserDao).
 *
 * More sophisticated implementations will use a factory
 * method to instantiate the appropriate DAO concrete classes
 * by reading database configuration information from a
 * configuration file (that can be changed without altering source code)
 *
 * Interfaces are also useful when testing, as concrete classes
 * can be replaced by mock DAO objects.
 */



import java.util.List;
import java.sql.Date;
import sd2.DTOs.CurrentPlayers;
import sd2.Exceptions.DaoException;

public interface CurrentPlayersDaoInterface
{
    List<CurrentPlayers> findAllPlayers() throws DaoException;
    CurrentPlayers getCurrentPlayerByID(int id) throws DaoException;
    boolean DeleteCurrentPlayerByID(int id) throws DaoException;
    boolean AddCurrentPlayer(String Name,  double height, Date DOB) throws DaoException;



}

