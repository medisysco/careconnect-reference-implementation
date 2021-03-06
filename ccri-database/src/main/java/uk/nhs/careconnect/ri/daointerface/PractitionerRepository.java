package uk.nhs.careconnect.ri.daointerface;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.ConditionalUrlParam;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.dstu3.model.Practitioner;
import uk.nhs.careconnect.ri.entity.practitioner.PractitionerEntity;

import java.util.List;

public interface PractitionerRepository extends BaseDao<PractitionerEntity,Practitioner> {

    void save(FhirContext ctx,PractitionerEntity practitioner);

    Practitioner read(FhirContext ctx, IdType theId);

    PractitionerEntity readEntity(FhirContext ctx, IdType theId);

    Practitioner create(FhirContext ctx, Practitioner practitioner, @IdParam IdType theId, @ConditionalUrlParam String theConditional);


    List<Practitioner> searchPractitioner (FhirContext ctx,
            @OptionalParam(name = Practitioner.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = Practitioner.SP_NAME) StringParam name,
            @OptionalParam(name = Practitioner.SP_ADDRESS_POSTALCODE) StringParam postCode
            ,@OptionalParam(name= Practitioner.SP_RES_ID) TokenParam id


    );

    List<PractitionerEntity> searchPractitionerEntity (FhirContext ctx,
            @OptionalParam(name = Practitioner.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = Practitioner.SP_NAME) StringParam name,
            @OptionalParam(name = Practitioner.SP_ADDRESS_POSTALCODE) StringParam postCode
            ,@OptionalParam(name= Practitioner.SP_RES_ID) TokenParam id
    );


}
