package uk.nhs.careconnect.ri.entity.carePlan;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hl7.fhir.dstu3.model.CarePlan;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;

import javax.persistence.*;

@Entity
@Table(name="CarePlanActivityDetail", uniqueConstraints= @UniqueConstraint(name="PK_CAREPLAN_ACTIVITY_DETAIL", columnNames={"CAREPLAN_ACTIVITY_DETAIL_ID"})
		,indexes = {}
		)
public class CarePlanActivityDetail {

	public CarePlanActivityDetail() {
	}
    public CarePlanActivityDetail(CarePlanActivity carePlanActivity) {
		this.carePlanActivity = carePlanActivity;
	}


	@Id 
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name= "CAREPLAN_ACTIVITY_DETAIL_ID")
    private Long carePlanActivityDetailId;

    @ManyToOne
    @JoinColumn (name = "CAREPLAN_ACTIVITY_ID",foreignKey= @ForeignKey(name="FK_CAREPLAN_ACTIVITY_DETAIL_CAREPLAN_ACTIVITY_ID"))
	@LazyCollection(LazyCollectionOption.TRUE)
    private CarePlanActivity carePlanActivity;


	@ManyToOne
	@JoinColumn(name="CATEGORY_CONCEPT_ID",nullable = true,foreignKey= @ForeignKey(name="FK_CAREPLAN_ACTIVITY_DETAIL_CATEGORY_CONCEPT_ID"))
	@LazyCollection(LazyCollectionOption.TRUE)
	private ConceptEntity category;

	@ManyToOne
	@JoinColumn(name="CODE_CONCEPT_ID",nullable = false,foreignKey= @ForeignKey(name="FK_CAREPLAN_ACTIVITY_DETAIL_CODE_CONCEPT_ID"))
	@LazyCollection(LazyCollectionOption.TRUE)
	private ConceptEntity code;

	@Enumerated(EnumType.ORDINAL)
	@Column(name="status")
	CarePlan.CarePlanActivityStatus status;

	public Long getCarePlanActivityDetailId() {
		return carePlanActivityDetailId;
	}

	public void setCarePlanActivityDetailId(Long carePlanActivityDetailId) {
		this.carePlanActivityDetailId = carePlanActivityDetailId;
	}

	public CarePlanActivity getCarePlanActivity() {
		return carePlanActivity;
	}

	public void setCarePlanActivity(CarePlanActivity carePlanActivity) {
		this.carePlanActivity = carePlanActivity;
	}

	public ConceptEntity getCode() {
		return code;
	}

	public void setCode(ConceptEntity code) {
		this.code = code;
	}

	public ConceptEntity getCategory() {
		return category;
	}

	public void setCategory(ConceptEntity category) {
		this.category = category;
	}

	public CarePlan.CarePlanActivityStatus getStatus() {
		return status;
	}

	public void setStatus(CarePlan.CarePlanActivityStatus status) {
		this.status = status;
	}
}
