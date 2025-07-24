package sd2.BusinessObjects;

/** OOP Feb 2022
 * This AppMain demonstrates the use of a Data Access Object (DAO)
 * to separate Business logic from Database specific logic.
 * It uses Data Access Objects (DAOs),
 * Data Transfer Objects (DTOs), and  a DAO Interface to define
 * a contract between Business Objects and DAOs.
 *
 * "Use a Data Access Object (DAO) to abstract and encapsulate all
 * access to the data source. The DAO manages the connection with
 * the data source to obtain and store data" Ref: oracle.com
 *
 * Here, we use one DAO per database table.
 *
 * Use the SQL script "CreateUsers.sql" included with this project
 * to create the required MySQL user_database and User table.
 */

import com.dkit.oop.sd2.DAOs.MySqlUserDao;
import com.dkit.oop.sd2.DAOs.UserDaoInterface;
import com.dkit.oop.sd2.DTOs.User;
import com.dkit.oop.sd2.Exceptions.DaoException;
import java.util.List;

public class AppMain
{
    public static void main(String[] args)
    {
        UserDaoInterface IUserDao = new MySqlUserDao();  //"IUserDao" -> "I" stands for for

//        // Notice that the userDao reference is an Interface type.
//        // This allows for the use of different concrete implementations.
//        // e.g. we could replace the MySqlUserDao with an OracleUserDao
//        // (accessing an Oracle Database)
//        // without changing anything in the Interface.
//        // If the Interface doesn't change, then none of the
//        // code in this file that uses the interface needs to change.
//        // The 'contract' defined by the interface will not be broken.
//        // This means that this code is 'independent' of the code
//        // used to access the database. (Reduced coupling).
//
//        // The Business Objects require that all User DAOs implement
//        // the interface called "UserDaoInterface", as the code uses
//        // only references of the interface type to access the DAO methods.

        try
        {
            System.out.println("\nCall findAllUsers()");
            List<User> users = IUserDao.findAllUsers();     // call a method in the DAO

            if( users.isEmpty() )
                System.out.println("There are no Users");
            else {
                for (User user : users)
                    System.out.println("User: " + user.toString());
            }

            // test dao - with username and password that we know are present in the database
            System.out.println("\nCall: findUserByUsernamePassword()");
            String username = "smithj";
            String password = "password";
            User user = IUserDao.findUserByUsernamePassword(username, password);

            if( user != null ) // null returned if userid and password not valid
                System.out.println("User found: " + user);
            else
                System.out.println("Username with that password not found");

            // test dao - with an invalid username (i.e. not in database)
            username = "madmax";
            password = "thunderdome";
            user = IUserDao.findUserByUsernamePassword(username, password);
            if(user != null)
                System.out.println("Username: " + username + " was found: " + user);
            else
                System.out.println("Username: " + username + ", password: " + password +" is not valid.");
        }
        catch( DaoException e )
        {
            e.printStackTrace();
        }
    }
}
