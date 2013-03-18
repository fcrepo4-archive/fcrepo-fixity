package org.fcrepo.services.fixity.model;

import java.util.ArrayList;
import java.util.Collection;
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

import org.fcrepo.services.fixity.model.DatastreamFixity.ResultType;

@XmlRootElement(name = "fixity-result")
@XmlAccessorType(XmlAccessType.FIELD)
@Entity
@Table(name = "fixity_results")
public class ObjectFixity {

	@GeneratedValue
	@Id
	@XmlAttribute(name = "record-id")
	private long id;

	@XmlAttribute(name = "pid")
	private String pid;

	@XmlAttribute(name = "success")
	private boolean success;
	
	@XmlAttribute(name = "repaired")
	private boolean repaired;

	@XmlAttribute(name = "timestamp")
	private Date timestamp;

	@XmlElementWrapper(name = "successes")
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "SUCCESS_ID")
	private List<DatastreamFixity> successes;

	@XmlElementWrapper(name = "errors")
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "ERROR_ID")
	private List<DatastreamFixity> errors;

	public ObjectFixity() {
		super();
	}

	public ObjectFixity(String pid, Date timestamp, List<DatastreamFixity> successes,
			List<DatastreamFixity> errors) {
		super();
		this.pid = pid;
		this.timestamp = timestamp;
		this.success = (errors.size() == 0);
		this.repaired = (!success);
		for (DatastreamFixity df:errors){
			this.repaired &= (df.getType() == ResultType.REPAIRED);
		}
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

	public List<DatastreamFixity> getSuccesses() {
		return successes;
	}

	public void setSuccesses(List<DatastreamFixity> successes) {
		this.successes = successes;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	public void setRepaired(boolean repaired) {
		this.repaired = repaired;
	}

	public void setErrors(List<DatastreamFixity> errors) {
		this.errors = errors;
	}

	public boolean isSuccess() {
		return success;
	}
	
	public boolean isRepaired() {
		return repaired;
	}

	public List<DatastreamFixity> getErrors() {
		return errors;
	}

}