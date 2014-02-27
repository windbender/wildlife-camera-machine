package com.github.windbender.core;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.S3Object;
import com.github.windbender.domain.ImageRecord;

public class S3ImageStore implements ImageStore {

	Logger log = LoggerFactory.getLogger(S3ImageStore.class);

	private String bucketName;
	private String myAccessKeyID;
	private String mySecretKey;
	
	AmazonS3 s3Client;

	public S3ImageStore(String accessKey, String secretKey, String bucketName) {
		this.bucketName = bucketName;
		this.myAccessKeyID = accessKey;
		this.mySecretKey = secretKey;
		AWSCredentials myCredentials = new BasicAWSCredentials( myAccessKeyID, mySecretKey);
		s3Client = new AmazonS3Client(myCredentials);        
		Owner o = s3Client.getS3AccountOwner();
		log.info("using S3 as "+o.getDisplayName());
		List<Bucket> bl =s3Client.listBuckets();
		for(Bucket b : bl) {
			log.info("  bucket:"+b.getName());
		}
		
	}
	
	@Override
	public InputStream getInputStreamFor(ImageRecord ir, String id, int displayWidth)
			throws IOException {
		log.info("attempting to get an inpustream for "+id);
		try {
			if(displayWidth == 0) displayWidth=1300;
			String szs = "native";
			for(int sz : sizes) {
				if(sz > displayWidth) {
					szs = ""+sz;
				}
			}
			String s3Key = szs+"/"+ ir.getId();

			S3Object object = s3Client.getObject(new GetObjectRequest(bucketName, s3Key));
			InputStream objectData = object.getObjectContent();
			return objectData;
		} catch (Exception e) {
			log.error("failed because ",e);
			throw e;
		}
	}

	@Override
	public void saveImages(BufferedImage bi, ImageRecord newImage)
			throws IOException {
		// we should make a 640X480 at 1280X960 and then a native.
		for (int maxSize : sizes) {
			BufferedImage outImage = bi;
			String szs;
			szs = "native";
			if (maxSize == -1) {
				maxSize = bi.getWidth();
			} else {
				szs = "" + maxSize;
			}
			outImage = Scalr.resize(bi, maxSize);
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(outImage, "jpg", baos);
			baos.flush();
			byte[] imageInByteArray = baos.toByteArray();
			upload(szs,newImage,imageInByteArray);
			baos.flush();
			baos.close();
		}

	}

	private void upload(String szs, ImageRecord newImage,
			byte[] imageInByteArray) {
		InputStream stream = null;
		try {
			log.info("attempting to upload to S3 for "+newImage.getId());
			stream = new ByteArrayInputStream(imageInByteArray);
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(imageInByteArray.length);
			meta.setContentType("image/jpeg");
			String s3Key = szs+"/"+ newImage.getId();
			s3Client.putObject(bucketName, s3Key, stream, meta);
			s3Client.setObjectAcl(bucketName, s3Key,CannedAccessControlList.PublicRead);
			log.info("in theory finished");
		} catch(Exception e) {
			log.error("unable to do an upload because ",e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
			}
		}
	}

}
