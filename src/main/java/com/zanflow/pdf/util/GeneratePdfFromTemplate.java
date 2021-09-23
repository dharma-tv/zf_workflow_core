package com.zanflow.pdf.util;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.lowagie.text.DocumentException;
import com.zanflow.bpmn.dao.BPMNTaskDAO;
import com.zanflow.bpmn.exception.ApplicationException;
import com.zanflow.bpmn.model.BPMNRptTemplates;
import com.zanflow.bpmn.model.TXNDocments;
import com.zanflow.bpmn.model.pk.BPMNRptTemplatesPK;
import com.zanflow.common.db.JPersistenceProvider;

public class GeneratePdfFromTemplate {

    public String generatePdfFromHtml(String companyCode,String processid,String bpmnid,String templateid,String bpmnTxrefNo,String stepName, Map<String, Object> dataMap) throws IOException, DocumentException, EntityNotFoundException, EntityExistsException, ApplicationException {
        
    	System.out.println("#companyCode#"+companyCode+"#processid#"+processid+"#bpmnid#"+bpmnid+"#templateid#"+templateid+"#bpmnTxrefNo#"+bpmnTxrefNo+"#stepName#"+stepName);
        
    	/**Retrieve the template Details**/
        BPMNRptTemplatesPK templatePk = new BPMNRptTemplatesPK(companyCode,processid,bpmnid,templateid);
        JPersistenceProvider provider = new JPersistenceProvider("zanflowdb");
        BPMNRptTemplates template = (BPMNRptTemplates)provider.find(BPMNRptTemplates.class, templatePk);
        if(template == null &&  template.getTemplateContent() == null) {
        	return null;
        }
    
        /**Write template to local file */
        OutputStream htmlOutputStream = new FileOutputStream(template.getTemplateId()+".html");
        htmlOutputStream.write(template.getTemplateContent().getBytes());
        htmlOutputStream.close();
        
        /**Processing Template to feed txn data and get the actual html content*/
        /**Rendering generated content to create PDF file*/
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(processTemplate(template.getTemplateId(),dataMap));
        renderer.layout();
        renderer.createPDF(outputStream);
        
        OutputStream filestream = new FileOutputStream(template.getTemplateId());
        filestream.write(outputStream.toByteArray());
        filestream.close();
        
        try(BPMNTaskDAO dao = new BPMNTaskDAO(provider)) {
			TXNDocments objTXNDocments=dao.createDocument(bpmnTxrefNo, stepName, "", outputStream.toByteArray(),"", companyCode,"content\\pdf", null);
			System.out.println("#DocID#"+objTXNDocments.getDocumentId());
        } catch (Exception e) {
			e.printStackTrace();
		}
		outputStream.close();
        return null;
    }

    private String processTemplate(String templateName, Map<String, Object> dataMap) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
        Context context = new Context();
        context.setVariable("data", dataMap);
        return templateEngine.process(templateName, context);
	}
}
