package uk.nhs.careconnect.ri.entity.practitioner;

import uk.nhs.careconnect.ri.entity.BaseIdentifier;

import javax.persistence.*;


@Entity
@Table(name="PractitionerIdentifier",
		uniqueConstraints= @UniqueConstraint(name="PK_PRACTITIONER_IDENTIFIER", columnNames={"PRACTITIONER_IDENTIFIER_ID"})
		,indexes =
		{
				@Index(name = "IDX_PRACTITIONER_IDENTIFER", columnList="value,SYSTEM_ID")

		})
public class PractitionerIdentifier extends BaseIdentifier {

	public PractitionerIdentifier() {

	}

	public PractitionerIdentifier(PractitionerEntity practitionerEntity) {
		this.practitionerEntity = practitionerEntity;
	}
	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "PRACTITIONER_IDENTIFIER_ID")
	private Integer identifierId;

	@ManyToOne
	@JoinColumn (name = "PRACTITIONER_ID",foreignKey= @ForeignKey(name="FK_PRACTITIONER_IDENTIFIER_PRACTITIONER_ID"))
	private PractitionerEntity practitionerEntity;


    public Integer getIdentifierId() { return identifierId; }
	public void setIdentifierId(Integer identifierId) { this.identifierId = identifierId; }

	public PractitionerEntity getPractitioner() {
	        return this.practitionerEntity;
	}
	public void setPractitioner(PractitionerEntity practitionerEntity) {
	        this.practitionerEntity = practitionerEntity;
	}


}
