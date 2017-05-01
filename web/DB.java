/**
 * Created by John on 4/29/2017.
 */

import java.sql.*;

import java.io.*;
import java.util.Enumeration;
import javax.servlet.*;
import javax.servlet.http.*;

import oracle.jvm.hotspot.jfr.StackTrace;
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
            String dbURL = "jdbc:postgresql:CSE135?user=snapp&password=saving?bay";
            conn = DriverManager.getConnection(dbURL);
            System.out.println("Connected to CSE135");
        } catch (Exception e) {
            error = e.toString();
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        switch(request.getParameter("func")) {
            case "login":
                login(request.getParameter("username"), out, response, request);
                break;
            case "signup":
                signup(request.getParameter("name"),
                        request.getParameter("role"),
                        request.getParameter("age"),
                        request.getParameter("state"),
                        out);
                break;
            case "home":
                out.println("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/html\">\n" +
                        "<head>\n" +
                        "    <style>\n" +
                        "        .center {\n" +
                        "            text-align: center;\n" +
                        "            margin: auto;\n" +
                        "            padding: 10px;\n" +
                        "        }\n" +
                        "        .left {\n" +
                        "            text-align: left;\n" +
                        "            width: 200px;\n" +
                        "            padding: 0px;\n" +
                        "            top: 10px;\n" +
                        "            border: 3px solid #660000;\n" +
                        "        }\n" +
                        "        .frame {\n" +
                        "            position: absolute;\n" +
                        "            padding: 10px;\n" +
                        "            left: 210px;\n" +
                        "            right: 10px;\n" +
                        "            top: 10px;\n" +
                        "            bottom: 10px;\n" +
                        "            border: 3px solid #009900;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "    <link rel=\"stylesheet\" href=\"css/bootstrap.css\">\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Home</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"frame\">\n" +
                        "    <iframe id=\"iframe\" src=\"/DB?func=categories\" style=\"width:100%; height: 100%\"></iframe>\n" +
                        "</div>\n" +
                        "<div class=\"left\">\n" +
                        "    <script>\n" +
                        "        function setURL(url) {\n" +
                        "            document.getElementById('iframe').src = url;\n" +
                        "        }\n" +
                        "    </script>\n" +
                        "    <input type=\"button\" onclick=\"setURL('/DB?func=categories')\" value=\"Categories\"/><br/>\n" +
                        "    <input type=\"button\" onclick=\"setURL('/DB?func=products')\" value=\"Products\"/><br/>\n" +
                        "    <input type=\"button\" onclick=\"setURL('/DB?func=browse')\" value=\"Products Browsing\"/><br/>\n" +
                        "    <input type=\"button\" onclick=\"setURL('/DB?func=order_history')\" value=\"Product Order\"/><br/>\n" +
                        "    <input type=\"button\" onclick=\"setURL('/DB?func=cart')\" value=\"Buy Shopping Cart\"/><br/>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            case "categories":
                out.println("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/html\">\n" +
                        "<head>\n" +
                        "    <style>\n" +
                        "        .center {\n" +
                        "            text-align: center;\n" +
                        "            margin: auto;\n" +
                        "            padding: 10px;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "    <link rel=\"stylesheet\" href=\"css/bootstrap.css\">\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Categories</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"center\">\n" +
                        "   <h1>Categories<h1>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            case "browse":
                out.println("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/html\">\n" +
                        "<head>\n" +
                        "    <style>\n" +
                        "        .center {\n" +
                        "            text-align: center;\n" +
                        "            margin: auto;\n" +
                        "            padding: 10px;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "    <link rel=\"stylesheet\" href=\"css/bootstrap.css\">\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Browse</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"center\">\n" +
                        "   <h1>Browse<h1>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            case "products":
                out.println("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/html\">\n" +
                        "<head>\n" +
                        "    <style>\n" +
                        "        .center {\n" +
                        "            text-align: center;\n" +
                        "            margin: auto;\n" +
                        "            padding: 10px;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "    <link rel=\"stylesheet\" href=\"css/bootstrap.css\">\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Products</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"center\">\n" +
                        "   <h1>Products<h1>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            case "order_history":
                out.println("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/html\">\n" +
                        "<head>\n" +
                        "    <style>\n" +
                        "        .center {\n" +
                        "            text-align: center;\n" +
                        "            margin: auto;\n" +
                        "            padding: 10px;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "    <link rel=\"stylesheet\" href=\"css/bootstrap.css\">\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Order History</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"center\">\n" +
                        "   <h1>Order History<h1>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            case "cart":
                out.println("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/html\">\n" +
                        "<head>\n" +
                        "    <style>\n" +
                        "        .center {\n" +
                        "            text-align: center;\n" +
                        "            margin: auto;\n" +
                        "            padding: 10px;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "    <link rel=\"stylesheet\" href=\"css/bootstrap.css\">\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Shopping Cart</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"center\">\n" +
                        "   <h1>Shopping Cart<h1>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            default:
                break;
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        switch(request.getParameter("func")) {
            case "login":
                login(request.getParameter("username"), out, response, request);
                break;
            case "signup":
                signup(request.getParameter("name"),
                        request.getParameter("role"),
                        request.getParameter("age"),
                        request.getParameter("state"),
                        out);
                break;
            case "home":
                out.println("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/html\">\n" +
                        "<head>\n" +
                        "    <style>\n" +
                        "        .center {\n" +
                        "            text-align: center;\n" +
                        "            margin: auto;\n" +
                        "            padding: 10px;\n" +
                        "        }\n" +
                        "        .left {\n" +
                        "            text-align: left;\n" +
                        "            width: 200px;\n" +
                        "            padding: 0px;\n" +
                        "            top: 10px;\n" +
                        "            border: 3px solid #660000;\n" +
                        "        }\n" +
                        "        .frame {\n" +
                        "            position: absolute;\n" +
                        "            left: 200px;\n" +
                        "            right: 10px;\n" +
                        "            top: 10px;\n" +
                        "            bottom: 10px;\n" +
                        "            border: 3px solid #009900;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "    <link rel=\"stylesheet\" href=\"css/bootstrap.css\">\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Home</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"frame\">\n" +
                        "    <iframe id=\"iframe\" src=\"/DB?func=categories\" style=\"width:100%; height: 100%\"></iframe>\n" +
                        "</div>\n" +
                        "<div class=\"left\">\n" +
                        "    <script>\n" +
                        "        function setURL(url) {\n" +
                        "            document.getElementById('iframe').src = url;\n" +
                        "        }\n" +
                        "    </script>\n" +
                        "    <input type=\"button\" onclick=\"setURL('/DB?func=categories')\" value=\"Categories\"/><br/>\n" +
                        "    <input type=\"button\" onclick=\"setURL('/DB?func=products')\" value=\"Products\"/><br/>\n" +
                        "    <input type=\"button\" onclick=\"setURL('/DB?func=browse')\" value=\"Products Browsing\"/><br/>\n" +
                        "    <input type=\"button\" onclick=\"setURL('/DB?func=order_history')\" value=\"Product Order\"/><br/>\n" +
                        "    <input type=\"button\" onclick=\"setURL('/DB?func=cart')\" value=\"Buy Shopping Cart\"/><br/>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            case "categories":
                out.println("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/html\">\n" +
                        "<head>\n" +
                        "    <style>\n" +
                        "        .center {\n" +
                        "            text-align: center;\n" +
                        "            margin: auto;\n" +
                        "            padding: 10px;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "    <link rel=\"stylesheet\" href=\"css/bootstrap.css\">\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Categories</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"center\">\n" +
                        "   <h1>Categories<h1>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            case "products":
                out.println("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/html\">\n" +
                        "<head>\n" +
                        "    <style>\n" +
                        "        .center {\n" +
                        "            text-align: center;\n" +
                        "            margin: auto;\n" +
                        "            padding: 10px;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "    <link rel=\"stylesheet\" href=\"css/bootstrap.css\">\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Categories</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"center\">\n" +
                        "   <h1>Categories<h1>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            case "order_history":
                out.println("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/html\">\n" +
                        "<head>\n" +
                        "    <style>\n" +
                        "        .center {\n" +
                        "            text-align: center;\n" +
                        "            margin: auto;\n" +
                        "            padding: 10px;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "    <link rel=\"stylesheet\" href=\"css/bootstrap.css\">\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Categories</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"center\">\n" +
                        "   <h1>Categories<h1>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            case "cart":
                out.println("<html lang=\"en\" xmlns=\"http://www.w3.org/1999/html\">\n" +
                        "<head>\n" +
                        "    <style>\n" +
                        "        .center {\n" +
                        "            text-align: center;\n" +
                        "            margin: auto;\n" +
                        "            padding: 10px;\n" +
                        "        }\n" +
                        "    </style>\n" +
                        "    <link rel=\"stylesheet\" href=\"css/bootstrap.css\">\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Categories</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"center\">\n" +
                        "   <h1>Categories<h1>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            default:
                break;
        }
    }

    private void signup(String user, String role, String age, String state, PrintWriter out) {
        try {
            Class.forName("org.postgresql.Driver");
            DriverManager.registerDriver(new org.postgresql.Driver());
            String dbURL = "jdbc:postgresql:CSE135?user=snapp&password=saving?bay";
            conn = DriverManager.getConnection(dbURL);
            System.out.println("Connected to CSE135");
        } catch (Exception e) {
            error = e.toString();
            printStackTrace(e, out);
        }
        String query = "SELECT * FROM Accounts WHERE \"Username\" = ?";
        PreparedStatement requestQuery;
        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, user);
            rset = requestQuery.executeQuery();
            if (rset != null && rset.next()) {
                out.print("<h1 style=\"color: blue\">Sign Up Failure!</h1>");
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
            out.print("<h1>Sign Up Successful!</h1>");
        } catch (Exception e) {
            out.print("<h1>Sign Up Failure!</h1>");
            printStackTrace(e, out);
        }
    }

    private void printStackTrace(Exception e, PrintWriter out) {
        out.println(e.toString());
        StackTraceElement[] st = e.getStackTrace();
        for (StackTraceElement s : st) {
            out.println(s.toString());
        }
    }

    private void login(String user, PrintWriter out, HttpServletResponse response, HttpServletRequest request) {
        String query = "SELECT * FROM Accounts WHERE \"Username\"=?";
        PreparedStatement requestQuery;

        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, user);
            rset = requestQuery.executeQuery();
            if (rset != null && rset.next()) {
                request.getRequestDispatcher("/DB?func=home").forward(request, response);
                out.print("<h1>Success! Found User: " + rset.getString("Username") + "</h1>");
            }
        } catch (Exception e) {
            StackTraceElement[] st = e.getStackTrace();
            for (StackTraceElement s : st) {
                out.println(s.toString());
            }
            out.print("<h1>Failure, no user found with that name!</h1>");
        }
    }

    public void destroy() {

    }
}
