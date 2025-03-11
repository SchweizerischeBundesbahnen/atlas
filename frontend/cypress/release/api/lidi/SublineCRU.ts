import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils from '../../../support/util/release-api/release-api-utils';

describe(
  'LiDi: Scenario Subline-CRUD: New Line',
  { testIsolation: false },
  () => {
    let mainSlnid = '';
    let sublineSlnid = '';
    let sboid = '';
    let sublineVersionId = -1;
    let etagVersion = -1;

    it('Step-1: Login on ATLAS', () => {
      cy.atlasLogin();
    });

    it('Step-2: Create dependent Business Organisation', () => {
      CommonUtils.createDependentBusinessOrganisation(
        ReleaseApiUtils.today(),
        ReleaseApiUtils.today()
      ).then((sboidOfBO: string) => {
        sboid = sboidOfBO;
      });
    });

    it('Step-3: Create dependent line version', () => {
      CommonUtils.post('/line-directory/v2/lines/versions', {
        swissLineNumber: ReleaseApiUtils.today(),
        lineType: 'ORDERLY',
        businessOrganisation: sboid,
        validFrom: ReleaseApiUtils.todayAsAtlasString(),
        validTo: ReleaseApiUtils.tomorrowAsAtlasString(),
        lineConcessionType: 'LINE_ABROAD',
        offerCategory: 'SL',
        description: 'This is just a description.',
        number: '222222',
      }).then((response) => {
        expect(response.status).to.equal(201);

        expect(response.body).to.have.property('slnid').that.is.a('string');
        mainSlnid = response.body.slnid;
      });
    });

    it('Step-3: Create a new subline version', () => {
      CommonUtils.post('/line-directory/v2/sublines/versions', {
        mainlineSlnid: mainSlnid,
        sublineType: 'TECHNICAL',
        paymentType: 'REGIONALWITHOUT', // Should be ignored by atlas
        businessOrganisation: sboid,
        validFrom: ReleaseApiUtils.todayAsAtlasString(),
        validTo: ReleaseApiUtils.tomorrowAsAtlasString(),
        description: 'This field is now also mandatory.',
      }).then((response) => {
        expect(response.status).to.equal(201); // Verify successful creation

        // Store necessary identifiers from the response for future steps
        sublineSlnid = response.body.slnid;
        sublineVersionId = response.body.id;
        etagVersion = response.body.etagVersion;
      });
    });

    it('Step-4: Read the subline version', () => {
      CommonUtils.get(
        `/line-directory/v2/sublines/versions/${sublineSlnid}`
      ).then((response) => {
        const sublineVersionsFirst = ReleaseApiUtils.makeCommonChecks(
          response,
          sublineSlnid,
          sublineVersionId
        );
        etagVersion = sublineVersionsFirst.etagVersion; // Update etagVersion for future updates
      });
    });

    it('Step-5: Update the subline version', () => {
      CommonUtils.put(
        `/line-directory/v2/sublines/versions/${sublineVersionId}`,
        {
          mainlineSlnid: mainSlnid,
          slnid: sublineSlnid,
          status: 'VALIDATED',
          sublineType: 'TECHNICAL',
          paymentType: 'REGIONALWITHOUT', // Should be ignored by atlas in v2
          businessOrganisation: sboid,
          validFrom: ReleaseApiUtils.todayAsAtlasString(),
          validTo: ReleaseApiUtils.date(7), // Changed from tomorrow
          description: 'This field is now also mandatory.',
          etagVersion: etagVersion, // Include the ETag version for update
        }
      ).then((response) => {
        ReleaseApiUtils.makeCommonChecks(
          response,
          sublineSlnid,
          sublineVersionId
        );
      });
    });
  }
);
