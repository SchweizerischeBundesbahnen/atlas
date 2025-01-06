import CommonUtils from '../../../support/util/common-utils';

describe('LiDi: Scenario Subline-CRUD: New Line', { testIsolation: false }, () => {
  let mainSlnid = "";
  let sublineSlnid = "";
  let sboid = "";
  let sublineVersionId = "";
  let etagVersion = "";


  const today = new Date();
  const tomorrow = new Date();
  const updatedValidTo = new Date();
  tomorrow.setDate(today.getDate() + 1);
  updatedValidTo.setDate(tomorrow.getDate() + 1);

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Create dependent Business Organisation', () => {
    CommonUtils.createDependentBusinessOrganisation(today, today).then((sboidOfBO:string) => {
      sboid = sboidOfBO
    });
  });

  it('Step-3: Create dependent line version', () => {
    CommonUtils.post('/line-directory/v2/lines/versions', {
      swissLineNumber: today,
      lineType: "ORDERLY",
      businessOrganisation: sboid,
      validFrom: today.toISOString().split('T')[0],
      validTo: tomorrow.toISOString().split('T')[0],
      lineConcessionType: "LINE_ABROAD",
      offerCategory: "SL"
    }).then((response) => {
      expect(response.status).to.equal(201);

      expect(response.body).to.have.property('slnid').that.is.a('string');
      mainSlnid = response.body.slnid;
    });
  });

  it('Step-3: Create a new subline version', () => {
    CommonUtils.post('/line-directory/v2/sublines/versions', {
      mainlineSlnid: mainSlnid,
      sublineType: "TECHNICAL",
      paymentType: "REGIONALWITHOUT",
      businessOrganisation: sboid,
      validFrom: today.toISOString().split('T')[0],
      validTo: tomorrow.toISOString().split('T')[0],
      description: "This field is now also mandatory."
    }).then((response) => {
      expect(response.status).to.equal(201); // Verify successful creation

      // Store necessary identifiers from the response for future steps
      sublineSlnid = response.body.slnid;
      sublineVersionId = response.body.id;
      etagVersion = response.body.etagVersion;
    });
  });

  it('Step-4: Read the subline version', () => {
    CommonUtils.get(`/line-directory/v2/sublines/versions/${sublineSlnid}`).then((response) => {
      expect(response.status).to.equal(200); // Verify successful retrieval

      const sublineVersions = response.body;
      expect(Array.isArray(sublineVersions)).to.be.true; // Ensure response is an array
      expect(sublineVersions.length).to.equal(1); // Check for exactly one version

      const sublineVersionsFirst = sublineVersions[0];

      // Validate retrieved values against expected identifiers
      expect(sublineVersionsFirst.slnid).to.equal(sublineSlnid);
      etagVersion = sublineVersionsFirst.etagVersion; // Update etagVersion for future updates
    });
  });

  it('Step-5: Update the subline version', () => {
    CommonUtils.post(`/line-directory/v2/sublines/versions/${sublineVersionId}`, {
      mainlineSlnid: mainSlnid,
      slnid: sublineSlnid,
      status: "VALIDATED",
      sublineType: "TECHNICAL",
      paymentType: "REGIONALWITHOUT", // Should be ignored by atlas
      businessOrganisation: sboid,
      validFrom: today.toISOString().split('T')[0],
      validTo: updatedValidTo, // Use the new validTo date
      description: "This field is now also mandatory.",
      etagVersion: etagVersion // Include the ETag version for update
    }).then((response) => {
      expect(response.status).to.equal(200); // Verify successful update

      const sublineVersions = response.body;
      expect(Array.isArray(sublineVersions)).to.be.true;
      expect(sublineVersions.length).to.equal(1); // Check for exactly one version

      const sublineVersionsFirst = sublineVersions[0];
      expect(sublineVersionsFirst.slnid).to.equal(sublineSlnid); // Validate identifiers
    });
  });
});
