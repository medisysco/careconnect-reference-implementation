package uk.nhs.careconnect.ri.daointerface.transforms;

import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.DateTimeType;
import org.hl7.fhir.dstu3.model.Meta;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.stereotype.Component;
import uk.nhs.careconnect.ri.entity.condition.ConditionCategory;
import uk.nhs.careconnect.ri.entity.condition.ConditionEntity;
import uk.nhs.careconnect.ri.entity.condition.ConditionIdentifier;
import uk.org.hl7.fhir.core.Stu3.CareConnectProfile;

@Component
public class ConditionEntityToFHIRConditionTransformer implements Transformer<ConditionEntity, Condition> {


    @Override
    public Condition transform(final ConditionEntity conditionEntity) {
        final Condition condition = new Condition();

        Meta meta = new Meta().addProfile(CareConnectProfile.Condition_1);

        if (conditionEntity.getUpdated() != null) {
            meta.setLastUpdated(conditionEntity.getUpdated());
        }
        else {
            if (conditionEntity.getCreated() != null) {
                meta.setLastUpdated(conditionEntity.getCreated());
            }
        }
        condition.setMeta(meta);

        condition.setId(conditionEntity.getId().toString());

        if (conditionEntity.getPatient() != null) {
            condition
                    .setSubject(new Reference("Patient/"+conditionEntity.getPatient().getId())
                    .setDisplay(conditionEntity.getPatient().getNames().get(0).getDisplayName()));
        }
        if (conditionEntity.getAssertedDateTime() != null) {
            condition.setAssertedDate(conditionEntity.getAssertedDateTime());
        }
        if (conditionEntity.getAsserterPractitioner() != null) {
            condition.setAsserter(new Reference("Practitioner/"+conditionEntity.getAsserterPractitioner().getId()));
        }
        if (conditionEntity.getClinicalStatus() != null) {
            condition.setClinicalStatus(conditionEntity.getClinicalStatus());
        }
        if (conditionEntity.getCode() != null) {
            condition.getCode().addCoding()
                    .setCode(conditionEntity.getCode().getCode())
                    .setDisplay(conditionEntity.getCode().getDisplay())
                    .setSystem(conditionEntity.getCode().getSystem());
        }
        if (conditionEntity.getContextEncounter() != null) {
            condition.setContext(new Reference("Encounter/"+conditionEntity.getContextEncounter().getId()));
        } else if (conditionEntity.getContextEpisode() != null) {
            condition.setContext(new Reference("EpisodeOfCare/"+conditionEntity.getContextEpisode().getId()));
        }
        if (conditionEntity.getOnsetDateTime() != null) {
            condition.setOnset(new DateTimeType().setValue(conditionEntity.getOnsetDateTime()));
        }
        if (conditionEntity.getVerificationStatus()!=null) {
            condition.setVerificationStatus(conditionEntity.getVerificationStatus());
        }
        if (conditionEntity.getAssertedDateTime() != null) {
            condition.setAssertedDate(conditionEntity.getAssertedDateTime());
        }
        if (conditionEntity.getSeverity() != null) {
            condition.getSeverity().addCoding()
                    .setCode(conditionEntity.getSeverity().getCode())
                    .setDisplay(conditionEntity.getSeverity().getDisplay())
                    .setSystem(conditionEntity.getSeverity().getSystem());
        }
        for (ConditionCategory category : conditionEntity.getCategories()) {
            condition.addCategory().addCoding()
                    .setCode(category.getCategory().getCode())
                    .setSystem(category.getCategory().getSystem())
                    .setDisplay(category.getCategory().getDisplay());
        }
        for (ConditionIdentifier identifier : conditionEntity.getIdentifiers()) {
            condition.addIdentifier()
                    .setSystem(identifier.getSystem().getUri())
                    .setValue(identifier.getValue());
        }

        return condition;

    }
}
