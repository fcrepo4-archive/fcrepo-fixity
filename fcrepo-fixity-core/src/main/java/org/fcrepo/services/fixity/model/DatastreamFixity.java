package org.fcrepo.services.fixity.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlTransient;

import org.fcrepo.utils.FixityResult;
import org.fcrepo.utils.FixityResult.FixityState;
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
		EnumSet<FixityState> aggregateStatus = EnumSet.noneOf(FixityResult.FixityState.class);
		aggregateStatus.add(FixityResult.FixityState.SUCCESS);
		for (FixityResult status:result.statuses){
			if ((status.status.contains(FixityResult.FixityState.BAD_CHECKSUM))){
				aggregateStatus.remove(FixityResult.FixityState.SUCCESS);
				aggregateStatus.add(FixityResult.FixityState.BAD_CHECKSUM);
				FixityProblem problem = new FixityProblem();
				problem.cacheId = status.storeIdentifier;
				problem.details = checksumErrorDetails(result, status.dsChecksumType, status.dsChecksum, status.computedChecksum);
				if (!(status.status.contains(FixityResult.FixityState.REPAIRED))) {
					aggregateStatus.remove(FixityResult.FixityState.REPAIRED);
					problem.type = ResultType.ERROR;
				} else {
					if (!aggregateStatus.contains(FixityResult.FixityState.BAD_CHECKSUM)
					 && !aggregateStatus.contains(FixityResult.FixityState.BAD_SIZE)){
						aggregateStatus.add(FixityResult.FixityState.REPAIRED);
					}
					aggregateStatus.add(FixityResult.FixityState.BAD_CHECKSUM);
					problem.type = ResultType.REPAIRED;
				}
				this.problems.add(problem);
			}
			if ((status.status.contains(FixityResult.FixityState.BAD_SIZE))){
				aggregateStatus.remove(FixityResult.FixityState.SUCCESS);
				FixityProblem problem = new FixityProblem();
				problem.cacheId = status.storeIdentifier;
				problem.details = checksumErrorDetails(result, status.dsChecksumType, status.dsChecksum, status.computedChecksum);
				if (!(status.status.contains(FixityResult.FixityState.REPAIRED))) {
					aggregateStatus.remove(FixityResult.FixityState.REPAIRED);
					problem.type = ResultType.ERROR;
				} else {
					if (!aggregateStatus.contains(FixityResult.FixityState.BAD_CHECKSUM)
					 && !aggregateStatus.contains(FixityResult.FixityState.BAD_SIZE)){
						aggregateStatus.add(FixityResult.FixityState.REPAIRED);
					}
					problem.type = ResultType.REPAIRED;
				}
				aggregateStatus.add(FixityResult.FixityState.BAD_SIZE);
				this.problems.add(problem);
			}
		}
		if (aggregateStatus.contains(FixityResult.FixityState.SUCCESS)) {
			type = ResultType.SUCCESS;
			details = successDetails(result);
		}
		else if (aggregateStatus.contains(FixityResult.FixityState.REPAIRED)) {
			type = ResultType.REPAIRED;
			details = "There were fixity problems detected, but they were repaired.";
		} else {
			type = ResultType.ERROR;
			details = "There were fixity problems detected, and they were not repaired.";
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
	
	public List<FixityProblem> getProblems() {
		return this.problems;
	}
	
	public void setProblems(List<FixityProblem> problems) {
		this.problems = problems;
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
