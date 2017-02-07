package br.com.example.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserProfilePermission implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4054176758680817517L;
	
	private User user;
	private List<ProfilePermission> profilePermissions = new ArrayList<ProfilePermission>();
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<ProfilePermission> getProfilePermissions() {
		return profilePermissions;
	}
	public void setProfilePermissions(List<ProfilePermission> profilePermissions) {
		this.profilePermissions = profilePermissions;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (user == null || profilePermissions == null ? 0 : user.hashCode()+profilePermissions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if(obj instanceof UserProfilePermission){
			if(this.getUser() == null || this.getProfilePermissions() == null) 
				return false;
			if(this.getUser().equals(((UserProfilePermission)obj).getUser())){
				boolean found = false;
				for(ProfilePermission pp : this.getProfilePermissions()){
					UserProfilePermission upp = (UserProfilePermission)obj;		
					found = false;
					for(ProfilePermission ppObj: upp.getProfilePermissions()){
						if(pp.equals(ppObj)){
							found = true;
							break;
						}
					}
					if(!found)
						return false;
				}
				return found;
			}
		}
		
		return false;
	}
	
}
