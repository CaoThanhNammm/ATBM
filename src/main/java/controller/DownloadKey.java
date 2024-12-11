package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.JFileChooser;

@WebServlet("/html/downloadKey")
public class DownloadKey extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public DownloadKey() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String privateKey = request.getParameter("privateKey");
		openFolderAndSaveKey(privateKey);
		response.setContentType("text/plain");
		response.getWriter().write("");
	}


	private void openFolderAndSaveKey(String encodedKey) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Chọn thư mục lưu privateKey");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = fileChooser.showOpenDialog(null);

		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFolder = fileChooser.getSelectedFile();

			Path filePath = Paths.get(selectedFolder.getAbsolutePath(), "privateKey");

			try {
				Files.write(filePath, encodedKey.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("No folder selected");
		}
	}
}
