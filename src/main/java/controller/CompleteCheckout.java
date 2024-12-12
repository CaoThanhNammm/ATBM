package controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.jdbi.v3.core.Handle;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import dao.OrderDAO;
import database.JDBIConnectionPool;
import model.Account;
import model.Constant;
import model.Order;
import model.OrderDetail;
import model.ProductModel;
import model.Status;

@MultipartConfig
@WebServlet("/html/complete_checkout")
public class CompleteCheckout extends HttpServlet {
	private Hash sha256;
	private DigitalSign dsa;
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		sha256 = new Hash();
		dsa = new DigitalSign();

		Account account = (Account) req.getSession().getAttribute("account");
		String username = req.getParameter("username");
		String phone = req.getParameter("phone");
		String address = req.getParameter("address");
		Order order = new Order(-1, account, LocalDate.now(), LocalDate.now(), phone, address, new Status(1, null),
				null);
		List<OrderDetail> details = new ArrayList<OrderDetail>();
		Map<ProductModel, Integer> models = (Map<ProductModel, Integer>) req.getSession().getAttribute("checkout");
		for (Map.Entry<ProductModel, Integer> entry : models.entrySet()) {
			ProductModel model = entry.getKey();
			OrderDetail detail = new OrderDetail(order, model, model.getProduct().getPrice(),
					model.getProduct().getDiscount(), entry.getValue());

			details.add(detail);
		}
		
		order.setDetails(details);

		String orderJson = order.createJson().toString();
		String hashedData = hashData(orderJson);

		PrivateKey priKey = readPrivateKey(req);
		String digitalSign = sign(hashedData, priKey);

		if (digitalSign != null) {
			Handle connection = JDBIConnectionPool.get().getConnection();
			OrderDAO orderDAO = new OrderDAO(connection);
			order.setHash(hashedData);
			order.setSign(digitalSign);
			order.setPublicKey(account.getPublicKey());
			orderDAO.addOrder(order);
			JDBIConnectionPool.get().releaseConnection(connection);
			req.getRequestDispatcher("checkoutCompleted.jsp").forward(req, resp);
		}
	}

	private String sign(String hashedData, PrivateKey priKey) {
		try {
			String sign = dsa.sign(hashedData, priKey);
			return sign;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private String hashData(String data) {
		return sha256.hash(data);
	}

	private PrivateKey readPrivateKey(HttpServletRequest req) {
		try {
			Part filePart = req.getPart("file");
			InputStream fileContent = filePart.getInputStream();
			String privateKeyBase64 = new String(fileContent.readAllBytes(), StandardCharsets.UTF_8);
			fileContent.close();
			byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);

			try {
				PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
				KeyFactory keyFactory = KeyFactory.getInstance("DSA");
				PrivateKey privateKey = keyFactory.generatePrivate(spec);
				return privateKey;
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				e.printStackTrace();
			}
		} catch (IOException | ServletException e) {
			e.printStackTrace();
		}

		return null;
	}
}
