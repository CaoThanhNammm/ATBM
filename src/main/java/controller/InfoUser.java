package controller;

import static database.TableUsers.ADDRESS;
import static database.TableUsers.DOB;
import static database.TableUsers.EMAIL;
import static database.TableUsers.FULL_NAME;
import static database.TableUsers.GENDER;
import static database.TableUsers.ID;
import static database.TableUsers.PASSWORD;
import static database.TableUsers.PHONE;
import static database.TableUsers.PUBLIC_KEY;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.jdbi.v3.core.Handle;

import dao.AccountDAO;
import dao.OrderDAO;
import database.JDBIConnectionPool;
import model.Account;
import model.Encrypt;
import model.Gender;
import model.Order;

@MultipartConfig
@WebServlet("/html/infoUser")
public class InfoUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DigitalSign dsa;

	public InfoUser() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String info = req.getParameter("info");
		Account ac = (Account) req.getSession().getAttribute("account");
		Account acInfo = AccountDAO.getMoreInfo(ac);
		if (ac != null) {
			Handle connection = JDBIConnectionPool.get().getConnection();
			List<Order> orders = new OrderDAO(connection).getAccountOrders(ac);
			JDBIConnectionPool.get().releaseConnection(connection);
			req.getSession().setAttribute("orders", orders);
			if (info == null)
				info = "";
			switch (info) {
			case "info":
				String name = req.getParameter("name");
				String email = req.getParameter("email");
				String phone = req.getParameter("phone");
				String address = req.getParameter("address");
				String gender = req.getParameter("gender");
				String dob = req.getParameter("dob");

				if (isNotNull(name, email, phone, gender, dob) && AccountDAO.isEmail(email)
						&& AccountDAO.isPhoneNumber(phone)) {
					if (AccountDAO.updateAccount(ID, ac.getId(), FULL_NAME, name)
							&& AccountDAO.updateAccount(ID, ac.getId(), EMAIL, email)
							&& AccountDAO.updateAccount(ID, ac.getId(), PHONE, phone)
							&& AccountDAO.updateAccount(ID, ac.getId(), ADDRESS, address)
							&& AccountDAO.updateAccount(ID, ac.getId(), GENDER, Gender.getGender(gender).getId() + "")
							&& AccountDAO.updateAccount(ID, ac.getId(), DOB, dob)) {
						acInfo.setFullName(name);
						acInfo.setEmail(email);
						acInfo.setPhone(phone);
						acInfo.setAddress(address);
						acInfo.setGender(Gender.getGender(gender));
						acInfo.setDob(LocalDate.parse(dob));
						req.getSession().setAttribute("accountInfo", acInfo);
						req.getRequestDispatcher("user.jsp?status=success&field=info").forward(req, resp);
					} else {
						req.getRequestDispatcher("user.jsp?status=failed-2&field=info").forward(req, resp);
					}
				} else {
					req.getRequestDispatcher("user.jsp?status=failed-0&field=info").forward(req, resp);
				}
				break;
			case "pass":
				// Xu ly
				String oldPass = req.getParameter("oldPassword");
				String newPass = req.getParameter("newPassword");
				String confirmPass = req.getParameter("confirmPassword");
				if (isNotNull(oldPass, newPass, confirmPass)) {
					if (newPass.equals(confirmPass) && AccountDAO.getAccount(acInfo.getEmail(), oldPass) != null) {
						if (AccountDAO.updateAccount(ID, ac.getId(), PASSWORD, Encrypt.encrypt(newPass))) {
							req.getRequestDispatcher("user.jsp?status=success&field=pass").forward(req, resp);
						} else {
							req.getRequestDispatcher("user.jsp?status=failed-2&field=pass").forward(req, resp);
							;
						}
					} else {
						req.getRequestDispatcher("user.jsp?status=failed-1&field=pass").forward(req, resp);
						;
					}
				} else {
					req.getRequestDispatcher("user.jsp?status=failed-0&field=pass").forward(req, resp);
					;
				}
				break;
			case "createNewKey":
				dsa = new DigitalSign();

				String confirmPassword = req.getParameter("confirmPassword");
				Account hasAc = AccountDAO.getAccount("email-" + acInfo.getEmail(), confirmPassword);

				if (hasAc == null) {
					System.out.println("sai mat khau");
					req.getRequestDispatcher("user.jsp?status=failed-1&field=createNewKey").forward(req, resp);
				} else {
					try {
						KeyPair keyPair = dsa.generateKeyPair();

						PublicKey pubKey = keyPair.getPublic();
						PrivateKey priKey = keyPair.getPrivate();

						// lưu publickey xuống table users
						String userId = ac.getId();
						AccountDAO.updateAccount(ID, userId, PUBLIC_KEY,
								Base64.getEncoder().encodeToString(pubKey.getEncoded()));
						// cho phép người dùng chọn thư mục lưu privateKey
						writePrivateKey(Base64.getEncoder().encodeToString(priKey.getEncoded()), req);
						req.getRequestDispatcher("user.jsp?status=success&field=createNewKey").forward(req, resp);
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				break;
			default:
				req.getSession().setAttribute("accountInfo", acInfo);
				req.getRequestDispatcher("user.jsp").forward(req, resp);
				break;
			}
		} else {
			resp.sendRedirect("../index/index.jsp");
		}
	}

	// Kiểm tra có trường null không
	public boolean isNotNull(Object... objs) {
		for (Object obj : objs) {
			if (obj == null)
				return false;
		}
		return true;
	}

	private void writePrivateKey(String privateKeyBase64, HttpServletRequest req) {

		// Lấy phần file từ yêu cầu
		String fileName = req.getParameter("filename");
		String folderName = "C:/privatekey";

		File file = new File(folderName);
		file.mkdir();

		try (FileWriter writer = new FileWriter(folderName + "/" + fileName)) {
			writer.write(privateKeyBase64);
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
