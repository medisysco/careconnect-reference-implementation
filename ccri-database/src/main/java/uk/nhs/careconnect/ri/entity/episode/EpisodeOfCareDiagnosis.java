package uk.nhs.careconnect.ri.entity.episode;

import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;
import uk.nhs.careconnect.ri.entity.condition.ConditionEntity;

import javax.persistence.*;

@Entity
@Table(name="EpisodeOfCareDiagnosis")
public class EpisodeOfCareDiagnosis {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "EPISODE_DIAGNOSIS_ID")
    private Long Id;

    @ManyToOne
    @JoinColumn (name = "EPISODE_ID",foreignKey= @ForeignKey(name="FK_EPISODE_DIAGNOSIS_EPISODE"))
    private EpisodeOfCareEntity episode;

    @ManyToOne
    @JoinColumn (name = "CONDITION_ID",foreignKey= @ForeignKey(name="FK_EPISODE_DIAGNOSIS_CONDITION"))
    private ConditionEntity condition;

    @ManyToOne
    @JoinColumn (name = "ROLE_CONCEPT_ID",foreignKey= @ForeignKey(name="FK_EPISODE_DIAGNOSIS_ROLE"))
    private ConceptEntity role;

    public EpisodeOfCareDiagnosis setEpisode(EpisodeOfCareEntity episode) {
        this.episode = episode;
        return this;
    }

    public EpisodeOfCareEntity getEpisode() {
        return episode;
    }

    public ConditionEntity getCondition() {
        return condition;
    }

    public EpisodeOfCareDiagnosis setCondition(ConditionEntity condition) {
        this.condition = condition;
        return this;
    }

    public ConceptEntity getRole() {
        return role;
    }

    public EpisodeOfCareDiagnosis setRole(ConceptEntity role) {
        this.role = role;
        return this;
    }
}
