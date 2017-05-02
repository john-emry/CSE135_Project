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

public class Servlet extends HttpServlet {
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

    private String categoryDropDown() {
        String query = "Select * from Categories";
        PreparedStatement requestQuery;
        StringBuilder sb = new StringBuilder();

        try {
            requestQuery = conn.prepareStatement(query);
            rset = requestQuery.executeQuery();
            while (rset.next()) {
                sb.append("<option value=\"" + rset.getString("CategoryID") + "\">" + rset.getString("Name") + "</option>\n");
            }
            return sb.toString();
        } catch (Exception e) {
            return sb.toString();
        }
    }

    private String selectCategoryButtons(String notChoose) {
        PreparedStatement requestQuery;
        StringBuilder sb = new StringBuilder();
        if (notChoose.equals("")) {
            String query = "Select * from Categories";
            try {
                requestQuery = conn.prepareStatement(query);
                rset = requestQuery.executeQuery();
            } catch (Exception e) {

            }
        } else {
            String query = "Select * from Categories where not \"CategoryID\"=?";
            try {
                requestQuery = conn.prepareStatement(query);
                requestQuery.setInt(1, Integer.valueOf(notChoose));
                rset = requestQuery.executeQuery();
            } catch (Exception e) {

            }
        }


        try {


            while (rset.next()) {
                sb.append("<form action=\"/Servlet?func=Products\" method=\"post\">\n");
                sb.append("<input type=\"text\" name=\"productCatID\" value=\"" + rset.getInt("CategoryID") + "\" style=\"display: none\"/>");
                sb.append("<input type=\"text\" name=\"productFunc\" value=\"Category\" style=\"display: none\"/>");
                sb.append("<input type=\"submit\" name=\"Name\" value=\"" + String.valueOf(rset.getString("Name")) + "\"/>");
                sb.append("</form>");
            }
            if (!notChoose.equals("-1")) {
                sb.append("<form action=\"/Servlet?func=Products\" method=\"post\">\n");
                sb.append("<input type=\"text\" name=\"productCatID\" value=\"-1\" style=\"display: none\"/>");
                sb.append("<input type=\"text\" name=\"productFunc\" value=\"Category\" style=\"display: none\"/>");
                sb.append("<input type=\"submit\" name=\"Name\" value=\"All Products\"/>");
                sb.append("</form>");
            }
            return sb.toString();
        } catch (Exception e) {
            return sb.toString();
        }
    }

    private String productSelect(int catID, int accountID, PrintWriter out) {
        StringBuilder sb = new StringBuilder();
        try {
            String query = "";
            PreparedStatement requestQuery;
            if (catID != -1) {
                query = "Select * from Products where \"AccountID\" = ? AND \"CategoryID\" = ?";
                requestQuery = conn.prepareStatement(query);
                requestQuery.setInt(1, accountID);
                requestQuery.setInt(2, catID);
            } else {
               query = "Select * from Products where \"AccountID\" = ?";
                requestQuery = conn.prepareStatement(query);
                requestQuery.setInt(1, accountID);
            }

            rset = requestQuery.executeQuery();
            while (rset.next()) {
                sb.append("<form action=\"/Servlet?func=Products\" method=\"post\">\n");
                sb.append("<input type=\"text\" name=\"prodID\" value=\"" + String.valueOf(rset.getInt("ProductID")) + "\" style=\"display: none\"/>");
                sb.append("Name: <input type=\"text\" name=\"Name\" value=\"" + rset.getString("Name") + "\" style=\"text-align: center\"/>");
                sb.append("SKU: <input type=\"text\" name=\"SKU\" value=\"" + rset.getString("SKU") + "\" style=\"text-align: center\"/>");
                sb.append("Price: <input type=\"text\" name=\"Price\" value=\"" + rset.getString("Price") + "\" style=\"text-align: center\"/>");
                sb.append("<input type=\"submit\" name=\"productFunc\" value=\"Delete\"/>");
                sb.append("<input type=\"submit\" name=\"productFunc\" value=\"Update\"/><br/>");
                sb.append("</form>");
            }
            return sb.toString();
        } catch (Exception e) {
            //////PrintStackTrace(e, out);
            return sb.toString();
        }
    }

    private Boolean productDelete(int prodID, PrintWriter out) {
        try {
            String query = "Delete from public.Products where \"ProductID\" = ?\n";
            PreparedStatement requestQuery;
            try {
                requestQuery = conn.prepareStatement(query);
                requestQuery.setInt(1, prodID);
                requestQuery.executeUpdate();
                return true;
            } catch (Exception e) {
                return false;
            }

        } catch (Exception e) {
            //PrintStackTrace(e, out);
            return false;
        }
    }

    private Boolean productUpdate(int prodID, String name, String SKU, String price) {
        try {
            if (name.equals("") || SKU.equals("") || price.equals("") || Integer.valueOf(price) < 0)  {
                return false;
            }
        } catch (Exception e) {
            ////PrintStackTrace(e, out);
            return false;
        }
        String query = "UPDATE public.Products\n" +
                "SET \"Name\"= ?, \"SKU\"=?, \"Price\"=?\n" +
                "WHERE \"ProductID\"=?;";
        PreparedStatement requestQuery;
        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, name);
            requestQuery.setString(2, SKU);
            requestQuery.setString(3, price);
            requestQuery.setInt(4, prodID);
            requestQuery.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String categorySelect(String ID) {
        String query = "Select * from Categories where \"AccountID\" = ?";
        PreparedStatement requestQuery;
        StringBuilder sb = new StringBuilder();

        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setInt(1, Integer.valueOf(ID));
            rset = requestQuery.executeQuery();
            while (rset.next()) {
                sb.append("<form action=\"/Servlet?func=Categories\" method=\"post\">\n");
                sb.append("<input type=\"text\" name=\"catID\" value=\"" + String.valueOf(rset.getInt("CategoryID")) + "\" style=\"display: none\"/>");
                sb.append("<input type=\"text\" name=\"Name\" value=\"" + rset.getString("Name") + "\" style=\"text-align: center\"/>");
                sb.append("<textarea name=\"Description\" rows=\"4\" columns=\"50\">");
                sb.append(rset.getString("Description"));
                sb.append("</textarea>");
                sb.append("<input type=\"submit\" name=\"categoryFunc\" value=\"Delete\"/>");
                sb.append("<input type=\"submit\" name=\"categoryFunc\" value=\"Update\"/><br/>");
                sb.append("</form>");
            }
            sb.append("<form action=\"/Servlet?func=Categories\" method=\"post\">\n");
            sb.append("<input type=\"text\" name=\"Name\" style=\"text-align: center\"/>");
            sb.append("<textarea name=\"Description\" rows=\"4\" columns=\"50\">");
            sb.append("</textarea>");
            sb.append("<input type=\"submit\" name=\"categoryFunc\" value=\"Insert\"/>");
            sb.append("</form>");
            return sb.toString();
        } catch (Exception e) {
            sb.append("<form action=\"/Servlet?func=Categories\" method=\"post\">\n");
            sb.append("<input type=\"text\" name=\"Name\" style=\"text-align: center\"/>");
            sb.append("<textarea name=\"Description\" rows=\"4\" columns=\"50\">");
            sb.append("</textarea>");
            sb.append("<input type=\"submit\" name=\"categoryFunc\" value=\"Insert\"/>");
            sb.append("</form>");
            return sb.toString();
        }
    }

    private Boolean insertCategory(String name, String description, int AccountID, PrintWriter out) {
        if (name.equals("")) {
            return false;
        }
        String query = "SELECT * FROM public.Categories WHERE \"Name\" = ?";
        PreparedStatement requestQuery;
        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, name);
            rset = requestQuery.executeQuery();
            if (rset != null && rset.next()) {
                return false;
            }
        } catch (Exception e) {
        }
        query = "INSERT INTO public.Categories(\"Name\", \"Description\", \"AccountID\")\n" +
                "SELECT ?, ?, ?\n" +
                "WHERE NOT EXISTS (SELECT * from public.Categories where \"Name\" = ?);";
        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, name);
            requestQuery.setString(2, description);
            requestQuery.setInt(3, AccountID);
            requestQuery.setString(4, name);
            requestQuery.executeUpdate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Boolean insertProduct(String name, String SKU, String price, int AccountID, Integer catID, PrintWriter out) {
        try {
            if (name.equals("") || SKU.equals("") || price.equals("") || Integer.valueOf(price) < 0 || catID.equals(null))  {
                return false;
            }
        } catch (Exception e) {
            ////PrintStackTrace(e, out);
            return false;
        }
        String query = "Select * from public.products where \"SKU\" = ?;";

        PreparedStatement requestQuery;

        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, SKU);
            rset = requestQuery.executeQuery();
            if (rset != null && rset.next()) {
                return false;
            }
        } catch (Exception e) {
        }
        query = "INSERT INTO public.products(\"SKU\", \"CategoryID\", \"Price\", \"Name\", \"AccountID\")\n" +
                "Select ?, ?, ?, ?, ?\n" +
                "WHERE \n" +
                "Not Exists (Select * from public.products where \"SKU\" = ?)\n;";
        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, SKU);
            requestQuery.setInt(2, catID);
            requestQuery.setString(3, price);
            requestQuery.setString(4, name);
            requestQuery.setInt(5, AccountID);
            requestQuery.setString(6, SKU);
            requestQuery.executeUpdate();
            return true;
        } catch (Exception e) {
            ////PrintStackTrace(e, out);
            return false;
        }

    }

    private Boolean deleteCategory(int catID, int AccountID, PrintWriter out) {
        try {
            String query = "SELECT * FROM public.Products WHERE \"CategoryID\" = ?";
            PreparedStatement requestQuery;
            requestQuery = conn.prepareStatement(query);
            requestQuery.setInt(1, catID);
            rset = requestQuery.executeQuery();
            if (rset != null && rset.next()) {
                return false;
            }
            query = "Delete from public.Categories where \"CategoryID\" = ?\n" +
                    "AND\n" +
                    "NOT EXISTS (Select * from public.Products where \"CategoryID\" = ?)";
            try {
                requestQuery = conn.prepareStatement(query);
                requestQuery.setInt(1, catID);
                requestQuery.setInt(2, catID);
                requestQuery.executeUpdate();
                return true;
            } catch (Exception e) {
                ////PrintStackTrace(e, out);
                return false;
            }

        } catch (Exception e) {
            ////PrintStackTrace(e, out);
            return false;
        }

    }

    private Boolean updateCategory(int catID, String name, String description, int AccountID, PrintWriter out) {
        if (name.equals("")) {
            return false;
        }
        String query = "SELECT * FROM public.Categories WHERE \"Name\" = ?";
        PreparedStatement requestQuery;
        try {

            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, name);
            rset = requestQuery.executeQuery();
            if (rset != null && rset.next()) {
                return false;
            }
        } catch (Exception e) {

        }
        query = "UPDATE public.categories\n" +
                "SET \"Name\"= ?, \"Description\"= ?\n" +
                "WHERE \"CategoryID\" = ?;";
        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, name);
            requestQuery.setString(2, description);
            requestQuery.setInt(3, catID);
            requestQuery.executeUpdate();
            return true;
        } catch (Exception e) {
            ////PrintStackTrace(e, out);
            return false;
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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
                        "<h1>Welcome " + request.getSession().getAttribute("Username") + "</h1>\n" +
                        "</div>\n" +
                        "<div class=\"left\">\n" +
                        "<form action=\"/Servlet\" method=\"post\">\n");
                if (request.getSession().getAttribute("Role").equals("Owner")) {
                    out.println("    <input type=\"submit\" name=\"func\" value=\"Categories\"/><br/>\n" +
                            "    <input type=\"submit\" name=\"func\" value=\"Products\"/><br/>\n");
                } else {
                    out.println("    <input type=\"submit\" name=\"func\" value=\"Checkout\"/><br/>\n");
                }
                out.println(       "    <input type=\"submit\" name=\"func\" value=\"Products Browsing\"/><br/>\n" +
                        "</form>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            case "Categories":

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
                        "            text-align: center;\n" +
                        "            margin: auto;\n" +
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
                        "    <title>Categories</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"frame\">\n");
                try {
                    switch (request.getParameter("categoryFunc")) {
                        case "Delete":
                            if (!deleteCategory(Integer.valueOf(request.getParameter("catID")), (int) request.getSession().getAttribute("AccountID"), out)) {
                                out.println("<p style=\"color:red;text-align:center\">data modification failure</p>");
                            }
                            break;
                        case "Insert":
                            if(!insertCategory(request.getParameter("Name"), request.getParameter("Description"), (int) request.getSession().getAttribute("AccountID"), out)) {
                                out.println("<p style=\"color:red;text-align:center\">data modification failure</p>");
                            }
                            break;
                        case "Update":
                            if (!updateCategory(Integer.valueOf(request.getParameter("catID")), request.getParameter("Name"), request.getParameter("Description"), (int) request.getSession().getAttribute("AccountID"), out)) {
                                out.println("<p style=\"color:red;text-align:center\">data modification failure</p>");
                            }
                            break;
                        default:
                            break;
                    }
                } catch (Exception e) {
                }
                if (request.getSession().getAttribute("Role").equals("Owner")) {
                    out.println("<h1>Welcome " + request.getSession().getAttribute("Username") + "</h1>\n" +
                        "<h1>Categories</h1>");
                    out.println(categorySelect(request.getSession().getAttribute("AccountID").toString()));
                } else {
                    out.println("this page is available to owners only");
                }
                 out.println(       "</div>\n" +
                        "<div class=\"left\">\n" +
                        "<form action=\"/Servlet\" method=\"post\">\n");
                if (request.getSession().getAttribute("Role").equals("Owner")) {
                    out.println("    <input type=\"submit\" name=\"func\" value=\"Products\"/><br/>\n");
                } else {
                    out.println("    <input type=\"submit\" name=\"func\" value=\"Checkout\"/><br/>\n");
                }
                out.println(       "    <input type=\"submit\" name=\"func\" value=\"Products Browsing\"/><br/>\n" +
                        "</form>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            case "Products":
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
                        "       .frameleft {\n" +
                        "            text-align: left;\n" +
                        "            width: 200px;\n" +
                        "            padding: 10px;\n" +
                        "        }\n" +
                        "        .framecenter {\n" +
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
                        "<div class=\"frame\">\n" +
                        "<div class=\"frameleft\">\n");
                try {
                    out.println(selectCategoryButtons(request.getParameter("productCatID")));
                } catch (Exception e) {
                    try {
                        out.println(selectCategoryButtons(request.getSession().getAttribute("productCatID").toString()));
                    } catch (Exception err) {
                        out.println(selectCategoryButtons(""));
                    }
                }
                out.println("</div>");
                out.println("<div class=\"framecenter\">\n");
                if (request.getSession().getAttribute("Role").equals("Owner")) {
                    out.println("<h1>Welcome " + request.getSession().getAttribute("Username") + "</h1>\n");
                    out.println("<h1>Products</h1>");
                    out.println("<form action=\"/Servlet?func=Products\" method=\"post\">\n");
                    out.println("Name: <input type=\"text\" name=\"Search\" value=\"\" style=\"text-align: center\"/>");
                    out.println("<input type=\"submit\" name=\"productFunc\" value=\"Search\"/><br/>");
                    try {
                        switch (request.getParameter("productFunc")) {
                            case "Delete":
                                if (!productDelete(Integer.valueOf(request.getParameter("prodID")), out)) {
                                    out.println("<p style=\"color:red;text-align:center\">data modification failure</p>");
                                }
                                break;
                            case "Insert":
                                if(!insertProduct(request.getParameter("Name"), request.getParameter("SKU"), request.getParameter("Price"),
                                        (int) request.getSession().getAttribute("AccountID"), Integer.valueOf(request.getParameter("catID")), out)) {
                                    out.println("<p style=\"color:red;text-align:center\">data modification failure</p>");
                                }
                                break;
                            case "Update":
                                if (!productUpdate(Integer.valueOf(request.getParameter("prodID")), request.getParameter("Name"), request.getParameter("SKU"), request.getParameter("Price"))) {
                                    out.println("<p style=\"color:red;text-align:center\">data modification failure</p>");
                                }
                                break;
                            default:
                                break;

                        }
                    } catch (Exception e) {
                        //PrintStackTrace(e, out);
                    }
                    try {
                        out.println(productSelect(Integer.valueOf(request.getParameter("productCatID")), (int) request.getSession().getAttribute("AccountID"), out));
                        request.getSession().setAttribute("productCatID", request.getParameter("productCatID"));
                    } catch (Exception e) {
                        try {
                            out.println(productSelect(Integer.valueOf(request.getSession().getAttribute("productCatID").toString()), (int) request.getSession().getAttribute("AccountID"), out));
                            request.getSession().setAttribute("productCatID", request.getParameter("productCatID"));
                        } catch (Exception err) {
                            //PrintStackTrace(err, out);
                        }
                    }

                    StringBuilder sb = new StringBuilder();
                    sb.append("<form action=\"/Servlet?func=Products\" method=\"post\">\n");
                    sb.append("Name: <input type=\"text\" name=\"Name\" value=\"\" style=\"text-align: center\"/>");
                    sb.append("SKU: <input type=\"text\" name=\"SKU\" value=\"\" style=\"text-align: center\"/>");
                    sb.append("Price: <input type=\"text\" name=\"Price\" value=\"\" style=\"text-align: center\"/>");
                    sb.append("Category: <select name=\"catID\">");
                    sb.append(categoryDropDown());
                    sb.append("</select>");
                    sb.append("<input type=\"submit\" name=\"productFunc\" value=\"Insert\"/>");
                    sb.append("</form>");
                    out.println(sb.toString());

                } else {
                    out.println("this page is available to owners only");
                }
                out.println(       "</div>\n" +
                        "</div>\n" +
                        "<div class=\"left\">\n" +
                        "<form action=\"/Servlet\" method=\"post\">\n");
                if (request.getSession().getAttribute("Role").equals("Owner")) {
                    out.println("    <input type=\"submit\" name=\"func\" value=\"Categories\"/><br/>\n");
                } else {
                    out.println("    <input type=\"submit\" name=\"func\" value=\"Checkout\"/><br/>\n");
                }
                out.println(       "    <input type=\"submit\" name=\"func\" value=\"Products Browsing\"/><br/>\n" +
                        "    <input type=\"submit\" name=\"func\" value=\"Product Order\"/><br/>\n" +
                        "</form>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            case "Products Browsing":
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
                        "            text-align: center;\n" +
                        "            margin: auto;\n" +
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
                        "    <title>Browsing</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"frame\">\n");
                out.println("<h1>Browsing</h1>");
                out.println(       "</div>\n" +
                        "<div class=\"left\">\n" +
                        "<form action=\"/Servlet\" method=\"post\">\n");
                if (request.getSession().getAttribute("Role").equals("Owner")) {
                    out.println("    <input type=\"submit\" name=\"func\" value=\"Categories\"/><br/>\n" +
                            "    <input type=\"submit\" name=\"func\" value=\"Products\"/><br/>\n");
                } else {
                    out.println("    <input type=\"submit\" name=\"func\" value=\"Checkout\"/><br/>\n");
                }
                        out.println("    <input type=\"submit\" name=\"func\" value=\"Product Order\"/><br/>\n" +
                        "</form>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            case "Product Order":
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
                        "            text-align: center;\n" +
                        "            margin: auto;\n" +
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
                        "    <title>Order</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"frame\">\n");
                out.println("<h1>Order</h1>");
                out.println(       "</div>\n" +
                        "<div class=\"left\">\n" +
                        "<form action=\"/Servlet\" method=\"post\">\n");
                if (request.getSession().getAttribute("Role").equals("Owner")) {
                    out.println("    <input type=\"submit\" name=\"func\" value=\"Categories\"/><br/>\n" +
                            "    <input type=\"submit\" name=\"func\" value=\"Products\"/><br/>\n");
                } else {
                    out.println("    <input type=\"submit\" name=\"func\" value=\"Checkout\"/><br/>\n");
                }
                out.println(       "    <input type=\"submit\" name=\"func\" value=\"Products Browsing\"/><br/>\n" +
                        "</form>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            case "Checkout":
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
                        "            text-align: center;\n" +
                        "            margin: auto;\n" +
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
                        "    <title>Browsing</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<div class=\"frame\">\n");
                out.println("<h1>Browsing</h1>");
                out.println(       "</div>\n" +
                        "<div class=\"left\">\n" +
                        "<form action=\"/Servlet\" method=\"post\">\n");
                if (request.getSession().getAttribute("Role").equals("Owner")) {
                    out.println("    <input type=\"submit\" name=\"func\" value=\"Categories\"/><br/>\n" +
                            "    <input type=\"submit\" name=\"func\" value=\"Products\"/><br/>\n");
                }
                out.println("    <input type=\"submit\" name=\"func\" value=\"Product Order\"/><br/>\n" +
                        "<input type=\"submit\" name=\"func\" value=\"Checkout\"/><br/>\n" +
                        "</form>\n" +
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
            ////PrintStackTrace(e, out);
        }
    }

    private void PrintStackTrace(Exception e, PrintWriter out) {
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
                request.getSession().setAttribute("Role", rset.getString("Role"));
                request.getSession().setAttribute("Username", rset.getString("Username"));
                request.getSession().setAttribute("AccountID", rset.getInt("AccountID"));
                request.getRequestDispatcher("/Servlet?func=home").forward(request, response);
                out.print("<h1>Success! Found User: " + rset.getString("Username") + "</h1>");
            }
            out.print("<h1>Failure, no user found with that name!</h1>");
        } catch (Exception e) {
            //printStackTrace(e, out);
            out.print("<h1>Failure, no user found with that name!</h1>");
        }
    }

    public void destroy() {

    }
}
