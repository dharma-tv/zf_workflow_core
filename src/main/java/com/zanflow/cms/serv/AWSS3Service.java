package com.zanflow.cms.serv;

import org.springframework.web.multipart.MultipartFile;

public interface AWSS3Service {


	void uploadFile(MultipartFile multipartFile, String bucketName, long docid);
}
