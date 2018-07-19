package com.cs673.teamA.Iteration2;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Project {
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	private String projectname;
	private Long createuser;
	
	public Project() {
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the projectname
	 */
	public String getProjectname() {
		return projectname;
	}

	/**
	 * @param projectname the projectname to set
	 */
	public void setProjectname(String projectname) {
		this.projectname = projectname;
	}

	/**
	 * @return the createuser
	 */
	public Long getCreateuser() {
		return createuser;
	}

	/**
	 * @param createuser the createuser to set
	 */
	public void setCreateuser(Long createuser) {
		this.createuser = createuser;
	}

	
}
