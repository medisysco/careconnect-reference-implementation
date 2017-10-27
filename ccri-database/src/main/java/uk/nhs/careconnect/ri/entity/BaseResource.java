package uk.nhs.careconnect.ri.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;


@MappedSuperclass
public abstract class BaseResource {

	//private static final int MAX_PROFILE_LENGTH = 1000;
	
	
	@Column(name = "RES_UPDATED",insertable=false, updatable=false)
	@UpdateTimestamp
	private Date resUpdated;
	public Date getUpdated() {
		return this.resUpdated;
	}
   /* public void setUpdated(Date resUpdated) {
        this.resUpdated=resUpdated;
    }
    */
	
	@Column(name = "RES_CREATED", nullable = true)
	@CreationTimestamp
	private Date resCreated;
	public Date getCreated() {
		return this.resCreated;
	}
	
	
	@Column(name = "RES_DELETED", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date resDeleted;
	public Date getDeleted() {
		return this.resDeleted;
	}
	public void setDeleted(Date theDate) {
		this.resDeleted = theDate;
	}
	
	@Column(name = "RES_MESSAGE_REF", nullable = true)
	
	private String resMessage;
	public String getResourceMessage() {
		return this.resMessage;
	}
	public void setResourceMessage(String resMessage) {
		this.resMessage = resMessage;
	}


}
