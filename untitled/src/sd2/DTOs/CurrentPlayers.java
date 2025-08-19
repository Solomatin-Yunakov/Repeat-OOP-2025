

/**                                                     OOP Feb 2022
 *  Data Transfer Object (DTO)
 *
 * This POJO (Plain Old Java Object) is called the Data Transfer Object (DTO).
 * (or, alternatively, the Model Object or the Value Object).
 * It is used to transfer data between the DAO and the Business Objects.
 * Here, it represents a row of data from the CurrentPlayers database table.
 * The DAO creates and populates a CurrentPlayers object (DTO) with data retrieved from
 * the resultSet and passes the CurrentPlayers object to the Business Layer.
 *
 * Collections of DTOs( e.g. ArrayList<CurrentPlayers> ) may also be passed
 * between the Data Access Layer (DAOs) and the Business Layer objects.
 */
package untitled.src.sd2.DTOs;
import untitled.src.sd2.DAOs.MySqlDao;
import untitled.src.sd2.DTOs.CurrentPlayers;
import untitled.src.sd2.Exceptions.DaoException;
import untitled.src.sd2.DAOs.CurrentPlayersDaoInterface;
import java.sql.Date;

public class CurrentPlayers
{
    private int currentplayerid;
    private String playerName;
    private double playerHeight;
    private Date dateofbirth;

    public CurrentPlayers(int currentplayerid, String playerName, double playerHeight, Date dateofbirth)
    {
        this.currentplayerid = currentplayerid;
        this.playerName = playerName;
        this.playerHeight = playerHeight;
        this.dateofbirth = dateofbirth;

    }

    public CurrentPlayers(String playerName, double playerHeight, Date dateofbirth)
    {
        this.currentplayerid = 0;
        this.playerName = playerName;
        this.playerHeight = playerHeight;
        this.dateofbirth = dateofbirth;
    }


    public int getPlayerid()
    {
        return currentplayerid;
    }
    public void setPlayerid(int currentplayerid)
    {
        this.currentplayerid = currentplayerid;
    }
    public String getPlayerName()
    {
        return playerName;
    }
    public void setPlayerName(String playerName)
    {
        this.playerName = playerName;
    }
    public double getPlayerHeight()
    {
        return playerHeight;
    }
    public void setPlayerHeight(double playerHeight)
    {
        this.playerHeight = playerHeight;
    }
    public Date getdateofbirth()
    {
        return dateofbirth;
    }
    public void setdateofbirth(Date dateofbirth)
    {
        this.dateofbirth = dateofbirth;
    }



    @Override
    public String toString()
    {
       return "CurrentPlayers{" +
               "currentplayerid=" + currentplayerid +
               ", playerName='" + playerName + '\'' +
               ", playerHeight=" + playerHeight +
               ", dateofbirth=" + dateofbirth +
               '}';

    }

}
