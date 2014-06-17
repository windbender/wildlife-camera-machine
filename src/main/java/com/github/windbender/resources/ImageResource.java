package com.github.windbender.resources;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.hibernate.Hibernate;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.github.windbender.auth.Priv;
import com.github.windbender.auth.SessionAuth;
import com.github.windbender.auth.SessionCurProj;
import com.github.windbender.auth.SessionUser;
import com.github.windbender.core.HibernateDataStore;
import com.github.windbender.core.IdentificationRequest;
import com.github.windbender.core.ImageStore;
import com.github.windbender.core.NextEventRecord;
import com.github.windbender.core.SessionFilteredAuthorization;
import com.github.windbender.dao.ImageRecordDAO;
import com.github.windbender.dao.ReportDAO;
import com.github.windbender.dao.SpeciesDAO;
import com.github.windbender.domain.ImageEvent;
import com.github.windbender.domain.ImageRecord;
import com.github.windbender.domain.Project;
import com.github.windbender.domain.Species;
import com.github.windbender.domain.User;
import com.github.windbender.service.TimeZoneGetter;
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
	private HibernateDataStore ds;
	private ImageStore store;
	ImageRecordDAO irDAO;
	private SpeciesDAO speciesDAO;
	private ReportDAO reportDAO;
	private TimeZoneGetter timeZoneGetter;

	public ImageResource(HibernateDataStore ds, ImageStore store,	ImageRecordDAO irDAO, SpeciesDAO speciesDAO, ReportDAO reportDAO,TimeZoneGetter timeZoneGetter) {
		this.ds = ds;
		this.store = store;
		this.irDAO = irDAO;
		this.speciesDAO = speciesDAO;
		this.reportDAO = reportDAO;
		this.timeZoneGetter = timeZoneGetter;
	}

	@GET
	@Timed
	@Path("{id}")
	@UnitOfWork
	public Response fetch(@SessionAuth(required={Priv.CATEGORIZE,Priv.REPORT}) SessionFilteredAuthorization auths,@SessionUser User user, @PathParam("id") String id, @QueryParam("sz") int displayWidth) {
		log.info("attempting to fetch image id = " + id+" with width "+displayWidth);
		try {
			ImageRecord ir = this.ds.getRecordFromId(id);
			InputStream is = store.getInputStreamFor(ir, id,displayWidth);
			CacheControl control = new CacheControl();
			control.setMaxAge(6 * 60 * 60);   // 6 hours
			return Response.ok(is).cacheControl(control).build();
		} catch (IOException e) {
			log.error("can't deliver because ", e);
			throw new WebApplicationException();
		}
	}
	
	@GET
	@Timed
	@Produces(MediaType.APPLICATION_JSON)
	@UnitOfWork
	@Path("nextEvent")
	public NextEventRecord getNextEvent(@SessionAuth(required={Priv.CATEGORIZE}) SessionFilteredAuthorization auths,@SessionCurProj Project currentProject,@SessionUser User user, @QueryParam("lastEvent") String lastEventIdStr) {
		Long lastEventId = null;
		try {
			lastEventId = Long.parseLong(lastEventIdStr);
		} catch (NumberFormatException e) {
			// stupid API ignore this exception
		}
		NextEventRecord ner = this.ds.makeNextEventRecord(user,currentProject,lastEventId);
		if(ner.getImageEvent() != null) {
			for(ImageRecord ir: ner.getImageEvent().getImageRecords()) {
				ir.getDatetime();
			}
			ImageEvent upie = initializeAndUnproxy(ner.getImageEvent());
			ner.setImageEvent(upie);
		}
		return ner;
	}
	
	public static <T> T initializeAndUnproxy(T entity) {
	    if (entity == null) {
	        throw new 
	           NullPointerException("Entity passed for initialization is null");
	    }

	    Hibernate.initialize(entity);
	    if (entity instanceof HibernateProxy) {
	        entity = (T) ((HibernateProxy) entity).getHibernateLazyInitializer().getImplementation();
	    }
	    return entity;
	}

	
	@GET
	@Timed
	@Produces(MediaType.APPLICATION_JSON)
	@UnitOfWork
	@Path("species")
	public List<Species> listSpecies(@SessionAuth(required={Priv.CATEGORIZE}) SessionFilteredAuthorization auths,@SessionUser User user) {
		List<Species> l = this.speciesDAO.findAll();
		return l;
	}
	@GET
	@Timed
	@Produces(MediaType.APPLICATION_JSON)
	@UnitOfWork
	@Path("topSpecies")
	public List<Species> listTopSpecies(@SessionAuth(required={Priv.CATEGORIZE,Priv.REPORT}) SessionFilteredAuthorization auths,@SessionCurProj Project currentProject, @SessionUser User user, @QueryParam("includeNone") boolean includeNone,@QueryParam("includeUnknown") boolean includeUnknown,@QueryParam("count") Integer count) {
		List<Species> outList = null;
		if(count == null) {
			count = 10;
		}
		count = count -2;
		List<Long> l = reportDAO.makeTopSpeciesIdList(count,currentProject.getId());
		if(l.size() < 3) {
			outList = getTopTenForProject();
		} else {
			outList = new ArrayList<Species>();
			for(Long id : l) {
				Species s = speciesDAO.findById(id);
				outList.add(s);
			}
		}
		
		// filter out "none"
		List<Species> realOut = new ArrayList<Species>();
		if(includeNone) {
			Species s = speciesDAO.findByNameContains("none");
			if(s != null) {
				realOut.add(s);
			}
		}
		if(includeUnknown) {
			Species s = speciesDAO.findByNameContains("unknown");
			if(s != null) {
				realOut.add(s);
			}
		}
		
		for(Species s: outList) {
			if(s.getName().equals("none")) {
				//
			} else if(s.getName().equals("unknown")) {
				//				
			} else {
				realOut.add(s);
			}
		}
		return realOut;
	}
	
	private List<Species> getTopTenForProject() {
		
		return getHardWiredSpecies();
	}

	private List<Species> getHardWiredSpecies() {
		List<Species> l = new ArrayList<Species>();
		Species s = this.speciesDAO.findByNameContains("puma");
		s.setC('p');
		if(s != null) l.add(s);
		
		s = this.speciesDAO.findByNameContains("mule");
		s.setC('d');
		if(s != null) l.add(s);
		
		s = this.speciesDAO.findByNameContains("Striped Skunk");
		if(s != null) {
			s.setC('s');
			l.add(s);
		}
		
		s = this.speciesDAO.findByNameContains("lynx");
		s.setC('b');
		if(s != null) l.add(s);

		s = this.speciesDAO.findByNameContains("Wild Turkey");
		if(s != null) {
			s.setC('t');
			l.add(s);
		}
		
		s = this.speciesDAO.findByNameContains("sapiens");
		s.setC('h');
		if(s != null) l.add(s);

		s = this.speciesDAO.findByNameContains("familiaris");
		s.setC('g');
		if(s != null) l.add(s);

		return l;
	}

	@POST
	@Timed
	@UnitOfWork
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("identification")
	public Response identify(@SessionAuth(required={Priv.CATEGORIZE}) SessionFilteredAuthorization auths,@SessionCurProj Project currentProject,@SessionUser User user,IdentificationRequest idRequest) {
		log.info("GOT an ID "+idRequest);

		// null sh ould be the user
		long id = this.ds.recordIdentification(idRequest, user,currentProject);
		return Response.ok(id).build();
	}
	
	@POST
	@Timed
	@UnitOfWork
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("clearid")
	public Response unid(@SessionAuth(required={Priv.CATEGORIZE}) SessionFilteredAuthorization auths,@SessionCurProj Project currentProject,@SessionUser User user,long idToClear) {
		log.info("we should clear "+idToClear);
		this.ds.removeId(idToClear,currentProject);
		// null should be the user
		//long id = this.ds.recordIdentification(idRequest, user);
		return Response.ok().build();
	}
	
	@POST
	@Timed
	@UnitOfWork
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public Response add(@SessionAuth(required={Priv.UPLOAD}) SessionFilteredAuthorization auths,@SessionCurProj Project currentProject,@SessionUser User user, @Context HttpServletRequest request, FormDataMultiPart formData) {

		ImageRecord newImage = null;
		try {
			for(BodyPart bp : formData.getBodyParts()) {
				try {
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
						//String cameraID = null; // perhaps part of the form upload ?
						String cameraIDStr = request.getHeader("camera_id");
						Long cameraId = null;
						if(cameraIDStr != null) {
							try {
								cameraId = Long.parseLong(cameraIDStr);
							} catch (NumberFormatException e) {
								throw new ConflictException("must have camera_id in header");
							}
						}
						String latStr = request.getHeader("pos_lat");
						String lonStr = request.getHeader("pos_lon");
						
						newImage = ImageRecord.makeImageFromExif(timeZoneGetter, directory,gpsDirectory,filename,cameraId,latStr,lonStr);
					}
					ImageRecord exist = irDAO.findById(newImage.getId());
					if(exist == null) {
						bis.reset();
						BufferedImage bi = ImageIO.read(bis);
		
						store.saveImages(bi,newImage);
						log.info("new image save done");
						bis.close();
						ds.addImage(newImage,currentProject);
						bi.flush();
						
						URI uri = UriBuilder.fromResource(ImageResource.class).build(newImage.getId());
						log.info("the response uri will be " + uri);
						return Response.created(uri).build();
					} else {
						bis.close();
						throw new ConflictException("that image already exists");
					}
				} catch(Exception e) {
					log.info("could not upload one because ",e);
					throw e;
				} finally {
					log.info("and we uploaded one, for better or worse");
				}
			}
		} catch ( ImageProcessingException | IOException e) {
			throw new WebApplicationException(e);
		} finally {
			formData.cleanup();
		}
		
		URI uri = UriBuilder.fromResource(ImageResource.class).build();
		log.info("the response uri will be " + uri);
		return Response.created(uri).build();
	}
}
