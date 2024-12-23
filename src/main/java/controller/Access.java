package controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import dao.AccountDAO;
import database.TableUsers;
import model.Account;
import model.AccountRole;
import model.AccountStatus;
import model.Constant;
import model.Encrypt;
import model.Gender;
import model.VerifyEmail;
import service.MailService;

/**
 * Create: Nguyễn Khải Nam Note: Xử lý các tác vụ từ client đến db Date:
 * 24/11/2023 Servlet implementation class access
 */
@WebServlet("/html/access")
public class Access extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static int minute = 3;
	private static long exist = 60 * minute;

	// Dùng cho việc phân tích
	private long countUser = 0;
	private long countSign = 0;

	private static String[] subject = { minute + " PHÚT - MÃ BẢO MẬT - ĐĂNG KÝ",
			minute + " PHÚT - MÃ BẢO MẬT - ĐẶT LẠI MẬT KHẨU", minute + " PHÚT - THIẾT LẬP MẬT KHẨU MỚI" };
	private static String[] mess = {
			"Xin chào,\nChúng tôi là n2q, rất cảm ơn bạn đã lựa chọn chúng tôi.\nĐây là mã bảo mật của bạn: ",
			"Chào mừng trở lại,\nCó vẻ như bạn gặp sự cố đăng nhập.\n Đây là mã bảo mật của bạn: ",
			"Xin chào, \nChúng tôi là n2q, đây là mật khẩu mới của bạn: " };

	// Dùng cho việc đăng ký
	private static VerifyEmail verify;

	public Access() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");

		String access = request.getParameter("access");
		HttpSession session = request.getSession();

		if (access == null)
			access = "";

		request.getSession().setAttribute("count-user", countUser);
		request.getSession().setAttribute("count-sign", countSign);

		switch (access) {
		case "login": {
			String name = request.getParameter("name");
			if (AccountDAO.isPhoneNumber(name) || AccountDAO.isEmail(name)) {
				String prefix = (AccountDAO.isPhoneNumber(name) ? "phone" : "email") + "-";
				Account ac = AccountDAO.getAccount(prefix + name, request.getParameter("password"));
				if (ac != null) {
					if (ac.getStatus().isAction()) {
						session.setAttribute("account", ac);
						if (ac.getRole().isAdmin()) {
							Account moreInfo = AccountDAO.getMoreInfo(ac);
							request.getSession().setAttribute("moreInfo", moreInfo);
							response.sendRedirect("overviewAdmin.jsp");
						} else {
							if (countSign == 0) {
								resest(2592000);
							}
							countSign += 1;
							response.sendRedirect("../index/index.jsp");
						}
					} else {
						request.getRequestDispatcher("login.jsp?status=failed-0").forward(request, response);
					}
				} else {
					request.getRequestDispatcher("login.jsp?status=failed").forward(request, response);
				}
			} else {
				request.getRequestDispatcher("login.jsp?status=failed").forward(request, response);
			}
		}
			break;
		case "register": {
			String name = request.getParameter("name");
			String phone = request.getParameter("phone");
			String email = request.getParameter("email");
			String address = request.getParameter("address");
			String dob = request.getParameter("date");
			String[] gender = request.getParameterValues("gender");
			String pass = request.getParameter("pass");
			String rePass = request.getParameter("rePass");

			if (isNotNull(name, phone, email, pass, rePass, dob, gender)) {
				if (pass.equals(rePass) && AccountDAO.isEmail(email) && AccountDAO.isPhoneNumber(phone)) {
					if (AccountDAO.hasAccount("", email, phone)) {
						request.getRequestDispatcher("register.jsp?status=failed-1").forward(request, response);
					} else {
						String code = MailService.sendEmail(email, subject[0], mess[0], null);
						String id = AccountDAO.generateID(email, phone);

						Account ac = new Account(id, email, phone, pass, name, Gender.getGender(gender[0]),
								LocalDate.parse(dob), AccountRole.getRole(1), address, AccountStatus.getStatus(2));

						verify = new VerifyEmail(Encrypt.encrypt(code), ac, exist);

						if (code != null && !code.isBlank()) {
							request.getSession().setAttribute("statusConfirm", "register");
							response.sendRedirect("confirm.jsp");
						} else {
							// Không xác định được lỗi do email hay do server, tạm thời cứ xem là do client
							request.getRequestDispatcher("register.jsp?status=failed-0").forward(request, response);
						}
					}
				} else {
					request.getRequestDispatcher("register.jsp?status=failed-0").forward(request, response);
				}
			} else {
				request.getRequestDispatcher("register.jsp?status=failed").forward(request, response);
			}
		}
			break;
		case "confirm":
			if (verify == null) {
				request.getRequestDispatcher("login.jsp?status=failed").forward(request, response);
			} else {

				String input = Encrypt.encrypt((String) request.getParameter("verificationCode"));
				String status = (String) request.getSession().getAttribute("statusConfirm");
				String publicKeyBase64 = request.getParameter("publicKey");

				if (verify.isCode(input)) {
					if (status == null) {
						verify = null;
						response.sendRedirect("login.jsp");
					} else if (status.equals("forget")) {
						String mainPass = Encrypt.generateCode(12);
						String pass = Encrypt.encrypt(mainPass);
						String email = (String) request.getSession().getAttribute("email");
						if (AccountDAO.updateAccount(TableUsers.EMAIL, email, TableUsers.PASSWORD, pass)) {
							if (MailService.sendEmail(email, subject[2], mess[2], mainPass) != null) {
								request.getRequestDispatcher("login.jsp?status=success-1").forward(request, response);
							} else {
								request.getRequestDispatcher("forget.jsp?status=failed").forward(request, response);
							}
						} else {
							request.getRequestDispatcher("forget.jsp?status=failed").forward(request, response);
						}
					} else if (status.equals("register")) {
						// Đăng ký
						verify.getAc().setPublicKey(publicKeyBase64);
						int count = AccountDAO.insertAccount(verify.getAc());
						if (count > 0) {
							if (countUser == 0) {
								resest(2592000); // 30 days
							}
							countUser += 1;
							request.getRequestDispatcher("login.jsp?status=success").forward(request, response);
						} else {
							request.getRequestDispatcher("register.jsp?status=failed").forward(request, response);
						}
					} else {
						// Đổi mk của admin
						request.getRequestDispatcher("overviewAdmin.jsp?status=change").forward(request, response);
					}
					request.getSession().removeAttribute("email");
					request.getSession().removeAttribute("statusConfirm");
				} else {
					request.getRequestDispatcher("confirm.jsp?status=failed").forward(request, response);
				}
			}
			break;
		case "forget":
			String email = request.getParameter("email");
			if (AccountDAO.hasAccount("", email, "")) {
				String code = MailService.sendEmail(email, subject[1], mess[1], null);
				Account ac = new Account(email);
				verify = new VerifyEmail(Encrypt.encrypt(code), ac, exist);
				request.getSession().setAttribute("email", email);
				request.getSession().setAttribute("statusConfirm", "forget");
				response.sendRedirect("confirm.jsp");
			} else {
				request.getRequestDispatcher("forget.jsp?status=failed").forward(request, response);
			}
			break;

		case "admin": {
			Account admin = (Account) request.getSession().getAttribute("account");
			Account moreInfo = (Account) request.getSession().getAttribute("moreInfo");
			String codeAdmin = MailService.sendEmail(moreInfo.getEmail(), subject[2], "Đây là mã bảo mật: ", null);
			verify = new VerifyEmail(Encrypt.encrypt(codeAdmin), admin, exist);
			request.getSession().setAttribute("email", moreInfo.getEmail());
			request.getSession().setAttribute("statusConfirm", "resest-pass-admin");
			response.sendRedirect("confirm.jsp");
		}
			break;
		default:
			session.removeAttribute("account");
			response.sendRedirect("../index/index.jsp");
			break;
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

	private void resest(long time) {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		scheduler.schedule(() -> {
			countUser = 0;
			scheduler.shutdown(); // Đóng scheduler sau khi công việc hoàn thành
		}, time, TimeUnit.SECONDS);
	}

}