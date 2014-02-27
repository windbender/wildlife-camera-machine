package com.github.windbender.resources;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.github.windbender.core.DataStore;
import com.github.windbender.core.IdentificationRequest;
import com.github.windbender.core.ImageRecordTO;
import com.github.windbender.core.ImageStore;
import com.github.windbender.dao.ImageRecordDAO;
import com.github.windbender.domain.ImageEvent;
import com.github.windbender.domain.ImageRecord;
import com.github.windbender.domain.Species;
import com.sun.jersey.api.ConflictException;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.yammer.dropwizard.hibernate.UnitOfWork;
import com.yammer.metrics.annotation.Timed;

@Path("/images/")
@Produces("image/jpeg")
@Consumes("image/jpeg")
public class ImageResource {

	Logger log = LoggerFactory.getLogger(ImageResource.class);
	private DataStore ds;
	private ImageStore store;
	ImageRecordDAO irDAO;

	public ImageResource(DataStore ds, ImageStore store,	ImageRecordDAO irDAO) {
		this.ds = ds;
		this.store = store;
		this.irDAO = irDAO;
	}

	@GET
	@Timed
	@Path("{id}")
	@UnitOfWork
	// public Response fetch(@SessionUser(required=false) User user,
	// @PathParam("id") String id) {
	public Response fetch(@PathParam("id") String id, @QueryParam("sz") int displayWidth) {
		log.info("attempting to fetch image id = " + id+" with width "+displayWidth);
		try {
			ImageRecord ir = this.ds.getRecordFromId(id);
			InputStream is = store.getInputStreamFor(ir, id,displayWidth);
			
			return Response.ok(is).build();
		} catch (IOException e) {
			log.error("can't deliver because ", e);
			throw new WebApplicationException();
		}
	}

	@GET
	@Timed
	@UnitOfWork
	@Produces(MediaType.APPLICATION_JSON)
	public List<ImageRecordTO> list() {
		List<ImageRecord> list = ds.getTimeOrderedImages();
		
		List<ImageRecordTO> outList = new ArrayList<ImageRecordTO>();
		
		for(ImageRecord ir : list) {
			ImageRecordTO irto = new ImageRecordTO(ir);
			outList.add(irto);
		}
		return outList;
	}
	
	@GET
	@Timed
	@Produces(MediaType.APPLICATION_JSON)
	@UnitOfWork
	@Path("events")
	public List<ImageEvent> listEvents() {
		return ds.getImageEvents();
	}
	
	@GET
	@Timed
	@Produces(MediaType.APPLICATION_JSON)
	@Path("species")
	public List<Species> listSpecies() {
		
		List<Species> l = new ArrayList<Species>();
		l.add(new Species().setC('p').setName("puma").setId(1));
		l.add(new Species().setC('d').setName("deer").setId(2));
		l.add(new Species().setC('k').setName("buck").setId(3));
		l.add(new Species().setC('s').setName("skunk").setId(4));
		l.add(new Species().setC('b').setName("bobcat").setId(5));
		l.add(new Species().setC('h').setName("human").setId(6));
		l.add(new Species().setC('g').setName("dog").setId(7));
		return l;
	}
	
	@POST
	@Timed
	@UnitOfWork
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("identification")
	public Response identify(IdentificationRequest idRequest) {
		log.info("GOT an ID "+idRequest);
		// null sh ould be the user
		this.ds.recordIdentification(idRequest, null);
		return Response.ok().build();
	}
	
	@POST
	@Timed
	@UnitOfWork
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	// public Response add(@SessionUser User user, FormDataMultiPart formData) {
	public Response add(FormDataMultiPart formData) {

		ImageRecord newImage = null;
		try {
			for(BodyPart bp : formData.getBodyParts()) {
				MediaType mt = bp.getMediaType();
				if(!"image/jpeg".equals(mt.toString())) {
					continue;
				}
				InputStream is = bp.getEntityAs(InputStream.class);
				long size = bp.getContentDisposition().getSize();
				int sz = (int)size;
				String filename = bp.getContentDisposition().getFileName();

				BufferedInputStream bis = new BufferedInputStream(is);
				if( bis.markSupported()) {
				
					if(sz > 0) {
						bis.mark(sz);
					} else {
						// hard 5MB limit on size of images from wildlife camera ?
						bis.mark(1024*1024*5);
					}
				}
				{
					Metadata md = JpegMetadataReader.readMetadata(bis);
					ExifSubIFDDirectory directory = md.getDirectory(ExifSubIFDDirectory.class);
					GpsDirectory gpsDirectory = md.getDirectory(GpsDirectory.class);
	
					newImage = ImageRecord.makeImageFromExif(directory,gpsDirectory,filename);
				}
				ImageRecord exist = irDAO.findById(newImage.getId());
				if(exist == null) {
					bis.reset();
					BufferedImage bi = ImageIO.read(bis);
	
					store.saveImages(bi,newImage);
					bis.close();
					ds.addImage(newImage);
					URI uri = UriBuilder.fromResource(ImageResource.class).build(newImage.getId());
					log.info("the response uri will be " + uri);
					return Response.created(uri).build();
				} else {
					bis.close();
					throw new ConflictException("that image already exists");
				}

			}
		} catch ( ImageProcessingException | IOException e) {
			throw new WebApplicationException(e);
		} finally {
			formData.cleanup();
		}

		URI uri = UriBuilder.fromResource(ImageResource.class).build(newImage.getId());
		log.info("the response uri will be " + uri);
		return Response.created(uri).build();
	}
}
