import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils from '../../../support/util/release-api-utils';
import Constants from 'constants';

let parentServicePointSloid = "";
let numberWithoutCheckDigit = -1;
let trafficPointSloid = "";
let prmId = -1;
let etagVersion = -1;
let sboid = "";

const statusForAllPRMObjects = "VALIDATED";
// Means of Transport values
const BUS = "BUS";
const ELEVATOR = "ELEVATOR";

describe('Create new ServicePoint and TrafficPoint for reduced StopPoint', { testIsolation: false }, () => {

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Create dependent Business Organisation', () => {
    CommonUtils.createDependentBusinessOrganisation(ReleaseApiUtils.today(), ReleaseApiUtils.today()).then((sboidOfBO:string) => {
      sboid = sboidOfBO
    });
  });

  it('Step-3: New ServicePoint', () => {
    CommonUtils.post('/service-point-directory/v1/service-points', {
      country: "SWITZERLAND",
      designationOfficial: ReleaseApiUtils.today(),
      businessOrganisation: sboid,
      // The service-point needs to have a meansOfTransport, so that a PRM-stopPoint can be created.
      // The meansOfTransport BUS leads to the reduced recording variant
      // Code: https://code.sbb.ch/projects/KI_ATLAS/repos/atlas/browse/base-atlas/src/main/java/ch/sbb/atlas/servicepoint/enumeration/MeanOfTransport.java
      meansOfTransport: [BUS],
      validFrom: ReleaseApiUtils.LAST_ATLAS_DATE,
      validTo: ReleaseApiUtils.LAST_ATLAS_DATE
    }).then((response) => {
      expect(response.status).to.equal(201);

      expect(response.body).to.have.property('sloid').that.is.a('string');
      parentServicePointSloid = response.body.sloid;

      expect(response.body).property('number').property('number').that.is.a('number');
      numberWithoutCheckDigit = response.body.number.number;
    });
  });

  it('Step-4: New Traffic Point', () => {
    trafficPointSloid = `${parentServicePointSloid}:trafficPoint:1`;

    CommonUtils.post('/service-point-directory/v1/traffic-point-elements', {
      numberWithoutCheckDigit: numberWithoutCheckDigit,
      sloid: trafficPointSloid,
      parentSloid: parentServicePointSloid,
      validFrom: ReleaseApiUtils.FIRST_ATLAS_DATE,
      validTo: ReleaseApiUtils.FIRST_ATLAS_DATE,
      trafficPointElementType: "BOARDING_PLATFORM"
    }).then((response) => {
      expect(response.status).to.equal(201);
      expect(response.body.parentSloid).to.equal(parentServicePointSloid);

      expect(response.body).to.have.property('sloid').that.is.a('string');
      trafficPointSloid = response.body.sloid;

      expect(response.body).property('servicePointNumber').property('number').that.is.a('number').and.equals(numberWithoutCheckDigit);
    });
  });

  describe('PRM: New reduced Stop Point', { testIsolation: false }, () => {

    const validFrom = ReleaseApiUtils.todayAsAtlasString();
    const validTo = ReleaseApiUtils.todayAsAtlasString();
    const freeText = "freeText";

    it('Step-1: New reduced Stop Point', () => {
      // Docu: https://confluence.sbb.ch/display/ATLAS/Data-Fact-Matrix#DataFactMatrix-StopPlaces(Haltestellen)

      CommonUtils.post('/prm-directory/v1/stop-points', {
        sloid: parentServicePointSloid,
        validFrom: validFrom,
        validTo: validTo,
        etagVersion: 0, // TODO: Can this be removed?
        meansOfTransport: [BUS],
        freeText: freeText,
        numberWithoutCheckDigit: numberWithoutCheckDigit
      }).then((response) => {
        expect(response.status).to.equal(201);

        expect(response.body).to.have.property('id').that.is.a('number');
        prmId = response.body.id;

        expect(response.body).to.have.property('reduced').which.is.true;

        expect(response.body).to.have.property('meansOfTransport').that.is.an('array');
        expect(response.body.meansOfTransport.length).to.equal(1);
        expect(response.body.meansOfTransport[0]).to.equal(BUS);

        expect(response.body).to.have.property('sloid').that.is.a('string').and.to.equal(parentServicePointSloid);
        expect(response.body).to.have.property('validFrom').that.is.a('string').and.to.equal(validFrom);
        expect(response.body).to.have.property('validTo').that.is.a('string').and.to.equal(validTo);
        expect(response.body).to.have.property('freeText').that.is.a('string').and.to.equal(freeText);
        expect(response.body).to.have.property('number').to.have.property('number').that.is.a('number').and.to.equal(numberWithoutCheckDigit);
        expect(response.body).to.have.property('status').that.is.a('string').and.to.equal(statusForAllPRMObjects);
      });
    });

    // TODO: Checked with http-Files until here.

    it('Step-2: Check reduced Stop Point', () => {
      CommonUtils.get(`/prm-directory/v1/stop-points?sloids=${parentServicePointSloid}&fromDate=${validFrom}&toDate=${validTo}`).then((response) => {

        expect(response.status).to.equal(200);
        expect(response.body.totalCount).to.be.greaterThan(0);

        expect(response.body).to.have.property('objects').that.is.an('array');
        const objects = response.body.objects;
        expect(objects.length).to.be.greaterThan(0);

        const prmObject = objects.find(obj => obj.id === prmId);
        expect(prmObject.sloid).to.equal(parentServicePointSloid);

        expect(prmObject).to.have.property('etagVersion').that.is.an('number').and.greaterThan(-1);
        etagVersion = prmObject.etagVersion;
        expect(prmObject.reduced).to.be.true;
      });
    });

    it.skip('Step-3: Update reduced Stop Point', () => {
      CommonUtils.put(`/prm-directory/v1/stop-points/${prmId}`, {
        sloid: parentServicePointSloid,
        validFrom: ReleaseApiUtils.today(),
        validTo: ReleaseApiUtils.tomorrow(),
        etagVersion: etagVersion,
        meansOfTransport: [BUS, ELEVATOR],
        freeText: freeText,
        numberWithoutCheckDigit: numberWithoutCheckDigit
      }).then((response) => {
        expect(response.status).to.equal(200);
        const prmObject = response.body;
        expect(Array.isArray(prmObject)).to.be.true;
        expect(prmObject.length).to.equal(1);
      });
    });
  });

});
