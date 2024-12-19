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
		//int orderID = Integer.parseInt(req.getParameter("orderId"));
		System.out.println("this is order id : "+orderID);
		System.out.println("start");
		//Handle connection = JDBIConnectionPool.get().getConnection();
		// tạo các biến cho orderjson
        String userId = null ;
        String fullName = null;
        String dateCreated = null; 
        //String Hash=null;
        String publicKey = null;
       // String publicKeynewnew = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmSCDVnqw9mO4UYykFcHanWzeaSu5ru8J+lnTnwV8C+06mQAktxb0tLjgbivfVgLMRWHHhSo88nyx0/dLS0zceQJXmRaXE6kkSR56Xhu14qr8xzdxUmHjcg8be5mX+nFfEyWC/2GeufE1q9v4yYi5KK3QWoxj7y8qto928Qjc/Xpo+rtl0xzMdpTatJYQfxPQi+Oe2zHdH3J7mF9Teb2Lh9gkyv0tmj5Csp+mUitU2Vascxd2kQWF3EQqa71BYOp3INV1ho3V8MTw+HdMEWGu6v7gx4UrXkmZee76JrHHV2EbEGAfOmQ98tTn6QwisgTicLMJWVc+NRMHK7abt4upJQIDAQAB";
        String Sign = null;
        boolean isVerified = false;
		int total = 0 ;
		// code kết nối database
		// start
		OrderDAO orderDAO = new OrderDAO(connection);
       // Order order = orderDAO.getOrderAllByID(orderID);
        JDBIConnectionPool.get().releaseConnection(connection);
        // end
        
        if (order != null) {
        	// truy xuất dữ liệu order 
        	publicKey = order.getPublicKey();
        	System.out.println("publicKey : " +publicKey);
        	dateCreated = order.getDateCreated().toString();
        	System.out.println("dateCreated : "+dateCreated);
        	//Hash = order.getHash();
           // System.out.println("Hash: " + Hash);  // In ra hash
            Sign = order.getSign();
            System.out.println("Sign: " + Sign);  // In ra sign
            // lấy sumprice
            for(OrderDetail detail : order.getDetails()){ 
       		 Product product = detail.getModel().getProduct();
       			int finalPrice = product.getPrice() - product.getDiscount();
       			total += finalPrice * detail.getQuantity();
       			}
            // lấy ra account
            Account acount =  AccountDAO.getMoreInfo(order.getAccount());
            fullName = acount.getFullName();
            System.out.println(fullName);
            userId  = order.getAccount().getId();
            System.out.println(userId);
            // In ra chi tiết đơn hàng
            System.out.println("Order Details:");
            for (OrderDetail detail : order.getDetails()) {
               
                System.out.println("  Quantity: " + detail.getQuantity());
                System.out.println("  Total: " + (detail.getPrice() - detail.getDiscount()) * detail.getQuantity());
            }
        } 
        
        
        
        
        ArrayList<ProductJson> productList = new ArrayList<>();
		for(OrderDetail detail : order.getDetails()) {
			Product product = detail.getModel().getProduct();
			System.out.println(product.getId());
			System.out.println(product.getName().toString());
			System.out.println(product.getPrice());
			System.out.println(product.getDiscount());
			System.out.println("day là option "+detail.getModel().getOptionValue());
			System.out.println(detail.getQuantity());
			
			ProductJson productJson = new ProductJson(
					product.getId(),
					product.getName().toString(),
					product.getPrice(),
					product.getDiscount(),
					product.getPrice() - product.getDiscount(),
					detail.getModel().getOptionValue(),detail.getQuantity());
			productList.add(productJson);
			
	}
		System.out.println(productList.size());
		OrderJson orderJson = new OrderJson(userId, fullName, productList, dateCreated, total);
		OrderJson orderJson2 = new OrderJson(userId, fullName, productList, dateCreated, 10);
		////// chữ key1 điện tử
		//
	
		//
		DigitalSignature digitalSignature = new DigitalSignature();
		// Bước 1: Tạo cặp khóa RSA
        KeyPair keyPair;
		try {
			keyPair = digitalSignature.generateRSAKeyPair();
			PublicKey publicKeyyy = keyPair.getPublic();
	        PrivateKey privateKey = keyPair.getPrivate();

	        // In public và private key dưới dạng Base64
	        String base64PublicKey = Base64.getEncoder().encodeToString(publicKeyyy.getEncoded());
	        String base64PrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());
	        System.out.println("Public Key: " + base64PublicKey);
	        System.out.println("Private Key: " + base64PrivateKey);

	        // Bước 2: Tạo danh sách sản phẩm
	        

	        // Bước 3: Tạo chữ ký cho danh sách sản phẩm
	        String signature = digitalSignature.signData(orderJson, privateKey);
	        System.out.println("Signature: " + signature);

	        // Bước 4: Xác minh chữ ký
	         isVerified = digitalSignature.verifySignature(orderJson, Sign,publicKey );
	        System.out.println("Signature Verified: " + isVerified);
		}  catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	if(isVerified) {
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
