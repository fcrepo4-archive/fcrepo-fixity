package org.fcrepo.services.fixity.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
public class FixityCheckResult {

	@XmlTransient
	@GeneratedValue
	@Id
	private long id;

	@XmlAttribute(name = "pid")
	private String pid;

	@XmlAttribute(name = "success")
	private boolean success;

	@XmlElementWrapper(name = "errors")
	@OneToMany(cascade = CascadeType.ALL)
	private List<FixityError> errors;

	@XmlElementWrapper(name = "warnings")
	@OneToMany(cascade = CascadeType.ALL)
	private List<FixityWarning> warnings;

	public FixityCheckResult() {
		super();
	}

	public FixityCheckResult(String pid, boolean success, List<FixityError> errors, List<FixityWarning> warnings) {
		super();
		this.pid = pid;
		this.success = success;
		this.errors = errors;
		this.warnings = warnings;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setErrors(List<FixityError> errors) {
		this.errors = errors;
	}

	public void setWarnings(List<FixityWarning> warnings) {
		this.warnings = warnings;
	}

	public boolean isSuccess() {
		return success;
	}

	public List<FixityError> getErrors() {
		return errors;
	}

	public List<FixityWarning> getWarnings() {
		return warnings;
	}
}
