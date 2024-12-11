<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">

<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="icon" href="../image/general/logo.png" type="image/x-icon">
<link rel="stylesheet" href="../css/index.css">
<link rel="stylesheet" href="../css/access.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css">
<title>Nhập mã xác nhận</title>
</head>

<%
String note = (String) request.getParameter("status");
if (note != null && !note.isBlank()) {
	switch (note) {
	case "failed":
		note = "Mật mã không chính xác";
		break;
	default:
		note = "Có lỗi";
		break;
	}
} else {
	note = "";
}
%>

<body>
	<!-- 
        Create: Nguyễn Khải Nam
        Date: 19/10/2003
        Note: Nơi người dùng quên mật khẩu
     -->
	<div id="root">
		<div id="forget" class="container access">
			<div class="row">
				<div class="col access_mb">
					<form id="form_forget" action="access">
						<div class="access_group">
							<input type="hidden" name="access" value="confirm">
							<h1 class="access_group_h1">Nhập mã xác nhận</h1>
							<h5 class="access_group_h5">Hãy nhập mã xác nhận chúng tôi
								đã gửi qua email</h5>
							<a href="../index/index.jsp"> <img
								class="access_group_logo_img-black"
								src="../image/general/logo-black.png"
								alt="Logo của N2Q trên nền trắng"> <img
								class="access_group_logo_img-white"
								src="../image/general/logo.png" alt="Logo của N2Q trên nền đen">
							</a>
							<p class="text-danger">
								<%=note%>
							</p>
						</div>

						<div class="mt-4">
							<div class="access_group">
								<i class="fa-solid fa-envelope access_group_icon"></i> <input
									name="verificationCode" type="text"
									placeholder="*Đoạn mã gồm 6 sô" minlength="6" maxlength="6"
									required>
							</div>
							<div class="access_group">
								<div style="display: flex; flex-direction: column; align-items: center;">
									<span>Private key</span>
									<textarea style="height: 30vh; width: 55%" readonly id="privatekey" type="text" name="privateKey"
										required><%=request.getAttribute("privateKey")%></textarea>
								</div>
								<div style="display: flex; flex-direction: column; align-items: center;">
									<span>Public key</span>
									<textarea style="height: 30vh; width: 55%" readonly id="publicKey" type="text" name="publicKey"
									required><%=request.getAttribute("publicKey")%></textarea>
								</div>
									
								<i id="again" onclick="reAgainKey()" class="fa fa-repeat"
									aria-hidden="true" style="cursor: pointer"></i> 
									
								<i id="downloadKey" onclick="downloadKey()"
									class="fa-solid fa-download" style="cursor: pointer"></i>
							</div>
							<button type="submit" class="btn access_btn access_btn_submit">Tiếp
								tục</button>
						</div>

						<div>
							<h6>
								Tiếp tục <a class="color-yellow" href="login.jsp">Tài khoản
									của bạn</a>
							</h6>
							<h6>
								Chưa có tài khoản hãy <a class="color-yellow"
									href="register.jsp">Đăng ký ngay</a>
							</h6>
						</div>
					</form>
				</div>

				<div class="col d-flex justify-content-center">
					<img class="access_img" src="../image/access/image.png"
						alt="Ảnh giới thiệu">
				</div>

			</div>
		</div>
	</div>

	<script src="../js/access.js"></script>
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
	<script type="text/javascript">
		function reAgainKey() {
			var inputData = $("#privatekey").val();
			$.ajax({
				url : "againKey",
				type : "POST",
				data : {
					privateKey : inputData
				},
				success : function(response) {
					$("#privatekey").val(response.privateKey); 
			        $("#publicKey").val(response.publicKey); 
				},
				error : function() {
					console.log("error")
				}
			});
		}

		function downloadKey() {
			var inputData = $("#privatekey").val();
			$.ajax({
				url : "downloadKey",
				type : "POST",
				data : {
					privateKey : inputData
				},
				success : function(response) {
					console.log("ok")
					console.log(response)
				},
				error : function() {
					console.log("error")
				}
			});
		}
	</script>
</body>

</html>