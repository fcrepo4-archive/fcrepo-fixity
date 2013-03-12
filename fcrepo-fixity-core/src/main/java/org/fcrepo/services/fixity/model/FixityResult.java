package org.fcrepo.services.fixity.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "fixity-result")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "fixity_results")
public class FixityResult {

	@GeneratedValue
	@Id
	@XmlAttribute(name = "record-id")
	private long id;

	@XmlAttribute(name = "pid")
	private String pid;

	@XmlAttribute(name = "success")
	private boolean success;

	@XmlAttribute(name = "timestamp")
	private Date timestamp;

	@XmlElementWrapper(name = "successes")
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "SUCCESS_ID")
	private List<DatastreamFixityResult> successes;

	@XmlElementWrapper(name = "errors")
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "ERROR_ID")
	private List<DatastreamFixityResult> errors;

	public FixityResult() {
		super();
	}

	public FixityResult(String pid, Date timestamp, List<DatastreamFixityResult> successes, List<DatastreamFixityResult> errors) {
		super();
		this.pid = pid;
		this.timestamp = timestamp;
		this.success = (errors.size() == 0);
		this.errors = errors;
		this.successes = successes;
	}

	public long getId() {
		return id;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getPid() {
		return pid;
	}

	public List<DatastreamFixityResult> getSuccesses() {
		return successes;
	}

	public void setSuccesses(List<DatastreamFixityResult> successes) {
		this.successes = successes;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setErrors(List<DatastreamFixityResult> errors) {
		this.errors = errors;
	}

	public boolean isSuccess() {
		return success;
	}

	public List<DatastreamFixityResult> getErrors() {
		return errors;
	}

}