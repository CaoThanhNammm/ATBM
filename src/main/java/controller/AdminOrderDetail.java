package controller;

import java.io.File;
import java.io.IOException;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;

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
import security.DigitalSignature;
import security.OrderJson;
import security.ProductJson;

@WebServlet("/html/adminOrderDetail")
public class AdminOrderDetail extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int orderID = Integer.valueOf(req.getParameter("orderID"));
        String verificationResult = null;
        DigitalSign digitalSign = new DigitalSign();
        Hash hashUtil = new Hash();
      
        
		Handle connection = JDBIConnectionPool.get().getConnection();
		Order order = new OrderDAO(connection).getOrderByID(orderID);
		JDBIConnectionPool.get().releaseConnection(connection);
		for(OrderDetail detail : order.getDetails()) {
			Product p = detail.getModel().getProduct();
			String realPath = req.getServletContext().getRealPath("/image/product/" + p.getId());
	        File productImagePath = new File(realPath);
	        p.setImgs(productImagePath.list()[0]);
		}
		////////////////
		///
		///
		///
		///
		///
		///
		///
		String publicKey = null;
		String Sign = null;
		boolean isVerifiedAgain = false;
        if (order != null) {
        	try {
        	// truy xuất dữ liệu order 
        	publicKey = order.getPublicKey();
        	System.out.println("publicKey : " +publicKey);
        	
        	//Hash = order.getHash();
           // System.out.println("Hash: " + Hash);  // In ra hash
            Sign = order.getSign();
            System.out.println("Sign: " + Sign);  // In ra sign
            String orderJson = order.createJson().toString();
            String hashedData = hashUtil.hash(orderJson);
         // Bước 6: Đọc lại khóa công khai từ chuỗi Base64
            PublicKey decodedPublicKey = digitalSign.readPublicKey(publicKey);

            // Bước 7: Xác minh chữ ký lại với khóa công khai đã đọc
            isVerifiedAgain = digitalSign.verify(hashedData, Sign, decodedPublicKey);
            System.out.println("Re-verification Result: " + (isVerifiedAgain ? "Valid" : "Invalid"));}
        	catch (Exception e) {
                e.printStackTrace();
            }
            
        }
           
		
        
	if(isVerifiedAgain) {
		verificationResult = "Xác thực đơn hàng thành công";
	}else {
		verificationResult = "Đơn hàng đã được chỉnh sửa";
	}
	
		
		
		
		
		
		///
		///
		///
		req.setAttribute("verificationResult", verificationResult);
		req.setAttribute("order", order);
		req.setAttribute("acInfo", AccountDAO.getMoreInfo(order.getAccount()));
		req.getRequestDispatcher("orderAdminDetail.jsp").forward(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int statusID = Integer.valueOf(req.getParameter("statusID"));
		int orderID = Integer.valueOf(req.getParameter("id"));
		Handle connection = JDBIConnectionPool.get().getConnection();
		new OrderDAO(connection).changeStatus(orderID, statusID);
		JDBIConnectionPool.get().releaseConnection(connection);
		resp.sendRedirect("orderAdmin");
	}
}
