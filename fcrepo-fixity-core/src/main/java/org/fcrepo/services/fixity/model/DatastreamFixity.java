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

@Entity
@Table(name = "datastream_results")
@XmlAccessorType(XmlAccessType.FIELD)
public class DatastreamFixity {

	@XmlEnum
	public enum ResultType {
		SUCCESS, ERROR;
	}

//	public static DatastreamFixity newError(String datastreamId, Date timestamp, String details) {
//		return new DatastreamFixity(ResultType.ERROR, timestamp, datastreamId, details);
//	}
//
//	public static DatastreamFixity newSuccess(String datastreamId, Date timestamp, String details) {
//		return new DatastreamFixity(ResultType.SUCCESS, timestamp, datastreamId, details);
//	}

	@Id
	@GeneratedValue
	@XmlAttribute(name = "record-id")
	private long id;

	private ResultType type;

	private String details;
	
	@Transient
	private org.fcrepo.jaxb.responses.management.DatastreamFixity result;
	
	public DatastreamFixity() {
		super();
	}

	public DatastreamFixity(org.fcrepo.jaxb.responses.management.DatastreamFixity result) {
		super();
		this.result = result;
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

//	private DatastreamFixity(ResultType type, Date timestamp, String datastreamId, String details) {
//		super();
//		this.type = type;
//		this.timestamp = timestamp;
//		this.datastreamId = datastreamId;
//		this.details = details;
//	}
//
//	private DatastreamFixity(ResultType type, Date timestamp, String datastreamId) {
//		super();
//		this.type = type;
//		this.timestamp = timestamp;
//		this.datastreamId = datastreamId;
//	}

	public long getId() {
		return id;
	}

	@XmlAttribute(name = "type")
	public ResultType getType() {
		return type;
	}

	@XmlAttribute(name = "timestamp")
	public Date getTimestamp() {
		return (result != null) ? result.timestamp : null;
	}

	@XmlAttribute(name = "datastream-id")
	public String getDatastreamId() {
		return (result != null) ? result.dsId : null;
	}

	@XmlElement(name = "details")
	public String getDetails() {
		return details;
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
    			         " does not match the saved value: [" + expected + " != " + actual + "(expected)]"; 
		return details;
	}
}
