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
public class Student1
{

    int st_id;
    String name;

    public Student1(int id)
    {
        st_id = id;
        try
        {
            db_access db = new db_access();
            Connection con = db.connect();
            // find the name of student
            PreparedStatement ps = con.prepareStatement("select name from student where st_id=?");

            ps.setInt(1, st_id);
            ResultSet rs_total = ps.executeQuery();
            rs_total.next();

            name = rs_total.getString(1);
            db.close(con);
        } catch (SQLException se)
        {
            System.out.println("SQLException in showCourseAttendance: " + se);
        }
    }

    public void showSingleCourseAtt(Connection con, int c_id, int back) throws SQLException
    {
        try
        {

            int total_class = find_total_class(con, c_id);

            String query = "Select count(*) from attendance where st_id=? and course_id=? and presence=1";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, st_id);
            ps.setInt(2, c_id);

            ResultSet rs = ps.executeQuery();

            System.out.println("Student id: " + st_id);
            System.out.println("Name: " + name);
            System.out.println("Course_id: " + c_id);
            System.out.println("Total Class: " + total_class);

            // if record doesn't exist
            if (!rs.isBeforeFirst())
            {
                System.out.println("Student with id " + st_id + " doesn't exist");
            } else
            {
                rs.next();
                int present = rs.getInt(1);
                System.out.println("Present: " + present);

                Teacher2 t = new Teacher2(c_id);
                int t_id = t.get_teacher_id();
                String t_name = t.get_teacher_name();

                String course = Integer.toString(c_id);
                String id = Integer.toString(st_id);

                double percent = Math.round((present / (double)total_class) * 100);
                String perc = Double.toString(percent) + "%";
                System.out.println("percent: " + percent);
                
                ParticularStudentInfo ob = new ParticularStudentInfo(course, id, total_class, present, perc, back);
                ob.teacherid(t_id, t_name);

                ParticularStudentInfo ob1 = new ParticularStudentInfo();
                ob1.show();
            }
        } catch (SQLException se)
        {
            System.out.println("SQLException in showCourseAttendance: " + se);
        }

    }

    // this method will show the attendance info the student from all courses
    public void showAllCourseAtt(Connection con, int back) throws SQLException
    {
        try
        {
            String query = "select course_id, count(*) from attendance where course_id in (select course_id from teacher) and st_id=?  and presence=1 group by course_id";
            PreparedStatement ps_att_info = con.prepareStatement(query);

            ps_att_info.setInt(1, st_id);
            ResultSet rs_att = ps_att_info.executeQuery();

            Allcourseinfo ob2, ob1;
            String s_id = Integer.toString(st_id);
            ob1 = new Allcourseinfo(s_id, name, back);
            ob2 = new Allcourseinfo();
            ob2.show();
            System.out.println("Student name: " + name);
            while (rs_att.next())
            {
                // stores course_id
                int c_id = rs_att.getInt(1);

                // find total classes of a particular class
                int total_class = find_total_class(con, c_id);
                String t_class = Integer.toString(total_class);
                
                int present = rs_att.getInt(2);
                String pres = Integer.toString(present);
                double percent = Math.round((present / (double)total_class) * 100);
                String perc = Double.toString(percent) + "%";
                
                String cs_id = Integer.toString(c_id);

                ob2.set_table(cs_id, t_class, pres, perc);

                System.out.println("Course_id: " + c_id + " Total Class: " + total_class + " Present: " + rs_att.getInt(2));
            }

        } catch (SQLException se)
        {
            System.out.println("SQLException in showCourseAttendance: " + se);
        }
    }

    private int find_total_class(Connection con, int c_id) throws SQLException
    {

        // count total number of classes of a particular course 
        PreparedStatement ps_total_class = con.prepareStatement("select count(distinct to_char(att_date)) from attendance where course_id=?");

        ps_total_class.setInt(1, c_id);
        ResultSet rs_total = ps_total_class.executeQuery();
        rs_total.next();

        // return total_class
        return rs_total.getInt(1);
    }

}
