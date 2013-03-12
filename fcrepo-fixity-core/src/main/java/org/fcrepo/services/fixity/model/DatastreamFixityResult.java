package org.fcrepo.services.fixity.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "datastream_results")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatastreamFixityResult {

	@XmlEnum
	public enum ResultType {
		SUCCESS, ERROR;
	}

	public static DatastreamFixityResult newError(String datastreamId, Date timestamp, String details) {
		return new DatastreamFixityResult(ResultType.ERROR, timestamp, datastreamId, details);
	}

	public static DatastreamFixityResult newSuccess(String datastreamId, Date timestamp, String details) {
		return new DatastreamFixityResult(ResultType.SUCCESS, timestamp, datastreamId, details);
	}

	public DatastreamFixityResult() {
		super();
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

	private DatastreamFixityResult(ResultType type, Date timestamp, String datastreamId, String details) {
		super();
		this.type = type;
		this.timestamp = timestamp;
		this.datastreamId = datastreamId;
		this.details = details;
	}

	private DatastreamFixityResult(ResultType type, Date timestamp, String datastreamId) {
		super();
		this.type = type;
		this.timestamp = timestamp;
		this.datastreamId = datastreamId;
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

	public void setTimestamp(Date timestamp) {
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

}
