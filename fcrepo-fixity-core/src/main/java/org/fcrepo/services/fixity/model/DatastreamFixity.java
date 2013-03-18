package org.fcrepo.services.fixity.model;

import java.net.URI;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlTransient;

import org.fcrepo.utils.FixityResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@Table(name = "datastream_results")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatastreamFixity {

	private static final Logger logger = LoggerFactory.getLogger(DatastreamFixity.class);

	@XmlEnum
	public enum ResultType {
		SUCCESS, ERROR;
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
	
	@Transient
	private org.fcrepo.jaxb.responses.management.DatastreamFixity result;
	
    /**
     * This constructor is used to deserialize from the results database
     */
	public DatastreamFixity() {
		super();
	}

	/**
	 * This constructor is used to adapt the repository fixity result to the the db serialization
	 * @param result
	 */
	public DatastreamFixity(org.fcrepo.jaxb.responses.management.DatastreamFixity result) {
		super();
		this.result = result;
		this.datastreamId = result.dsId;
		this.timestamp = result.timestamp;
		this.type = ResultType.SUCCESS;
		for (FixityResult status:result.statuses){
			if (!status.validChecksum){
				details = checksumErrorDetails(result, status.dsChecksumType, status.dsChecksum, status.computedChecksum);
				type = ResultType.ERROR;
				break;
			}
			if (!status.validSize){
				details = sizeErrorDetails(result, status.dsSize, status.computedSize);
				type = ResultType.ERROR;
				break;
			}
		}
		if (type != ResultType.ERROR) {
			details = successDetails(result);
		}
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

	private static String successDetails(org.fcrepo.jaxb.responses.management.DatastreamFixity result) {
		String details = "Success for Checksum: " + result.statuses.get(0).dsChecksum;
		return details;
	}
	
	private static String checksumErrorDetails(org.fcrepo.jaxb.responses.management.DatastreamFixity result, String type, URI expected, URI actual) {
		String details = "The calculated checksums of type " + type + " for the datastream " + result.dsId
				+ " of the object "
				+ result.objectId + " does not match the saved value: [" + actual + " != " + expected + " (expected)]";
		return details;
	}
	
	private static String sizeErrorDetails(org.fcrepo.jaxb.responses.management.DatastreamFixity result, long expected, long actual) {
    	String details = "The calculated size for datastream " + result.dsId + " of the object " + result.objectId +
    			         " does not match the saved value: [" + actual + " != " + expected + "(expected)]"; 
		return details;
	}
}
