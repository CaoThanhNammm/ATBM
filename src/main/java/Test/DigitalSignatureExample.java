package Test;

import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
public class DigitalSignatureExample {

    // 1. Tạo cặp khóa RSA
    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Sử dụng độ dài khóa 2048 bit
        return keyPairGenerator.generateKeyPair();
    }

    // 2. Băm dữ liệu sản phẩm trong giỏ hàng (ví dụ: id, price, quantity)
    public static String hashData(String data) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = messageDigest.digest(data.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashedBytes) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    // 3. Tạo chữ ký điện tử (Ký dữ liệu đã băm bằng private key)
    public static String signData(String data, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(data.getBytes());
        byte[] signedData = signature.sign();
        return Base64.getEncoder().encodeToString(signedData);  // Mã hóa chữ ký dưới dạng Base64 để dễ dàng lưu trữ/hiển thị
    }

    // 4. Xác thực chữ ký (Dùng public key để kiểm tra tính hợp lệ của chữ ký)
    public static boolean verifySignature(String data, String base64Signature, PublicKey publicKey) throws Exception {
        byte[] signatureBytes = Base64.getDecoder().decode(base64Signature);
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(data.getBytes());
        return signature.verify(signatureBytes);
    }
    public static String publicKeyToString(PublicKey publicKey) {
        // Mã hóa khóa công khai thành chuỗi Base64
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    // Chuyển String (Base64) thành PublicKey
    public static PublicKey stringToPublicKey(String publicKeyStr) throws Exception {
        // Giải mã chuỗi Base64 thành mảng byte
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyStr);

        // Khôi phục PublicKey từ mảng byte
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        return keyFactory.generatePublic(keySpec);
    }


    public static void main(String[] args) throws Exception {
        // 1. Tạo cặp khóa RSA
        KeyPair keyPair = generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        // 
        // Tạo danh sách sản phẩm
        List<Product> productList = List.of(
                new Product(1, "Laptoppp", "Laptop Dell XPS 13", 15000000, 10, LocalDate.of(2023, 11, 1), 200, "laptop.jpg"),
                new Product(2, "Smartphoneeee", "iPhone 14 Pro Max", 30000000, 5, LocalDate.of(2023, 10, 15), 150, "iphone.jpg"),
                new Product(3, "Smart Watch", "Apple Watch Series 8", 8000000, 15, LocalDate.of(2023, 9, 10), 120, "watch.jpg"),
                new Product(4, "Headphones", "Sony WH-1000XM5", 7000000, 20, LocalDate.of(2023, 8, 5), 250, "headphones.jpg"),
                new Product(5, "Tablet", "Samsung Galaxy Tab S8", 12000000, 12, LocalDate.of(2023, 7, 20), 180, "tablet.jpg")
        );
        
        // Khởi tạo ObjectMapper và đăng ký JavaTimeModule để xử lý LocalDate
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Đăng ký module để xử lý LocalDate
        
        // Duyệt qua tất cả các sản phẩm và thêm thông tin vào chuỗi
        for (Product product : productList) {
            // Chuyển đối tượng Java thành chuỗi JSON
            String jsonString = objectMapper.writeValueAsString(product);
            System.out.println(jsonString);  // In ra chuỗi JSON của từng sản phẩm
        }
    }}
        

 
        
        
        
        // In khóa công khai và khóa bí mật (Lưu ý: khóa bí mật không nên in ra trong thực tế)
   //     System.out.println("Public Key: " + Base64.getEncoder().encodeToString(publicKey.getEncoded()));
     //   System.out.println("Private Key: " + Base64.getEncoder().encodeToString(privateKey.getEncoded()));
       // String result = DigitalSignatureExample.publicKeyToString(publicKey);
       // System.out.println(result);
        // 2. Giả lập dữ liệu giỏ hàng (Ví dụ: ID sản phẩm, giá, số lượng)
 //       String cartData = "ProductID: 12345, Price: 199.99, Quantity: 2";
   //     System.out.println("\nGiỏ hàng (dữ liệu): " + cartData);
//
        // 3. Băm dữ liệu giỏ hàng
  //      String hashedData = hashData(cartData);
  //      System.out.println("\nDữ liệu đã băm (Hash): " + hashedData);
//
////        // 4. Tạo chữ ký điện tử (Sử dụng private key để ký dữ liệu đã băm)
//        String digitalSignature = signData(hashedData, privateKey);
//        System.out.println("\nChữ ký điện tử (Base64): " + digitalSignature);
//        PublicKey test = DigitalSignatureExample.stringToPublicKey(result);
////
//        // 5. Admin muốn xác thực chữ ký (Dùng public key để xác minh chữ ký)
//        boolean isSignatureValid = verifySignature(hashedData, digitalSignature, test);
//        System.out.println("\nChữ ký hợp lệ: " + isSignatureValid);
////
////        // Giả sử admin sửa đổi dữ liệu giỏ hàng và kiểm tra lại chữ ký
////        String tamperedCartData = "ProductID: 12345, Price: 199.99, Quantity: 3";  // Sửa đổi số lượng
////        String tamperedHashedData = hashData(tamperedCartData);
////        boolean isTamperedValid = verifySignature(tamperedHashedData, digitalSignature, publicKey);
////        System.out.println("\nChữ ký hợp lệ sau khi sửa đổi dữ liệu: " + isTamperedValid);
// 
    


