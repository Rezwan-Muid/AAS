/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aas;

import java.sql.*;

/**
 *
 * @author sreej
 */
public class db_access
{

    Connection con;

    public Connection connect() throws SQLException
    {

        String host = "jdbc:oracle:thin:@localhost:1521:XE";
        String u_name = "aat";
        String passwd = "1569";

        // establish connection
        con = DriverManager.getConnection(host, u_name, passwd);
        return con;
    }

    public boolean verifyUser(String type, int id, String passwd) throws SQLException
    {

        PreparedStatement ps = null;

        // make the program ready to execute statement
        if (type.equalsIgnoreCase("teacher"))
        {
            ps = con.prepareStatement("select passwd from Teacher where t_id = ?");
            ps.setInt(1, id);
        } else if (type.equalsIgnoreCase("student"))
        {
            ps = con.prepareStatement("select passwd from student where st_id = ?");
            ps.setInt(1, id);
        }

        ResultSet rs = null;
        if (ps != null)
        {
            rs = ps.executeQuery();

        }

        if (rs != null)
        {
            // if no record is returned
            if (!rs.isBeforeFirst())
            {
                System.out.println(type + " ID doesn't exist");
                return false;
            } else
            {
                // move the cursor to returned record
                rs.next();

                // check to see if passwd matches
                if (passwd.equals(rs.getString(1)))
                {
                    return true;
                }
            }
        }

        // if passwd doesn't match then return false 
        return false;

    }

    public boolean verifyAdmin(String name, String passwd)
    {
        if (name.equalsIgnoreCase("GENESIS") && passwd.equals("1569"))
        {
            return true;
        }
        return false;

    }

    public int storeAttendance(int st_id, int course_id, int period) throws SQLException
    {
        String query = "insert into attendance values(SEQ_ID.NEXTVAL, default, 1, ?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(query);

        ps.setInt(1, period);
        ps.setInt(2, st_id);
        ps.setInt(3, course_id);

        ps.executeUpdate();
        con.commit();

        // returns 69 on success
        return 69;
    }

    public int find_course_id(int t_id) throws SQLException
    {
        // find the name of teacher
        PreparedStatement ps = con.prepareStatement("select course_id from teacher where t_id=?");

        ps.setInt(1, t_id);
        ResultSet rs_name = ps.executeQuery();
        rs_name.next();

        return rs_name.getInt(1);
    }

    public void close(Connection con) throws SQLException
    {
        con.close();
    }
}
