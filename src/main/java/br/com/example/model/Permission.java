package br.com.example.model;

import java.io.Serializable;

public class Permission implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6217503294182680976L;	
	public static final String PUBLIC = "Público";
	public static final String READ_ONLY = "Somente Leitura";
	public static final String WRITE = "Leitura e escrita";
	
	
	private Long id;
	private String access;
	
	public Permission(){}
	
	public Permission(Long id, String access){
		this.id = id;
		this.access = access;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getAccess() {
		return access;
	}
	public void setAccess(String access) {
		this.access = access;
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
		
		if(obj instanceof Permission){
			if(this.getId() == null) 
				return false;
			if(this.getId() == ((Permission)obj).getId())
					return true;
		}
		
		return false;
	}

}
