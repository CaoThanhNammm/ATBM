<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<link rel="icon" href="../image/general/logo.png" type="image/x-icon">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet" href="../css/index.css">
<link rel="stylesheet" href="../css/indexRes.css">
<link rel="stylesheet" href="../css/header.css">
<link rel="stylesheet" href="../css/headerRes.css">
<link rel="stylesheet" href="../css/footer.css">
<link rel="stylesheet" href="../css/user.css">
<link rel="stylesheet" href="../css/tab.css">
<title>Thông tin tài khoản</title>
</head>
<%@ page import="model.Account"%>
<%@ page import="model.Order"%>
<%@ page import="java.util.List"%>
<%
Account acInfo = (Account) request.getSession().getAttribute("accountInfo");
String status = request.getParameter("status");
String field = request.getParameter("field");
Object ordersObject = request.getSession().getAttribute("orders");
List<Order> orders = null;
if (ordersObject != null) {
	orders = (List<Order>) ordersObject;
}
if (field == null)
	field = "";
String note = "";
if (status != null) {
	switch (status) {
		case "success" :
	note = "Cập nhật thông tin thành công";
	break;
		case "failed" :
	note = "Cập nhật thông tin không thành công";
	break;
		case "failed-0" :
	note = "Lỗi thông tin nhập vào";
	break;
		case "failed-1" :
	note = "Thông tin không khớp";
	break;
		default :
	note = "Có lỗi";
	break;
	}
} else {
	note = "";
}
%>

<body>
	<%@include file="header.jsp"%>

	<div class="user">
		<%
		if (ac == null) {
		%>
		<div class="container d-flex flex-column">
			<h3 class="text-center">Vui lòng đăng nhập</h3>
			<a href="login.jsp" class="btn user-btn">Đăng nhập</a>
		</div>
		<%
		} else {
		%>
		<div>
			<div class="tab">
				<button class="tablinks active" onclick="_switchTab(event, 'form')">Thông
					tin người dùng</button>
				<button class="tablinks" onclick="_switchTab(event, 'createNewKey')">Tạo
					khóa mới</button>
				<button class="tablinks" onclick="_switchTab(event, 'password')">Đổi
					mật khẩu</button>
				<button class="tablinks" onclick="_switchTab(event, 'order')">Lịch
					sử mua hàng</button>
			</div>
			<div id="form" class="tabcontent active">
				<h2>Chỉnh sửa thông tin người dùng</h2>
				<form action="../html/infoUser" method="POST">
					<span class="text-warning "><%=field.equals("info") ? note : ""%></span>
					<input type="hidden" name="info" value="info">
					<div class="form-group">
						<label for="name">Tên:</label> <input type="text" id="name"
							name="name" placeholder="Họ và tên"
							value="<%=acInfo.getFullName()%>" required>
					</div>
					<div class="form-group">
						<label for="email">Email:</label> <input type="email" id="email"
							name="email" placeholder="Địa chỉ email"
							value="<%=acInfo.getEmail()%>" required>
					</div>
					<div class="form-group">
						<label for="phone">Số điện thoại:</label> <input type="tel"
							id="phone" name="phone" placeholder="Số điện thoại"
							value="<%=acInfo.getPhone()%>" required>
					</div>
					<div class="form-group">
						<label for="address">Địa chỉ:</label> <input type="text"
							id="address" name="address" placeholder="Địa chỉ..."
							value="<%=acInfo.getAddress()%>">
					</div>
					<div class="form-group">
						<label for="gender">Giới tính:</label> <select id="gender"
							name="gender">
							<%
							String sex = acInfo.getGender().getSex();
							%>
							<option value="<%=sex.equals("Nam") ? "Male" : "Female"%>"><%=sex%></option>
							<%
							String sex2 = sex.equals("Nam") ? "Nữ" : "Nam";
							%>
							<option value="<%=sex2.equals("Nam") ? "Male" : "Female"%>"><%=sex2%></option>
							<option value="Other">Khác</option>
						</select>
					</div>
					<div class="form-group">
						<label for="dob">Ngày sinh:</label> <input type="date" id="dob"
							name="dob" value="<%=acInfo.getDob()%>" required>
					</div>
					<div class="form-actions">
						<button type="submit">Lưu thông tin</button>
					</div>
				</form>
			</div>
			<div id="password" class="tabcontent">
				<h3>Đổi mật khẩu</h3>
				<form action="../html/infoUser" method="POST">
					<span class="text-warning "><%=field.equals("pass") ? note : ""%></span>
					<input type="hidden" name="info" value="pass">
					<div class="form-group">
						<label for="oldPassword">Mật khẩu cũ:</label> <input
							type="password" id="oldPassword" name="oldPassword"
							placeholder="Nhập mật khẩu cũ" required>
					</div>
					<div class="form-group">
						<label for="newPassword">Mật khẩu mới:</label> <input
							type="password" id="newPassword" name="newPassword"
							placeholder="Nhập mật khẩu mới" required>
					</div>
					<div class="form-group">
						<label for="confirmPassword">Nhập lại mật khẩu mới:</label> <input
							type="password" id="confirmPassword" name="confirmPassword"
							placeholder="Nhập lại mật khẩu mới" required>
					</div>
					<div class="form-actions">
						<button type="submit">Đổi mật khẩu</button>
					</div>
				</form>
			</div>
			<div id="order" class="tabcontent">
				<table>
					<thead>
						<tr>
							<th style="text-align: center;">ID Đơn hàng</th>
							<th style="text-align: center;">Ngày tạo</th>
							<th style="text-align: center;">Lần cập nhật cuối</th>
							<th style="text-align: center;">Giá tiền tổng</th>
							<th style="text-align: center;">Trạng thái đơn hàng</th>
							<th style="text-align: center;"></th>
						</tr>
					</thead>
					<tbody>
						<%
						for (Order order : orders) {
						%>
						<tr>
							<td><%=order.getId()%></td>
							<td><%=order.getDateCreated().toString()%></td>
							<td><%=order.getLastUpdated().toString()%></td>
							<td><%=new Product().formatNumber(order.getTotalPrice())%>đ</td>
							<td><%=order.getStatus().getName()%></td>
							<td><a target="_blank" rel="noopener noreferrer"
								class="details-button"
								href="orderDetail?orderID=<%=order.getId()%>">Chi tiết</a></td>
						</tr>
						<%
						}
						%>
					</tbody>
				</table>
			</div>

			<div id="createNewKey" class="tabcontent">
				<form action="../html/infoUser" method="POST">
					<span class="text-warning "><%=field.equals("createNewKey") ? note : ""%></span>
					
					<input type="hidden" name="info" value="createNewKey"> <label
						for="textarea3">Mật khẩu</label> <input type="password"
						name="confirmPassword" id="textarea3" /> <label>
						Privatekey(đường dẫn của khóa: "C:/privatekey")</label> 
						<input type="text" id="filename" name="filename" required>
					<button class="btn_new_key" id="openModalBtn">Tạo mới</button>
				</form>
			</div>
			<%
			request.getSession().removeAttribute("infoAccount");
			request.getSession().removeAttribute("orders");
			%>
			<%
			}
			%>
		</div>
		<%@include file="footer.jsp"%>
</body>


<script src="../js/index.js"></script>
<script src="../js/tab.js"></script>
<script src="https://code.jquery.com/jquery-3.7.1.js"
	integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4="
	crossorigin="anonymous">
	
</script>
<script>
	const openModalBtn = document.getElementById("openModalBtn");
	const modal = document.getElementById("modal");
	const closeBtn = document.getElementsByClassName("closeBtn")[0];

	openModalBtn.onclick = function() {
		modal.style.display = "flex";
	}

	closeBtn.onclick = function() {
		modal.style.display = "none";
	}

	window.onclick = function(event) {
		if (event.target == modal) {
			modal.style.display = "none";
		}
	}
</script>
</html>