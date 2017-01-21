package br.com.alis.model;

import java.io.Serializable;

public class Profile implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4327867353308874534L;
	private Long id;
	private String name;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((id == null ) ? 0
						: id.hashCode() + id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		if(obj instanceof Profile){
			if(this.getId() == null) 
				return false;
			if(this.getId() == ((Profile)obj).getId())
					return true;
		}
		
		return false;
	}
	
}
