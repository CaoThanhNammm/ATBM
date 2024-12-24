package security;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
public class DigitalSignature {

	 // Tạo cặp khóa RSA (Private Key và Public Key)
    public  KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048); // Khóa dài 2048 bit
        return keyPairGenerator.generateKeyPair();
    }// Chuyển đổi PublicKey từ Base64 sang đối tượng PublicKey
    public  PublicKey getPublicKeyFromBase64(String base64PublicKey) throws Exception {
        byte[] publicKeyBytes = Base64.getDecoder().decode(base64PublicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    // Chuyển đổi PrivateKey từ Base64 sang đối tượng PrivateKey
    public  PrivateKey getPrivateKeyFromBase64(String base64PrivateKey) throws Exception {
        byte[] privateKeyBytes = Base64.getDecoder().decode(base64PrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    // Băm dữ liệu JSON thành Base64 (SHA-256)
    public  String hashJson(Object jsonData) throws Exception {
        // Chuyển đổi đối tượng JSON thành chuỗi
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(jsonData);
        
        // In ra chuỗi JSON (Chỉ để kiểm tra)
        System.out.println("Chuỗi JSON: " + jsonString);
        
        // Băm chuỗi JSON bằng thuật toán SHA-256
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        byte[] hashedBytes = messageDigest.digest(jsonString.getBytes());
        
        // Chuyển mảng byte băm thành chuỗi Base64
        return Base64.getEncoder().encodeToString(hashedBytes);
    }

    // Tạo chữ ký điện tử (Ký dữ liệu đã băm bằng private key)
    public  String signData(Object data, PrivateKey privateKey) throws Exception {
        // Băm dữ liệu JSON thành Base64
        String hashedData = hashJson(data);
        
        // Chuyển đổi Base64 thành byte[]
        byte[] dataBytes = Base64.getDecoder().decode(hashedData);

        // Ký dữ liệu đã băm
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(dataBytes);
        byte[] signatureBytes = signature.sign();

        // Trả về chữ ký đã mã hóa dưới dạng Base64
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    // Xác minh chữ ký (Sử dụng public key để xác minh chữ ký)
    public  boolean verifySignature(Object data, String base64Signature, String base64PublicKey) throws Exception {
        // Chuyển đổi publicKey từ Base64 sang PublicKey
        PublicKey publicKey = getPublicKeyFromBase64(base64PublicKey);

        // Chuyển đổi signature từ Base64 sang byte[]
        byte[] signatureBytes = Base64.getDecoder().decode(base64Signature);

        // Băm lại dữ liệu JSON thành Base64
        String hashedData = hashJson(data);
        
        // Chuyển đổi Base64 thành byte[]
        byte[] dataBytes = Base64.getDecoder().decode(hashedData);

        // Khởi tạo đối tượng Signature để xác minh
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(dataBytes);

        // Xác minh chữ ký
        return signature.verify(signatureBytes);
    }

   
    public static void main(String[] args) throws Exception {
    	DigitalSignature digitalSignature = new DigitalSignature();
        // Bước 1: Tạo cặp khóa RSA
        KeyPair keyPair = digitalSignature.generateRSAKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        // In public và private key dưới dạng Base64
        String base64PublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
        String base64PrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());
        String pk = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAueBneWcN4v6OOo4VVrOIiF2b3VGcFrnzhWJrT9FVbR3NXR/rr8U0DQKTJNLoP5WL4adoCS0NGJTkLENmz3wPdwFle+xMobZIr9P+G/pG7KW0zvvjp4ICNJiEcTUvOBm1gvAbvYptqD28MNZlhuGGhRyvbNpRW/FWggXJlPB4Od+HxLk0ES3OXn8hC7KlmlQ3+GmVCvMIvgrgBCvCNSubb9nq7OJpgWD0tHizY34a+NhmrTvqv1HBJv1+KHOeVyB3uMewrYMW6auoo4eCdfPvy5KZk109SiX6MSvyv1o6qddwB0PM31wS/DMe+61TVsH8C+KUfcko4HUtJK5uBL0a3QIDAQAB";
        System.out.println("Public Key: " + base64PublicKey);
        System.out.println("Private Key: " + base64PrivateKey);

       

    }
    }
        

 
        
        
        
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
    


