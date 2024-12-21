package controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import org.jdbi.v3.core.Handle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import dao.AccountDAO;
import dao.OrderDAO;
import database.JDBIConnectionPool;
import model.Account;
import model.Constant;
import model.Order;
import model.OrderDetail;
import model.Product;
import security.OrderJson;
import security.ProductJson;

public class DigitalSign {

	public KeyPair generateKeyPair() throws NoSuchAlgorithmException {
		KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("DSA");
		keyPairGen.initialize(Constant.keySize);
		return keyPairGen.generateKeyPair();
	}

	public String sign(String data, PrivateKey privateKey) throws Exception {
		Signature signature = Signature.getInstance("SHA256withDSA");
		signature.initSign(privateKey);
		signature.update(data.getBytes("UTF-8"));

		byte[] signedData = signature.sign();
		return Base64.getEncoder().encodeToString(signedData);
	}

	public boolean verify(String data, String signedData, PublicKey publicKey) throws Exception {
		Signature signature = Signature.getInstance("SHA256withDSA");
		signature.initVerify(publicKey);
		signature.update(data.getBytes("UTF-8"));

		byte[] signedBytes = Base64.getDecoder().decode(signedData);
		return signature.verify(signedBytes);
	}

	public PublicKey readPublicKey(String pubKeyBase64) {
		byte[] publicKeyBytes = Base64.getDecoder().decode(pubKeyBase64);
		try {
			X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
			KeyFactory keyFactory = KeyFactory.getInstance("DSA");
			PublicKey publicKey = keyFactory.generatePublic(spec);
			return publicKey;
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return null;
	}
public static void main(String[] args) {
	try {
        // Khởi tạo đối tượng DigitalSign
        DigitalSign digitalSign = new DigitalSign();
        Hash hashUtil = new Hash();
        // Bước 1: Tạo cặp khóa
       // KeyPair keyPair = digitalSign.generateKeyPair();
        //PublicKey publicKey = keyPair.getPublic();
        //PrivateKey privateKey = keyPair.getPrivate();

        // Bước 2: Chuẩn bị dữ liệu cần ký
        //String data = "{\"userId\":\"h6sh3YPkLYVXJvZTo/O2\",\"products\":[{\"productId\":97,\"price\":420162,\"discount\":286680,\"priceAfterdiscount\":133482,\"option\":\"Nhỏ hơn 1.8 lít - Đen\",\"quantity\":3},{\"productId\":119,\"price\":2997272,\"discount\":623360,\"priceAfterdiscount\":2373912,\"option\":\"2 vòi - Đen\",\"quantity\":1}],\"dateCreated\":\"2024-12-21\",\"total\":2774358}";
       
        //start
        //
        //
        int orderID =34;
      		System.out.println("this is order id : "+orderID);
      		System.out.println("start");
      		Handle connection = JDBIConnectionPool.get().getConnection();
      		// tạo các biến cho orderjson
              String userId = null ;
              String fullName = null;
              String dateCreated = null; 
              String HashTop =null;
              String publicKeyy = null;
             // String publicKeynewnew = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmSCDVnqw9mO4UYykFcHanWzeaSu5ru8J+lnTnwV8C+06mQAktxb0tLjgbivfVgLMRWHHhSo88nyx0/dLS0zceQJXmRaXE6kkSR56Xhu14qr8xzdxUmHjcg8be5mX+nFfEyWC/2GeufE1q9v4yYi5KK3QWoxj7y8qto928Qjc/Xpo+rtl0xzMdpTatJYQfxPQi+Oe2zHdH3J7mF9Teb2Lh9gkyv0tmj5Csp+mUitU2Vascxd2kQWF3EQqa71BYOp3INV1ho3V8MTw+HdMEWGu6v7gx4UrXkmZee76JrHHV2EbEGAfOmQ98tTn6QwisgTicLMJWVc+NRMHK7abt4upJQIDAQAB";
              String Sign = null;
              boolean isVerifiedTop = false;
      		int total = 0 ;
      		// code kết nối database
      		// start
      		OrderDAO orderDAO = new OrderDAO(connection);
            Order order = orderDAO.getOrderAllByID(orderID);
              JDBIConnectionPool.get().releaseConnection(connection);
              // end
              
              if (order != null) {
              	// truy xuất dữ liệu order 
              	publicKeyy = order.getPublicKey();
              	System.out.println("publicKey : " +publicKeyy);
              	dateCreated = order.getDateCreated().toString();
              	System.out.println("dateCreated : "+dateCreated);
              	HashTop = order.getHash();
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
      		ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS); // Sắp xếp thuộc tính theo thứ tự
            ///
            String canonicalJson = mapper.writeValueAsString(orderJson);
            String canonicalJson2 = mapper.writeValueAsString(orderJson2);
      		// String hashedData = hashUtil.hash(orderJson);
            
            String hashedData = hashUtil.hash(canonicalJson);
            System.out.println("///////////////////////////////////////////////////////////////////////////////");
            System.out.println(hashedData);
            String hashedData2 = hashUtil.hash(canonicalJson2);
            ////
            //
            String orderJsonnew = order.createJson().toString();
            String hashedData3 = hashUtil.hash(orderJsonnew);
        
        
        
        //
        //end
        
        // Bước 3: Tạo chữ ký
       // String signedData = digitalSign.sign(hashedData, privateKey);
        //System.out.println("Original Data: " + data);
        System.out.println("Signed Data: " + Sign);

        // Bước 4: Xác minh chữ ký
    //    boolean isVerified = digitalSign.verify(hashedData, Sign, publicKeyy);
     //   System.out.println("Verification Result: " + (isVerified ? "Valid" : "Invalid"));

        // Bước 5: Chuyển đổi khóa công khai thành chuỗi Base64
    //    String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
      //  System.out.println("Public Key (Base64): " + publicKeyBase64);

        // Bước 6: Đọc lại khóa công khai từ chuỗi Base64
        PublicKey decodedPublicKey = digitalSign.readPublicKey(publicKeyy);

        // Bước 7: Xác minh chữ ký lại với khóa công khai đã đọc
        boolean isVerifiedAgain = digitalSign.verify(hashedData3, Sign, decodedPublicKey);
        System.out.println("Re-verification Result: " + (isVerifiedAgain ? "Valid" : "Invalid"));

    } catch (Exception e) {
        e.printStackTrace();
    }

}
}
