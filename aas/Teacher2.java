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


public class Teacher2
{

    int course_id;
    int total_class;
    int teacher_id;
    String name;

    public Teacher2(int t_id, int c_id, String _name)
    {
        teacher_id = t_id;
        course_id = c_id;
        name = _name;
        try
        {
            db_access db = new db_access();
            Connection con = db.connect();
            // count total number of classes of a particular course 
            PreparedStatement ps_total_class = con.prepareStatement("select count(distinct(att_date)) from attendance where course_id=?");

            ps_total_class.setInt(1, course_id);
            ResultSet rs_total = ps_total_class.executeQuery();
            rs_total.next();

            total_class = rs_total.getInt(1);
            db.close(con);
        } catch (SQLException se)
        {
            System.out.println("SQLException in TeacherConstructor: " + se);
        }

    }
    
    public Teacher2(int c_id)
    {
        course_id = c_id;
        try
        {
            db_access db = new db_access();
            Connection con = db.connect();
            
            String query = "select t_id, name from Teacher where course_id=?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, c_id);
            
            ResultSet rs = ps.executeQuery();
            rs.next();
            teacher_id = rs.getInt(1);
            name = rs.getString(2);
            
            query = "select count(distinct(att_date)) from attendance where course_id=?";
            ps = con.prepareStatement(query);
            ps.setInt(1, c_id);
            
            rs = ps.executeQuery();
            rs.next();
            total_class = rs.getInt(1);
            
            db.close(con); 
        } catch (SQLException e)
        {
        }
    }
    
    // this method will show attendance info of all the students in a particular course
    public void showCourseAtt(Connection con, int back) throws SQLException
    {

        try
        {

            String query = "select st_id from student";
            
            PreparedStatement ps_att_info = con.prepareStatement(query);
            ResultSet rs_att = ps_att_info.executeQuery();
            
            String t_class = Integer.toString(total_class);
            String c_id= Integer.toString(course_id);
            Allstudentinfo ob1,ob2;
            ob1= new Allstudentinfo(c_id,t_class, back);
            ob2= new Allstudentinfo();
            ob2.show();
            
            while (rs_att.next())
            {
                int st_id = rs_att.getInt(1);
                
                // procedure interface: check_present(c_id in number, s_id in number, present out number)
                CallableStatement pstmt = con.prepareCall("{call check_present(?, ?, ?)}");
                pstmt.setInt(1, course_id);
                pstmt.setInt(2, st_id);
                pstmt.registerOutParameter(3, Types.INTEGER);
                
                pstmt.executeUpdate();
                
                int present = pstmt.getInt(3); // here 3 means the 3rd parameter which is an out parameter
                
                String st_name = find_student_name(con, st_id);
                String s_id = Integer.toString(st_id);
                String pres = Integer.toString(present);
                
                int pre = present;               
                double percent = Math.round((pre / (double)total_class) * 100);
                String perc = Double.toString(percent) + "%";
                
                ob2.set_table(s_id, pres, st_name, perc);
                System.out.println("Student id: " + st_id + " Student Name: "  + st_name + " Present: " + present);
            }

            System.out.println("Teacher id: " + teacher_id);
            System.out.println("Name: " + name);
            System.out.println("Course_id: " + course_id);
            System.out.println("total_class: " + total_class);

            
        } catch (SQLException se)
        {
            System.out.println("SQLException in showCourseAttendance: " + se);
        }

    }

    // this method will show the attendance info of a particular student in the particular course
    public void showStudentAttendance(Connection con, int st_id, int back) throws SQLException
    {
        try
        {
            String query = "Select count(*) from attendance where st_id=? and course_id=? and presence=1";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, st_id);
            ps.setInt(2, course_id);

            ResultSet rs = ps.executeQuery();
            
            System.out.println("Teacher id: " + teacher_id);
            System.out.println("Teacher Name: " + name);
            System.out.println("Course_id: " + course_id);
            System.out.println("Total Class: " + total_class);
            System.out.println("Student Name: " + find_student_name(con, st_id));
            
            // if record doesn't exist
            if (!rs.isBeforeFirst())
            {
                System.out.println("Student with id " + st_id + " doesn't exist");
            } 
            else
            {
                rs.next();
                System.out.println("Student id: " + st_id + " Present: " + rs.getInt(1));
                
                String course= Integer.toString(course_id);
                String id= Integer.toString(st_id);
                
                int present = rs.getInt(1);               
                double percent = Math.round((present / (double)total_class) * 100);
                String perc = Double.toString(percent) + "%";
                
                ParticularStudentInfo ob = new ParticularStudentInfo(course,id,total_class, present, perc, back);
                ob.teacherid(teacher_id,name);
                ParticularStudentInfo ob1=new ParticularStudentInfo();
                ob1.show();
            }
        } catch (SQLException se)
        {
            System.out.println("SQLException in showStudentAttendance: " + se);
        }
    }

    private String find_student_name(Connection con, int st_id) throws SQLException
    {
        // find the name of student
        PreparedStatement ps = con.prepareStatement("select name from student where st_id=?");

        ps.setInt(1, st_id);
        ResultSet rs_total = ps.executeQuery();
        rs_total.next();

        //returns name of student
        return rs_total.getString(1);
    }
    
    public String get_teacher_name()
    {
        return name;
    }
    
    public int get_teacher_id()
    {
        return  teacher_id;
    }
}
