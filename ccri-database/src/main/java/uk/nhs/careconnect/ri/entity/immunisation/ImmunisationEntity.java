package uk.nhs.careconnect.ri.entity.immunisation;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hl7.fhir.dstu3.model.Immunization;
import uk.nhs.careconnect.ri.entity.BaseResource;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;
import uk.nhs.careconnect.ri.entity.encounter.EncounterEntity;
import uk.nhs.careconnect.ri.entity.location.LocationEntity;
import uk.nhs.careconnect.ri.entity.organization.OrganisationEntity;
import uk.nhs.careconnect.ri.entity.patient.PatientEntity;
import uk.nhs.careconnect.ri.entity.practitioner.PractitionerEntity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Immunisation")
public class ImmunisationEntity extends BaseResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="IMMUNISATION_ID")
    private Long id;

    @ManyToOne
    @LazyCollection(LazyCollectionOption.TRUE)
    @JoinColumn (name = "PATIENT_ID", nullable = false, foreignKey= @ForeignKey(name="FK_IMMUNISATION_PATIENT"))
    private PatientEntity patient;

    @ManyToOne
    @JoinColumn (name = "MEDICATION_CODE_ID",nullable = false, foreignKey= @ForeignKey(name="FK_IMMUNISATION_VACCINE_CODE"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity vacinationCode;

    @ManyToOne
    @LazyCollection(LazyCollectionOption.TRUE)
    @JoinColumn(name="ENCOUNTER_ID",foreignKey= @ForeignKey(name="FK_IMMUNISATION_ENCOUNTER"))
    private EncounterEntity encounter;


    @Column(name="STATUS_ID")
    private Immunization.ImmunizationStatus status;

    @OneToMany(mappedBy="immunisation", targetEntity = ImmunisationIdentifier.class)
    @LazyCollection(LazyCollectionOption.TRUE)
    Set<ImmunisationIdentifier> identifiers = new HashSet<>();

    @Column(name="notGiven", nullable = false)
    Boolean notGiven;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "administrationDateTime")
    private Date administrationDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expirationDateTime")
    private Date expirationDate;

    @Column(name="primarySource", nullable = false)
    Boolean primarySource;

    @ManyToOne
    @JoinColumn (name = "REPORT_ORIGIN_CODE_ID",foreignKey= @ForeignKey(name="FK_IMMUNISATION_REPORT_ORIGIN_CODE"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity reportOrigin;

    @ManyToOne
    @JoinColumn(name="LOCATION_ID",foreignKey= @ForeignKey(name="FK_IMMUNISATION_LOCATION"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private LocationEntity location;

    @ManyToOne
    @JoinColumn(name="MANUFACTURER_ORGANISATION_ID",foreignKey= @ForeignKey(name="FK_IMMUNISATION_MANUFACTURER_ORGANISATION"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private OrganisationEntity organisation;

    @Column(name="lotNumber")
    String lotNumber;

    @ManyToOne
    @JoinColumn (name = "SITE_CODE_ID",foreignKey= @ForeignKey(name="FK_IMMUNISATION_SITE_CODE"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity site;

    @ManyToOne
    @JoinColumn (name = "ROUTE_CODE_ID",foreignKey= @ForeignKey(name="FK_IMMUNISATION_ROUTE_CODE"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity route;

    @Column(name="valueQuantity")
    private BigDecimal valueQuantity;

    @ManyToOne
    @JoinColumn(name="valueUnitOfMeasure_CONCEPT_ID",foreignKey= @ForeignKey(name="FK_IMMUNISATION_valueUnitOfMeasure_CONCEPT_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity valueUnitOfMeasure;

    @ManyToOne
    @JoinColumn(name="PRACTITIONER_ID",foreignKey= @ForeignKey(name="FK_IMMUNISATION_PRACTITIONER_ID"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private PractitionerEntity practitioner;

    @ManyToOne
    @JoinColumn (name = "REASON_GIVEN_CODE_ID",foreignKey= @ForeignKey(name="FK_IMMUNISATION_REASON_GIVEN_CODE"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity explanationReasonGiven;

    @ManyToOne
    @JoinColumn (name = "REASON_NOTGIVEN_CODE_ID",foreignKey= @ForeignKey(name="FK_IMMUNISATION_REASON_NOTGIVEN_CODE"))
    @LazyCollection(LazyCollectionOption.TRUE)
    private ConceptEntity explanationReasonNotGiven;

    @Column(name="note")
    String note;


    @Column(name="parentPresent")
    Boolean parentPresent;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "recordedDateTime")
    private Date recordedDate;

    public Set<ImmunisationIdentifier> getIdentifiers() {
        return identifiers;
    }

    public ImmunisationEntity setIdentifiers(Set<ImmunisationIdentifier> identifiers) {
        this.identifiers = identifiers;
        return this;
    }

    public Boolean getNotGiven() {
        return notGiven;
    }

    public ImmunisationEntity setNotGiven(Boolean notGiven) {
        this.notGiven = notGiven;
        return this;
    }

    public Date getAdministrationDate() {
        return administrationDate;
    }

    public ImmunisationEntity setAdministrationDate(Date administrationDate) {
        this.administrationDate = administrationDate;
        return this;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public ImmunisationEntity setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
        return this;
    }

    public Boolean getPrimarySource() {
        return primarySource;
    }

    public ImmunisationEntity setPrimarySource(Boolean primarySource) {
        this.primarySource = primarySource;
        return this;
    }

    public ConceptEntity getReportOrigin() {
        return reportOrigin;
    }

    public ImmunisationEntity setReportOrigin(ConceptEntity reportOrigin) {
        this.reportOrigin = reportOrigin;
        return this;
    }

    public OrganisationEntity getOrganisation() {
        return organisation;
    }

    public ImmunisationEntity setOrganisation(OrganisationEntity organisation) {
        this.organisation = organisation;
        return this;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public ImmunisationEntity setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
        return this;
    }

    public ConceptEntity getSite() {
        return site;
    }

    public ImmunisationEntity setSite(ConceptEntity site) {
        this.site = site;
        return this;
    }

    public ConceptEntity getRoute() {
        return route;
    }

    public ImmunisationEntity setRoute(ConceptEntity route) {
        this.route = route;
        return this;
    }

    public BigDecimal getValueQuantity() {
        return valueQuantity;
    }

    public ImmunisationEntity setValueQuantity(BigDecimal valueQuantity) {
        this.valueQuantity = valueQuantity;
        return this;
    }

    public ConceptEntity getValueUnitOfMeasure() {
        return valueUnitOfMeasure;
    }

    public ImmunisationEntity setValueUnitOfMeasure(ConceptEntity valueUnitOfMeasure) {
        this.valueUnitOfMeasure = valueUnitOfMeasure;
        return this;
    }

    public PractitionerEntity getPractitioner() {
        return practitioner;
    }

    public ImmunisationEntity setPractitioner(PractitionerEntity practitioner) {
        this.practitioner = practitioner;
        return this;
    }

    public ConceptEntity getExplanationReasonGiven() {
        return explanationReasonGiven;
    }

    public ImmunisationEntity setExplanationReasonGiven(ConceptEntity reasonGiven) {
        this.explanationReasonGiven = reasonGiven;
        return this;
    }

    public ConceptEntity getExplanationReasonNotGiven() {
        return explanationReasonNotGiven;
    }

    public ImmunisationEntity setReasonNotGiven(ConceptEntity reasonNotGiven) {
        this.explanationReasonNotGiven = reasonNotGiven;
        return this;
    }

    public String getNote() {
        return note;
    }

    public ImmunisationEntity setNote(String note) {
        this.note = note;
        return this;
    }


    public Boolean getParentPresent() {
        return parentPresent;
    }

    public ImmunisationEntity setParentPresent(Boolean parentPresent) {
        this.parentPresent = parentPresent;
        return this;
    }

    public Date getRecordedDate() {
        return recordedDate;
    }

    public ImmunisationEntity setRecordedDate(Date recordedDate) {
        this.recordedDate = recordedDate;
        return this;
    }

    public Long getId() {
        return id;
    }

    public ImmunisationEntity setPatient(PatientEntity patient) {
        this.patient = patient;
        return this;
    }

    public PatientEntity getPatient() {
        return patient;
    }

    public EncounterEntity getEncounter() {
        return encounter;
    }

    public ImmunisationEntity setEncounter(EncounterEntity contextEncounter) {
        this.encounter = contextEncounter;
        return this;
    }

    public ConceptEntity getVacinationCode() {
        return vacinationCode;
    }

    public ImmunisationEntity setVacinationCode(ConceptEntity vacinationCode) {
        this.vacinationCode = vacinationCode;
        return this;
    }

    public LocationEntity getLocation() {
        return location;
    }

    public ImmunisationEntity setLocation(LocationEntity location) {
        this.location = location;
        return this;
    }

    public Immunization.ImmunizationStatus getStatus() {
        return status;
    }

    public ImmunisationEntity setStatus(Immunization.ImmunizationStatus status) {
        this.status = status;
        return this;
    }

}
