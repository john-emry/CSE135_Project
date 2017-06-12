/**
 * Created by John on 4/29/2017.
 */

import org.json.JSONObject;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.*;
import java.util.Date;
import org.json.*;

public class Servlet extends HttpServlet implements HttpSessionListener {
    private static Connection conn = null;
    private static Statement stmt = null;
    private static ResultSet rset = null;
    private String error = "";

    private String[] states = {
            "Alabama",
            "Alaska",
            "Arizona",
            "Arkansas",
            "California",
            "Colorado",
            "Connecticut",
            "Delaware",
            "Florida",
            "Georgia",
            "Hawaii",
            "Idaho",
            "Illinois",
            "Indiana",
            "Iowa",
            "Kansas",
            "Kentucky",
            "Louisiana",
            "Maine",
            "Maryland",
            "Massachusetts",
            "Michigan",
            "Minnesota",
            "Mississippi",
            "Missouri",
            "Montana",
            "Nebraska",
            "Nevada",
            "New Hampshire",
            "New Jersey",
            "New Mexico",
            "New York",
            "North Carolina",
            "North Dakota",
            "Ohio",
            "Oklahoma",
            "Oregon",
            "Pennsylvania",
            "Rhode Island",
            "South Carolina",
            "South Dakota",
            "Tennessee",
            "Texas",
            "Utah",
            "Vermont",
            "Virginia",
            "Washington",
            "West Virginia",
            "Wisconsin",
            "Wyoming",
    };

    public void init() throws ServletException {
        try {
            Class.forName("org.postgresql.Driver");
            DriverManager.registerDriver(new org.postgresql.Driver());
            String dbURL = "jdbc:postgresql:CSE135?user=snap&password=";
            conn = DriverManager.getConnection(dbURL);
            System.out.println("Connected to CSE135");
        } catch (Exception e) {
            e.printStackTrace();
            error = e.toString();
        }
    }

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {

    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        /*try {
            int AccountID = (int) httpSessionEvent.getSession().getAttribute("AccountID");
            String query = "DELETE * FROM public.sales_log WHERE \"AccountID\" = ?;" +
                    "DELETE * FROM public.active_users WHERE \"AccountID\" = ?";
            PreparedStatement delete = conn.prepareStatement(query);
            delete.setInt(1, AccountID);
            delete.setInt(2, AccountID);
            delete.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
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
            String query = "Delete from public.Products where \"ProductID\" = ?;\n";/* +
                    "INSERT INTO public.sales_log(\n" +
                    "\"PID\", \"State\", \"AccountID\", \"ChangeType\")\n" +
                    "Select ?, a.\"State\", au.\"AccountID\", CAST('p' as text) as \"ChangeType\"\n" +
                    "FROM accounts a, active_users au\n" +
                    "WHERE a.\"AccountID\" = au.\"AccountID\"";*/
            PreparedStatement requestQuery;
            try {
                requestQuery = conn.prepareStatement(query);
                requestQuery.setInt(1, prodID);
                requestQuery.setInt(2, prodID);
                requestQuery.executeUpdate();
                return true;
            } catch (Exception e) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Boolean productUpdate(int prodID, String name, String SKU, String price) {
        try {
            if (name.equals("") || SKU.equals("") || price.equals("") || Integer.valueOf(price) < 0)  {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        String query = "SELECT \"SKU\" FROM public.Products WHERE \"SKU\"=? AND NOT \"ProductID\"=?";
        PreparedStatement requestQuery;
        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, SKU);
            requestQuery.setInt(2, prodID);
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
            /*String query2 = "INSERT INTO public.sales_log(\n" +
                    "\"PID\", \"State\", \"AccountID\", \"ChangeType\")\n" +
                    "Select ?, a.\"State\", au.\"AccountID\", CAST('p' as text) as \"ChangeType\"\n" +
                    "FROM accounts a\n"
            requestQuery = conn.prepareStatement(query2);
            requestQuery.setInt(1, prodID);
            requestQuery.executeUpdate();*/
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
                    /*String query2 = "INSERT INTO public.sales_log(\n" +
                            "\"PID\", \"Price\", \"State\", \"AccountID\", \"ChangeType\")\n" +
                            "Select ?, ?, a.\"State\", au.\"AccountID\", CAST('r' as text) as \"ChangeType\"\n" +
                            "FROM accounts a, active_users au\n" +
                            "WHERE a.\"AccountID\" = au.\"AccountID\"\n";
                    requestQuery = conn.prepareStatement(query2);
                    requestQuery.setInt(1, productIDs[i]);
                    requestQuery.setString(2, String.valueOf(prices[i]));*/
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

    private final String TOP_K = "ORDER BY totalprice desc, header, productprice desc, producttable.\"Name\"";

    private final String ALPHABETICAL = "ORDER BY header, totalprice desc, producttable.\"Name\", productprice desc";

    private List<List<Map<String, String>>> createTempTable(int AccountID, String cat) {
        String query = "drop table if exists precomp; drop table if exists sales_log;\n" +
                "create table precomp as" +
                "(with overall_table as \n" +
                "(select p.\"CategoryID\", pc.\"ProductID\",c.\"State\",sum(CAST(pc.\"Price\" as bigint)*pc.\"Quantity\") as amount  \n" +
                "from order_history_products pc  \n" +
                "inner join order_history sc on (sc.\"OrderHistoryID\" = pc.\"OrderHistoryID\")\n" +
                "inner join products p on (pc.\"ProductID\" = p.\"ProductID\") -- add category filter if any\n" +
                "inner join accounts c on (sc.\"AccountID\" = c.\"AccountID\")\n" +
                "group by pc.\"ProductID\",c.\"State\", p.\"CategoryID\"\n" +
                "),\n" +
                "top_state as\n" +
                "(select \"State\", sum(amount) as dollar from (\n" +
                "select \"State\", amount from overall_table\n" +
                "UNION ALL\n" +
                "select name as \"State\", 0.0 as amount from states\n" +
                ") as state_union\n" +
                " group by \"State\" order by dollar desc limit 50\n" +
                "),\n" +
                "top_n_state as \n" +
                "(select row_number() over(order by dollar desc) as state_order, \"State\", dollar from top_state\n" +
                "),\n" +
                "top_prod as \n" +
                "(select \"ProductID\", \"CategoryID\", sum(amount) as dollar from (\n" +
                "select \"ProductID\", \"CategoryID\", amount from overall_table\n" +
                "UNION ALL\n" +
                "select \"ProductID\", \"CategoryID\", 0.0 as amount from products\n" +
                ") as product_union\n" +
                "group by \"ProductID\", \"CategoryID\" order by dollar desc\n" +
                "),\n" +
                "top_n_prod as \n" +
                "(select row_number() over(order by dollar desc) as product_order, \"ProductID\", \"CategoryID\", dollar from top_prod\n" +
                ")\n" +
                "select ts.\"State\" as header, tp.\"CategoryID\", tp.\"ProductID\", pr.\"Name\", COALESCE(ot.amount, 0.0) as cell_sum, ts.dollar as totalprice, tp.dollar as productprice\n" +
                "from top_n_prod tp CROSS JOIN top_n_state ts \n" +
                "LEFT OUTER JOIN overall_table ot \n" +
                "ON ( tp.\"ProductID\" = ot.\"ProductID\" and ts.\"State\" = ot.\"State\")\n" +
                "inner join products pr ON tp.\"ProductID\" = pr.\"ProductID\"\n" +
                "order by ts.state_order, tp.product_order);\n" +
                "CREATE TABLE public.sales_log\n" +
                "(\n" +
                "\"SLID\" serial NOT NULL,\n" +
                "\"PID\" bigint NOT NULL,\n" +
                "\"Price\" text,\n" +
                "\"State\" text,\n" +
                "\"AccountID\" INTEGER NOT NULL,\n" +
                "PRIMARY KEY (\"SLID\")\n" +
                ")";
        System.out.println(query);
        try {
            PreparedStatement requestQuery = conn.prepareStatement(query);
            //requestQuery.setInt(1, AccountID);
            requestQuery.execute();

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return getPreCompValues(cat);
    }

    private void buyProducts(int orders) {
        String INSERT_SHOPPING_CART = "INSERT INTO order_history(\"AccountID\", \"TotalPrice\", \"Date\") VALUES(?, ?, ?) ";
        String INSERT_PRODUCTS_IN_CART = "INSERT INTO order_history_products(\"OrderHistoryID\", \"ProductID\", \"Price\", \"Quantity\") VALUES(?, ?, ?, ?)";
        String GET_RANDOM_PERSON = "SELECT \"AccountID\" FROM accounts OFFSET floor(random()* (select count(*) from accounts)) LIMIT 1";
        String GET_RANDOM_5_PRODUCTS = "SELECT \"ProductID\", CAST(\"Price\" as bigint) FROM products OFFSET floor(random()* (select count(*) from products)) LIMIT 5";
        Random rand = new Random();
        int batchSize = 10000;
        int personId = 0;
        int noOfRows = 0;
        int productId = 0;
        int productPrice = 0;
        int quantity = 0;
        PreparedStatement shoppingCartPtst = null, productsCartPtst  = null;
        Statement personSt = null, productSt = null;
        ArrayList<Integer> cartIds = new ArrayList<Integer>();

        try {
            conn.setAutoCommit(false);
            shoppingCartPtst = conn.prepareStatement(INSERT_SHOPPING_CART, Statement.RETURN_GENERATED_KEYS);
            productsCartPtst = conn.prepareStatement(INSERT_PRODUCTS_IN_CART);
            personSt = conn.createStatement();
            productSt = conn.createStatement();

            for(int i=0;i<orders;i++) {
                ResultSet personRs = personSt.executeQuery(GET_RANDOM_PERSON);
                if(personRs.next()) {
                    personId = personRs.getInt("AccountID");
                }
                personRs.close();

                shoppingCartPtst.setInt(1, personId);
                shoppingCartPtst.setString(2, "0");
                shoppingCartPtst.setDate(3, new java.sql.Date(System.currentTimeMillis()));

                shoppingCartPtst.addBatch();
                noOfRows++;

                if(noOfRows % batchSize == 0) {
                    shoppingCartPtst.executeBatch();
                    ResultSet cartRs = shoppingCartPtst.getGeneratedKeys();
                    while(cartRs.next()) {
                        cartIds.add(cartRs.getInt(1));
                    }
                    cartRs.close();
                }

            }
            shoppingCartPtst.executeBatch();
            ResultSet cartRs = shoppingCartPtst.getGeneratedKeys();
            while(cartRs.next()) {
                cartIds.add(cartRs.getInt(1));
            }
            cartRs.close();
            shoppingCartPtst.close();

            int totalRows = 0;
            for(int i=0;i<orders;i++) {
                ResultSet productRs = productSt.executeQuery(GET_RANDOM_5_PRODUCTS);
                int shoppingCartPrice = 0;
                while(productRs.next()) {
                    productsCartPtst.setInt(1, cartIds.get(i));
                    productId = productRs.getInt("ProductID");
                    productsCartPtst.setInt(2, productId);
                    productPrice = Integer.valueOf(productRs.getString("Price"));
                    productsCartPtst.setInt(3, productPrice);
                    quantity = rand.nextInt(10)+1;
                    productsCartPtst.setInt(4, quantity);
                    shoppingCartPrice += quantity * productPrice;

                    productsCartPtst.addBatch();
                    totalRows++;

                    if(totalRows % batchSize == 0) {
                        productsCartPtst.executeBatch();
                    }
                }
                productsCartPtst.executeBatch();
                String query = "UPDATE order_history\n" +
                        "Set \"TotalPrice\" = ?\n" +
                        "WHERE \"OrderHistoryID\" = ?";
                PreparedStatement UpdateStatement = conn.prepareStatement(query);
                UpdateStatement.setString(1, String.valueOf(shoppingCartPrice));
                UpdateStatement.setInt(2, cartIds.get(i));
                UpdateStatement.executeUpdate();
            }
            String query2 = "INSERT INTO public.sales_log(\n" +
                    "\"PID\", \"State\", \"Price\", \"AccountID\")\n" +
                    "select \"ProductID\" as \"PID\",\"State\", \"Quantity\"*Cast(\"Price\" as bigint) as \"Price\", a.\"AccountID\"\n" +
                    "from (select \"TotalPrice\", \"AccountID\", \"ProductID\", \"Quantity\", \"Price\" from\n" +
                    "order_history oh inner join order_history_products ohp on oh.\"OrderHistoryID\" = ohp.\"OrderHistoryID\") totals\n" +
                    "inner join accounts a on totals.\"AccountID\" = a.\"AccountID\"";
            PreparedStatement requestQuery = conn.prepareStatement(query2);
            requestQuery.executeUpdate();
            conn.commit();
            conn.setAutoCommit(true);
        } catch(Exception e) {
            try {
                conn.rollback();
            } catch (Exception err) {
                err.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if(shoppingCartPtst != null) {
                    shoppingCartPtst.close();
                }
                if(productsCartPtst != null) {
                    productsCartPtst.close();
                }
                if(personSt != null) {
                    personSt.close();
                }
                if(productSt != null) {
                    productSt.close();
                }
            } catch(Exception e1) {
                e1.getStackTrace();
            }
        }
    }

    private final String CAT_FILTER_YES =
            ", categories c\n" +
                    "WHERE precomp.\"CategoryID\" = c.\"CategoryID\"\n" +
                    "AND c.\"Name\" = ?";

    private List<List<Map<String, String>>> getPreCompValues(String cat) {
        List<List<Map<String, String>>> rValue = new ArrayList<>();
        String catFilter = "";
        if (!cat.equals("")) {
            catFilter = CAT_FILTER_YES;
        } else {
            catFilter = "";
        }
        try {
            PreparedStatement requestQuery;
            String query = "SELECT header, \"ProductID\", \"Name\", cell_sum, totalprice, productprice\n" +
                    "FROM (SELECT\n" +
                    "header, \"ProductID\", \"Name\", cell_sum, totalprice, productprice, row_number() over (partition by header order by productprice DESC)\n" +
                    "FROM precomp\n" + catFilter +
                    "\nGROUP BY \"ProductID\", header, \"Name\", cell_sum, totalprice, productprice\n" +
                    "ORDER BY totalprice DESC, header, productprice DESC) as subquery\n" +
                    "WHERE row_number <= 50";
            requestQuery = conn.prepareStatement(query);
            if (!cat.equals("")) {
                requestQuery.setString(1, cat);
            }
            ResultSet rset = requestQuery.executeQuery();
            String tempState = "";
            List<Map<String, String>> tempArray = new ArrayList<>();
            List<Map<String, String>> firstTimeArray = new ArrayList<>();
            boolean firstTime = true;
            while (rset.next()) {
                String rsetState = rset.getString("header");
                if (!rsetState.equals(tempState)) {
                    if (!tempArray.isEmpty()) {
                        if (firstTime) {
                            rValue.add(firstTimeArray);
                            firstTime = false;
                        }
                        rValue.add(tempArray);
                    } else if (firstTime) {
                        Map<String, String> tempMap = new HashMap<>();
                        tempMap.put("id", "");
                        tempMap.put("value", "");
                        firstTimeArray.add(tempMap);
                    }
                    tempState = rsetState;
                    tempArray = new ArrayList<>();
                    Map<String, String> tempMap = new HashMap<>();
                    tempMap.put("id", rset.getString("header"));
                    tempMap.put("value", tempState + " ($" + String.valueOf(rset.getLong("totalprice")) + ")");
                    tempArray.add(tempMap);
                }
                if (firstTime) {
                    Map<String, String> tempMap = new HashMap<>();
                    tempMap.put("id", String.valueOf(rset.getInt("ProductID")));
                    tempMap.put("value", rset.getString("Name") + " ($" + String.valueOf(rset.getLong("productprice")) + ")");
                    tempArray.add(tempMap);
                    firstTimeArray.add(tempMap);
                }
                Map<String, String> tempMap = new HashMap<>();
                tempMap.put("id", rset.getString("header") + String.valueOf(rset.getInt("ProductID")));
                tempMap.put("value", String.valueOf(rset.getLong("cell_sum")));
                tempArray.add(tempMap);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        return rValue;
    }

    private JSONArray findLogs(int AccountID) {
        String query = "select \"ProductID\", \"State\", cell_sum, totalprice, coalesce(productprice, 0)\n" +
                "from (select header, \"ProductID\", \"Name\", cell_sum, totalprice, productprice\n" +
                "from (select header, \"ProductID\", \"Name\", cell_sum, totalprice, productprice, row_number() over (partition by header order by productprice DESC)\n" +
                "from precomp\n" +
                "group by \"ProductID\", header, \"Name\", cell_sum, totalprice, productprice\n" +
                "order by totalprice desc, header, productprice desc) as subquery\n" +
                "where row_number <= 50) st inner join sales_log sl on st.\"ProductID\" = sl.\"PID\"";
        JSONArray rArray = new JSONArray();
        try {
            PreparedStatement requestQuery = conn.prepareStatement(query);
            requestQuery.setInt(1, AccountID);
            ResultSet rset = requestQuery.executeQuery();
            while (rset.next()) {
                JSONObject tempObj = new JSONObject();
                String changeType = "";
                if (rset.getInt("productprice") == 0) {
                    changeType = "p";
                } else {
                    changeType = "r";
                }
                tempObj.put("Type", changeType);
                tempObj.put("State", rset.getString("State"));
                tempObj.put("PID", rset.getInt("ProductID"));
                tempObj.put("Value", rset.getInt("cell_sum"));
                rArray.put(tempObj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return rArray;
        }
        return rArray;
    }

    private Boolean insertProduct(String name, String SKU, String price, int AccountID, Integer catID, PrintWriter out) {
        try {
            if (name.equals("") || SKU.equals("") || price.equals("") || Integer.valueOf(price) < 0 || catID.equals(null))  {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
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
                e.printStackTrace();
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    private Boolean updateCategory(int catID, String name, String description, int AccountID, PrintWriter out) {
        if (name.equals("")) {
            return false;
        }
        String query = "SELECT * FROM public.Categories WHERE \"Name\" = ? AND NOT \"CategoryID\"=?";
        PreparedStatement requestQuery;
        try {
            requestQuery = conn.prepareStatement(query);
            requestQuery.setString(1, name);
            requestQuery.setInt(2, catID);
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
            e.printStackTrace();
            return false;
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("func").equals("refresh")) {
            //JSONArray responseValue = findLogs((int) request.getSession().getAttribute("AccountID"));
            JSONArray rValue = findLogs((int) request.getSession().getAttribute("AccountID"));
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(rValue.toString());
            // findLogs((int) request.getSession().getAttribute("AccountID"));
        } else if (request.getParameter("func").equals("buyOrders")) {
            System.out.println("BUYING");
            buyProducts( Integer.valueOf(request.getParameter("numOrders")));
        } else {
            doPost(request, response);
        }
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
                    out.println("    <input type=\"submit\" name=\"func\" value=\"SalesAnalytics\"/><br/>\n");
                    out.println("    <input type=\"submit\" name=\"func\" value=\"SimilarProducts\"/><br/>\n");
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
                    out.println("    <input type=\"submit\" name=\"func\" value=\"SalesAnalytics\"/><br/>\n");
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
                try {
                    if (Integer.valueOf(quantity) < 1) {
                        System.err.println("quantity was less than 1");
                        dispatcher = request.getRequestDispatcher("/Servlet?func=ProductsBrowsing");
                        dispatcher.forward(request, response);
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("quantity was less than 1");
                    dispatcher = request.getRequestDispatcher("/Servlet?func=ProductsBrowsing");
                    dispatcher.forward(request, response);
                    break;
                }
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
            case "SalesAnalytics":
                String rowSelect = request.getParameter("rowSelect");
                String orderSelect = request.getParameter("orderSelect");
                category =(request.getParameter("productCategoryFilter"));
                String next20 = request.getParameter("next20button");
                String next10 = request.getParameter("next10button");
                Enumeration<String> enu = request.getParameterNames();
                while (enu.hasMoreElements()) {
                    System.out.println(enu.nextElement());
                }
                if (category == null) {
                    try {
                        category = request.getSession().getAttribute("category").toString();
                        if (category == null || category == "-1") {
                            category = "";
                        }
                    } catch (Exception e) {
                        category = "";
                    }
                } else if (category.equals("allCategories")) {
                    category = "";
                }
                Integer rowNum = 0;
                Integer columnNum = 0;
                if (request.getSession().getAttribute("rowNum") != null) {
                    rowNum = (Integer) request.getSession().getAttribute("rowNum");
                }
                if (request.getSession().getAttribute("columnNum") != null) {
                    columnNum = (Integer) request.getSession().getAttribute("columnNum");
                }
                if (next20 == null && next10 == null) {
                    if (rowSelect == null) {
                        rowSelect = "customer";
                    }
                    if (orderSelect == null) {
                        orderSelect = "alphabetical";
                    }
                    rowNum = 0;
                    columnNum = 0;
                    request.getSession().removeAttribute("rowNum");
                    request.getSession().removeAttribute("columnNum");
                } else {
                    if (next20 != null) {
                        rowNum += 20;
                        request.getSession().setAttribute("rowNum", rowNum);
                    } else {
                        columnNum += 10;
                        request.getSession().setAttribute("columnNum", columnNum);
                    }
                    request.setAttribute("onFirstPage", true);
                    rowSelect = request.getSession().getAttribute("row").toString();
                    orderSelect = request.getSession().getAttribute("order").toString();
                    category = request.getSession().getAttribute("category").toString();
                    if (category.equals("All Categories")) {
                        category = "";
                    }
                }
                System.out.println(rowSelect);
                System.out.println(orderSelect);
                System.out.println(category);
                System.out.println(next10);
                System.out.println(next20);
                List<List<Map<String, String>>> productList = createTempTable(((int) request.getSession().getAttribute("AccountID")), category);
                System.out.println(productList.toString());

                if (category.equals("")) {
                    category = "All Categories";
                }

                request.setAttribute("displayTableRows", productList);
                request.setAttribute("custOrState", rowSelect + "s");
                request.getSession().setAttribute("row", rowSelect);
                request.getSession().setAttribute("order", orderSelect);
                request.getSession().setAttribute("category", category);

                request.setAttribute("categoriesList", categoryList("-1"));
                dispatcher = request.getRequestDispatcher("SalesAnalytics.jsp");
                dispatcher.forward(request, response);
                break;
            case "SimilarProducts":
                dispatcher = request.getRequestDispatcher("SimilarProducts.jsp");
                dispatcher.forward(request, response);
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
            e.printStackTrace();
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
            e.printStackTrace();
            out.print("<h1>Failure, no user found with that name!</h1>");
        }
    }

    public void destroy() {
        try {
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
