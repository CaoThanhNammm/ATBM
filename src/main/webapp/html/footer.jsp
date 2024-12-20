<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
	
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="icon" href="./image/general/logo.png" type="image/x-icon">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
<link rel="stylesheet" href="../css/footer.css">

<title>Footer</title>
</head>
<body>
	<!-- 
        Create: Nguyễn Khải Nam
        Date: 17/10/2023
        Note: Đây là phần chân trang, nó cung cấp một số thông tin và là điểm thông báo kết thúc khi người dùng kéo xuống cuối
        -->
	<div id="footer" class="container-fluid">
		<div class="container pt-4">
			<div class="row">
				<div class="col-lg-3 col-md-6">
					<ul class="footer_list">
						<li class="footer_list_item">
							<h5>Kết nối với chúng tôi</h5>
						</li>
						<li class="footer_list_item"><img
							class="footer_list_item_img" src="../image/general/logo.png"
							alt="Logo cửa hàng"></li>
						<li class="footer_list_item">Địa chỉ email: <a
							href="mailto: dobanbietday@gmail.com" type="email">dobanbietday@gmail.com</a></li>
						<li class="footer_list_item">Số điện thoại: <a
							href="tel: +84 123 456 789">+84 123 456 789</a></li>
						<li class="footer_list_item"><img
							class="footer_list_item_img" src="../image/general/qr.png"
							alt="Mã qr cửa hàng"></li>
					</ul>
				</div>

				<div class="col-lg-3 col-md-6">
					<ul class="footer_list">
						<li class="footer_list_item">
							<h5>N2Q</h5>
						</li>
						<li class="footer_list_item"><a href="../html/about.jsp">Giới
								thiệu</a></li>
						<li class="footer_list_item"><a href="../html/about.jsp">Liên
								hệ</a></li>
						<li class="footer_list_item"><a href="">Tuyển dụng</a></li>
					</ul>
				</div>

				<div class="col-lg-3 col-md-6">
					<ul class="footer_list">
						<li class="footer_list_item">
							<h5>Theo dõi chúng tôi trên</h5>
						</li>
						<li class="footer_list_item"><a href=""> <i
								class="fa-brands fa-facebook"></i> <span>Facebook</span>
						</a></li>
						<li class="footer_list_item"><a href=""> <i
								class="fa-brands fa-instagram"></i> <span>Instagram</span>
						</a></li>
						<li class="footer_list_item"><a href=""> <i
								class="fa-brands fa-linkedin"></i> <span>Linkedin</span>
						</a></li>
					</ul>
				</div>

				<div class="col-lg-3 col-md-6">
					<ul class="footer_list">
						<li class="footer_list_item">
							<h5>Trợ giúp</h5>
						</li>
						<li class="footer_list_item"><a href="../html/about.jsp">Liên hệ người
								quản trị</a></li>
						<li class="footer_list_item"><a href="../html/policy.jsp">Chính sách</a></li>
						<li class="footer_list_item"><a href="">Trả hàng</a></li>
						<li class="footer_list_item"><a href="../html/user.jsp">Tài khoản của bạn</a></li>
					</ul>
				</div>
			</div>

			<div class="row mt-5">
				<div class="footer_coppyright col">
					@<span class="year"></span> - Bản quyền thuộc thương hiệu của N2Q -
					thương hiệu thuộc Nông Lâm
				</div>
			</div>
		</div>
	</div>
</body>

<script src="https://code.jquery.com/jquery-3.7.1.js"
	integrity="sha256-eKhayi8LEQwp4NKxN+CfCh+3qOVUtJn3QNZ0TciWLP4="
	crossorigin="anonymous">
	
</script>
<script>
	$(document).ready(function() {
		$('.year').html((new Date(jQuery.now())).getFullYear())
	});
</script>
</html>