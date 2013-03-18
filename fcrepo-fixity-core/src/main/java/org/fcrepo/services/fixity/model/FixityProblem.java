package org.fcrepo.services.fixity.model;

import java.net.URI;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.fcrepo.services.fixity.model.DatastreamFixity.ResultType;

@Entity
@Table(name = "cache_problems")
@XmlAccessorType(XmlAccessType.FIELD)
public class FixityProblem {
	@Id
	@GeneratedValue
	@XmlAttribute(name = "record-id")
	private long id;
	
	@XmlAttribute(name = "type")
	public ResultType type;

	@XmlAttribute(name = "cache-id")
	public String cacheId;
	
	@XmlElement(name = "details")
	public String details;
	
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
