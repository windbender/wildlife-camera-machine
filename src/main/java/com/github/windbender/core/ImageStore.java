package com.github.windbender.core;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import com.github.windbender.domain.ImageRecord;

public interface ImageStore {
	int SMALLEST_SIZE = 64;
	int[] sizes = {-1, 1280, 640, SMALLEST_SIZE};

	InputStream getInputStreamFor(ImageRecord ir, String id, int displayWidth) throws IOException;

	void saveImages(BufferedImage bi, ImageRecord newImage) throws IOException;

}
