package com.zanflow.cms.serv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Blob.BlobSourceOption;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.util.AppProperties;
import com.zanflow.common.db.Constants;

@Service
public class GoogleCloudStorageServiceImpl implements StorageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3ServiceImpl.class);

	
	@Override
	// @Async annotation ensures that the method is executed in a different background thread 
	// but not consume the main thread.
	@Async
	public void uploadFile(final MultipartFile multipartFile,String companyCode, long docid) {
		//System.out.println("File upload in progress.");
		try {
			//final File file = convertMultiPartFileToFile(multipartFile);
			String fileKey = docid+multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().indexOf("."));
			uploadFileToBucket(companyCode,getBucketName(companyCode), multipartFile.getBytes() ,fileKey);
			//System.out.println("File upload is completed.");
			
			//file.delete();	// To remove the file locally created in the project folder.
		} catch (Exception ex) {
			//System.out.println("File upload is failed.");
			ex.printStackTrace();
		}
	}
	
	@Override
	// @Async annotation ensures that the method is executed in a different background thread 
	// but not consume the main thread.
	@Async
	public void uploadFile(final byte[] data, String filename,String companyCode, long docid) {
		//System.out.println("File upload in progress.");
		try {
			//final File file = convertMultiPartFileToFile(multipartFile);
			String fileKey = docid+filename.substring(filename.indexOf("."));
			uploadFileToBucket(companyCode,getBucketName(companyCode),data ,fileKey);
			//System.out.println("File upload is completed.");
			
			//file.delete();	// To remove the file locally created in the project folder.
		} catch (Exception ex) {
			//System.out.println("File upload is failed.");
			ex.printStackTrace();
		}
	}

	/**
	 * 
	 * @param companyCode
	 * @param filename
	 * @return
	 * @throws ApplicationException
	 */
	public byte[] getDocument(String companyCode,String filename) throws ApplicationException {
		//AmazonS3 s3client = getAmazonS3Cient(companyCode);
		
		try {
			Credentials credentials = GoogleCredentials.fromStream(this.getClass().getClassLoader().getResourceAsStream("zanflow-credentials.json"));
			Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId("zanflow").build().getService();
			//BlobId blobId = BlobId.of("zanflow-docs", getDocFolder(companyCode, filename));
			//return storage.get(blobId).getContent(Storage.BlobGetOption.fields(Storage.BlobField.values()));
			Blob blob = storage.get("zanflow-docs", getDocFolder(companyCode, filename));
			////System.out.println(blob.getBucket() + blob.getContentType());
			return blob.getContent(BlobSourceOption.generationMatch());
			//return IOUtils.toByteArray(s3client.getObject(getBucketName(companyCode), getDocFolder(companyCode, filename)).getObjectContent());
		} catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException("Document not available");
		} finally {
			//s3client.shutdown();
		}
	}
	
	/**
	 * 
	 * @param multipartFile
	 * @return
	 */
 	private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
		final File file = new File(multipartFile.getOriginalFilename());
		try (final FileOutputStream outputStream = new FileOutputStream(file)) {
			outputStream.write(multipartFile.getBytes());
		} catch (final IOException ex) {
			ex.printStackTrace();
		}
		return file;
	}

	private void uploadFileToBucket(String companyCode,final String bucketName, byte[] file, String fileKey) {
		//System.out.println("Uploading file with name= " + fileKey);
		try{
			//AmazonS3 s3client = getAmazonS3Cient(companyCode);
			//final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, getDocFolder(companyCode, fileKey), file);
			//s3client.putObject(putObjectRequest);
			//s3client.shutdown();
			Credentials credentials = GoogleCredentials.fromStream(this.getClass().getClassLoader().getResourceAsStream("zanflow-credentials.json"));
			Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId("zanflow").build().getService();
		    BlobId blobId = BlobId.of(bucketName, getDocFolder(companyCode, fileKey));
		    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
		    storage.create(blobInfo, file);

		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public void deleteFileBucket(final String companyCode, String docid) {
		//System.out.println("deleteFileS3Bucket#filename#" + docid);
		try {
			//AmazonS3 s3client = getAmazonS3Cient(companyCode);
			//s3client.deleteObject(getBucketName(companyCode), getDocFolder(companyCode, docid));
			//s3client.shutdown();
			Credentials credentials = GoogleCredentials.fromStream(this.getClass().getClassLoader().getResourceAsStream("zanflow-credentials.json"));
			Storage storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId("zanflow").build().getService();
			storage.delete("zanflow-docs", getDocFolder(companyCode, docid));
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public AmazonS3 getAmazonS3Cient(String companycode) {
		final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(AppProperties.getInstance().getPropery(Constants.S3_ACCESS_KEY),AppProperties.getInstance().getPropery(Constants.S3_SECRET));//"AKIAIG3EM5LTKVT6ZOUA","BHKQ64B9L1RC2Gxz3E/UHr0xXy4kjy+vENTo+KgE");
		// Get AmazonS3 client and return the s3Client object.
		return AmazonS3ClientBuilder
				.standard()
				.withRegion(Regions.fromName(getRegion(companycode) ))
				.withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
				.build();
	}

	private String getRegion(String companycode) {
		return AppProperties.getInstance().getPropery(Constants.S3_REGION);
	}
	
	private String getBucketName(String companycode) {
		//System.out.println("CompanyCode#"+companycode);
		//return AppProperties.getInstance().getPropery(Constants.S3_BUCKETNAME+getRegion(companycode));
		return "zanflow-docs";
	}
	
	private String getDocFolder(final String companyCode, String docid) {
		//String folder = AppProperties.getInstance().getPropery(Constants.S3_FOLDER_NAME+companyCode);
		//return (folder == null ?docid:folder.trim()+docid);
		return companyCode+"/"+docid;
	}
	
	public static void main(String args[]) throws FileNotFoundException, IOException {
		GoogleCloudStorageServiceImpl service = new GoogleCloudStorageServiceImpl();
		
		//AmazonS3 s3client = service.getAmazonS3Cient("easygst");
		//IOUtils.toByteArray(s3client.getObject("zanflow-docs", "299").getObjectContent());
		//s3client.putObject("zanflow-docs", "easygst/", IOUtils.toByteArray(s3client.getObject("zanflow-docs", "299").getObjectContent()));
		//s3client.getObject("zanflow-easygst", "299").getObjectContent();
		try {
			service.getDocument("zanflow", "logo.png");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
