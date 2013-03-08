package org.fcrepo.services.fixity.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "fixity_errors")
public class FixityError {
	@XmlTransient
	@Id
	@GeneratedValue
	private long id;


	@XmlAttribute(name = "timestamp")
	private Date timestamp;

	@XmlAttribute(name = "datastream-id")
	private String datastreamId;

	@XmlElement(name = "details")
	private String details;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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
