import java.sql.*;
import java.sql.Date;
import java.util.*;

public class DataGenerator {
	static int noOfCategories;
	static int noOfProducts;
	static int noOfCustomers;
	static int noOfSales;
	static Connection con = null;
	static int batchSize = 1000;
	static Random rand = new Random();
	static String[] states = {
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
	
	private static String DROP_TABLES = "DROP TABLE accounts, order_history, products, categories, order_history_products";
	private static String CREATE_PERSON = "CREATE TABLE accounts ( \"AccountID\" SERIAL PRIMARY KEY, "
			+ "\"Username\" TEXT NOT NULL UNIQUE, "
			+ "\"Role\" TEXT NOT NULL, "
			+ "\"State\" TEXT NOT NULL, "
			+ "\"Age\" INTEGER NOT NULL CHECK(\"Age\" > 0), "
			+ "\"SessionToken\" TEXT)";
	
	private static String CREATE_CATEGORY = "CREATE TABLE categories ( \"CategoryID\" SERIAL PRIMARY KEY, "
			+ "\"Name\" TEXT UNIQUE NOT NULL, "
			+ "\"Description\" TEXT, "
			+ "\"AccountID\" INTEGER NOT NULL);";
	
	private static String CREATE_PRODUCT = "CREATE TABLE products ( \"ProductID\" SERIAL PRIMARY KEY, "
			+ "\"SKU\" TEXT NOT NULL UNIQUE, "
			+ "\"Name\" TEXT NOT NULL, "
			+ "\"Price\" TEXT NOT NULL, "
			+ "\"CategoryID\" INTEGER REFERENCES categories(\"CategoryID\") NOT NULL, "
			+ "\"AccountID\" INTEGER NOT NULL);";
	
	private static String CREATE_SHOPPING_CART = "CREATE TABLE order_history( \"OrderHistoryID\" SERIAL PRIMARY KEY, "
			+ "\"AccountID\" INTEGER REFERENCES Accounts(\"AccountID\") NOT NULL, "
			+ "\"TotalPrice\" TEXT NOT NULL, "
			+ "\"Date\" date NOT NULL);";
	
	private static String CREATE_PRODUCT_IN_CART = "CREATE TABLE order_history_products( \"OrderHistoryProductsID\" SERIAL PRIMARY KEY, "
			+ "\"OrderHistoryID\" INTEGER REFERENCES order_history(\"OrderHistoryID\") NOT NULL, "
			+ "\"ProductID\" INTEGER REFERENCES products(\"ProductID\") NOT NULL, "
			+ "\"Price\" TEXT NOT NULL, "
			+ "\"Quantity\" INTEGER NOT NULL CHECK (\"Quantity\" > 0));";
	
	private static String INSERT_CUSTOMER = "INSERT INTO accounts(\n" +
			"\"Username\", \"Age\", \"State\", \"Role\") VALUES(?,"
			+ " 25, "
			+ "?, "
			+ " User)";
	private static String INSERT_CATEGORY = "INSERT INTO categories(\"Name\", \"Description\", \"AccountID\") VALUES(?, ?, 1) ";
	private static String INSERT_PRODUCT = "INSERT INTO products(\"SKU\", \"Name\", \"Price\", \"CategoryID\", \"AccountID\") VALUES(?, ?, ?, ?, 1) ";
	private static String INSERT_SHOPPING_CART = "INSERT INTO order_history(\"AccountID\", \"TotalPrice\", \"Date\") VALUES(?, ?, ?) ";
	private static String INSERT_PRODUCTS_IN_CART = "INSERT INTO order_history_products(\"OrderHistoryID\", \"ProductID\", \"Price\", \"Quantity\") VALUES(?, ?, ?, ?)";
	
	int maxCustId = 0;
	int maxCatId = 0;
	int maxProdId = 0;
	
	static HashMap<Integer, Integer> productPrices = null;
	
	public static void createConnection() {
		try { 
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection("jdbc:postgresql:CSE135?user=postgres&password=saving?bay");
			System.out.println("Connection created successfully!!");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void closeConnection() {
		try {
			if(con != null) {
				con.close();
				System.out.println("Connection closed successfully!!");
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void assertNum(int x) throws Exception {
		if(x <= 0) {
			throw new Exception("Negative number provided!!");
		}
	}
	
	private static void resetTablesSequences() {
		System.out.println("Resetting Tables");
		Statement stmt = null;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate(DROP_TABLES);
			stmt.executeUpdate(CREATE_PERSON);
			stmt.executeUpdate(CREATE_CATEGORY);
			stmt.executeUpdate(CREATE_PRODUCT);
			stmt.executeUpdate(CREATE_SHOPPING_CART);
			stmt.executeUpdate(CREATE_PRODUCT_IN_CART);
			
			System.out.println("Tables reset done");
		} catch(Exception e) {
			System.out.println("Tables reset failed!!");
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if(stmt != null) {
					stmt.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void insertCustomers() {
		System.out.println("Inserting Customers");
		PreparedStatement ptst = null;
		try {
			int noOfRows = 0;
			int stateId=1;
			ptst = con.prepareStatement(INSERT_CUSTOMER);
			while(noOfRows < noOfCustomers) {
				ptst.setString(1, "CUST_"+noOfRows);
				stateId = rand.nextInt(50)+1;
				ptst.setString(2, states[stateId]);
				ptst.addBatch();
				noOfRows++;
				
				if(noOfRows % batchSize == 0) {
					ptst.executeBatch();
				}
				
			}
			ptst.executeBatch();
			System.out.println(noOfCustomers + " customers inserted successfully");
		} catch(Exception e) {
			System.out.println("Insert Customers failed!!");
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if(ptst != null) {
					ptst.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void insertCategories() {
		System.out.println("Inserting Categories");
		PreparedStatement ptst = null;
		try {
			int noOfRows = 0;
			ptst = con.prepareStatement(INSERT_CATEGORY);
			while(noOfRows < noOfCategories) {
				ptst.setString(1, "CAT_"+noOfRows);
				ptst.setString(2, "CAT_DESCRIPTION_"+noOfRows);
				ptst.addBatch();
				noOfRows++;
				
				if(noOfRows % batchSize == 0) {
					ptst.executeBatch();
				}
				
			}
			ptst.executeBatch();
			System.out.println(noOfCategories + " categories inserted successfully");
		} catch(Exception e) {
			System.out.println("Insert Categories failed!!");
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if(ptst != null) {
					ptst.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void insertProducts() {
		System.out.println("Inserting Products");
		PreparedStatement ptst = null;
		try {
			int noOfRows = 0;
			int categoryId = 0;
			int price = 0;
			productPrices = new HashMap<Integer, Integer>();
			ptst = con.prepareStatement(INSERT_PRODUCT);
			while(noOfRows < noOfProducts) {
				ptst.setString(1, "SKU_"+noOfRows);
				ptst.setString(2, "PROD_"+noOfRows);
				price = (rand.nextInt(1000)+1)*10;
				ptst.setString(3, String.valueOf(price));
				categoryId = rand.nextInt(noOfCategories)+1;
				ptst.setInt(4, categoryId);
				
				ptst.addBatch();
				
				productPrices.put(noOfRows+1, price);
				noOfRows++;
				
				if(noOfRows % batchSize == 0) {
					ptst.executeBatch();
				}
				
			}
			ptst.executeBatch();
			System.out.println(noOfProducts + " products inserted successfully");
		} catch(Exception e) {
			System.out.println("Insert Products failed!!");
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if(ptst != null) {
					ptst.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void insertSales() {
		System.out.println("Inserting Sales");
		PreparedStatement ptst = null;
		batchSize = 10000;
		try {
			int noOfRows = 1;
			int personId = 0;
			int totalRows = 0;
			//products_in_cart(cart_id, product_id, price, quantity)
			while(noOfRows <= noOfSales) {
				int noOfProductsInCart = rand.nextInt(10)+1;
				int products = 0;
				int productId = 1;
				int quantity = 1;
				int productPrice = 1;
				List<HashMap<Integer, Object>> productsMap = new ArrayList<>();
				int totalPrice = 0;
				while(products < noOfProductsInCart) {
					HashMap<Integer, Object> product = new HashMap<>();
					product.put(1, noOfRows);
					productId = rand.nextInt(noOfProducts)+1;
					product.put(2, productId);
					productPrice = productPrices.get(productId);
					product.put(3, productPrice);
					quantity = rand.nextInt(100)+1;
					product.put(4, quantity);
					productsMap.add(product);
					totalPrice += (Integer) product.get(3) * (Integer) product.get(4);

					products++;
				}
				noOfRows++;

				ptst = con.prepareStatement(INSERT_SHOPPING_CART);
				personId = rand.nextInt(noOfCustomers)+1;
				ptst.setInt(1, personId);
				ptst.setString(2, String.valueOf(totalPrice));
				ptst.setDate(3, new Date(System.currentTimeMillis()));

				ptst.executeUpdate();
				ptst.close();

				ptst = con.prepareStatement(INSERT_PRODUCTS_IN_CART);
				for (int i = 0; i < productsMap.size(); i++) {
					ptst.setInt(1, (Integer) productsMap.get(i).get(1));
					ptst.setInt(2, (Integer) productsMap.get(i).get(2));
					ptst.setString(3, String.valueOf(productsMap.get(i).get(3)));
					ptst.setInt(4, (Integer) productsMap.get(i).get(4));

					ptst.addBatch();
					totalRows++;

					if(totalRows % batchSize == 0) {
						ptst.executeBatch();
					}
				}
				ptst.executeBatch();
				ptst.close();


			}
			
			System.out.println(noOfSales + " sales inserted successfully");
		} catch(Exception e) {
			System.out.println("Insert Sales failed!!");
			e.printStackTrace();
			System.exit(0);
		} finally {
			try {
				if(ptst != null) {
					ptst.close();
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) {
		Scanner s = null;
		try {
			createConnection();
			System.out.println("");
			try{
				s = new Scanner(System.in);

				System.out.println("Provide Data Generator Inputs");
				System.out.println("Provide the number of Customers to be created : ");
				noOfCustomers = s.nextInt();
				assertNum(noOfCustomers);
				
				System.out.println("Provide the number of Categories to be created : ");
				noOfCategories = s.nextInt();
				assertNum(noOfCategories);
				
				System.out.println("Provide the number of Products to be created : ");
				noOfProducts = s.nextInt();
				assertNum(noOfProducts);
				
				System.out.println("Provide the number of Sales to be created : ");
				noOfSales = s.nextInt();
				assertNum(noOfSales);
			} catch(Exception e) {
				System.out.println("Invalid input!!");
				e.printStackTrace();
			}
			
			con.setAutoCommit(false);
			
			resetTablesSequences();
			con.commit();
			
			insertCustomers();
			insertCategories();
			con.commit();
			
			insertProducts();
			con.commit();
			
			insertSales();
			con.commit();
			
		} catch(Exception e) {
			e.printStackTrace();
			try {
				if(con != null) {
					con.rollback();
				}
			} catch(Exception e1) {
				e1.printStackTrace();
			}
		} finally {
			closeConnection();
		}
	}
}
