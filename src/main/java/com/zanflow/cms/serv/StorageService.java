package com.zanflow.cms.serv;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {


	void uploadFile(MultipartFile multipartFile, String bucketName, long docid);

	void uploadFile(byte[] data, String filename, String companyCode, long docid);
}
