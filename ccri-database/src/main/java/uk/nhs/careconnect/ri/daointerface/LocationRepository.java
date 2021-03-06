package uk.nhs.careconnect.ri.daointerface;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.annotation.ConditionalUrlParam;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.dstu3.model.IdType;
import org.hl7.fhir.dstu3.model.Location;
import org.hl7.fhir.dstu3.model.Patient;
import uk.nhs.careconnect.ri.entity.location.LocationEntity;

import java.util.List;

public interface LocationRepository extends BaseDao<LocationEntity,Location> {
    void save(FhirContext ctx,LocationEntity location);

    Location read(FhirContext ctx, IdType theId);

    LocationEntity readEntity(FhirContext ctx,IdType theId);

    Location create(FhirContext ctx,Location location, @IdParam IdType theId, @ConditionalUrlParam String theConditional);



    List<Location> searchLocation(FhirContext ctx,

            @OptionalParam(name = Location.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = Location.SP_NAME) StringParam name,
            @OptionalParam(name = Location.SP_ADDRESS_POSTALCODE) StringParam postCode
            ,@OptionalParam(name= Location.SP_RES_ID) TokenParam id

    );

    List<LocationEntity> searchLocationEntity (FhirContext ctx,

            @OptionalParam(name = Location.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = Location.SP_NAME) StringParam name,
            @OptionalParam(name = Location.SP_ADDRESS_POSTALCODE) StringParam postCode
            ,@OptionalParam(name= Location.SP_RES_ID) TokenParam id

    );
}
