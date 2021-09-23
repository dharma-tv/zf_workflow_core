package com.zanflow.bpmn.model.pk;

import java.io.Serializable;

import javax.persistence.Id;

public class MembershipPK implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	protected String userId;
	@Id
	protected String roleId;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRoleId() {
		return roleId;
	}
	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}
	
	public int hashCode()
	{
		return (int) this.userId.hashCode()+this.roleId.hashCode();
	}
	
	public boolean equals(Object ob)
	{
		if(ob==null)
		{
			return false;
		}
		if(!(ob instanceof MembershipPK))
		{
			return false;
		}
		if(ob== this)
		{
			return true;
		}
		MembershipPK objPk=(MembershipPK) ob;
		return (this.getUserId().equals(objPk.getUserId())&& this.getRoleId().equals(objPk.getRoleId()));
	}
}
