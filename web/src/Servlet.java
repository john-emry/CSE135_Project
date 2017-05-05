/**
 * Created by John on 4/29/2017.
 */

import java.sql.*;

import java.io.*;
import java.util.*;
import java.util.Date;
import javax.servlet.*;
import javax.servlet.http.*;

public class Servlet extends HttpServlet {
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
            e.printStackTrace();
            error = e.toString();
        }
    }

    private ResultSet selectCategoryForID(String ID) {
        int intID = Integer.valueOf(ID);
        String query = "Select * from Categories WHERE \"CategoryID\"=?";
        PreparedStatement requestQuery;
        StringBuilder sb = new StringBuilder();
        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setInt(1, intID);
            rset = requestQuery.executeQuery();
            return rset;
        } catch (Exception e) {
            e.printStackTrace();
            return rset;
        }
    }

    private List<Map<String, String>> categoryList(String notChoose) {
        PreparedStatement requestQuery;
        ArrayList<Map<String, String>> categories = new ArrayList<Map<String, String>>();
        if (notChoose.equals("")) {
            String query = "Select * from Categories";
            try {
                requestQuery = conn.prepareStatement(query);
                rset = requestQuery.executeQuery();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String query = "Select * from Categories where not \"CategoryID\"=?";
            try {
                requestQuery = conn.prepareStatement(query);
                requestQuery.setInt(1, Integer.valueOf(notChoose));
                rset = requestQuery.executeQuery();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            while (rset.next()) {
                HashMap<String, String> category = new HashMap<String, String>();
                category.put("name", rset.getString("name"));
                category.put("id", String.valueOf(rset.getInt("CategoryID")));
                categories.add(category);
            }
            if (!notChoose.equals("-1")) {
                HashMap<String, String> category = new HashMap<>();
                category.put("name", "All Products");
                category.put("id", "-1");
                categories.add(category);
            }
            return categories;
        } catch (Exception e) {
            e.printStackTrace();
            return categories;
        }
    }

    private List<Map<String, String>> productSelect(int catID, int accountID, String searchCriteria) {
        StringBuilder sb = new StringBuilder();
        ArrayList<Map<String, String>> products = new ArrayList<>();
        try {
            String query = "";
            PreparedStatement requestQuery;
            if (catID != -1 && accountID != -1) {
                query = "Select * from Products where \"AccountID\" = ? AND \"CategoryID\" = ?";
                requestQuery = conn.prepareStatement(query);
                requestQuery.setInt(1, accountID);
                requestQuery.setInt(2, catID);
            } else if (accountID != -1) {
               query = "Select * from Products where \"AccountID\" = ?";
                requestQuery = conn.prepareStatement(query);
                requestQuery.setInt(1, accountID);
            } else if (catID != -1){
                query = "Select * from Products where \"CategoryID\" = ?";
                requestQuery = conn.prepareStatement(query);
                requestQuery.setInt(1, catID);
            } else {
                query = "Select * from Products";
                requestQuery = conn.prepareStatement(query);
            }

            rset = requestQuery.executeQuery();
            while (rset.next()) {
                if (rset.getString("Name").contains(searchCriteria)) {
                    HashMap<String, String> product = new HashMap<>();
                    product.put("name", rset.getString("Name"));
                    product.put("sku", rset.getString("SKU"));
                    product.put("price", rset.getString("Price"));
                    product.put("productID", String.valueOf(rset.getInt("ProductID")));
                    products.add(product);
                }
            }
            return products;
        } catch (Exception e) {
            e.printStackTrace();
            return products;
        }
    }

    private ResultSet selectProductForID(String ID) {
        int intID = Integer.valueOf(ID);
        String query = "Select * from Products WHERE \"ProductID\"=?";
        PreparedStatement requestQuery;
        StringBuilder sb = new StringBuilder();
        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setInt(1, intID);
            rset = requestQuery.executeQuery();
            return rset;
        } catch (Exception e) {
            return rset;
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
        String query = "SELECT \"SKU\" FROM public.Products WHERE \"SKU\"=?";
        PreparedStatement requestQuery;
        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, SKU);
            rset = requestQuery.executeQuery();
            if (rset != null && rset.next()) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        query = "UPDATE public.Products\n" +
                "SET \"Name\"= ?, \"SKU\"=?, \"Price\"=?\n" +
                "WHERE \"ProductID\"=?;";
        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, name);
            requestQuery.setString(2, SKU);
            requestQuery.setString(3, price);
            requestQuery.setInt(4, prodID);
            requestQuery.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
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

    private void insertOrderHistory(int price, int AccountID, Date date, int[] productIDs, int[] prices, int[] quantity) {
        String query = "INSERT INTO public.order_history(\"TotalPrice\", \"Date\", \"AccountID\")\n" +
                "select ?, ?, ?;";
        PreparedStatement requestQuery;
        System.out.println(Arrays.toString(productIDs));
        System.out.println(Arrays.toString(prices));
        System.out.println(Arrays.toString(quantity));
        try {
            int orderHistoryID = -1;
            requestQuery = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            requestQuery.setInt(1, price);
            requestQuery.setDate(2, new java.sql.Date(date.getTime()));
            requestQuery.setInt(3, AccountID);
            requestQuery.executeUpdate();

            rset = requestQuery.getGeneratedKeys();

            query = "INSERT INTO public.order_history_products(\"ProductID\", \"OrderHistoryID\", \"Quantity\", \"Price\")\n" +
                    "select ?, ?, ?, ?;";

            if (rset.next()) {
                orderHistoryID = rset.getInt("OrderHistoryID");
            }
            if (orderHistoryID != -1) {
                for (int i = 0; i < productIDs.length; i++) {
                    requestQuery = conn.prepareStatement(query);
                    requestQuery.setInt(1, productIDs[i]);
                    requestQuery.setInt(2, orderHistoryID);
                    requestQuery.setInt(3, quantity[i]);
                    requestQuery.setInt(4, prices[i]);
                    requestQuery.executeUpdate();
                }
            } else {
                throw new Exception("no orders found");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
        doPost(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        switch(request.getParameter("func")) {
            case "NotLoggedIn":
                out.println("<h1>Only Those Logged In May See This Page</h1>");
                break;
            case "OwnersOnly":
                out.println("<h1>Only owners may view this page</h1>");
            case "login":
                request.getSession().invalidate();
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
                out.println(       "    <input type=\"submit\" name=\"func\" value=\"ProductsBrowsing\"/><br/>\n" +
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
                out.println(       "    <input type=\"submit\" name=\"func\" value=\"ProductsBrowsing\"/><br/>\n" +
                        "</form>\n" +
                        "</div>\n" +
                        "\n" +
                        "</body>\n" +
                        "</html>");
                break;
            case "Products":

                String category;
                category =(request.getParameter("Category"));
                if (category == null) {
                    try {
                        category = request.getSession().getAttribute("currentCategoryID").toString();
                        if (category == null) {
                            category = "-1";
                        }
                    } catch (Exception e) {
                        category = "-1";
                    }
                } else {
                    request.getSession().setAttribute("productSearchField", "");
                }

                String searchCriteria;
                searchCriteria = request.getParameter("searchField");
                System.out.println(searchCriteria);
                if (searchCriteria == null) {
                    try {
                        searchCriteria = request.getSession().getAttribute("productSearchField").toString();
                    } catch (Exception e) {
                        searchCriteria = "";
                    }
                } else {
                    request.getSession().setAttribute("productSearchField", searchCriteria);
                }

                if (!(request.getParameter("ProductAdd") == null)) {
                    if (!insertProduct(request.getParameter("newName"), request.getParameter("newSKU"), request.getParameter("newPrice"), Integer.valueOf(request.getSession().getAttribute("AccountID").toString()),
                            Integer.valueOf(request.getParameter("CategorySelect")), out)) {
                        request.setAttribute("errorMessage", "INSERT FAILURE");
                    }
                } else if (request.getParameter("ProductDelete") != null) {
                    if (!productDelete(Integer.valueOf(request.getParameter("ProductDelete")), out)) {
                        request.setAttribute("errorMessage", "DELETE FAILURE");
                    }
                } else if (request.getParameter("ProductUpdate") != null) {
                    if (!productUpdate(
                            Integer.valueOf(request.getParameter("ProductUpdate")),
                            request.getParameter("productName"),
                            request.getParameter("productSKU"),
                            request.getParameter("productPrice"))) {
                        request.setAttribute("errorMessage", "UPDATE FAILURE");
                    }
                }

                request.setAttribute("categoriesList", categoryList(category));
                request.setAttribute("categoriesDropDown", categoryList("-1"));
                try {
                    ResultSet categories = selectCategoryForID(category);
                    if (categories != null && categories.next()) {
                        request.setAttribute("currentCategory", categories.getString("Name"));
                        request.getSession().setAttribute("currentCategoryID", categories.getString("CategoryID"));
                    } else {
                        request.setAttribute("currentCategory", "All Products");
                        request.getSession().setAttribute("currentCategoryID", "-1");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("currentCategory", "All Products");
                    request.getSession().setAttribute("currentCategoryID", "-1");
                }

                try {
                    request.setAttribute("productList", productSelect(Integer.valueOf(request.getSession().getAttribute("currentCategoryID").toString()),
                            Integer.valueOf(request.getSession().getAttribute("AccountID").toString()), searchCriteria));
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("productList", productSelect(Integer.valueOf(request.getParameter("Category")),
                            Integer.valueOf(request.getSession().getAttribute("AccountID").toString()), searchCriteria));
                }

                RequestDispatcher dispatcher = request.getRequestDispatcher("Products.jsp");
                dispatcher.forward(request, response);
                break;
            case "ProductsBrowsing":

                if (request.getParameter("ProductBuy") != null) {
                    try {
                        HashMap<String, String> product = new HashMap<>();
                        ResultSet productSet = selectProductForID(request.getParameter("ProductBuy"));
                        if (productSet != null && productSet.next()) {
                            product.put("sku", productSet.getString("SKU"));
                            product.put("name", productSet.getString("Name"));
                            product.put("price", productSet.getString("Price"));
                            product.put("productID", productSet.getString("ProductID"));
                            request.setAttribute("product", product);
                        } else {
                            throw new Exception("Result Set Empty");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        Object tempCart = request.getSession().getAttribute("shoppingCart");
                        if (tempCart instanceof List) {
                            List<Map<String, String>> cart = (List<Map<String, String>>) tempCart;
                            for (Map<String, String> m : cart) {
                                System.out.println(m.get("sku"));
                            }
                            request.setAttribute("shoppingCart", cart);
                        } else {
                            request.getSession().setAttribute("shoppingCart", new ArrayList<>());
                            throw new Exception("shoppingCart not found");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        request.getSession().setAttribute("shoppingCart", new ArrayList<>());
                    }
                    dispatcher = request.getRequestDispatcher("ProductOrder.jsp");
                    dispatcher.forward(request, response);
                }

                category =(request.getParameter("Category"));
                if (category == null) {
                    try {
                        category = request.getSession().getAttribute("currentCategoryID").toString();
                        if (category == null) {
                            category = "-1";
                        }
                    } catch (Exception e) {
                        category = "-1";
                    }
                } else {
                    request.getSession().setAttribute("browsingSearchField", "");
                }

                searchCriteria = request.getParameter("searchField");
                if (searchCriteria == null) {
                    try {
                        searchCriteria = request.getSession().getAttribute("browsingSearchField").toString();
                    } catch (Exception e) {
                        searchCriteria = "";
                    }
                } else {
                    request.getSession().setAttribute("browsingSearchField", searchCriteria);
                }

                request.setAttribute("categoriesList", categoryList(category));
                try {
                    ResultSet categories = selectCategoryForID(category);
                    if (categories != null && categories.next()) {
                        request.setAttribute("currentCategory", categories.getString("Name"));
                        request.getSession().setAttribute("currentCategoryID", categories.getString("CategoryID"));
                    } else {
                        request.setAttribute("currentCategory", "All Products");
                        request.getSession().setAttribute("currentCategoryID", "-1");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("currentCategory", "All Products");
                    request.getSession().setAttribute("currentCategoryID", "-1");
                }

                try {
                    request.setAttribute("productList", productSelect(Integer.valueOf(request.getSession().getAttribute("currentCategoryID").toString()),
                            -1, searchCriteria));
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("productList", productSelect(Integer.valueOf(request.getParameter("Category")),
                            -1, searchCriteria));
                }
                dispatcher = request.getRequestDispatcher("ProductsBrowsing.jsp");
                dispatcher.forward(request, response);
                break;
            case "ProductOrder":

                String productID = request.getParameter("AddToCart");
                String quantity = request.getParameter("ProductQuantity");
                HashMap<String, String> product = new HashMap<>();

                try {
                    ResultSet productSet = selectProductForID(productID);
                    if (productSet != null && productSet.next()) {
                        product.put("sku", productSet.getString("SKU"));
                        product.put("name", productSet.getString("Name"));
                        product.put("price", productSet.getString("Price"));
                        product.put("productID", productSet.getString("ProductID"));
                        product.put("quantity", quantity);
                        request.setAttribute("product", product);
                    } else {
                        throw new Exception("Result Set Empty");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Object tempCart = request.getSession().getAttribute("shoppingCart");
                try {
                    List<Map<String, String>> cart = (List<Map<String, String>>) tempCart;
                    cart.add(product);
                    request.getSession().setAttribute("shoppingCart", cart);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                dispatcher = request.getRequestDispatcher("/Servlet?func=ProductsBrowsing");
                dispatcher.forward(request, response);
                break;
            case "Checkout":
                try {
                    tempCart = request.getSession().getAttribute("shoppingCart");
                    if (tempCart instanceof List) {
                        List<Map<String, String>> cart = (List<Map<String, String>>) tempCart;
                        int totalPrice = 0;
                        for (Map<String, String> m : cart) {
                            totalPrice += Integer.valueOf(m.get("price")) * Integer.valueOf(m.get("quantity"));
                        }
                        request.setAttribute("totalPrice", totalPrice);
                        request.setAttribute("shoppingCart", cart);
                    } else {
                        request.setAttribute("totalPrice", 0);
                        request.getSession().setAttribute("shoppingCart", new ArrayList<>());
                        throw new Exception("shoppingCart not found");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    request.setAttribute("totalPrice", 0);
                    request.getSession().setAttribute("shoppingCart", new ArrayList<>());
                }
                dispatcher = request.getRequestDispatcher("BuyShoppingCart.jsp");
                dispatcher.forward(request, response);
                break;
            case "Confirmation":
                tempCart = request.getSession().getAttribute("shoppingCart");
                try {
                    List<Map<String, String>> cart = (List<Map<String, String>>) tempCart;
                    request.setAttribute("shoppingCart", cart);
                    int[] prices = new int[cart.size()];
                    int[] quantities = new int[cart.size()];
                    int[] productIDs = new int[cart.size()];
                    int totalPrice = 0;
                    for (int i = 0; i < cart.size(); i++) {
                        Map<String, String> m = cart.get(i);
                        totalPrice += Integer.valueOf(m.get("price")) * Integer.valueOf(m.get("quantity"));
                        prices[i] = (Integer.valueOf(m.get("price")));
                        quantities[i] = (Integer.valueOf(m.get("quantity")));
                        productIDs[i] = (Integer.valueOf(m.get("productID")));
                    }
                    insertOrderHistory(totalPrice, (int) request.getSession().getAttribute("AccountID"), new Date(),
                            productIDs, prices, quantities);
                    request.getSession().removeAttribute("shoppingCart");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dispatcher = request.getRequestDispatcher("Confirmation.jsp");
                dispatcher.forward(request, response);
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
