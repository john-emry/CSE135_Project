/**
 * Created by John on 4/29/2017.
 */

import java.sql.*;

import org.omg.CORBA.Request;
import org.postgresql.*;
import java.io.*;
import java.util.Enumeration;
import java.util.Timer;
import java.util.TimerTask;
import javax.servlet.*;
import javax.servlet.http.*;
import org.postgresql.*;

public class DB extends HttpServlet {
    private static Connection conn = null;
    private static Statement stmt = null;
    private static ResultSet rset = null;
    private String error = "";

    public void init() throws ServletException {
        try {
            Class.forName("org.postgresql.Driver");
            DriverManager.registerDriver(new org.postgresql.Driver());
            String dbURL = "jdbc:postgresql:CSE135?user=postgres&password=saving?bay";
            conn = DriverManager.getConnection(dbURL);
            System.out.println("Connected to CSE135");
        } catch (Exception e) {
            error = e.toString();
            e.printStackTrace();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        String query = "SELECT * FROM Accounts WHERE \"Username\"=?";
        PreparedStatement requestQuery;
        PrintWriter out = response.getWriter();
        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, request.getParameter("username"));
            rset = requestQuery.executeQuery();
            if (rset != null && rset.next()) {
                out.print("<h1>Found User: " + rset.getString("Username") + "</h1>");
            }
        } catch (Exception e) {
            out.print("<h1>Could not find user</h1>");
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        Enumeration<String> enumer = request.getParameterNames();
        while(enumer.hasMoreElements()) {
            if (enumer.nextElement().equals("username")) {
                login(request.getParameter("username"), out, response);
                return;
            }
        }
        signup(request.getParameter("name"),
                request.getParameter("role"),
                request.getParameter("age"),
                request.getParameter("state"),
                out);

    }

    private void signup(String user, String role, String age, String state, PrintWriter out) {
        String query = "SELECT * FROM Accounts WHERE \"Username\" = ?";
        PreparedStatement requestQuery;
        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, user);
            rset = requestQuery.executeQuery();
            if (rset != null && rset.next()) {
                out.print("<h1>Sign Up Failure!</h1>");
                return;
            }
        } catch (Exception e) {
        }
        query = "Insert into Accounts(\"Username\", \"Age\", \"State\", \"Role\")\n" +
                "Select ?, ?, ?, ?\n" +
                "WHERE Not Exists (Select * from public.Accounts where \"Username\" = ?);";
        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, user);
            requestQuery.setInt(2, Integer.valueOf(age));
            requestQuery.setString(3, state);
            requestQuery.setString(4, role);
            requestQuery.setString(5, user);
            requestQuery.executeUpdate();
            out.print("<h1>Sign Up Successful!");
        } catch (Exception e) {
            out.print("<h1>Sign Up Failure!</h1>");
            out.println(e.getLocalizedMessage());
        }
    }

    private void login(String user, PrintWriter out, HttpServletResponse response) {
        String query = "SELECT * FROM Accounts WHERE \"Username\"=?";
        PreparedStatement requestQuery;

        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, user);
            rset = requestQuery.executeQuery();
            if (rset != null && rset.next()) {
                response.sendRedirect("main.jsp");
                out.print("<h1>Success! Found User: " + rset.getString("Username") + "</h1>");
            }
        } catch (Exception e) {
            out.print("<h1>Failure, no user found with that name!");
        }
    }

    public void destroy() {

    }
}
