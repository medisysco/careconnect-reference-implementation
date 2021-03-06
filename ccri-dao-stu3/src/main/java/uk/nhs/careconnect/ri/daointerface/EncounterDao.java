package uk.nhs.careconnect.ri.daointerface;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.Include;
import ca.uhn.fhir.rest.annotation.IncludeParam;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.nhs.careconnect.ri.daointerface.transforms.*;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;
import uk.nhs.careconnect.ri.entity.carePlan.CarePlanEntity;
import uk.nhs.careconnect.ri.entity.condition.ConditionEntity;
import uk.nhs.careconnect.ri.entity.diagnosticReport.DiagnosticReportEntity;
import uk.nhs.careconnect.ri.entity.encounter.EncounterDiagnosis;
import uk.nhs.careconnect.ri.entity.encounter.EncounterEntity;
import uk.nhs.careconnect.ri.entity.encounter.EncounterIdentifier;
import uk.nhs.careconnect.ri.entity.location.LocationEntity;
import uk.nhs.careconnect.ri.entity.medication.MedicationRequestEntity;
import uk.nhs.careconnect.ri.entity.observation.ObservationEntity;
import uk.nhs.careconnect.ri.entity.organization.OrganisationEntity;
import uk.nhs.careconnect.ri.entity.patient.PatientEntity;
import uk.nhs.careconnect.ri.entity.practitioner.PractitionerEntity;
import uk.nhs.careconnect.ri.entity.procedure.ProcedureEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.net.URI;
import java.util.*;

import static uk.nhs.careconnect.ri.daointerface.daoutils.MAXROWS;

@Repository
@Transactional
public class  EncounterDao implements EncounterRepository {

    @PersistenceContext
    EntityManager em;

    @Autowired
    private EncounterEntityToFHIREncounterTransformer encounterEntityToFHIREncounterTransformer;

    @Autowired
    ConceptRepository conceptDao;

    @Autowired
    PatientRepository patientDao;

    @Autowired
    PractitionerRepository practitionerDao;

    @Autowired
    OrganisationRepository organisationDao;

    @Autowired
    LocationRepository locationDao;

    @Autowired
    ConditionRepository conditionDao;

    @Autowired
    ProcedureEntityToFHIRProcedureTransformer procedureEntityToFHIRProcedureTransformer;

    @Autowired
    private ObservationEntityToFHIRObservationTransformer observationEntityToFHIRObservationTransformer;

    @Autowired
    ConditionEntityToFHIRConditionTransformer conditionEntityToFHIRConditionTransformer;

    @Autowired
    private MedicationRequestEntityToFHIRMedicationRequestTransformer
            medicationRequestEntityToFHIRMedicationRequestTransformer;


    @Autowired
    CarePlanEntityToFHIRCarePlanTransformer carePlanIntoleranceEntityToFHIRCarePlanTransformer;

    @Autowired
    DiagnosticReportEntityToFHIRDiagnosticReportTransformer diagnosticReportEntityToFHIRDiagnosticReportTransformer;
    @Autowired
    private CodeSystemRepository codeSystemSvc;

    private static final Logger log = LoggerFactory.getLogger(EncounterDao.class);


    @Override
    public void save(FhirContext ctx, EncounterEntity encounter) {

    }

    @Override
    public Long count() {

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        cq.select(qb.count(cq.from(EncounterEntity.class)));
        //cq.where(/*your stuff*/);
        return em.createQuery(cq).getSingleResult();
    }


    @Override
    public Encounter read(FhirContext ctx,IdType theId) {
        if (daoutils.isNumeric(theId.getIdPart())) {
            EncounterEntity encounter = (EncounterEntity) em.find(EncounterEntity.class, Long.parseLong(theId.getIdPart()));

            return encounter == null
                    ? null
                    : encounterEntityToFHIREncounterTransformer.transform(encounter);
        } else {
            return null;
        }
    }

    @Override
    public EncounterEntity readEntity(FhirContext ctx,IdType theId) {
        if (daoutils.isNumeric(theId.getIdPart())) {
            EncounterEntity encounter = (EncounterEntity) em.find(EncounterEntity.class, Long.parseLong(theId.getIdPart()));

            return encounter;
        } else {
            return null;
        }
    }

    @Override
    public Encounter create(FhirContext ctx,Encounter encounter, IdType theId, String theConditional) {
        log.debug("Encounter.save");
      //  log.info(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(encounter));
        EncounterEntity encounterEntity = null;

        if (encounter.hasId()) encounterEntity = readEntity(ctx, encounter.getIdElement());

        if (theConditional != null) {
            try {

                //CareConnectSystem.ODSOrganisationCode
                if (theConditional.contains("fhir.leedsth.nhs.uk/Id/encounter")) {
                    URI uri = new URI(theConditional);

                    String scheme = uri.getScheme();
                    String host = uri.getHost();
                    String query = uri.getRawQuery();
                    log.debug(query);
                    String[] spiltStr = query.split("%7C");
                    log.debug(spiltStr[1]);

                    List<EncounterEntity> results = searchEntity(ctx, null, null,null, new TokenParam().setValue(spiltStr[1]).setSystem("https://fhir.leedsth.nhs.uk/Id/encounter"),null, null);
                    for (EncounterEntity enc : results) {
                        encounterEntity = enc;
                        break;
                    }
                } else {
                    log.info("NOT SUPPORTED: Conditional Url = "+theConditional);
                }

            } catch (Exception ex) {

            }
        }

        if (encounterEntity == null) encounterEntity = new EncounterEntity();


        PatientEntity patientEntity = null;
        if (encounter.hasSubject()) {
            log.trace(encounter.getSubject().getReference());
            patientEntity = patientDao.readEntity(ctx, new IdType(encounter.getSubject().getReference()));
            encounterEntity.setPatient(patientEntity);
        }
        if (encounter.hasClass_()) {
            ConceptEntity code = conceptDao.findAddCode(encounter.getClass_());
            if (code != null) { encounterEntity._setClass(code); }
            else {
                log.info("Code: Missing System/Code = "+ encounter.getClass_().getSystem() +" code = "+encounter.getClass_().getCode());

                throw new IllegalArgumentException("Missing System/Code = "+ encounter.getClass_().getSystem() +" code = "+encounter.getClass_().getCode());
            }
        }

        if (encounter.hasType()) {
            ConceptEntity code = conceptDao.findAddCode(encounter.getType().get(0).getCoding().get(0));
            if (code != null) { encounterEntity.setType(code); }
            else {
                log.info("Code: Missing System/Code = "+encounter.getType().get(0).getCoding().get(0).getSystem() +" code = "+encounter.getType().get(0).getCoding().get(0).getCode());

                throw new IllegalArgumentException("Missing System/Code = "+ encounter.getType().get(0).getCoding().get(0).getSystem() +" code = "+encounter.getType().get(0).getCoding().get(0).getCode());
            }
        }

        if (encounter.hasLocation()) {
            LocationEntity locationEntity = locationDao.readEntity(ctx,new IdType(encounter.getLocation().get(0).getLocation().getReference()));
            encounterEntity.setLocation(locationEntity);
        }

        if (encounter.hasStatus()) {
            encounterEntity.setStatus(encounter.getStatus());
        }

        if (encounter.hasParticipant()) {
            for(Encounter.EncounterParticipantComponent participant : encounter.getParticipant()) {
                if (participant.getIndividual().getReference().contains("Practitioner")) {

                    PractitionerEntity practitionerEntity = practitionerDao.readEntity(ctx,new IdType("Practitioner/"+participant.getIndividual().getReference()));
                    if (practitionerEntity != null ) encounterEntity.setParticipant(practitionerEntity);
                }

                if (participant.hasType()) {
                    ConceptEntity code = conceptDao.findCode(participant.getType().get(0).getCoding().get(0));
                    if (code != null) {
                        encounterEntity.setParticipantType(code);
                    } else {
                        log.info("Code: Missing System/Code = "+participant.getType().get(0).getCoding().get(0).getSystem() +" code = "+participant.getType().get(0).getCoding().get(0).getCode());

                        throw new IllegalArgumentException("Missing System/Code = "+ participant.getType().get(0).getCoding().get(0).getSystem() +" code = "+participant.getType().get(0).getCoding().get(0).getCode());
                    }

                }

            }
        }

        if (encounter.hasPeriod()) {

            if (encounter.getPeriod().hasStart()) {
                encounterEntity.setPeriodStartDate(encounter.getPeriod().getStart());

            if (encounter.getPeriod().hasEnd()) {
                if (encounter.getPeriod().getEnd().after(encounter.getPeriod().getStart()))
                encounterEntity.setPeriodEndDate(encounter.getPeriod().getEnd());
                else
                    encounterEntity.setPeriodEndDate(null); // KGM 15/12/2017 Ensure end date is after start date , if not set end date to null
            }
            }
        }

        if (encounter.hasPriority()) {
            ConceptEntity code = conceptDao.findCode(encounter.getPriority().getCoding().get(0));
            if (code != null) { encounterEntity.setPriority(code); }
            else {
                log.info("Code: Missing System/Code = "+encounter.getPriority().getCoding().get(0).getSystem() +" code = "+encounter.getPriority().getCoding().get(0).getCode());

                throw new IllegalArgumentException("Missing System/Code = "+ encounter.getPriority().getCoding().get(0).getSystem() +" code = "+encounter.getPriority().getCoding().get(0).getCode());
            }
        }

        if (encounter.hasServiceProvider()) {

            log.debug("encounter.getServiceProvider().getReference=" + (encounter.getServiceProvider().getReference()));

            OrganisationEntity organisationEntity = organisationDao.readEntity(ctx, new IdType(encounter.getServiceProvider().getReference()));
            if (organisationEntity != null) encounterEntity.setServiceProvider(organisationEntity);
        }

        em.persist(encounterEntity);

        for (Identifier identifier : encounter.getIdentifier()) {
            EncounterIdentifier encounterIdentifier = null;

            for (EncounterIdentifier orgSearch : encounterEntity.getIdentifiers()) {
                if (identifier.getSystem().equals(orgSearch.getSystemUri()) && identifier.getValue().equals(orgSearch.getValue())) {
                    encounterIdentifier = orgSearch;
                    break;
                }
            }
            if (encounterIdentifier == null)  encounterIdentifier = new EncounterIdentifier();

            encounterIdentifier.setValue(identifier.getValue());
            encounterIdentifier.setSystem(codeSystemSvc.findSystem(identifier.getSystem()));
            encounterIdentifier.setEncounter(encounterEntity);
            em.persist(encounterIdentifier);
        }

        for (Encounter.DiagnosisComponent component:encounter.getDiagnosis()) {

            EncounterDiagnosis encounterDiagnosis = null;
            for (EncounterDiagnosis searchEncounter : encounterEntity.getDiagnoses()) {
                if (searchEncounter.getCondition().getId().equals(component.getCondition().getIdElement().getValue())) {
                    encounterDiagnosis = searchEncounter;
                    break;
                }
            }
            if (encounterDiagnosis == null) {
                encounterDiagnosis= new EncounterDiagnosis();
                encounterDiagnosis.setEncounter(encounterEntity);
                ConditionEntity condition = conditionDao.readEntity(ctx,new IdType(component.getCondition().getReference()));
                encounterDiagnosis.setCondition(condition);
                em.persist(encounterDiagnosis);
            }

        }

        return encounterEntityToFHIREncounterTransformer.transform(encounterEntity);
    }

    @Override
    public List<Resource> search(FhirContext ctx, ReferenceParam patient, DateRangeParam date, ReferenceParam episode, TokenParam identifier, TokenParam resid, Set<Include> reverseIncludes) {
        List<EncounterEntity> qryResults = searchEntity(ctx,patient, date, episode, identifier,resid,reverseIncludes);
        List<Resource> results = new ArrayList<>();

        for (EncounterEntity encounterEntity : qryResults)
        {
            // log.trace("HAPI Custom = "+doc.getId());
            Encounter encounter = encounterEntityToFHIREncounterTransformer.transform(encounterEntity);
            results.add(encounter);
        }
        // If reverse include selected
        if (reverseIncludes!= null) {
            log.info("Reverse includes");
            for (EncounterEntity encounterEntity : qryResults) {
                if (reverseIncludes.size() > 0) {
                    for (ProcedureEntity procedureEntity : encounterEntity.getProcedureEncounters()) {
                        results.add(procedureEntityToFHIRProcedureTransformer.transform(procedureEntity));
                    }
                    for (ObservationEntity observationEntity : encounterEntity.getObservationEncounters()) {
                        results.add(observationEntityToFHIRObservationTransformer.transform(observationEntity));
                    }
                    for (ConditionEntity conditionEntity : encounterEntity.getConditionEncounters()) {
                        results.add(conditionEntityToFHIRConditionTransformer.transform(conditionEntity));
                    }
                    for (MedicationRequestEntity medicationRequestEntity : encounterEntity.getMedicationRequestEncounters()) {
                        results.add(medicationRequestEntityToFHIRMedicationRequestTransformer.transform(medicationRequestEntity));
                    }
                    for (CarePlanEntity carePlanEntity : encounterEntity.getCarePlans()) {
                        results.add(carePlanIntoleranceEntityToFHIRCarePlanTransformer.transform(carePlanEntity));
                    }
                    for (DiagnosticReportEntity diagnosticReportEntity : encounterEntity.getDiagnosticReports()) {
                        results.add(diagnosticReportEntityToFHIRDiagnosticReportTransformer.transform(diagnosticReportEntity));
                    }
                }
            }
        }


        return results;
    }

    @Override
    public List<EncounterEntity> searchEntity(FhirContext ctx,ReferenceParam patient, DateRangeParam date, ReferenceParam episode, TokenParam identifier,TokenParam resid,Set<Include> reverseIncludes) {
        List<EncounterEntity> qryResults = null;

        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<EncounterEntity> criteria = builder.createQuery(EncounterEntity.class);
        Root<EncounterEntity> root = criteria.from(EncounterEntity.class);

        List<Predicate> predList = new LinkedList<Predicate>();
        List<Encounter> results = new ArrayList<Encounter>();


        if (identifier !=null)
        {
            Join<EncounterEntity, EncounterIdentifier> join = root.join("identifiers", JoinType.LEFT);

            Predicate p = builder.equal(join.get("value"),identifier.getValue());
            predList.add(p);
            // TODO predList.add(builder.equal(join.get("system"),identifier.getSystem()));

        }
        if (resid != null) {
            Predicate p = builder.equal(root.get("id"),resid.getValue());
            predList.add(p);
        }
        if (patient != null) {
            // KGM 4/1/2018 only search on patient id
            if (daoutils.isNumeric(patient.getIdPart())) {
                Join<EncounterEntity, PatientEntity> join = root.join("patient", JoinType.LEFT);

                Predicate p = builder.equal(join.get("id"), patient.getIdPart());
                predList.add(p);
            } else {
                Join<EncounterEntity, PatientEntity> join = root.join("patient", JoinType.LEFT);

                Predicate p = builder.equal(join.get("id"), -1);
                predList.add(p);
            }
        }


        ParameterExpression<java.util.Date> parameterLower = builder.parameter(java.util.Date.class);
        ParameterExpression<java.util.Date> parameterUpper = builder.parameter(java.util.Date.class);

        if (date !=null)
        {


            if (date.getLowerBoundAsInstant() != null) log.debug("getLowerBoundAsInstant()="+date.getLowerBoundAsInstant().toString());
            if (date.getUpperBoundAsInstant() != null) log.debug("getUpperBoundAsInstant()="+date.getUpperBoundAsInstant().toString());


            if (date.getLowerBound() != null) {

                DateParam dateParam = date.getLowerBound();
                log.debug("Lower Param - " + dateParam.getValue() + " Prefix - " + dateParam.getPrefix());

                switch (dateParam.getPrefix()) {
                    case GREATERTHAN:
                        /*{
                        Predicate p = builder.greaterThan(root.<Date>get("periodStartDate"), parameterLower);
                        predList.add(p);

                        break;
                    }*/
                    case GREATERTHAN_OR_EQUALS: {
                        Predicate p = builder.greaterThanOrEqualTo(root.<Date>get("periodStartDate"), parameterLower);
                        predList.add(p);
                        break;
                    }
                    case APPROXIMATE:
                    case EQUAL: {

                        Predicate plow = builder.greaterThanOrEqualTo(root.<Date>get("periodStartDate"), parameterLower);
                        predList.add(plow);
                        break;
                    }
                    case NOT_EQUAL: {
                        Predicate p = builder.notEqual(root.<Date>get("periodStartDate"), parameterLower);
                        predList.add(p);
                        break;
                    }
                    case STARTS_AFTER: {
                        Predicate p = builder.greaterThan(root.<Date>get("periodStartDate"), parameterLower);
                        predList.add(p);
                        break;

                    }
                    default:
                        log.trace("DEFAULT DATE(0) Prefix = " + date.getValuesAsQueryTokens().get(0).getPrefix());
                }
            }
            if (date.getUpperBound() != null) {

                DateParam dateParam = date.getUpperBound();

                log.debug("Upper Param - " + dateParam.getValue() + " Prefix - " + dateParam.getPrefix());

                switch (dateParam.getPrefix()) {
                    case APPROXIMATE:
                    case EQUAL: {
                        Predicate pupper = builder.lessThan(root.<Date>get("periodStartDate"), parameterUpper);
                        predList.add(pupper);
                        break;
                    }

                    case LESSTHAN_OR_EQUALS: {
                        Predicate p = builder.lessThanOrEqualTo(root.<Date>get("periodStartDate"), parameterUpper);
                        predList.add(p);
                        break;
                    }
                    case ENDS_BEFORE:
                    case LESSTHAN: {
                        Predicate p = builder.lessThan(root.<Date>get("periodStartDate"), parameterUpper);
                        predList.add(p);

                        break;
                    }
                    default:
                        log.trace("DEFAULT DATE(0) Prefix = " + date.getValuesAsQueryTokens().get(0).getPrefix());
                }
            }

        }



        Predicate[] predArray = new Predicate[predList.size()];
        predList.toArray(predArray);
        if (predList.size()>0)
        {
            criteria.select(root).where(predArray);
        }
        else
        {
            criteria.select(root);
        }
        criteria.orderBy(builder.desc(root.get("periodStartDate")));

        TypedQuery<EncounterEntity> typedQuery = em.createQuery(criteria).setMaxResults(MAXROWS);

        if (date != null) {
            if (date.getLowerBound() != null)
                typedQuery.setParameter(parameterLower, date.getLowerBoundAsInstant(), TemporalType.TIMESTAMP);
            if (date.getUpperBound() != null)
                typedQuery.setParameter(parameterUpper, date.getUpperBoundAsInstant(), TemporalType.TIMESTAMP);
        }
        qryResults = typedQuery.getResultList();

        return qryResults;
    }
}
