import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils from '../../../support/util/release-api-utils';

// # Documentation can be found at https://confluence.sbb.ch/x/pS4ynw
// # TODO: Use Verkehrsmittel & Haltestellentyp combinations
// # TODO: Add after each scenario-version-creation (POST/PUT) that the 4 attributes are in the right state
// #       - designationOfficial
// #       - servicePointGeolocation->north/east
// #       - meansOfTransport=TRAIN
// #       - validFrom-validTo

describe('SePo: Status Scenario', { testIsolation: false }, () => {
  let servicePointId: number;
  let sboid: string;
  let height: string;
  let scenario: string;
  let north: string;
  let east: string;
  let validFrom: string;
  let validTo: string;
  let designationOfficial: string;
  let designationLong: string;
  let etagVersion: number = -1;

  // country=SWITZERLAND -> countryCode=85, with that we can be sure that the status can go to DRAFT, because in other
  // countries it is not possible that the status is other than VALIDATED, meaning only in Switzerland we have a workflow for
  // service-point changes
  const country = 'SWITZERLAND';
  const spatialReference = 'LV95';
  const meansOfTransport = 'TRAIN';
  const statusDraft = 'DRAFT';
  const statusValidated = 'VALIDATED';
  const timeWithSeconds = new Date().toISOString().split('T')[1].split('.')[0]; // e.g. 16:25:20

  // TODO: Check: Use SBB as business organisation to be most flexible
  // const businessOrganisation = 'ch:1:sboid:100001';

  const setEtagVersion = (etagVersionContainer) => {
    expect(etagVersionContainer)
      .property('etagVersion')
      .to.be.a('number')
      .and.greaterThan(etagVersion);
    etagVersion = etagVersionContainer.etagVersion;
  };

  const checkStatusAndUpdateEtagVersion = (
    response: Cypress.Response<any>,
    status: string,
    responseStatus: number
  ) => {
    expect(response).property('status').to.equal(responseStatus);
    expect(response).to.have.property('body');

    const sePoVersion = response.body.find(
      (sePoVersion) => String(sePoVersion.designationLong) === designationLong
    );
    expect(sePoVersion)
      .property('status')
      .to.be.a('string')
      .and.to.equal(status);

    // Always use the first version as basis for the other scenarios
    // so that the whole test works
    const firstSePoVersion = response.body.find(
      (sePoVersion) => sePoVersion.id === servicePointId
    );
    setEtagVersion(firstSePoVersion);
  };

  const checkStatusAndUpdateEtagVersionPost = (
    response: Cypress.Response<any>,
    status: string,
    responseStatus: number
  ) => {
    expect(response).property('status').to.equal(responseStatus);

    expect(response).to.have.property('body');
    const body = response.body;

    expect(body).property('status').to.be.a('string').and.to.equal(status);

    setEtagVersion(body);
  };

  const setServicePointAttributes = (height: string) => {
    scenario = `0${height}`;
    north = `12051${scenario}.${scenario}`;
    east = `26520${scenario}.${scenario}`;

    // validFrom-validTo > 60 days -> status=DRAFT
    validFrom = `20${scenario}-01-01`;
    validTo = `20${scenario}-12-31`;

    designationOfficial = `change me ${timeWithSeconds}`;
    designationLong = `Scenario ${scenario} at ${timeWithSeconds}`;
  };

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Create dependent Business Organisation', () => {
    CommonUtils.createDependentBusinessOrganisation(
      ReleaseApiUtils.today(),
      ReleaseApiUtils.today()
    ).then((sboidOfBO) => {
      sboid = sboidOfBO;
    });
  });

  it('Step-3: Create new Service Point', () => {
    height = '1';
    setServicePointAttributes(height);

    CommonUtils.post('/service-point-directory/v1/service-points', {
      designationLong: designationLong,
      country: country,
      designationOfficial: designationOfficial,
      businessOrganisation: sboid,
      meansOfTransport: [meansOfTransport],
      validFrom: validFrom,
      validTo: validTo,
      servicePointGeolocation: {
        spatialReference: spatialReference,
        north: north,
        east: east,
        height: height,
      },
    }).then((response) => {
      checkStatusAndUpdateEtagVersionPost(
        response,
        statusDraft,
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );

      expect(response).property('body').property('id').to.be.a('number');
      servicePointId = response.body.id;
    });
  });

  it('Step-4: Change Version1-status to VALIDATED', () => {
    CommonUtils.post(
      `/service-point-directory/v1/service-points/versions/${servicePointId}/skip-workflow`,
      {}
    ).then((response) => {
      checkStatusAndUpdateEtagVersionPost(
        response,
        statusValidated,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );
    });
  });

  it('Step-5: SePo-Status Scenario 2', () => {
    height = '1';
    setServicePointAttributes(height);

    CommonUtils.put(
      `/service-point-directory/v1/service-points/${servicePointId}`,
      {
        designationLong: designationLong,
        designationOfficial: designationOfficial,
        businessOrganisation: sboid,
        meansOfTransport: [meansOfTransport],
        validFrom: validFrom,
        validTo: validTo,
        etagVersion: etagVersion,
      }
    ).then((response) => {
      checkStatusAndUpdateEtagVersion(
        response,
        statusValidated,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );
    });
  });
});
