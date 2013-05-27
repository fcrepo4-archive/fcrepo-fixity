package org.fcrepo.services.fixity.model;

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

}
