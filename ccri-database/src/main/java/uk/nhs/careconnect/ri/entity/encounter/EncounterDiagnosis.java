package uk.nhs.careconnect.ri.entity.encounter;

import uk.nhs.careconnect.ri.entity.condition.ConditionEntity;

import javax.persistence.*;

@Entity
@Table(name="EncounterDiagnosis", uniqueConstraints= @UniqueConstraint(name="PK_ENCOUNTER_REASON", columnNames={"ENCOUNTER_REASON_ID"})
        ,indexes = { @Index(name="IDX_ENCOUNTER_CONDITION", columnList = "DIAGNOSIS_CONDITION_ID")}
)
public class EncounterDiagnosis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "ENCOUNTER_REASON_ID")
    private Long Id;

    @ManyToOne
    @JoinColumn (name = "ENCOUNTER_ID",foreignKey= @ForeignKey(name="FK_ENCOUNTER_DIAGNOSIS_ENCOUNTER_ID"))
    private EncounterEntity encounter;


    @ManyToOne
    @JoinColumn (name = "DIAGNOSIS_CONDITION_ID", nullable = false, foreignKey= @ForeignKey(name="FK_ENCOUNTER_DIAGNOSIS_CONDITION_ID"))
    private ConditionEntity condition;


    public void setId(Long id) {
        Id = id;
    }

    public Long getId() {
        return Id;
    }

    public EncounterDiagnosis setEncounter(EncounterEntity encounter) {
        this.encounter = encounter;
        return this;
    }

    public EncounterDiagnosis setCondition(ConditionEntity condition) {
        this.condition = condition;
        return this;
    }

    public ConditionEntity getCondition() {
        return condition;
    }

    public EncounterEntity getEncounter() {
        return encounter;
    }
}
