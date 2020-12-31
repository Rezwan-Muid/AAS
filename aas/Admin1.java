/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aas;

import java.sql.*;
import javax.swing.JOptionPane;

/**
 *
 * @author sreej
 */
public class Admin1
{
    public static final int TOTAL_STUDENTS = 6;
    // adds student info to the database
    public void addStudent(Connection con, int st_id, String name) throws SQLException
    {
        try
        {
            String query = "insert into student values(?, ?, default)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, st_id);
            ps.setString(2, name);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null,"Student Added Successfully!","", JOptionPane.INFORMATION_MESSAGE);
            con.commit();
        } catch (SQLException se)
        {
            System.out.println("Exception in addStudent: " + se);
        }

    }

    // adds techer info to the database
    public void addTeacher(Connection con, int t_id, String name, int course_id) throws SQLException
    {
        try
        {
            String query = "insert into Teacher values(?, ?, ?, default)";
            PreparedStatement ps = con.prepareStatement(query);

            ps.setInt(1, t_id);
            ps.setString(2, name);
            ps.setInt(3, course_id);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(null,"Teacher Added Successfully!","", JOptionPane.INFORMATION_MESSAGE);
            con.commit();

        } catch (SQLException se)
        {
            System.out.println("Exception in addTeacher: " + se);
        }

    }
    
    // this method will show the personal info all students
    public void showAllStudentPesonalInfo(Connection con) throws SQLException
    {
        Studentpersonalinfo ob = new Studentpersonalinfo();
        try
        {
            String query = "Select st_id, name from student";
            PreparedStatement ps = con.prepareStatement(query);
            String sid;
            ResultSet rset = ps.executeQuery();
            while(rset.next())
            {
                sid=Integer.toString(rset.getInt(1));
                ob.set_Table(sid,rset.getString(2) );
                System.out.println("Student ID: " + rset.getInt(1) + " Name: " + rset.getString(2));
            }
            ob.show();
        } catch (SQLException se)
        {
            System.out.println("exception in showAllStudentPesonalInfo: " + se);
        }
    }
    
    // this method will show the personal info all teachers
    public void showAllTeacherPesonalInfo(Connection con) throws SQLException
    {
        Teacherpersonalinfo ob = new Teacherpersonalinfo();
        
        try
        {
            String query = "Select t_id, name, course_id from teacher";
            PreparedStatement ps = con.prepareStatement(query);
            String tid,cid;
            ResultSet rset = ps.executeQuery();
            while(rset.next())
            {
                tid= Integer.toString(rset.getInt(1));
                cid= Integer.toString(rset.getInt(3));
                ob.set_Table(tid,rset.getString(2),cid);
                
                System.out.println("Teacher ID: " + rset.getInt(1) + " Name: " + rset.getString(2) + " Course_id: " + rset.getInt(3));
            }
            ob.show();
        } catch (SQLException se)
        {
            System.out.println("exception in showAllTeacherPesonalInfo: " + se);
        }
    }
    
    // this method will show the attendance info a particular student from all courses
    public void showSingleStudentAllCourseAttendance(Connection con, int st_id)
    {
        try
        {
            String query = "select t.course_id, count(*) from attendance a, teacher t where a.course_id = t.course_id and st_id = ? and presence = 1 group by t.course_id";
            PreparedStatement ps_att_info = con.prepareStatement(query);

            ps_att_info.setInt(1, st_id);
            ResultSet rs_att = ps_att_info.executeQuery();
            
            System.out.println("Student name: " + find_student_name(con, st_id));
            while (rs_att.next())
            {
                // stores course_id
                int c_id = rs_att.getInt(1); 
                
                // find total classes of a particular class
                int total_class = find_total_class(con, c_id);
                System.out.println("Course_id: " + c_id + " Total Class: " + total_class + " Present: " + rs_att.getInt(2));
            }
        } catch (Exception e)
        {
        }
    }
    
    // this method will show the attendance info of a particular student in a particular course
    public  void showSingleStudentSingleCourseAttendance(Connection con, int st_id, int course_id) throws SQLException
    {
        try
        {
            String query = "Select count(*) from attendance where st_id=? and course_id=? and presence=1";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setInt(1, st_id);
            ps.setInt(2, course_id);

            ResultSet rs = ps.executeQuery();
            
            int t_id = find_teacher_id(con, course_id);
            System.out.println("Teacher id: " + t_id);
            System.out.println("Teacher Name: " + find_teacher_name(con, t_id));
            System.out.println("Course_id: " + course_id);
            System.out.println("Total Class: " + find_total_class(con, course_id));
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
            }
        } catch (SQLException se)
        {
            System.out.println("SQLException in showStudentAttendance: " + se);
        }
    }
    
    // this method will show attendance info of all students of a particular course
    public void showSingleCourseAllStudents(Connection con, int course_id) throws SQLException
    {
        try
        {

            String query = "select s.st_id, count(*) from attendance a, student s where a.st_id = s.st_id and course_id = ? and presence = 1 group by s.st_id";
            PreparedStatement ps_att_info = con.prepareStatement(query);

            ps_att_info.setInt(1, course_id);
            ResultSet rs_att = ps_att_info.executeQuery();

            int t_id = find_teacher_id(con, course_id);
            System.out.println("Teacher id: " + t_id);
            System.out.println("Name: " + find_teacher_name(con, t_id));
            System.out.println("Course_id: " + course_id);
            System.out.println("total_class: " + find_total_class(con, t_id));

            int present[] = new int[TOTAL_STUDENTS + 1];
            while (rs_att.next())
            {
                // store attendance info in the 'present' array
                present[rs_att.getInt(1)] = rs_att.getInt(2);
            }

            for (int i = 1; i <= TOTAL_STUDENTS; i++)
            {
                // show attendance info
                System.out.println("Student id: " + i + " Student Name: "  + find_student_name(con, i) + " Present: " + present[i]);
            }
        } catch (SQLException se)
        {
            System.out.println("SQLException in showCourseAttendance: " + se);
        }
    }
    
    
    private int find_total_class(Connection con, int c_id) throws SQLException
    {
        
        // count total number of classes of a particular course 
        PreparedStatement ps_total_class = con.prepareStatement("select count(distinct(att_date)) from attendance where course_id=?");

        ps_total_class.setInt(1, c_id);
        ResultSet rs_total = ps_total_class.executeQuery();
        rs_total.next();

        // return total_class
        return rs_total.getInt(1);
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
    
    private int find_teacher_id(Connection con, int c_id) throws SQLException
    {
        // find the name of student
        PreparedStatement ps = con.prepareStatement("select t_id from teacher where c_id=?");

        ps.setInt(1, c_id);
        ResultSet rs_total = ps.executeQuery();
        rs_total.next();

        //returns name of student
        return rs_total.getInt(1);
    }
    
    private String find_teacher_name(Connection con, int t_id) throws SQLException
    {
        // find the name of student
        PreparedStatement ps = con.prepareStatement("select name from teacher where t_id=?");

        ps.setInt(1, t_id);
        ResultSet rs_total = ps.executeQuery();
        rs_total.next();

        //returns name of student
        return rs_total.getString(1);
    }
}
