package controller.filter;

import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;

import controller.DigitalSign;

@WebFilter({ "/html/confirm.jsp" })
public class RegisterGenkey extends HttpFilter implements Filter {
	private DigitalSign sign;
	private static final long serialVersionUID = 1L;

	public RegisterGenkey() {
		super();
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		sign = new DigitalSign();
		try {
			KeyPair key = sign.generateKeyPair();
			
			PrivateKey priKey = key.getPrivate();
			PublicKey pubKey = key.getPublic();
			
			request.setAttribute("privateKey", Base64.getEncoder().encodeToString(priKey.getEncoded()));
			request.setAttribute("publicKey", Base64.getEncoder().encodeToString(pubKey.getEncoded()));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {

	}

}
