package br.com.example.model;

import java.io.Serializable;

public class ProfilePermission implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -723949372515121393L;
	private Profile profile;
	private Permission permission;

	public Profile getProfile() {
		return profile;
	}

	public void setProfile(Profile profile) {
		this.profile = profile;
	}

	public Permission getPermission() {
		return this.permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((profile.getId() == null) ? 0
						: profile.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if(obj instanceof ProfilePermission){
			if(this.getProfile() == null || this.getPermission() == null) 
				return false;
			if(this.getProfile().equals(((ProfilePermission)obj).getProfile()))
					return true;
		}
		
		return false;
	}
}
