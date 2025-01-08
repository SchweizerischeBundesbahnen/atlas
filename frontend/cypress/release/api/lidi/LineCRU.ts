import CommonUtils from '../../../support/util/common-utils';

describe('LiDi: Scenario Line-CRUD: New Line', { testIsolation: false }, () => {
  let slnid = "";
  let sboid = "";
  let lineVersionId = -1;
  let etagVersion = -1;

  const updatedValidTo = "2024-08-03"; // New validTo date
  const today = new Date();

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Create dependent Business Organisation', () => {
    CommonUtils.createDependentBusinessOrganisation(today, today).then((sboidOfBO:string) => {
      sboid = sboidOfBO
    });
  });

  it('Step-3: Create a new line version', () => {
    CommonUtils.post('/line-directory/v2/lines/versions', {
      swissLineNumber: today,
      lineType: "ORDERLY",
      businessOrganisation: sboid,
      validFrom: "2024-08-01",
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

  function makeCommonChecks(response: Cypress.Response<any>) {
    // Check the status code
    expect(response.status).to.equal(200);

    // Check if the response is an array
    const lineVersions = response.body;
    expect(Array.isArray(lineVersions)).to.be.true; // Verify that it is an array
    expect(lineVersions.length).to.equal(1); // Verify the length of the array

    const lineVersionsFirst = lineVersions[0];

    // Check the values of the first element in the array
    expect(lineVersionsFirst).to.have.property('slnid').that.equals(slnid);
    expect(lineVersionsFirst).to.have.property('id').that.is.a('number').and.equals(lineVersionId);
    return lineVersionsFirst;
  }

  it('Step-4: Read the line version', () => {
    // It is expected that slnid has already been set from a previous step
    CommonUtils.get(`/line-directory/v2/lines/versions/${slnid}`).then((response) => {
      const lineVersionsFirst = makeCommonChecks(response);

      expect(lineVersionsFirst).to.have.property('swissLineNumber').that.is.a('string');

      // Store the ETag version in a variable
      etagVersion = lineVersionsFirst.etagVersion;
      expect(etagVersion).to.exist.and.be.a('number');
    });
  });

  it('Step-5: Update the line version', () => {
    const todayInIso = today.toISOString();
    CommonUtils.put(`/line-directory/v2/lines/versions/${lineVersionId}`, {
      swissLineNumber: todayInIso,
      lineType: "ORDERLY",
      businessOrganisation: sboid,
      validFrom: "2024-08-01",
      validTo: updatedValidTo, // Use the updated validTo date
      etagVersion: etagVersion,
      offerCategory: "SL"
    }).then((response) => {
      const lineVersionsFirst = makeCommonChecks(response);
      expect(lineVersionsFirst).to.have.property('swissLineNumber').that.equals(todayInIso);
    });
  });
});
