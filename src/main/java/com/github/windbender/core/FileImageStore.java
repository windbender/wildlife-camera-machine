package com.github.windbender.core;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;

import com.github.windbender.domain.ImageRecord;

public class FileImageStore implements ImageStore {

	String storeInPlace;
	public FileImageStore(String storeInPlace) {
		this.storeInPlace = storeInPlace;
	}

	

	@Override
	public InputStream getInputStreamFor(ImageRecord ir, String id, int displayWidth) throws IOException {
		if(displayWidth == 0) displayWidth=1300;
		String szs = "native";
		for(int sz : sizes) {
			if(sz > displayWidth) {
				szs = ""+sz;
			}
		}
		String outName = storeInPlace+"/"+szs+"/"+id;
		File f = new File(outName);
		FileInputStream fis = new FileInputStream(f);
		return fis;
	}

	@Override
	public void saveImages(BufferedImage bi, ImageRecord newImage) throws IOException {
		// we should make a 640X480   at 1280X960 and then a native.
		for(int maxSize : sizes) {
			BufferedImage outImage = bi;
			String szs = "native";
			if(maxSize == -1) {
				maxSize = bi.getWidth();
			} else {
				szs = ""+maxSize;
			}
			outImage = Scalr.resize(bi, maxSize);
			String outName = storeInPlace+"/"+szs+"/"+newImage.getId();
			FileOutputStream fos = new FileOutputStream(outName);
			ImageIO.write(outImage, "jpg", fos);

			fos.flush();
			fos.close();
		}
		
	}

}
