package controller;

import java.io.File;
import java.io.IOException;
import java.security.PublicKey;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdbi.v3.core.Handle;

import dao.OrderDAO;
import database.JDBIConnectionPool;
import model.Account;
import model.Constant;
import model.Order;
import model.OrderDetail;
import model.Product;

@WebServlet("/html/orderDetail")
public class OrderDetailServlet extends HttpServlet {
	private Hash sha256;
	private DigitalSign dsa;

	public OrderDetailServlet() {
		super();
		sha256 = new Hash();
		dsa = new DigitalSign();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Account account = (Account) request.getSession().getAttribute("account");
		int orderID = Integer.valueOf(request.getParameter("orderID"));
		Handle connection = JDBIConnectionPool.get().getConnection();
		Order order = new OrderDAO(connection).getOrderByID(orderID);
		JDBIConnectionPool.get().releaseConnection(connection);

		if (account == null || !order.getAccount().getId().equals(account.getId())) {
			order = null;
		} else {
			for (OrderDetail detail : order.getDetails()) {
				Product p = detail.getModel().getProduct();
				String realPath = request.getServletContext().getRealPath("/image/product/" + p.getId());
				File productImagePath = new File(realPath);
				p.setImgs(productImagePath.list()[0]);
			}
		}
		PublicKey pubKey = dsa.readPublicKey(order.getPublicKey());
		request.setAttribute("isConfirm", isConfirmOrder(order, pubKey));
		request.setAttribute("order1", order);
		request.getRequestDispatcher("orderDetail.jsp").forward(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	private boolean isConfirmOrder(Order order, PublicKey pubKey) {
		try {
			String jsonOrder = order.createJson().toString();
			String hashedData = sha256.hash(jsonOrder);

			boolean verify = dsa.verify(hashedData, order.getSign(), pubKey);

			return order.getHash().equals(hashedData) && verify;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
}
