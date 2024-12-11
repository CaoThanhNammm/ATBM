package controller;

import java.security.MessageDigest;

import model.Constant;

public class Hash {
	public String hash(String data) {
		try {
			MessageDigest digest = MessageDigest.getInstance(Constant.algoHash);
			byte[] hash = digest.digest(data.getBytes("UTF-8"));

			StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString();

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
