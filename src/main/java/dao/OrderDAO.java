package dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.jdbi.v3.core.Handle;

import database.JDBIConnectionPool;
import model.Account;
import model.Order;
import model.OrderDetail;
import model.Status;

public class OrderDAO {
	private Handle connection;

	public OrderDAO(Handle connection) {
		this.connection = connection;
	}

	public void changeStatus(int orderID, int statusID) {
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE orders SET statusID = ? WHERE id = ?");
			PreparedStatement statement = connection.getConnection().prepareStatement(sql.toString());
			statement.setInt(1, statusID);
			statement.setInt(2, orderID);
			statement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Order> getAll() {
		List<Order> orders = new ArrayList<Order>();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(
					"SELECT o.id, o.userID, o.dateCreated, o.lastUpdated, s.id, s.name AS statusName, SUM((d.price - d.discount)*d.quantity) AS totalPrice ");
			sql.append("FROM orders o ");
			sql.append("JOIN order_details d ON o.id = d.orderID ");
			sql.append("JOIN order_status s ON o.statusID = s.id ");
			sql.append("GROUP BY o.id, o.userID, o.dateCreated, o.lastUpdated, s.id, s.name ");
			sql.append("ORDER BY o.lastUpdated DESC");
			PreparedStatement statement = connection.getConnection().prepareStatement(sql.toString());
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				int orderID = rs.getInt(1);
				String userID = rs.getString(2);
				LocalDate dateCreated = rs.getDate(3).toLocalDate();
				LocalDate lastUpdated = rs.getDate(4).toLocalDate();
				Status status = new Status(rs.getInt(5), rs.getString(6));
				int totalPrice = rs.getInt(7);
				Order order = new Order(orderID, AccountDAO.getMoreInfo(new Account(userID, null, null, null, null)),
						dateCreated, lastUpdated, null, null, status, null);
				order.setTotalPrice(totalPrice);
				orders.add(order);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return orders;
	}

	public List<Order> getByStatus(int statusID) {
		List<Order> orders = new ArrayList<Order>();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(
					"SELECT o.id, o.userID, o.dateCreated, o.lastUpdated, s.id, s.name AS statusName, SUM((d.price - d.discount)*d.quantity) AS totalPrice ");
			sql.append("FROM orders o ");
			sql.append("JOIN order_details d ON o.id = d.orderID ");
			sql.append("JOIN order_status s ON o.statusID = s.id ");
			sql.append("WHERE s.id = ? ");
			sql.append("GROUP BY o.id, o.userID, o.dateCreated, o.lastUpdated, s.id, s.name ");
			sql.append("ORDER BY o.lastUpdated DESC");
			PreparedStatement statement = connection.getConnection().prepareStatement(sql.toString());
			statement.setInt(1, statusID);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				System.out.println(statusID);
				int orderID = rs.getInt(1);
				String userID = rs.getString(2);
				LocalDate dateCreated = rs.getDate(3).toLocalDate();
				LocalDate lastUpdated = rs.getDate(4).toLocalDate();
				Status status = new Status(rs.getInt(5), rs.getString(6));
				int totalPrice = rs.getInt(7);
				Order order = new Order(orderID, AccountDAO.getMoreInfo(new Account(userID, null, null, null, null)),
						dateCreated, lastUpdated, null, null, status, null);
				order.setTotalPrice(totalPrice);
				orders.add(order);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return orders;
	}

	public void addOrder(Order order) {
		try {
			String userID = order.getAccount().getId();
			String dateCreated = order.getDateCreated().toString();
			String lastUpdated = order.getLastUpdated().toString();
			String phone = order.getPhone();
			String address = order.getAddress();
			int statusID = order.getStatus().getId();
			String sign = order.getSign();
			String hash = order.getHash();
			String publicKey = order.getPublicKey();

			StringBuilder sql = new StringBuilder();
			sql.append(
					"INSERT INTO orders(userID, dateCreated, lastUpdated, phone, address, statusID, hash, sign, publicKey) ");
			sql.append("VALUES(?,?,?,?,?,?,?,?,?)");
			PreparedStatement statement = connection.getConnection().prepareStatement(sql.toString());
			statement.setString(1, userID);
			statement.setString(2, dateCreated);
			statement.setString(3, lastUpdated);
			statement.setString(4, phone);
			statement.setString(5, address);
			statement.setInt(6, statusID);
			statement.setString(7, hash);
			statement.setString(8, sign);
			statement.setString(9, publicKey);
			statement.executeUpdate();
			statement = connection.getConnection().prepareStatement("SELECT MAX(id) FROM orders");
			ResultSet resultSet = statement.executeQuery();
			int orderID = -1;
			if (resultSet.next()) {
				orderID = resultSet.getInt(1);
			}

			for (OrderDetail detail : order.getDetails()) {
				StringBuilder sqlDetail = new StringBuilder();
				sqlDetail.append("INSERT INTO order_details(orderID, modelID, price, discount, quantity) ");
				sqlDetail.append("VALUES(?,?,?,?,?)");
				statement = connection.getConnection().prepareStatement(sqlDetail.toString());
				statement.setInt(1, orderID);
				statement.setInt(2, detail.getModel().getId());
				statement.setInt(3, detail.getPrice());
				statement.setInt(4, detail.getDiscount());
				statement.setInt(5, detail.getQuantity());
				statement.executeUpdate();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<Order> getAccountOrders(Account account) {
		List<Order> orders = new ArrayList<Order>();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(
					"SELECT o.id, o.dateCreated, o.lastUpdated, s.id, s.name AS statusName, SUM((d.price - d.discount)*d.quantity) AS totalPrice ");
			sql.append("FROM orders o ");
			sql.append("JOIN order_details d ON o.id = d.orderID ");
			sql.append("JOIN order_status s ON o.statusID = s.id ");
			sql.append("WHERE o.userID = ? ");
			sql.append("GROUP BY o.id, o.dateCreated, o.lastUpdated, s.name ");
			sql.append("ORDER BY o.lastUpdated DESC");
			PreparedStatement statement = connection.getConnection().prepareStatement(sql.toString());
			statement.setString(1, account.getId());
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				int orderID = rs.getInt(1);
				LocalDate dateCreated = rs.getDate(2).toLocalDate();
				LocalDate lastUpdated = rs.getDate(3).toLocalDate();
				Status status = new Status(rs.getInt(4), rs.getString(5));
				int totalPrice = rs.getInt(6);
				Order order = new Order(orderID, account, dateCreated, lastUpdated, null, null, status, null);
				order.setTotalPrice(totalPrice);
				orders.add(order);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return orders;
	}

	public Order getOrderByID(int orderID) {
		Order order = new Order(orderID, null, null, null, null, null, null, null);
		List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT o.dateCreated, o.lastUpdated, s.name AS statusName, ");
			sql.append("d.modelID, d.price, d.discount, d.quantity, o.userID, s.id, o.hash, o.sign, o.publicKey ");
			sql.append("FROM orders o ");
			sql.append("JOIN order_details d ON o.id = d.orderID ");
			sql.append("JOIN order_status s ON o.statusID = s.id ");
			sql.append("WHERE o.id = ? ");
			PreparedStatement statement = connection.getConnection().prepareStatement(sql.toString());
			statement.setInt(1, orderID);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				LocalDate dateCreated = rs.getDate(1).toLocalDate();
				LocalDate lastUpdated = rs.getDate(2).toLocalDate();
				Status status = new Status(rs.getInt(9), rs.getString(3));
				int modelID = rs.getInt(4);
				int price = rs.getInt(5);
				int discount = rs.getInt(6);
				int quantity = rs.getInt(7);
				Account account = new Account(rs.getString(8), null, null, null, null);
				String hash = rs.getString(10);
				String sign = rs.getString(11);
				String publicKey = rs.getString(12);

				order.setAccount(account);
				order.setDateCreated(dateCreated);
				order.setLastUpdated(lastUpdated);
				order.setStatus(status);
				order.setHash(hash);
				order.setSign(sign);
				order.setPublicKey(publicKey);

				ProductModelDAO pmDAO = new ProductModelDAO(connection);
				pmDAO.getModelByID(modelID);
				OrderDetail detail = new OrderDetail(order, pmDAO.getModelByID(modelID), price, discount, quantity);
				orderDetails.add(detail);
			}
			order.setDetails(orderDetails);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return order;
	}
//
	
	
	public Order getOrderAllByID(int orderID) {
	    Order order = new Order(orderID, null, null, null, null, null, null, null, null, null, null); // Cập nhật constructor với hash, sign và publicKey
	    List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
	    
	    try {
	        // Cập nhật câu truy vấn SQL để lấy giá trị của hash, sign và publicKey
	        StringBuilder sql = new StringBuilder();
	        sql.append("SELECT o.dateCreated, o.lastUpdated, s.name AS statusName, ");
	        sql.append("d.modelID, d.price, d.discount, d.quantity, o.userID, s.id, ");
	        sql.append("o.hash, o.sign, o.publicKey "); // Thêm publicKey vào SELECT
	        sql.append("FROM orders o ");
	        sql.append("JOIN order_details d ON o.id = d.orderID ");
	        sql.append("JOIN order_status s ON o.statusID = s.id ");
	        sql.append("WHERE o.id = ? ");
	        
	        PreparedStatement statement = connection.getConnection().prepareStatement(sql.toString());
	        statement.setInt(1, orderID);
	        ResultSet rs = statement.executeQuery();
	        
	        while (rs.next()) {
	            LocalDate dateCreated = rs.getDate(1).toLocalDate();
	            LocalDate lastUpdated = rs.getDate(2).toLocalDate();
	            Status status = new Status(rs.getInt(9), rs.getString(3));
	            int modelID = rs.getInt(4);
	            int price = rs.getInt(5);
	            int discount = rs.getInt(6);
	            int quantity = rs.getInt(7);
	            Account account = new Account(rs.getString(8), null, null, null, null);
	            String hash = rs.getString(10);  // Lấy giá trị của hash
	            String sign = rs.getString(11);  // Lấy giá trị của sign
	            String publicKey = rs.getString(12);  // Lấy giá trị của publicKey

	            // Khởi tạo đối tượng Order và thiết lập các thuộc tính
	            order.setAccount(account);
	            order.setDateCreated(dateCreated);
	            order.setLastUpdated(lastUpdated);
	            order.setStatus(status);
	            order.setHash(hash);  // Thiết lập hash cho đối tượng Order
	            order.setSign(sign);  // Thiết lập sign cho đối tượng Order
	            order.setPublicKey(publicKey);  // Thiết lập publicKey cho đối tượng Order

	            // Truy xuất chi tiết đơn hàng và thêm vào danh sách
	            ProductModelDAO pmDAO = new ProductModelDAO(connection);
	            pmDAO.getModelByID(modelID);
	            OrderDetail detail = new OrderDetail(order, pmDAO.getModelByID(modelID), price, discount, quantity);
	            orderDetails.add(detail);
	        }
	        
	        order.setDetails(orderDetails);
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    
	    return order;
	}

	//
	public long[] getGiveVai() {
		// TODO Auto-generated method stub
		List<Order> o1 = connection
				.select("SELECT id FROM orders WHERE statusID=1 AND dateCreated=?", Date.valueOf(LocalDate.now()))
				.mapToBean(Order.class).list();

		List<Order> o2 = connection
				.select("SELECT id FROM orders WHERE statusID=1 AND dateCreated >= ? AND dateCreated <= ?",
						Date.valueOf(LocalDate.now().minus(1, ChronoUnit.WEEKS)), Date.valueOf(LocalDate.now()))
				.mapToBean(Order.class).list();

		List<Order> o3 = connection
				.select("SELECT id FROM orders WHERE statusID=1 AND dateCreated >= ? AND dateCreated <= ?",
						Date.valueOf(LocalDate.now().minus(1, ChronoUnit.MONTHS)), Date.valueOf(LocalDate.now()))
				.mapToBean(Order.class).list();

		long[] count = new long[3];
		count[0] = o1.size();
		count[1] = o2.size();
		count[2] = o3.size();

		return count;
	}

	public long[] getApproach() {
		List<Order> o1 = connection.select("SELECT id FROM orders WHERE dateCreated=?", Date.valueOf(LocalDate.now()))
				.mapToBean(Order.class).list();

		List<Order> o2 = connection
				.select("SELECT id FROM orders WHERE dateCreated >= ? AND dateCreated <= ?",
						Date.valueOf(LocalDate.now().minus(1, ChronoUnit.WEEKS)), Date.valueOf(LocalDate.now()))
				.mapToBean(Order.class).list();

		List<Order> o3 = connection
				.select("SELECT id FROM orders WHERE dateCreated >= ? AND dateCreated <= ?",
						Date.valueOf(LocalDate.now().minus(1, ChronoUnit.MONTHS)), Date.valueOf(LocalDate.now()))
				.mapToBean(Order.class).list();

		long[] count = new long[3];
		count[0] = o1.size();
		count[1] = o2.size();
		count[2] = o3.size();

		return count;
	}
	public static void main(String[] args) {
		
		Handle connection = JDBIConnectionPool.get().getConnection(); // Thay thế bằng cách khởi tạo đúng kết nối của bạn
        
        // Tạo đối tượng OrderDAO và gọi getOrderByID để lấy đơn hàng theo ID
        OrderDAO orderDAO = new OrderDAO(connection);
        int orderID = 33;  // Thay đổi ID đơn hàng mà bạn muốn kiểm tra
        Order order = orderDAO.getOrderAllByID(orderID);
        JDBIConnectionPool.get().releaseConnection(connection);
        // In ra tất cả các thuộc tính của đối tượng Order
        if (order != null) {
            System.out.println("Order ID: " + order.getId());
            System.out.println("this is order public key " +order.getPublicKey());
            System.out.println("Hash: " + order.getHash());  // In ra hash
            System.out.println("Sign: " + order.getSign());  // In ra sign
            
            // In ra chi tiết đơn hàng
            System.out.println("Order Details:");
            for (OrderDetail detail : order.getDetails()) {
                System.out.println("  Model ID: " + detail.getModel().getId());
                System.out.println("  Price: " + detail.getPrice());
                System.out.println("  Discount: " + detail.getDiscount());
                System.out.println("  Quantity: " + detail.getQuantity());
                System.out.println("  Total: " + (detail.getPrice() - detail.getDiscount()) * detail.getQuantity());
            }
        } else {
            System.out.println("No order found with ID: " + orderID);
        }
    }
		
		
	
	
}
