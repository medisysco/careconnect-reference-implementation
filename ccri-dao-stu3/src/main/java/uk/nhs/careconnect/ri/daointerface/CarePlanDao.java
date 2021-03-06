package uk.nhs.careconnect.ri.daointerface;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.param.DateParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.dstu3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.nhs.careconnect.ri.daointerface.transforms.CarePlanEntityToFHIRCarePlanTransformer;
import uk.nhs.careconnect.ri.entity.Terminology.ConceptEntity;
import uk.nhs.careconnect.ri.entity.carePlan.*;
import uk.nhs.careconnect.ri.entity.condition.ConditionEntity;
import uk.nhs.careconnect.ri.entity.encounter.EncounterEntity;
import uk.nhs.careconnect.ri.entity.episode.EpisodeOfCareEntity;
import uk.nhs.careconnect.ri.entity.patient.PatientEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TemporalType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static uk.nhs.careconnect.ri.daointerface.daoutils.MAXROWS;

@Repository
@Transactional
public class CarePlanDao implements CarePlanRepository {

    @PersistenceContext
    EntityManager em;

    @Autowired
    ConceptRepository conceptDao;

    @Autowired
    PatientRepository patientDao;

    @Autowired
    PractitionerRepository practitionerDao;

    @Autowired
    EncounterRepository encounterDao;

    @Autowired
    EpisodeOfCareRepository episodeDao;

    @Autowired
    ConditionRepository conditionDao;

    @Autowired
    private CodeSystemRepository codeSystemSvc;


    private static final Logger log = LoggerFactory.getLogger(CarePlanDao.class);

    @Override
    public void save(FhirContext ctx,CarePlanEntity carePlan) {

    }



    @Autowired
    CarePlanEntityToFHIRCarePlanTransformer carePlanIntoleranceEntityToFHIRCarePlanTransformer;

    @Override
    public Long count() {

        CriteriaBuilder qb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = qb.createQuery(Long.class);
        cq.select(qb.count(cq.from(CarePlanEntity.class)));
        //cq.where(/*your stuff*/);
        return em.createQuery(cq).getSingleResult();
    }



    @Override
    public CarePlan read(FhirContext ctx,IdType theId) {
        if (daoutils.isNumeric(theId.getIdPart())) {
            CarePlanEntity carePlanIntolerance = (CarePlanEntity) em.find(CarePlanEntity.class, Long.parseLong(theId.getIdPart()));

            return carePlanIntolerance == null
                    ? null
                    : carePlanIntoleranceEntityToFHIRCarePlanTransformer.transform(carePlanIntolerance);
        } else {
            return null;
        }
    }

    @Override
    public CarePlanEntity readEntity(FhirContext ctx,IdType theId) {
        if (daoutils.isNumeric(theId.getIdPart())) {
            CarePlanEntity carePlanIntolerance = (CarePlanEntity) em.find(CarePlanEntity.class, Long.parseLong(theId.getIdPart()));
            return carePlanIntolerance;
        }
        return null;
    }

    @Override
    public CarePlan create(FhirContext ctx, CarePlan carePlan, IdType theId, String theConditional) {

        log.debug("Allergy.save");
        //  log.info(ctx.newXmlParser().setPrettyPrint(true).encodeResourceToString(encounter));
        CarePlanEntity carePlanEntity = null;

        if (carePlan.hasId()) carePlanEntity = readEntity(ctx, carePlan.getIdElement());

        if (theConditional != null) {
            try {


                if (theConditional.contains("fhir.leedsth.nhs.uk/Id/carePlan")) {
                    URI uri = new URI(theConditional);

                    String scheme = uri.getScheme();
                    String host = uri.getHost();
                    String query = uri.getRawQuery();
                    log.debug(query);
                    String[] spiltStr = query.split("%7C");
                    log.debug(spiltStr[1]);

                    List<CarePlanEntity> results = searchEntity(ctx, null, null,new TokenParam().setValue(spiltStr[1]).setSystem("https://fhir.leedsth.nhs.uk/Id/carePlan"),null);
                    for (CarePlanEntity con : results) {
                        carePlanEntity = con;
                        break;
                    }
                } else {
                    log.info("NOT SUPPORTED: Conditional Url = "+theConditional);
                }

            } catch (Exception ex) {

            }
        }

        if (carePlanEntity == null) carePlanEntity = new CarePlanEntity();


        PatientEntity patientEntity = null;
        if (carePlan.hasSubject()) {
            log.trace(carePlan.getSubject().getReference());
            patientEntity = patientDao.readEntity(ctx, new IdType(carePlan.getSubject().getReference()));
            carePlanEntity.setPatient(patientEntity);
        }

        if (carePlan.hasStatus()) {
            carePlanEntity.setStatus(carePlan.getStatus());
        }
        if (carePlan.hasIntent()) {
            carePlanEntity.setIntent(carePlan.getIntent());
        }
        if (carePlan.hasPeriod()) {
            if (carePlan.getPeriod().hasStart()) {
                carePlanEntity.setPeriodStartDateTime(carePlan.getPeriod().getStart());
            }
            if (carePlan.getPeriod().hasEnd()) {
                carePlanEntity.setPeriodEndDateTime(carePlan.getPeriod().getEnd());
            }
        }
        if (carePlan.hasContext()) {
            if (carePlan.getContext().getReference().contains("Encounter")) {
                EncounterEntity encounterEntity = encounterDao.readEntity(ctx, new IdType(carePlan.getContext().getReference()));
                carePlanEntity.setContextEncounter(encounterEntity);
            } else if (carePlan.getContext().getReference().contains("EpisodeOfCare")) {
                EpisodeOfCareEntity episodeEntity = episodeDao.readEntity(ctx, new IdType(carePlan.getContext().getReference()));
                carePlanEntity.setContextEpisodeOfCare(episodeEntity);
            }
        }

        em.persist(carePlanEntity);

        for (Reference reference : carePlan.getAddresses()) {
            ConditionEntity conditionEntity = conditionDao.readEntity(ctx, new IdType(reference.getReference()));
            CarePlanCondition carePlanCondition = null;
            for (CarePlanCondition conditionSearch : carePlanEntity.getAddresses()) {
                if (conditionSearch.getCondition().getCode().getCode().equals(conditionEntity.getCode())) {
                    carePlanCondition = conditionSearch;
                    break;
                }
            }
            if (carePlanCondition == null) {
                carePlanCondition = new CarePlanCondition();
                carePlanCondition.setCondition(conditionEntity);
                carePlanCondition.setCarePlan(carePlanEntity);
                em.persist(carePlanCondition);
            }
        }
        for (CodeableConcept conceptCategory : carePlan.getCategory()) {
            ConceptEntity concept = conceptDao.findAddCode(conceptCategory.getCodingFirstRep());

            CarePlanCategory carePlanCategory = null;
            for (CarePlanCategory categorySearch : carePlanEntity.getCategories()) {
                if (concept.getCode().equals(categorySearch.getCategory().getCode())) {
                    carePlanCategory = categorySearch;
                    break;
                }
            }
            if (carePlanCategory == null) {
                carePlanCategory = new CarePlanCategory();
                carePlanCategory.setCategory(concept);
                carePlanCategory.setCarePlan(carePlanEntity);
                em.persist(carePlanCategory);
            }
        }

        for (Identifier identifier : carePlan.getIdentifier()) {
            CarePlanIdentifier carePlanIdentifier = null;

            for (CarePlanIdentifier orgSearch : carePlanEntity.getIdentifiers()) {
                if (identifier.getSystem().equals(orgSearch.getSystemUri()) && identifier.getValue().equals(orgSearch.getValue())) {
                    carePlanIdentifier = orgSearch;
                    break;
                }
            }
            if (carePlanIdentifier == null)  carePlanIdentifier = new CarePlanIdentifier();

            carePlanIdentifier.setValue(identifier.getValue());
            carePlanIdentifier.setSystem(codeSystemSvc.findSystem(identifier.getSystem()));
            carePlanIdentifier.setCarePlan(carePlanEntity);
            em.persist(carePlanIdentifier);
        }

        for (CarePlan.CarePlanActivityComponent component : carePlan.getActivity()) {
            CarePlanActivity activity= null;
            CarePlanActivityDetail detail = null;
            if (component.hasDetail()) {
                for (CarePlanActivity searchActivity : carePlanEntity.getActivities()) {
                    for (CarePlanActivityDetail searchDetail : searchActivity.getDetails()) {
                        if (searchDetail.getCode().getCode().equals(component.getDetail().getCode())) {
                            activity= searchActivity;
                            detail= searchDetail;
                            break;
                        }

                    }
                }
            }
            if (activity == null ) {
                activity =  new CarePlanActivity();
                activity.setCarePlan(carePlanEntity);
                em.persist(activity);
            }
            if (component.hasDetail()) {
                if (detail == null) {
                    detail = new CarePlanActivityDetail();
                    detail.setCarePlanActivity(activity);
                }
                if (component.getDetail().hasStatus()) {
                    detail.setStatus(component.getDetail().getStatus());
                }
                if (component.getDetail().hasCode()) {
                    log.info("CarePlan Detail "+component.getDetail().getCode().getCodingFirstRep().getCode());
                    ConceptEntity concept = conceptDao.findAddCode(component.getDetail().getCode().getCodingFirstRep());
                    if (concept != null) {
                        detail.setCode(concept);
                        em.persist(detail);
                    }
                }

            }
        }


        return carePlanIntoleranceEntityToFHIRCarePlanTransformer.transform(carePlanEntity);
    }

    @Override
    public List<CarePlan> search(FhirContext ctx,ReferenceParam patient, DateRangeParam date, TokenParam identifier ,TokenParam resid) {
        List<CarePlanEntity> qryResults = searchEntity(ctx,patient, date, identifier,resid);
        List<CarePlan> results = new ArrayList<>();

        for (CarePlanEntity carePlanIntoleranceEntity : qryResults)
        {
            // log.trace("HAPI Custom = "+doc.getId());
            CarePlan carePlanIntolerance = carePlanIntoleranceEntityToFHIRCarePlanTransformer.transform(carePlanIntoleranceEntity);
            results.add(carePlanIntolerance);
        }

        return results;
    }

    @Override
    public List<CarePlanEntity> searchEntity(FhirContext ctx, ReferenceParam patient, DateRangeParam date,  TokenParam identifier ,TokenParam resid) {


        List<CarePlanEntity> qryResults = null;

        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<CarePlanEntity> criteria = builder.createQuery(CarePlanEntity.class);
        Root<CarePlanEntity> root = criteria.from(CarePlanEntity.class);

        List<Predicate> predList = new LinkedList<Predicate>();
        List<CarePlan> results = new ArrayList<CarePlan>();

        if (patient != null) {
            // KGM 4/1/2018 only search on patient id
            if (daoutils.isNumeric(patient.getIdPart())) {
                Join<CarePlanEntity, PatientEntity> join = root.join("patient", JoinType.LEFT);

                Predicate p = builder.equal(join.get("id"), patient.getIdPart());
                predList.add(p);
            } else {
                Join<CarePlanEntity, PatientEntity> join = root.join("patient", JoinType.LEFT);

                Predicate p = builder.equal(join.get("id"), -1);
                predList.add(p);
            }
        }
        if (resid != null) {
            Predicate p = builder.equal(root.get("id"),resid.getValue());
            predList.add(p);
        }
        if (identifier !=null)
        {
            Join<CarePlanEntity, CarePlanIdentifier> join = root.join("identifiers", JoinType.LEFT);

            Predicate p = builder.equal(join.get("value"),identifier.getValue());
            predList.add(p);
            // TODO predList.add(builder.equal(join.get("system"),identifier.getSystem()));

        }


        ParameterExpression<Date> parameterLower = builder.parameter(Date.class);
        ParameterExpression<Date> parameterUpper = builder.parameter(Date.class);

        if (date !=null)
        {


            if (date.getLowerBoundAsInstant() != null) log.debug("getLowerBoundAsInstant()="+date.getLowerBoundAsInstant().toString());
            if (date.getUpperBoundAsInstant() != null) log.debug("getUpperBoundAsInstant()="+date.getUpperBoundAsInstant().toString());


            if (date.getLowerBound() != null) {

                DateParam dateParam = date.getLowerBound();
                log.debug("Lower Param - " + dateParam.getValue() + " Prefix - " + dateParam.getPrefix());

                switch (dateParam.getPrefix()) {
                   /* case GREATERTHAN: {
                        Predicate p = builder.greaterThan(root.<Date>get("assertedDateTime"), parameterLower);
                        predList.add(p);

                        break;
                    }
                    */
                    case GREATERTHAN:
                    case GREATERTHAN_OR_EQUALS: {
                        Predicate p = builder.greaterThanOrEqualTo(root.<Date>get("assertedDateTime"), parameterLower);
                        predList.add(p);
                        break;
                    }
                    case APPROXIMATE:
                    case EQUAL: {

                        Predicate plow = builder.greaterThanOrEqualTo(root.<Date>get("assertedDateTime"), parameterLower);
                        predList.add(plow);
                        break;
                    }
                    case NOT_EQUAL: {
                        Predicate p = builder.notEqual(root.<Date>get("assertedDateTime"), parameterLower);
                        predList.add(p);
                        break;
                    }
                    case STARTS_AFTER: {
                        Predicate p = builder.greaterThan(root.<Date>get("assertedDateTime"), parameterLower);
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
                        Predicate pupper = builder.lessThan(root.<Date>get("assertedDateTime"), parameterUpper);
                        predList.add(pupper);
                        break;
                    }

                    case LESSTHAN_OR_EQUALS: {
                        Predicate p = builder.lessThanOrEqualTo(root.<Date>get("assertedDateTime"), parameterUpper);
                        predList.add(p);
                        break;
                    }
                    case ENDS_BEFORE:
                    case LESSTHAN: {
                        Predicate p = builder.lessThan(root.<Date>get("assertedDateTime"), parameterUpper);
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
        criteria.orderBy(builder.desc(root.get("assertedDateTime")));

        TypedQuery<CarePlanEntity> typedQuery = em.createQuery(criteria).setMaxResults(MAXROWS);

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
