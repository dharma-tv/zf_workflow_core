package com.zanflow.sec.dao;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Query;

import com.zanflow.bpmn.model.Membership;
import com.zanflow.sec.common.db.JPATransaction;
import com.zanflow.sec.common.db.JPersistenceProvider;
import com.zanflow.sec.common.exception.ApplicationException;
import com.zanflow.sec.common.exception.JPAPersistenceException;
import com.zanflow.sec.dto.UserDTO;
import com.zanflow.sec.model.CompanyProfile;
import com.zanflow.sec.model.Department;
import com.zanflow.sec.model.Leads;
import com.zanflow.sec.model.Location;
import com.zanflow.sec.model.Role;
import com.zanflow.sec.model.User;

public class UserMgmtDAO extends JPATransaction implements java.lang.AutoCloseable{

	public UserMgmtDAO(String pJunitName)throws ApplicationException
	{
		//System.out.println("#UserMgmtDAO#pJunitName#"+pJunitName);
		try {
            if(pJunitName != null && pJunitName.trim().length() > 0){
           	 strJPUnitName = pJunitName;
            }
            else {
           	 throw new ApplicationException("Arguments are NULL, Unable to create JPersistenceProvider.");
            }
            //System.out.println("#pJunitName#"+strJPUnitName);
       	 	objJProvider = new JPersistenceProvider(strJPUnitName);
		} catch (ApplicationException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (EntityExistsException e) {
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} 
	}	


	public UserMgmtDAO(JPersistenceProvider objJProvider) throws ApplicationException
	{
		try{
			if (objJProvider != null)
			{
				this.objJProvider = objJProvider;
				strJPUnitName = objJProvider.getpUnitName();
				//System.out.println("# UserMgmtDAO objJProvider constructor invoked");
			}else{
				throw new ApplicationException("UserMgmtDAO : JPersistenceProvider is null");
			}
		} catch (EntityNotFoundException e) {
			//log.printErrorMessage("# EntityNotFoundException #"+e.getMessage());
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (EntityExistsException e) {
			//log.printErrorMessage("# EntityExistsException #"+e.getMessage());
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		} catch (JPAPersistenceException e) {
			//log.printErrorMessage("# JPAPersistenceException #"+e.getMessage());
			e.printStackTrace();
			throw new ApplicationException(new Exception(e));
		}	
	}
	
	public List<Role> getRoles(String companyCode,String status)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#UserMgmtDAO#getRoles#"+companyCode+"#"+status);
		List<Role> roleList=null;
		try
		{
			Query objQuery=objJProvider.createQuery("select t from Role t where companyCode=:companyCode and status=:status");
			objQuery.setParameter("companyCode", companyCode);
			objQuery.setParameter("status", status);
			roleList=objQuery.getResultList();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#getRoles#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return roleList;
	  }
	
	
	public Role getRole(String companyCode,String roleid)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#UserMgmtDAO#getRole#"+companyCode+"#"+roleid);
		List<Role> roleList=null;
		try
		{
			Query objQuery=objJProvider.createQuery("select t from Role t where companyCode=:companyCode and roleId=:roleId");
			objQuery.setParameter("companyCode", companyCode);
			objQuery.setParameter("roleId", roleid);
			roleList=objQuery.getResultList();
			if (roleList.size()>0) {
				return roleList.get(0);
			}else {
				throw new ApplicationException("No role found for roleid "+roleid+"& companycode"+companyCode,1);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#getRoles#End#TT#" + (System.currentTimeMillis() - t1));
		}
	  }
	
	public Role createRole(Role objRole) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		Role newRole = null;
		try {
			newRole =(Role) objJProvider.merge((Role) objRole);
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e.getMessage());
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#createRole#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return newRole;
	}
	
	public Department createDepartment(Department objDepartment) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		Department newDepartment = null;
		try {
			newDepartment =(Department) objJProvider.merge((Department) objDepartment);
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e.getMessage());
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#createRole#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return newDepartment;
	}
	
	
	public void deleteDepartment(String companyCode,String departmentId) throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#UserMgmtDAO#deleteDepartment#"+companyCode+"#"+roleId);
		int iupdateCount=0;
		try{
			Query q=objJProvider.createNativeQuery("DELETE FROM {h-schema}ZF_ID_DEPARTMENT where companyCode=?1 and departmentId=?2");
			q.setParameter(1,companyCode);
			q.setParameter(2,departmentId);
			iupdateCount=q.executeUpdate();
			
		}catch(Exception e){
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e);
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#deleteDepartment#End#TT#" + (System.currentTimeMillis() - t1));
		}
	}
	
	public Location createLocation(Location objLocation) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		Location newLocation = null;
		try {
			newLocation =(Location) objJProvider.merge((Location) objLocation);
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e.getMessage());
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#createRole#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return newLocation;
	}
	
	public void deleteLocation(String companyCode,String locationId) throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#UserMgmtDAO#deleteDepartment#"+companyCode+"#"+roleId);
		int iupdateCount=0;
		try{
			Query q=objJProvider.createNativeQuery("DELETE FROM {h-schema}ZF_ID_LOCATION where companyCode=?1 and locationId=?2");
			q.setParameter(1,companyCode);
			q.setParameter(2,locationId);
			iupdateCount=q.executeUpdate();
			
		}catch(Exception e){
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e);
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#deleteDepartment#End#TT#" + (System.currentTimeMillis() - t1));
		}
	}
	
	public void deleteRole(String companyCode,String roleId) throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#UserMgmtDAO#deleteRole#"+companyCode+"#"+roleId);
		int iupdateCount=0;
		try{
			Query q=objJProvider.createNativeQuery("DELETE FROM {h-schema}ZF_ID_ROLE where companyCode=?1 and roleId=?2");
			q.setParameter(1,companyCode);
			q.setParameter(2,roleId);
			iupdateCount=q.executeUpdate();
			//System.out.println("#UserMgmtDAO#deleteRole#ZF_ID_ROLE count#"+iupdateCount);
			iupdateCount=0;
			Query q1=objJProvider.createNativeQuery("DELETE FROM {h-schema}ZF_ID_MEMBERSHIP where roleId=?1");
			q1.setParameter(1,roleId);
			iupdateCount=q1.executeUpdate();
			//System.out.println("#UserMgmtDAO#deleteRole#ZF_ID_MEMBERSHIP count#"+iupdateCount);
			
		}catch(Exception e){
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e);
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#deleteRole#End#TT#" + (System.currentTimeMillis() - t1));
		}
	}
	
	public User createUser(User objUser) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		User newUser = null;
		try {
			newUser =(User) objJProvider.merge((User) objUser);
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e.getMessage());
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#createUser#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return newUser;
	}
	
	
	public User findUser(String userId) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		User newUser = null;
		try {
			newUser =(User) objJProvider.find(User.class, userId);
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e.getMessage());
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#createUser#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return newUser;
	}
	
	
	public User validateUSer(UserDTO userDto) throws ApplicationException {
		long t1 = System.currentTimeMillis();
		try {
			Query objQuery = objJProvider
					.createQuery("select t from User t where status='A' and  userId=:userId and password=:password");
			objQuery.setParameter("userId", userDto.getUserId());
			objQuery.setParameter("password", userDto.getPassword());
			List<User> roleList = objQuery.getResultList();
			//System.out.println("#User#" +roleList);
			if (roleList.size() > 0) {
				return roleList.get(0);
			} else {
				throw new ApplicationException("Invalid userid/password", 1);
			}
		} finally {
			//System.out.println("#UserMgmtDAO#updateUser#End#TT#" + (System.currentTimeMillis() - t1));
		}
	}
	
	public User validateGoogleUser(UserDTO userDto) throws ApplicationException {
		long t1 = System.currentTimeMillis();
		try {
			Query objQuery = objJProvider
					.createQuery("select t from User t where status='A' and  userId=:userId");
			objQuery.setParameter("userId", userDto.getUserId());
			List<User> roleList = objQuery.getResultList();
			//System.out.println("#User#" +roleList);
			if (roleList.size() > 0) {
				return roleList.get(0);
			} else {
				throw new ApplicationException("Invalid userid/password", 1);
			}
		} finally {
			//System.out.println("#UserMgmtDAO#updateUser#End#TT#" + (System.currentTimeMillis() - t1));
		}
	}
	
	public User validateUSer(String username,String password) throws ApplicationException {
		long t1 = System.currentTimeMillis();
		try {
			Query objQuery = objJProvider
					.createQuery("select t from User t where status='A' and  userId=:userId and password=:password");
			objQuery.setParameter("userId", username);
			objQuery.setParameter("password",password);
			List<User> roleList = objQuery.getResultList();
			if (roleList.size() > 0) {
				return roleList.get(0);
			} else {
				throw new ApplicationException("Invalid userid/password", 1);
			}
		} finally {
			//System.out.println("#UserMgmtDAO#updateUser#End#TT#" + (System.currentTimeMillis() - t1));
		}
	}
	
	public void updateUser(User objUser) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		try {
			objJProvider.merge((User) objUser);
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e.getMessage());
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#updateUser#End#TT#"+ (System.currentTimeMillis() - t1));
		}
	}
	
	
	public void deleteUser(String companyCode,String userId) throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#UserMgmtDAO#deleteRole#"+companyCode+"#"+userId);
		int iupdateCount=0;
		try{
			Query q=objJProvider.createNativeQuery("DELETE FROM {h-schema}ZF_ID_USER where companyCode=?1 and userId=?2");
			q.setParameter(1,companyCode);
			q.setParameter(2,userId);
			iupdateCount=q.executeUpdate();
			//System.out.println("#UserMgmtDAO#deleteUser#ZF_ID_USER count#"+iupdateCount);	
		}catch(Exception e){
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e);
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#deleteUser#End#TT#" + (System.currentTimeMillis() - t1));
		}
	}
	
	public List<User> getAllUsers(String companyCode)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#UserMgmtDAO#getAllUsers#"+companyCode);
		List<User> userList=null;
		try
		{
			Query objQuery=objJProvider.createQuery("select t from User t where companyCode=:companyCode");
			objQuery.setParameter("companyCode", companyCode);
			userList=objQuery.getResultList();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#getAllUsers#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return userList;
	  }
	
	public List<Membership> getAllUsersRole(String roleId)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#UserMgmtDAO#getAllUsersRole#"+roleId);
		List<Membership> userList=null;
		try
		{
			Query objQuery=objJProvider.createQuery("select t from Membership t where roleId=:roleId");
			objQuery.setParameter("roleId", roleId);
			userList=objQuery.getResultList();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#getAllUsers#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return userList;
	  }
	
	public List<Membership> getCompUsersRole(String companyCode)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#UserMgmtDAO#companyCode#"+companyCode);
		List<Membership> membership=null;
		try
		{
			Query objQuery=objJProvider.createQuery("select t from Membership t,{h-schema}User u where u.companyCode=:companyCode and t.userId=u.userId");
			objQuery.setParameter("companyCode", companyCode);
			membership=objQuery.getResultList();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#getAllUsers#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return membership;
	  }
	public List<Membership> getCompUsersRole(String roleId,String companyCode)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#UserMgmtDAO#companyCode#"+companyCode);
		List<Membership> membership=null;
		try
		{
			Query objQuery=objJProvider.createQuery("select t from Membership t where t.companyCode=:companyCode and t.roleId=:roleId");
			objQuery.setParameter("companyCode", companyCode);
			objQuery.setParameter("roleId", roleId);
			
			membership=objQuery.getResultList();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#getAllUsers#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return membership;
	  }
	public List<Role> getCompRoles(String companyCode)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#UserMgmtDAO#companyCode#"+companyCode);
		List<Role> roles=null;
		try
		{
			Query objQuery=objJProvider.createQuery("select t from Role t where t.companyCode=:companyCode");
			objQuery.setParameter("companyCode", companyCode);
			roles=objQuery.getResultList();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#getAllUsers#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return roles;
	  }
	
	public List<Department> getCompDepartments(String companyCode)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#UserMgmtDAO#companyCode#"+companyCode);
		List<Department> departments=null;
		try
		{
			Query objQuery=objJProvider.createQuery("select t from Department t where t.companyCode=:companyCode");
			objQuery.setParameter("companyCode", companyCode);
			departments=objQuery.getResultList();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#getAllUsers#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return departments;
	  }
	
	public List<Location> getCompLocations(String companyCode)throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#UserMgmtDAO#companyCode#"+companyCode);
		List<Location> locations=null;
		try
		{
			Query objQuery=objJProvider.createQuery("select t from Location t where t.companyCode=:companyCode");
			objQuery.setParameter("companyCode", companyCode);
			locations=objQuery.getResultList();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			//log.printErrorMessage(ex);
			throw new ApplicationException(ex);
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#getAllUsers#End#TT#" + (System.currentTimeMillis() - t1));
		}
		return locations;
	  }
	public Membership createUserRole(Membership objUser) throws ApplicationException 
	{
		long t1 = System.currentTimeMillis();
		Membership newUser = null;
		try {
			newUser =(Membership) objJProvider.merge((Membership) objUser);
		} catch (Exception e) {
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e.getMessage());
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#createUserRole#End#TT#"+ (System.currentTimeMillis() - t1));
		}
		return newUser;
	}
	
	public void deleteUserRole(String roleId,String userId) throws ApplicationException
	{
		long t1 = System.currentTimeMillis();
		//System.out.println("#UserMgmtDAO#deleteUserRole#"+roleId+"#"+userId);
		int iupdateCount=0;
		try{
			Query q=objJProvider.createNativeQuery("DELETE FROM {h-schema}ZF_ID_MEMBERSHIP where roleId=?1 and userId=?2");
			q.setParameter(1,roleId);
			q.setParameter(2,userId);
			iupdateCount=q.executeUpdate();
			//System.out.println("#UserMgmtDAO#deleteUserRole#ZF_ID_MEMBERSHIP count#"+iupdateCount);	
		}catch(Exception e){
			e.printStackTrace();
			//log.printErrorMessage(e);
			throw new ApplicationException(e);
		}
		finally
		{
			//System.out.println("#UserMgmtDAO#deleteUserRole#End#TT#" + (System.currentTimeMillis() - t1));
		}
	}
	
	public List<CompanyProfile> validateCompany(CompanyProfile appIntModel) throws ApplicationException {
		try {
			Query query = objJProvider.createQuery("select cp from CompanyProfile cp where (companyMailId=:mailid)");
			query.setParameter("mailid", appIntModel.getCompanyMailId());
			//query.setParameter("companyCode", appIntModel.getCompanyCode());
			List<CompanyProfile> companyLst= (List<CompanyProfile>)query.getResultList();
			return companyLst;
		}catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}
	}
	
	public CompanyProfile getCompany(String companycode) throws ApplicationException {
		try {
			Query query = objJProvider.createQuery("select cp from CompanyProfile cp where (companycode=:companycode)");
			query.setParameter("companycode", companycode);
			//query.setParameter("companyCode", appIntModel.getCompanyCode());
			List<CompanyProfile> companyLst= (List<CompanyProfile>)query.getResultList();
			return companyLst.get(0);
		}catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}
	}
	
	public Boolean checkActivationCode(String key,String emailId) throws ApplicationException {
		try {
			
			Query query = objJProvider.createQuery("select ld from Leads ld where attempts!='3' and userid=:userid and activationcode=:activationkey");
			query.setParameter("activationkey", key);
			query.setParameter("userid", emailId);
			List<Leads> lead= (List<Leads>)query.getResultList();
			if (lead.size() == 0) {
				Query query1 = objJProvider.createQuery("select ld from Leads ld where userid=:userid");
				query1.setParameter("userid", emailId);
				List<Leads> leaddtl= (List<Leads>)query1.getResultList();
				if(Integer.parseInt(leaddtl.get(0).getAttempts())<3) {
					String attempts = String.valueOf(Integer.parseInt(leaddtl.get(0).getAttempts()) + 1);
					objJProvider.begin();
					Query queryAct = objJProvider.createQuery("update Leads set attempts=:attempts where userid=:userid");
					queryAct.setParameter("attempts", attempts);
					queryAct.setParameter("userid", emailId);
					queryAct.executeUpdate();
					objJProvider.commit();
				}
				throw new ApplicationException("Activation code is wrong.");
			}else {
				return true;
			}
		}catch (Exception e) {
			e.printStackTrace();
			throw new ApplicationException(e);
		}
	}
}
