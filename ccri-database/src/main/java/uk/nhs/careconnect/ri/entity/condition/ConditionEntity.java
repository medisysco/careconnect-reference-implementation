package uk.nhs.careconnect.ri.entity.condition;

import uk.nhs.careconnect.ri.entity.BaseResource;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;
import uk.nhs.careconnect.ri.entity.encounter.EncounterEntity;
import uk.nhs.careconnect.ri.entity.episode.EpisodeOfCareEntity;
import uk.nhs.careconnect.ri.entity.patient.PatientEntity;

import javax.persistence.*;

@Entity
@Table(name = "Condition_")
public class ConditionEntity extends BaseResource {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="CONDITION_ID")
    private Long id;

    @ManyToOne
    @JoinColumn (name = "PATIENT_ID",foreignKey= @ForeignKey(name="FK_PATIENT_CONDITION"))
    private PatientEntity patient;

    @ManyToOne
    @JoinColumn (name = "ENCOUNTER_ID",foreignKey= @ForeignKey(name="FK_CONDITION_ENCOUNTER"))
    private EncounterEntity contextEncounter;

    @ManyToOne
    @JoinColumn (name = "EPISODE_ID",foreignKey= @ForeignKey(name="FK_CONDITION_EPISODE"))
    private EpisodeOfCareEntity contextEpisode;

    @ManyToOne
    @JoinColumn (name = "CODE_CONCEPT_ID",foreignKey= @ForeignKey(name="FK_CONDITION_CODE"))
    private ConceptEntity code;


    public Long getId() {
        return id;
    }

    public ConditionEntity setPatient(PatientEntity patient) {
        this.patient = patient;
        return this;
    }

    public PatientEntity getPatient() {
        return patient;
    }

    public ConceptEntity getCode() {
        return code;
    }

    public ConditionEntity setCode(ConceptEntity code) {
        this.code = code;
        return this;
    }

    public ConditionEntity setContextEncounter(EncounterEntity contextEncounter) {
        this.contextEncounter = contextEncounter;
        return this;
    }

    public EncounterEntity getContextEncounter() {
        return contextEncounter;
    }

    public EpisodeOfCareEntity getContextEpisode() {
        return contextEpisode;
    }

    public ConditionEntity setContextEpisode(EpisodeOfCareEntity contextEpisode) {
        this.contextEpisode = contextEpisode;
        return this;
    }
}
