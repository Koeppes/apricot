package de.apricotroom.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;
import net.sourceforge.barbecue.BarcodeImageHandler;

public class BarCodeGenerator {

	public static void main(String[] args) throws Exception {

		// Get 128B Barcode instance from the Factory
		Barcode barcode = BarcodeFactory.createCode128B("be the coder");
		barcode.setBarHeight(60);
		barcode.setBarWidth(2);

		File imgFile = new File("testsize.png");

		// Write the bar code to PNG file
		BarcodeImageHandler.savePNG(barcode, imgFile);
	}

	public static String generate(String serial, boolean web) throws Exception {
		String imageNamePathDB = null;
		String imagePathPrefix = "src/main/webapp/";
		String parentPath = null;
		String webPath = FacesContext.getCurrentInstance().getExternalContext().getRealPath("");
		String fullWebPath = webPath + "resources/images";
		File parent = null;
		File f = null;
		File imgFile = null;
		// Get 128B Barcode instance from the Factory
		Barcode barcode1 = BarcodeFactory.createCode128B(serial);
		barcode1.setBarHeight(60);
		barcode1.setBarWidth(2);
		f = new File(serial + ".png");
		BarcodeImageHandler.savePNG(barcode1, f);
		parent = f.getAbsoluteFile();
		parentPath = parent.getAbsolutePath().substring(0,
				parent.getAbsolutePath().length() - parent.getName().length());
		imageNamePathDB = "resources/images/" + serial + ".png";
		if (web) {
			String imagePath = fullWebPath;
			imgFile = new File(imagePath + "/" + f.getName());
		} else {
			String imagePath = imagePathPrefix + imageNamePathDB;
			imgFile = new File(parentPath + imagePath);
		}
		// Files.copy(f.toPath(), imgFile.toPath(),
		// StandardCopyOption.REPLACE_EXISTING);
		copyByStream(f, imgFile);

		// Write the bar code to PNG file
		f.delete();
		return imageNamePathDB;

	}

	public static void copyByStream(File from, File to) throws Exception {
		FileInputStream infile = new FileInputStream(from);
		FileOutputStream outfile = new FileOutputStream(to);

		byte[] b = new byte[1024];
		while (infile.read(b, 0, 1024) > 0) {
			outfile.write(b);
		}
		infile.close();
		outfile.close();
	}
}
