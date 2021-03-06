package uk.nhs.careconnect.ri.entity.observation;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import uk.nhs.careconnect.ri.entity.BaseResource;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name="ObservationRange")
public class ObservationRange extends BaseResource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "OBSERVATION_RANGE_ID")
    private Long Id;

    @ManyToOne
    @JoinColumn (name = "OBSERVATION_ID",foreignKey= @ForeignKey(name="FK_OBSERVATION_RANGE_OBSERVATION_ID"))
    private ObservationEntity observation;

    @Column(name="lowQuantity")
    private BigDecimal lowQuantity;

    @Column(name="highQuantity")
    private BigDecimal highQuantity;

    @ManyToOne
    @JoinColumn(name="TYPE_CONCEPT_ID",foreignKey= @ForeignKey(name="FK_OBSERVATION_RANGE_TYPE_CONCEPT_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity type;

    @Column(name="lowAgeRange")
    private BigDecimal lowAgeRange;

    @Column(name="highAgeRange")
    private BigDecimal highAgeRange;

    public void setObservation(ObservationEntity observation) {
        this.observation = observation;
    }

    public ObservationEntity getObservation() {
        return observation;
    }

    public ConceptEntity getType() {
        return type;
    }

    public Long getId() {
        return Id;
    }

    public BigDecimal getHighAgeRange() {
        return highAgeRange;
    }

    public BigDecimal getHighQuantity() {
        return highQuantity;
    }

    public BigDecimal getLowAgeRange() {
        return lowAgeRange;
    }

    public BigDecimal getLowQuantity() {
        return lowQuantity;
    }

    public ObservationRange setHighAgeRange(BigDecimal highAgeRange) {
        this.highAgeRange = highAgeRange;
        return this;
    }

    public ObservationRange setHighQuantity(BigDecimal highQuantity) {
        this.highQuantity = highQuantity;
        return this;
    }

    public ObservationRange setLowAgeRange(BigDecimal lowAgeRange) {
        this.lowAgeRange = lowAgeRange;
        return this;
    }

    public ObservationRange setLowQuantity(BigDecimal lowQuantity) {
        this.lowQuantity = lowQuantity;
        return this;
    }

    public ObservationRange setType(ConceptEntity type) {
        this.type = type;
        return this;
    }
}
