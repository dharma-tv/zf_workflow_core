package com.zanflow.bpmn.dto;

import java.io.Serializable;
import java.util.List;

public class TXNDocumentListDTO  extends ResponseDTO implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	protected List<TXNDocumentDTO> txnDocList=null;

	public List<TXNDocumentDTO> getTxnDocList() {
		return txnDocList;
	}

	public void setTxnDocList(List<TXNDocumentDTO> txnDocList) {
		this.txnDocList = txnDocList;
	}
	
}
