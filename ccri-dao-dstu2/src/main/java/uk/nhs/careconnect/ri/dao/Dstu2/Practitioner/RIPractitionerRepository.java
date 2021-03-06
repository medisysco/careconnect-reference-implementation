package uk.nhs.careconnect.ri.dao.Dstu2.Practitioner;

import ca.uhn.fhir.rest.annotation.ConditionalUrlParam;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.OptionalParam;
import ca.uhn.fhir.rest.param.StringParam;
import ca.uhn.fhir.rest.param.TokenParam;
import org.hl7.fhir.instance.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import uk.nhs.careconnect.ri.dao.Stu3.CodeSystem.CodeSystemRepository;
import uk.nhs.careconnect.ri.dao.Stu3.CodeSystem.ConceptRepository;
import uk.nhs.careconnect.ri.dao.Dstu2.Organisation.OrganisationRepository;
import uk.nhs.careconnect.ri.entity.AddressEntity;
import uk.nhs.careconnect.ri.entity.practitioner.*;
import uk.org.hl7.fhir.core.Dstu2.CareConnectSystem;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Repository
@Transactional
public class RIPractitionerRepository implements PractitionerRepository {

    @PersistenceContext
    EntityManager em;
    @Autowired
    private PractitionerEntityToFHIRPractitionerTransformer practitionerEntityToFHIRPractitionerTransformer;

    @Autowired
    private CodeSystemRepository codeSystemSvc;

    @Autowired
    private OrganisationRepository organisationRepository;

    @Autowired
    private ConceptRepository codeSvc;

    private static final Logger log = LoggerFactory.getLogger(RIPractitionerRepository.class);

    public void save(PractitionerEntity practitioner)
    {
        em.persist(practitioner);
    }

    public Practitioner read(IdType theId) {

        PractitionerEntity practitionerEntity = (PractitionerEntity) em.find(PractitionerEntity.class,Long.parseLong(theId.getIdPart()));

        return practitionerEntity == null
                ? null
                : practitionerEntityToFHIRPractitionerTransformer.transform(practitionerEntity);

    }

    @Override
    public Practitioner create(Practitioner practitioner, @IdParam IdType theId, @ConditionalUrlParam String theConditional){
        PractitionerEntity practitionerEntity = null;

        if (practitioner.hasId()) {
            practitionerEntity =  (PractitionerEntity) em.find(PractitionerEntity.class,Long.parseLong(practitioner.getId()));
        }
        if (theConditional != null) {
            try {

                //CareConnectSystem.ODSPractitionerCode
                if (theConditional.contains("fhir.nhs.uk/Id/sds-user-id")) {
                    URI uri = new URI(theConditional);

                    String scheme = uri.getScheme();
                    String host = uri.getHost();
                    String query = uri.getRawQuery();
                    log.debug(query);
                    String[] spiltStr = query.split("%7C");
                    log.debug(spiltStr[1]);

                    List<PractitionerEntity> results = searchPractitioner(new TokenParam().setValue(spiltStr[1]).setSystem(CareConnectSystem.SDSUserId));
                    for (PractitionerEntity org : results) {
                        practitionerEntity = org;
                        break;
                    }
                } else {
                    log.info("NOT SUPPORTED: Conditional Url = "+theConditional);
                }

            } catch (Exception ex) {

            }
        }
        if (practitionerEntity == null) {
            practitionerEntity = new PractitionerEntity();
        }


        practitionerEntity.setActive(practitioner.getActive());

        em.persist(practitionerEntity);

        PractitionerName practitionerName = null;
        if (practitionerEntity.getNames().size()>0) {
            practitionerName = practitionerEntity.getNames().get(0);
        } else {
            practitionerName = new PractitionerName();
            practitionerName.setPractitionerEntity(practitionerEntity);
        }

        if (practitioner.getName().getFamily().size() >0 ) {
            practitionerName.setFamilyName(practitioner.getName().getFamily().get(0).getValue()); }
        if (practitioner.getName().getGiven().size() > 0 ) {
            practitionerName.setGivenName(practitioner.getName().getGiven().get(0).getValue()); }
        if (practitioner.getName().getPrefix().size() >0) {
            practitionerName.setPrefix(practitioner.getName().getPrefix().get(0).getValue()); }
        em.persist(practitionerName);

        for (Identifier ident : practitioner.getIdentifier()) {
            PractitionerIdentifier practitionerIdentifier = null;

            for (PractitionerIdentifier orgSearch : practitionerEntity.getIdentifiers()) {
                if (ident.getSystem().equals(orgSearch.getSystemUri()) && ident.getValue().equals(orgSearch.getValue())) {
                    practitionerIdentifier = orgSearch;
                    break;
                }
            }
            if (practitionerIdentifier == null)  practitionerIdentifier = new PractitionerIdentifier();

            practitionerIdentifier.setValue(ident.getValue());
            practitionerIdentifier.setSystem(codeSystemSvc.findSystem(ident.getSystem()));
            practitionerIdentifier.setPractitioner(practitionerEntity);
            em.persist(practitionerIdentifier);
        }

        for (ContactPoint telecom : practitioner.getTelecom()) {
            PractitionerTelecom practitionerTelecom = null;

            for (PractitionerTelecom orgSearch : practitionerEntity.getTelecoms()) {
                if (telecom.getValue().equals(orgSearch.getValue())) {
                    practitionerTelecom = orgSearch;
                    break;
                }
            }
            if (practitionerTelecom == null) {
                practitionerTelecom = new PractitionerTelecom();
                practitionerTelecom.setPractitionerEntity(practitionerEntity);
            }

            practitionerTelecom.setValue(telecom.getValue());
            practitionerTelecom.setSystemDstu2(telecom.getSystem());
            practitionerTelecom.setTelecomUseDstu2(telecom.getUse());

            em.persist(practitionerTelecom);
        }
        for (Address address  :practitioner.getAddress()) {

            PractitionerAddress practitionerAddress = null;

            if (practitionerEntity.getAddresses().size()>0) practitionerAddress = practitionerEntity.getAddresses().get(0);
            if (practitionerAddress == null) {
                practitionerAddress = new PractitionerAddress();
                practitionerAddress.setPractitioner(practitionerEntity);
            }
            AddressEntity addr = practitionerAddress.getAddress();
            if (addr == null) {
                addr = practitionerAddress.setAddress(new AddressEntity());
            }

            if (address.getLine().size()>0) addr.setAddress1(address.getLine().get(0).getValue());
            if (address.getLine().size()>1) addr.setAddress2(address.getLine().get(1).getValue());
            if (address.getLine().size()>2) addr.setAddress3(address.getLine().get(2).getValue());
            if (address.getCity() != null) addr.setCity(address.getCity());
            if (address.getDistrict() != null) addr.setCounty(address.getDistrict());
            if (address.getPostalCode() != null) addr.setPostcode(address.getPostalCode());
            if (address.getUse() != null) practitionerAddress.setAddressUseDstu2(address.getUse());
            em.persist(addr);
            em.persist(practitionerAddress);

        }

        if (practitioner.getPractitionerRole().size()>0) {

            PractitionerRole practitionerRole = null;

            if (practitionerEntity.getRoles().size()>0) {
                practitionerRole = practitionerEntity.getRoles().get(0);
            } else {
                practitionerRole = new PractitionerRole();
                practitionerRole.setPractitioner(practitionerEntity);
            }

            if (practitioner.getPractitionerRole().get(0).getManagingOrganization().getReference() != null) {
                practitionerRole.setManaginsOrganisation(organisationRepository.readEntity(new IdType(practitioner.getPractitionerRole().get(0).getManagingOrganization().getReference())));
            }

            if (practitioner.getPractitionerRole().get(0).getRole().getCoding().size()>0) {
                if (practitioner.getPractitionerRole().get(0).getRole().getCoding().get(0).getSystem().equals(CareConnectSystem.SDSJobRoleName)) {
                    practitionerRole.setRole(codeSvc.findCode(CareConnectSystem.SDSJobRoleName, practitioner.getPractitionerRole().get(0).getRole().getCoding().get(0).getCode()));
                }
            }
            em.persist(practitionerRole);
        }

        log.debug("Called PERSIST id="+practitionerEntity.getId().toString());
        practitioner.setId(practitionerEntity.getId().toString());

        return practitioner;
    }

    public List<PractitionerEntity> searchPractitioner (
            @OptionalParam(name = Practitioner.SP_IDENTIFIER) TokenParam identifier

    )
    {
        List<PractitionerEntity> qryResults = null;

        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<PractitionerEntity> criteria = builder.createQuery(PractitionerEntity.class);
        Root<PractitionerEntity> root = criteria.from(PractitionerEntity.class);


        List<Predicate> predList = new LinkedList<Predicate>();
        List<PractitionerEntity> results = new ArrayList<PractitionerEntity>();

        if (identifier !=null)
        {

            Join<PractitionerEntity, PractitionerIdentifier> join = root.join("identifiers", JoinType.LEFT);

            Predicate p = builder.equal(join.get("value"),identifier.getValue());
            predList.add(p);
            // TODO predList.add(builder.equal(join.get("system"),identifier.getSystem()));

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

        qryResults = em.createQuery(criteria).getResultList();

        for (PractitionerEntity practitionerEntity : qryResults)
        {
            // log.trace("HAPI Custom = "+doc.getId());
           // Practitioner practitioner = practitionerEntityToFHIRPractitionerTransformer.transform(practitionerEntity);
            results.add(practitionerEntity);
        }

        return results;
    }


    public List<Practitioner> searchPractitioner (
            @OptionalParam(name = Practitioner.SP_IDENTIFIER) TokenParam identifier,
            @OptionalParam(name = Location.SP_NAME) StringParam name
    )
    {
        List<PractitionerEntity> qryResults = null;

        CriteriaBuilder builder = em.getCriteriaBuilder();

        CriteriaQuery<PractitionerEntity> criteria = builder.createQuery(PractitionerEntity.class);
        Root<PractitionerEntity> root = criteria.from(PractitionerEntity.class);


        List<Predicate> predList = new LinkedList<Predicate>();
        List<Practitioner> results = new ArrayList<Practitioner>();

        if (identifier !=null)
        {

            Join<PractitionerEntity, PractitionerIdentifier> join = root.join("identifiers", JoinType.LEFT);

            Predicate p = builder.equal(join.get("value"),identifier.getValue());
            predList.add(p);
            // TODO predList.add(builder.equal(join.get("system"),identifier.getSystem()));

        }

        if ( /*(familyName != null) || (givenName != null) ||*/ (name != null)) {

            Join<PractitionerEntity,PractitionerName> namejoin = root.join("names",JoinType.LEFT);
           /*
            if (familyName != null) {
                Predicate p =
                        builder.like(
                                builder.upper(namejoin.get("familyName").as(String.class)),
                                builder.upper(builder.literal("%"+familyName.getValue()+"%"))
                        );
                predList.add(p);
            }
            if (givenName != null) {
                Predicate p =
                        builder.like(
                                builder.upper(namejoin.get("givenName").as(String.class)),
                                builder.upper(builder.literal("%"+givenName.getValue()+"%"))
                        );

                predList.add(p);
            }
             */
            if (name != null) {
                Predicate pgiven = builder.like(
                        builder.upper(namejoin.get("givenName").as(String.class)),
                        builder.upper(builder.literal("%"+name.getValue()+"%"))
                );
                Predicate pfamily = builder.like(
                        builder.upper(namejoin.get("familyName").as(String.class)),
                        builder.upper(builder.literal("%"+name.getValue()+"%"))
                );
                Predicate p = builder.or(pfamily, pgiven);
                predList.add(p);
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

        qryResults = em.createQuery(criteria).getResultList();

        for (PractitionerEntity practitionerEntity : qryResults)
        {
           // log.trace("HAPI Custom = "+doc.getId());
            Practitioner practitioner = practitionerEntityToFHIRPractitionerTransformer.transform(practitionerEntity);
            results.add(practitioner);
        }

        return results;
    }


}
