package sd2.BusinessObjects;

/**
 * OOP Feb 2022
 * This AppMain demonstrates the use of a Data Access Object (DAO)
 * to separate Business logic from Database specific logic.
 * It uses Data Access Objects (DAOs),
 * Data Transfer Objects (DTOs), and  a DAO Interface to define
 * a contract between Business Objects and DAOs.
 * <p>
 * "Use a Data Access Object (DAO) to abstract and encapsulate all
 * access to the data source. The DAO manages the connection with
 * the data source to obtain and store data" Ref: oracle.com
 * <p>
 * Here, we use one DAO per database table.
 * <p>
 * Use the SQL script "CreateUsers.sql" included with this project
 * to create the required MySQL user_database and User table.
 */

import java.util.List;
import java.sql.Date;

import sd2.DTOs.CurrentPlayers;
import sd2.Exceptions.DaoException;
import sd2.DAOs.CurrentPlayersDaoInterface;
import sd2.DAOs.MySqlCurrentPlayersDao;

import static java.lang.System.out;
import static sd2.DAOs.Server.GET_ALL_ENTITIES_REQUEST;

public class AppMain {
    public static void main(String[] args) {

        CurrentPlayersDaoInterface ICurrentPlayersDao = new MySqlCurrentPlayersDao();


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
        try {
            out.println(GET_ALL_ENTITIES_REQUEST);

            out.println("\nCall findAllPlayers()");
            List<CurrentPlayers> currentplayers = ICurrentPlayersDao.findAllPlayers();     // call a method in the DAO

            if (currentplayers.isEmpty())
                out.println("There are no Players");
            else {
                for (CurrentPlayers currentplayer : currentplayers)
                    out.println("Players: " + currentplayer.toString());
            }

            // test dao - with username and password that we know are present in the database
            out.println("\nCall: findUserByID()");
            String username = "smithj";
            String password = "password";
            int lookingid = 1;
            CurrentPlayers currentplayer2 = ICurrentPlayersDao.getCurrentPlayerByID(lookingid);

            if (currentplayer2 != null) // null returned if userid and password not valid
                out.println("User found: " + currentplayer2);
            else
                out.println("Username with that password not found");



            // test dao - with an invalid username (i.e. not in database)
            username = "madmax";
            password = "thunderdome";
            int idtodell = 2;
//            boolean currentplayer3 = ICurrentPlayersDao.DeleteCurrentPlayerByID(idtodell);
//            if(currentplayer3 != false)
//                System.out.println("Username: " + username + " was found: " + currentplayer3);
//            else
//                System.out.println("Username: " + username + ", password: " + password +" is not valid.");
            String Name = "John Smith";
            double height = 1.85;
            Date DOB = Date.valueOf("1990-01-01");
            boolean currentplayer4 = ICurrentPlayersDao.AddCurrentPlayer(Name, height, DOB);
//            if(currentplayer3 != false)
//                System.out.println("Username: " + username + " was found: " + currentplayer4);
//            else
//                System.out.println("Username: " + username + ", password: " + password +" is not valid.");
//
//
        } catch (DaoException e) {
            e.printStackTrace();
        }

    }
}
