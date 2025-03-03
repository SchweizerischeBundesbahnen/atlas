import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils from '../../../support/util/release-api-utils';

describe('SePo: Status Scenario', { testIsolation: false }, () => {
  let servicePointId = -1;
  let sboid = '';
  const timeWithSeconds = new Date().toISOString();
  const meansOfTransport = 'TRAIN';
  const height = '1';
  const scenario = `0${height}`;
  const north = `12051${scenario}.${scenario}`;
  const east = `26520${scenario}.${scenario}`;
  const validFrom = `20${scenario}-01-01`;
  const validTo = `20${scenario}-12-31`;
  const country = 'SWITZERLAND';
  const businessOrganisation = 'ch:1:sboid:100001';
  const designationLong = `Scenario ${scenario} at ${timeWithSeconds}`;
  const designationOfficial = `change me ${timeWithSeconds}`;

  const spatialReference = 'LV95';
  const statusDraft = 'DRAFT';

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
    CommonUtils.post('/service-point-directory/v1/service-points', {
      designationLong: designationLong,
      country: country,
      designationOfficial: designationOfficial,
      businessOrganisation: businessOrganisation,
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
      expect(response)
        .property('status')
        .to.equal(CommonUtils.HTTP_REST_API_RESPONSE_CREATED);
      servicePointId = response.body.id;
      expect(response.body.status).to.equal(statusDraft);
    });
  });
});
