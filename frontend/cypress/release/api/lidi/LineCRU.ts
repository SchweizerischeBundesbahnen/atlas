import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils from "../../../support/util/release-api-utils";

const VALID_FROM = "2024-08-01";
describe('LiDi: Scenario Line-CRUD: New Line', { testIsolation: false }, () => {
  let slnid = "";
  let sboid = "";
  let lineVersionId = -1;
  let etagVersion = -1;

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Create dependent Business Organisation', () => {
    CommonUtils.createDependentBusinessOrganisation(ReleaseApiUtils.today(), ReleaseApiUtils.today()).then((sboidOfBO:string) => {
      sboid = sboidOfBO
    });
  });

  it('Step-3: Create a new line version', () => {
    CommonUtils.post('/line-directory/v2/lines/versions', {
      swissLineNumber: ReleaseApiUtils.today(),
      lineType: "ORDERLY",
      businessOrganisation: sboid,
      validFrom: VALID_FROM,
      validTo: "2024-08-02",
      lineConcessionType: "RACK_FREE_TRIPS",
      offerCategory: "SL"
    }).then((response) => {
      expect(response.status).to.equal(201);

      expect(response.body).to.have.property('slnid').that.is.a('string');
      slnid = response.body.slnid;

      expect(response.body).to.have.property('id').that.is.a('number');
      lineVersionId = response.body.id;
    });
  });

  it('Step-4: Read the line version', () => {
    // It is expected that slnid has already been set from a previous step
    CommonUtils.get(`/line-directory/v2/lines/versions/${slnid}`).then((response) => {
      const lineVersionsFirst = ReleaseApiUtils.makeCommonChecks(response, slnid, lineVersionId);

      expect(lineVersionsFirst).to.have.property('swissLineNumber').that.is.a('string');

      // Store the ETag version in a variable
      etagVersion = lineVersionsFirst.etagVersion;
      expect(etagVersion).to.exist.and.be.a('number');
    });
  });

  it('Step-5: Update the line version', () => {
    const todayInIso = ReleaseApiUtils.today().toISOString();
    CommonUtils.put(`/line-directory/v2/lines/versions/${lineVersionId}`, {
      swissLineNumber: todayInIso, // Changed, see above
      lineType: "ORDERLY",
      businessOrganisation: sboid,
      validFrom: VALID_FROM,
      validTo: ReleaseApiUtils.tomorrowAsAtlasString(), // Changed, see above
      etagVersion: etagVersion,
      offerCategory: "SL"
    }).then((response) => {
      const lineVersionsFirst = ReleaseApiUtils.makeCommonChecks(response, slnid, lineVersionId);
      expect(lineVersionsFirst).to.have.property('swissLineNumber').that.equals(todayInIso);
    });
  });
});
