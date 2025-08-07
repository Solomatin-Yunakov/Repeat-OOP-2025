package sd2.DTOs;

/**                                                     OOP Feb 2022
 *  Data Transfer Object (DTO)
 *
 * This POJO (Plain Old Java Object) is called the Data Transfer Object (DTO).
 * (or, alternatively, the Model Object or the Value Object).
 * It is used to transfer data between the DAO and the Business Objects.
 * Here, it represents a row of data from the User database table.
 * The DAO creates and populates a User object (DTO) with data retrieved from
 * the resultSet and passes the User object to the Business Layer.
 *
 * Collections of DTOs( e.g. ArrayList<User> ) may also be passed
 * between the Data Access Layer (DAOs) and the Business Layer objects.
 */

public class PerspectivePlayers
{
    private int perspectiveplayerid;
    private String playerName;
    private double playerHeight;
    private Date DateOfBirth;

    public PerspectivePlayers(int perspectiveplayerid, String playerName, double playerHeight, Date DateOfBirth)
    {
        this.perspectiveplayerid = perspectiveplayerid;
        this.playerName = playerName;
        this.playerHeight = playerHeight;
        this.DateOfBirth = DateOfBirth;

    }

    public User(String playerName, double playerHeight, Date DateOfBirth)
    {
        this.perspectiveplayerid = 0;
        this.playerName = playerName;
        this.playerHeight = playerHeight;
        this.DateOfBirth = DateOfBirth;
    }

    public PerspectivePlayers()
    {

    }
    public int getPlayerid()
    {
        return perspectiveplayerid;
    }
    public void setPlayerid(int perspectiveplayerid)
    {
        this.perspectiveplayerid = persplayerid;
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
    public Date getDateOfBirth()
    {
        return DateOfBirth;
    }
    public void setDateOfBirth(Date DateOfBirth)
    {
        this.DateOfBirth = DateOfBirth;
    }



    @Override
    public String toString()
    {
       return "PerspectivePlayers{" +
               "perspectiveplayerid=" + perspectiveplayerid +
               ", playerName='" + playerName + '\'' +
               ", playerHeight=" + playerHeight +
               ", DateOfBirth=" + DateOfBirth +
               '}';

    }

}
