package com.zanflow.cms.serv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.util.IOUtils;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.util.AppProperties;
import com.zanflow.common.db.Constants;
import org.springframework.core.env.Environment;

@Service
public class AWSS3ServiceImpl implements StorageService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AWSS3ServiceImpl.class);

	
	
	private String awssecret;

	
	private String accessKey;
	
	public AWSS3ServiceImpl(@Value("${aws.s3.accesskey}") String accessKey, @Value("${aws.s3.secret}") String awssecret) {
		// TODO Auto-generated constructor stub
		this.accessKey = accessKey;
		this.awssecret = awssecret;
	}
	
	@Override
	// @Async annotation ensures that the method is executed in a different background thread 
	// but not consume the main thread.
	@Async
	public void uploadFile(final MultipartFile multipartFile,String companyCode, long docid) {
		//System.out.println("File upload in progress.");
		try {
			final File file = convertMultiPartFileToFile(multipartFile);
			String fileKey = docid+multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().indexOf("."));
			uploadFileToS3Bucket(companyCode,getBucketName(companyCode), file,fileKey);
			//System.out.println("File upload is completed.");
			file.delete();	// To remove the file locally created in the project folder.
		} catch (final AmazonServiceException ex) {
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
		AmazonS3 s3client = getAmazonS3Cient(companyCode);
		try {
			return IOUtils.toByteArray(s3client.getObject(getBucketName(companyCode), getDocFolder(companyCode, filename)).getObjectContent());
		} catch (SdkClientException | IOException e) {
			e.printStackTrace();
			throw new ApplicationException("Document not available");
		}finally {
			s3client.shutdown();
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

	private void uploadFileToS3Bucket(String companyCode,final String bucketName, final File file, String fileKey) {
		//System.out.println("Uploading file with name= " + fileKey);
		try{
			AmazonS3 s3client = getAmazonS3Cient(companyCode);
			final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, getDocFolder(companyCode, fileKey), file);
			s3client.putObject(putObjectRequest);
			s3client.shutdown();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	public void deleteFileS3Bucket(final String companyCode, String docid) {
		//System.out.println("deleteFileS3Bucket#filename#" + docid);
		try {
			AmazonS3 s3client = getAmazonS3Cient(companyCode);
			s3client.deleteObject(getBucketName(companyCode), getDocFolder(companyCode, docid));
			s3client.shutdown();
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public AmazonS3 getAmazonS3Cient(String companycode) {
		System.out.println(accessKey + " -- " + awssecret);
		final BasicAWSCredentials basicAWSCredentials = new BasicAWSCredentials(accessKey,awssecret);
		// Get AmazonS3 client and return the s3Client object.
		return AmazonS3ClientBuilder
				.standard()
				.withRegion(Regions.fromName(getRegion(companycode) ))
				.withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
				.build();
	}

	private String getRegion(String companycode) {
		return "us-east-2"; //AppProperties.getInstance().getPropery(Constants.S3_REGION);
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
	
	public static void main(String args[]) {
		AWSS3ServiceImpl service = new AWSS3ServiceImpl("","");
		//AmazonS3 s3client = service.getAmazonS3Cient("easygst");
		//IOUtils.toByteArray(s3client.getObject("zanflow-docs", "299").getObjectContent());
		//s3client.putObject("zanflow-docs", "easygst/", IOUtils.toByteArray(s3client.getObject("zanflow-docs", "299").getObjectContent()));
		//s3client.getObject("zanflow-easygst", "299").getObjectContent();
		try {
			service.getDocument("easygst", "111.pdf");
		} catch (ApplicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void uploadFile(byte[] data, String filename, String companyCode, long docid) {
		// TODO Auto-generated method stub
		
	}

}
