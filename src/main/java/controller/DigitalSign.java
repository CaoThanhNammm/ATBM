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
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;

import model.Constant;

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
}
