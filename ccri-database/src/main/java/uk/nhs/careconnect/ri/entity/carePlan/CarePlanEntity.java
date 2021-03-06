package uk.nhs.careconnect.ri.entity.carePlan;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hl7.fhir.dstu3.model.CarePlan;

import uk.nhs.careconnect.ri.entity.BaseResource;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;
import uk.nhs.careconnect.ri.entity.encounter.EncounterEntity;
import uk.nhs.careconnect.ri.entity.episode.EpisodeOfCareEntity;
import uk.nhs.careconnect.ri.entity.patient.PatientEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "CarePlan", indexes = {


})
public class CarePlanEntity extends BaseResource {

    private static final int MAX_DESC_LENGTH = 4096;

    public enum CarePlanType  { component, valueQuantity }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="CAREPLAN_ID")
    private Long id;

    @OneToMany(mappedBy="carePlan", targetEntity=CarePlanIdentifier.class)
    private Set<CarePlanIdentifier> identifiers = new HashSet<>();

    @ManyToOne
    @LazyCollection(LazyCollectionOption.TRUE)
    @JoinColumn(name="PARENT_CAREPLAN_ID",foreignKey= @ForeignKey(name="FK_CAREPLAN_PARENT_CAREPLAN_ID"))
    private CarePlanEntity partOfCarePlan;

    // The parent should not be null but child carePlans don't have a status.
    @Enumerated(EnumType.ORDINAL)
    @Column(name="status")
    private CarePlan.CarePlanStatus status;

    // The parent should not be null but child carePlans don't have a status.
    @Enumerated(EnumType.ORDINAL)
    @Column(name="intent")
    private CarePlan.CarePlanIntent intent;

    @OneToMany(mappedBy="carePlan", targetEntity=CarePlanCategory.class)
    private Set<CarePlanCategory> categories = new HashSet<>();

    @Column(name="TITLE",length = MAX_DESC_LENGTH,nullable = true)
    private String title;

    @Column(name="DESCRIPTION",length = MAX_DESC_LENGTH,nullable = true)
    private String description;

    @ManyToOne
    @LazyCollection(LazyCollectionOption.TRUE)
    @JoinColumn (name = "PATIENT_ID",foreignKey= @ForeignKey(name="FK_CAREPLAN_PATIENT_ID"))
    private PatientEntity patient;

    @ManyToOne
    @JoinColumn(name="ENCOUNTER_ID",foreignKey= @ForeignKey(name="FK_CAREPLAN_ENCOUNTER_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private EncounterEntity contextEncounter;

    @ManyToOne
    @JoinColumn(name="EPISODE_ID",foreignKey= @ForeignKey(name="FK_CAREPLAN_EPISODE_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private EpisodeOfCareEntity contextEpisodeOfCare;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "periodStartDateTime")
    private Date periodStartDateTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "periodEndDateTime")
    private Date periodEndDateTime;

    @OneToMany(mappedBy="carePlan", targetEntity=CarePlanCondition.class)
    private Set<CarePlanCondition> addresses = new HashSet<>();

    // goal todo

    @OneToMany(mappedBy="carePlan", targetEntity=CarePlanActivity.class)
    private Set<CarePlanActivity> activities = new HashSet<>();

    public Long getId() {
        return id;
    }

    public Set<CarePlanActivity> getActivities() {
        return activities;
    }

    public void setActivities(Set<CarePlanActivity> activities) {
        this.activities = activities;
    }

    public PatientEntity getPatient() {
        return patient;
    }

    public void setPatient(PatientEntity patient) {
        this.patient = patient;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public Set<CarePlanIdentifier> getIdentifiers() {
        if (identifiers == null) { identifiers = new HashSet<CarePlanIdentifier>(); }
        return identifiers;
    }

    public CarePlan.CarePlanStatus getStatus() {
        return status;
    }

    public CarePlanEntity setIdentifiers(Set<CarePlanIdentifier> identifiers) {
        this.identifiers = identifiers;
        return this;
    }


    public CarePlanEntity setStatus(CarePlan.CarePlanStatus status) {
        this.status = status;
        return this;
    }

    public EncounterEntity getContext() {
        return contextEncounter;
    }

    public CarePlanEntity setContext(EncounterEntity context) {
        this.contextEncounter = context;
        return this;
    }

    public EncounterEntity getContextEncounter() {
        return contextEncounter;
    }

    public CarePlanEntity setContextEncounter(EncounterEntity contextEncounter) {
        this.contextEncounter = contextEncounter;
        return this;
    }

    public CarePlanEntity getPartOfCarePlan() {
        return partOfCarePlan;
    }

    public void setPartOfCarePlan(CarePlanEntity partOfCarePlan) {
        this.partOfCarePlan = partOfCarePlan;
    }

    public CarePlan.CarePlanIntent getIntent() {
        return intent;
    }

    public void setIntent(CarePlan.CarePlanIntent intent) {
        this.intent = intent;
    }

    public Set<CarePlanCategory> getCategories() {
        return categories;
    }

    public void setCategories(Set<CarePlanCategory> categories) {
        this.categories = categories;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public EpisodeOfCareEntity getContextEpisodeOfCare() {
        return contextEpisodeOfCare;
    }

    public void setContextEpisodeOfCare(EpisodeOfCareEntity contextEpisodeOfCare) {
        this.contextEpisodeOfCare = contextEpisodeOfCare;
    }

    public Date getPeriodStartDateTime() {
        return periodStartDateTime;
    }

    public void setPeriodStartDateTime(Date periodStartDateTime) {
        this.periodStartDateTime = periodStartDateTime;
    }

    public Date getPeriodEndDateTime() {
        return periodEndDateTime;
    }

    public void setPeriodEndDateTime(Date periodEndDateTime) {
        this.periodEndDateTime = periodEndDateTime;
    }

    public Set<CarePlanCondition> getAddresses() {
        return addresses;
    }

    public void setAddresses(Set<CarePlanCondition> addresses) {
        this.addresses = addresses;
    }
}
