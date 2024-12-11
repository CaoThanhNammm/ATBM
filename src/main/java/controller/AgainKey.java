package controller;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;

@WebServlet("/html/againKey")
public class AgainKey extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DigitalSign sign = new DigitalSign();

	public AgainKey() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			KeyPair key = sign.generateKeyPair();

			PublicKey pubKey = key.getPublic();
			PrivateKey priKey = key.getPrivate();
			
			String priKeyEncoded = Base64.getEncoder().encodeToString(priKey.getEncoded());
			String pubKeyEncoded = Base64.getEncoder().encodeToString(pubKey.getEncoded());
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			// Tạo một đối tượng JSON để chứa publicKey và privateKey
			JsonObject jsonResponse = new JsonObject();
			jsonResponse.addProperty("privateKey", priKeyEncoded);
			jsonResponse.addProperty("publicKey", pubKeyEncoded);

			// Ghi đối tượng JSON vào response
			response.getWriter().write(jsonResponse.toString());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

}
