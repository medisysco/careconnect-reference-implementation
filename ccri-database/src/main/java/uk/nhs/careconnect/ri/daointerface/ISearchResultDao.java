package uk.nhs.careconnect.ri.daointerface;

import ca.uhn.fhir.rest.api.server.IBundleProvider;

public interface ISearchResultDao {

    String save(IBundleProvider searchResults);

    IBundleProvider read(String searchId);
}
