package controller;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jdbi.v3.core.Handle;

import dao.AccountDAO;
import dao.OrderDAO;
import database.JDBIConnectionPool;
import model.Account;
import model.Order;
import model.OrderDetail;
import model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
/**
 * Servlet implementation class VerifyOrderServlet
 */
@WebServlet("/html/VerifyOrderServlet")
public class VerifyOrderServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VerifyOrderServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	System.out.println("toi o get");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		int orderID = Integer.parseInt(req.getParameter("orderId"));
		System.out.println(orderID);
		System.out.println("start");
		Handle connection = JDBIConnectionPool.get().getConnection();
		//int sumPrice = 0;
		//StringBuilder productData = new StringBuilder();
		//StringBuilder data = new StringBuilder();
		OrderDAO orderDAO = new OrderDAO(connection);
        Order order = orderDAO.getOrderAllByID(orderID);
        JDBIConnectionPool.get().releaseConnection(connection);
        
        if (order != null) {
            System.out.println("Order ID: " + order.getId());
            
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
        } 
        
        
        
        
        
		//for(OrderDetail detail : order.getDetails()) {
			//Product product = detail.getModel().getProduct();
//			int finalPrice = product.getPrice() - product.getDiscount();
//			productData.append(product.getId()).append(",").append(finalPrice)
//			 .append(",").append(detail.getQuantity()).append(",")
//			 .append("|");
			
//		}
		
        
		
//		for(OrderDetail detail : order.getDetails()){ 
//		 Product product = detail.getModel().getProduct();
//			int finalPrice = product.getPrice() - product.getDiscount();
//			sumPrice += finalPrice * detail.getQuantity();
//			}
//		
	//	Account ac =  AccountDAO.getMoreInfo(order.getAccount());
	//	System.out.println("this is create at"+order.getDateCreated().toString());
		
		//System.out.println("this is id ac "+order.getAccount().getId());
	//	System.out.println("this is ac name "+ ac.getFullName());
		
		
		
		System.out.println("dddddddddddddddddddddddddddddddddddddd");
//		Handle connection = JDBIConnectionPool.get().getConnection();
//		
//		Order order = new OrderDAO(connection).getOrderByID(orderId);
//		
//		System.out.println("this is toal " + order.getTotalPrice());
//		System.out.println("this is order id   "+order.getId());
//		
//		Account ac = AccountDAO.getMoreInfo(order.getAccount());
//		System.out.println();
//		System.out.println("this is ac email   : "+ ac.getEmail());
//		System.out.println("this is ac phone   : "+ ac.getPhone());
//		System.out.println("this is ac pass   : "+ ac.getPass());
//		System.out.println("this is name : " +ac.getFullName());
//		JDBIConnectionPool.get().releaseConnection(connection);
//		for(OrderDetail detail : order.getDetails()) {
//			Product p = detail.getModel().getProduct();
//			System.out.println(p.getId());
//			System.out.println(p.getName());
//			System.out.println(p.getPrice());
//			System.out.println(p.getAmountSold());
//		}
//		
		System.out.println("end");
		
		 // Tạo thông báo xác thực
        String verificationResult = "Xác thực đơn hàng thành công";

        // Lưu thông báo vào session để sử dụng sau
        // Lấy đối tượng HttpSession từ request
        HttpSession session = req.getSession();

        // Lưu đối tượng Order vào session
        session.setAttribute("verificationResult", verificationResult); 
       
		
		 response.sendRedirect("adminOrderDetail?orderID=" + orderID);
	}

}
