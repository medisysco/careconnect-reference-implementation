package uk.nhs.careconnect.ri.fhirserver.provider;


import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.*;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.api.server.RequestDetails;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import org.hl7.fhir.dstu3.model.Condition;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.OperationOutcome;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.careconnect.ri.daointerface.ConditionRepository;
import uk.nhs.careconnect.ri.lib.OperationOutcomeFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public class ConditionProvider implements ICCResourceProvider {



    @Autowired
    private ConditionRepository conditionDao;

    @Autowired
    FhirContext ctx;

    @Override
    public Long count() {
        return conditionDao.count();
    }

    @Override
    public Class<? extends IBaseResource> getResourceType() {
        return Condition.class;
    }


    @Update
    public MethodOutcome update(HttpServletRequest theRequest, @ResourceParam Condition condition, @IdParam IdType theId, @ConditionalUrlParam String theConditional, RequestDetails theRequestDetails) {


        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();

        method.setOperationOutcome(opOutcome);


        Condition newCondition = conditionDao.create(ctx,condition, theId, theConditional);
        method.setId(newCondition.getIdElement());
        method.setResource(newCondition);

        return method;
    }
    /*
    @Update
    public MethodOutcome update(HttpServletRequest theRequest, @ResourceParam Condition condition, @IdParam IdType theId) {


        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();

        method.setOperationOutcome(opOutcome);
        Condition newCondition = conditionDao.create(ctx,condition, theId, null);
        method.setId(newCondition.getIdElement());
        method.setResource(newCondition);



        return method;
    }
    */

    @Create
    public MethodOutcome create(HttpServletRequest theRequest, @ResourceParam Condition condition) {


        MethodOutcome method = new MethodOutcome();
        method.setCreated(true);
        OperationOutcome opOutcome = new OperationOutcome();

        method.setOperationOutcome(opOutcome);


        Condition newCondition = conditionDao.create(ctx,condition, null,null);
        method.setId(newCondition.getIdElement());
        method.setResource(newCondition);



        return method;
    }

    @Search
    public List<Condition> search(HttpServletRequest theRequest,
                                  @OptionalParam(name = Condition.SP_PATIENT) ReferenceParam patient
            , @OptionalParam(name = Condition.SP_CATEGORY) TokenParam category
            , @OptionalParam(name = Condition.SP_CLINICAL_STATUS) TokenParam clinicalstatus
            , @OptionalParam(name = Condition.SP_ASSERTED_DATE) DateRangeParam asserted
            , @OptionalParam(name = Condition.SP_IDENTIFIER) TokenParam identifier
            , @OptionalParam(name = Condition.SP_RES_ID) TokenParam resid
                                  ) {
        return conditionDao.search(ctx,patient, category, clinicalstatus, asserted, identifier,resid);
    }

    @Read()
    public Condition get(@IdParam IdType conditionId) {

        Condition condition = conditionDao.read(ctx,conditionId);

        if ( condition == null) {
            throw OperationOutcomeFactory.buildOperationOutcomeException(
                    new ResourceNotFoundException("No Condition/ " + conditionId.getIdPart()),
                    OperationOutcome.IssueSeverity.ERROR, OperationOutcome.IssueType.NOTFOUND);
        }

        return condition;
    }


}
