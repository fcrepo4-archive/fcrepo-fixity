package org.fcrepo.services.fixity.model;

import java.util.ArrayList;
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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "datastream_results")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatastreamFixity {

	private static final Logger logger = LoggerFactory.getLogger(DatastreamFixity.class);

	@XmlEnum
	public enum ResultType {
		SUCCESS, ERROR, REPAIRED;
	}

	@Id
	@GeneratedValue
	@XmlAttribute(name = "record-id")
	private long id;

	@XmlAttribute(name = "type")
	private ResultType type;

	@XmlAttribute(name = "timestamp")
	private Date timestamp;

	@XmlAttribute(name = "datastream-id")
	private String datastreamId;

	@XmlElement(name = "details")
	private String details;

	@XmlElementWrapper(name = "problems")
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(name = "PROBLEM_ID")
	private List<FixityProblem> problems = new ArrayList<FixityProblem>();


    /**
     * This constructor is used to deserialize from the results database
     */
	public DatastreamFixity() {
		super();
	}

	public long getId() {
		return id;
	}

	public ResultType getType() {
		return type;
	}

	public void setType(ResultType type) {
		this.type = type;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp){
		this.timestamp = timestamp;
	}

	public String getDatastreamId() {
		return datastreamId;
	}

	public void setDatastreamId(String datastreamId) {
		this.datastreamId = datastreamId;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public List<FixityProblem> getProblems() {
		return this.problems;
	}

	public void setProblems(List<FixityProblem> problems) {
		this.problems = problems;
	}
}
