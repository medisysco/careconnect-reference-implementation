package uk.nhs.careconnect.ri.daointerface.transforms;


import org.apache.commons.collections4.Transformer;
import org.hl7.fhir.dstu3.model.Address;
import org.hl7.fhir.dstu3.model.Meta;
import org.hl7.fhir.dstu3.model.Organization;
import org.hl7.fhir.dstu3.model.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.nhs.careconnect.ri.entity.BaseAddress;
import uk.nhs.careconnect.ri.entity.organization.OrganisationAddress;
import uk.nhs.careconnect.ri.entity.organization.OrganisationEntity;
import uk.nhs.careconnect.ri.entity.organization.OrganisationTelecom;
import uk.org.hl7.fhir.core.Stu3.CareConnectProfile;

@Component
public class OrganisationEntityToFHIROrganizationTransformer implements Transformer<OrganisationEntity, Organization> {

    private final Transformer<BaseAddress, Address> addressTransformer;

    public OrganisationEntityToFHIROrganizationTransformer(@Autowired Transformer<BaseAddress, Address> addressTransformer) {
        this.addressTransformer = addressTransformer;
    }

    @Override
    public Organization transform(final OrganisationEntity organisationEntity) {
        final Organization organisation = new Organization();

        Meta meta = new Meta().addProfile(CareConnectProfile.Organization_1);

        if (organisationEntity.getUpdated() != null) {
            meta.setLastUpdated(organisationEntity.getUpdated());
        }
        else {
            if (organisationEntity.getCreated() != null) {
                meta.setLastUpdated(organisationEntity.getCreated());
            }
        }
        organisation.setMeta(meta);

        for(int f=0;f<organisationEntity.getIdentifiers().size();f++)
        {
            organisation.addIdentifier()
                    .setSystem(organisationEntity.getIdentifiers().get(f).getSystem().getUri())
                    .setValue(organisationEntity.getIdentifiers().get(f).getValue());
        }


        organisation.setId(organisationEntity.getId().toString());

        organisation.setName(
                organisationEntity.getName());


        for(OrganisationTelecom telecom : organisationEntity.getTelecoms())
        {
            organisation.addTelecom()
                    .setSystem(telecom.getSystem())
                    .setValue(telecom.getValue())
                    .setUse(telecom.getTelecomUse());
        }

        for(OrganisationAddress organisationAddress : organisationEntity.getAddresses()) {
            Address adr = addressTransformer.transform(organisationAddress);
            organisation.addAddress(adr);
        }

        if (organisationEntity.getType()!=null) {
            organisation.addType()
                    .addCoding()
                    .setCode(organisationEntity.getType().getCode())
                    .setDisplay(organisationEntity.getType().getDisplay())
                    .setSystem(organisationEntity.getType().getSystem());
        }
        if (organisationEntity.getPartOf() != null) {
            organisation.setPartOf(new Reference("Organization/"+organisationEntity.getPartOf().getId()));
            organisation.getPartOf().setDisplay(organisationEntity.getPartOf().getName());
        }
        if (organisationEntity.getActive() != null) {
            organisation.setActive(organisationEntity.getActive());
        }
        return organisation;

    }
}
