/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aas;

import java.net.*;
import java.io.*;
import java.util.Scanner;
import java.sql.*;

/**
 *
 * @author sreej
 */
public class NetworkHandler
{

    // operation will be 1 for GIVE_ATTENDANCE, 2 for ENROLL and 1569 for termination of connection from client side
    // if operation is 2 then course_id and period will be 0 
    @SuppressWarnings("CallToPrintStackTrace")
    public void listen(int port, int operation, int course_id, int period)
    {
        try
        {
            ServerSocket listener = new ServerSocket(port);
            Socket server;

            while (true)
            {
                System.out.println("Waiting for client....");
                server = listener.accept();
                if (server.isConnected())
                {
                    System.out.println("Client connected. Client IP: " + server.getInetAddress());
                }
                int control = handleConnection(server, operation, course_id, period);
                if (control == 0)
                {
                    return;
                }
            }
        } catch (IOException ioe)
        {
            System.out.println("IOException: " + ioe);
            ioe.printStackTrace();
        } catch (SecurityException se)
        {
            System.out.println("SecurityException: " + se);
            se.printStackTrace();
        }
    }

    protected int handleConnection(Socket server, int operation, int course_id, int period)
            throws IOException
    {
        Scanner in;

        String op = Integer.toString(operation);

        PrintWriter out = new PrintWriter(server.getOutputStream(), true);
        in = new Scanner(server.getInputStream());

        int resp, code = 0;
        String data_out;

        // first send the control code to initiate operation
        out.println(op);
        String response = in.nextLine();

        // the response will normally contain the id of the student to be stored in attendance database
        resp = Integer.parseInt(response);
        System.out.println("resp: " + resp);
        do
        {
            if (resp != 0)
            {
                db_access db = new db_access();
                try
                {
                    db.connect();
                    code = db.storeAttendance(resp, course_id, period); // will return 69 on success
                } catch (SQLException se)
                {
                    System.out.println("sqlexception in handleConnection: " + se);
                }
                data_out = Integer.toString(code);
                out.println(data_out);
                response = in.nextLine();

                // the response will normally contain the id of the student to be stored in attendance database
                // it might also contain control value 0 to close the connection
                resp = Integer.parseInt(response);
                System.out.println("resp: " + resp);
            }
            

        } while (resp != 0);

        // esp will return 1569 to indicate close of operation
        if (resp == 0)
        {
            System.out.println("Closing Server....");
            out.println(1569);
            server.close();
            return resp;
        }
        return 1;
    }
}
