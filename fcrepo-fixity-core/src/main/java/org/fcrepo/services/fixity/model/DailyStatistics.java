package org.fcrepo.services.fixity.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


@Entity
@Table(name = "stat_daily")
@XmlRootElement(name = "stat-daily")
@XmlAccessorType(XmlAccessType.FIELD)
public class DailyStatistics {
	@GeneratedValue
	@Id
	@XmlTransient
	private long id;
	@Temporal(TemporalType.DATE)
	@XmlAttribute(name = "date")
	private Date date;
	@XmlAttribute(name = "errorCount")
	private int errorCount;
	@XmlAttribute(name = "successCount")
	private int successCount;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getErrorCount() {
		return errorCount;
	}

	public void setErrorCount(int errorCount) {
		this.errorCount = errorCount;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

}
