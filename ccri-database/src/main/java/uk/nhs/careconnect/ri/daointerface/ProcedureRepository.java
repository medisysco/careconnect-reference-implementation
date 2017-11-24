package uk.nhs.careconnect.ri.daointerface;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.ConditionalUrlParam;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.DateRangeParam;
import ca.uhn.fhir.rest.param.ReferenceParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Procedure;
import uk.nhs.careconnect.ri.entity.condition.ConditionEntity;
import uk.nhs.careconnect.ri.entity.procedure.ProcedureEntity;

import java.util.List;

public interface ProcedureRepository extends BaseInterface {
    void save(FhirContext ctx,ProcedureEntity procedure);

    Procedure read(FhirContext ctx, IdType theId);

    Procedure create(FhirContext ctx,Procedure procedure, @IdParam IdType theId, @ConditionalUrlParam String theConditional);

    ProcedureEntity readEntity(FhirContext ctx, IdType theId);

    List<Procedure> search(FhirContext ctx,
            @OptionalParam(name = Procedure.SP_PATIENT) ReferenceParam patient
            ,@OptionalParam(name = Procedure.SP_DATE) DateRangeParam date
            , @OptionalParam(name = Procedure.SP_SUBJECT) ReferenceParam subject
            , @OptionalParam(name = Procedure.SP_IDENTIFIER) TokenParam identifier
    );

    List<ProcedureEntity> searchEntity(FhirContext ctx,
            @OptionalParam(name = Procedure.SP_PATIENT) ReferenceParam patient
            ,@OptionalParam(name = Procedure.SP_DATE) DateRangeParam date
            , @OptionalParam(name = Procedure.SP_SUBJECT) ReferenceParam subject
            , @OptionalParam(name = Procedure.SP_IDENTIFIER) TokenParam identifier
    );
}
