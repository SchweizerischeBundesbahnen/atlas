import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils, {
  SePoDependentInfo,
} from '../../../support/util/release-api-utils';
import PrmConstants from '../../../support/util/prm-constants';

// Documentation: PRM-Data-Fact-Matrix: https://confluence.sbb.ch/x/vgdpl

let info: SePoDependentInfo;
let trafficPointSloid = '';
let etagVersion = -1;

const statusForAllPRMObjects = 'VALIDATED';

// Means of Transport values
const BUS = 'BUS';
const ELEVATOR = 'ELEVATOR';
let meansOfTransport = [BUS];

const validatePrmObject = (object, validFrom: string, validTo: string) => {
  expect(object).to.have.property('id').that.is.a('number');

  expect(object)
    .to.have.property('validFrom')
    .that.is.a('string')
    .and.to.equal(validFrom);
  expect(object)
    .to.have.property('validTo')
    .that.is.a('string')
    .and.to.equal(validTo);
  expect(object)
    .to.have.property('number')
    .to.have.property('number')
    .that.is.a('number')
    .and.to.equal(info.numberWithoutCheckDigit);
  expect(object)
    .to.have.property('status')
    .that.is.a('string')
    .and.to.equal(statusForAllPRMObjects);

  expect(object)
    .to.have.property('etagVersion')
    .that.is.an('number')
    .and.greaterThan(-1);
};

describe(
  'Create new ServicePoint and TrafficPoint for reduced StopPoint',
  { testIsolation: false },
  () => {
    let sboid: string;

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

    it('Step-3: New ServicePoint', () => {
      ReleaseApiUtils.createDependentServicePoint(sboid, meansOfTransport).then(
        (sePoDependentInfo: SePoDependentInfo) => (info = sePoDependentInfo)
      );
    });

    it('Step-4: New Traffic Point', () => {
      trafficPointSloid = `${info.parentServicePointSloid}:trafficPoint:1`;
      ReleaseApiUtils.createDependentTrafficPoint(trafficPointSloid, info);
    });
  }
);

describe(
  'PRM: New reduced Stop Point based on Service and Traffic Point',
  { testIsolation: false },
  () => {
    let stopPointId = -1;

    const validFrom = ReleaseApiUtils.todayAsAtlasString();
    let validTo = ReleaseApiUtils.todayAsAtlasString();

    const freeText = 'freeText';

    const validate = (stopPoint: any) => {
      validatePrmObject(stopPoint, validFrom, validTo);
      expect(stopPoint).to.have.property('reduced').to.be.true;
      expect(stopPoint)
        .to.have.property('sloid')
        .that.is.a('string')
        .and.to.equal(info.parentServicePointSloid);
      expect(stopPoint)
        .to.have.property('freeText')
        .that.is.a('string')
        .and.to.equal(freeText);

      expect(stopPoint)
        .to.have.property('meansOfTransport')
        .that.is.an('array');
      expect(stopPoint)
        .to.have.property('meansOfTransport')
        .to.have.property('length')
        .to.equal(meansOfTransport.length);
      meansOfTransport.forEach((mot) => {
        expect(stopPoint.meansOfTransport).to.include(mot);
      });
    };

    it('Step-1: New reduced Stop Point', () => {
      CommonUtils.post('/prm-directory/v1/stop-points', {
        sloid: info.parentServicePointSloid,
        validFrom: validFrom,
        validTo: validTo,
        meansOfTransport: meansOfTransport,
        freeText: freeText,
        numberWithoutCheckDigit: info.numberWithoutCheckDigit,
      }).then((response) => {
        expect(response.status).to.equal(
          CommonUtils.HTTP_REST_API_RESPONSE_CREATED
        );

        const stopPoint = response.body;
        validate(stopPoint);
        stopPointId = stopPoint.id;
      });
    });

    it('Step-2: Check reduced Stop Point', () => {
      CommonUtils.get(
        `/prm-directory/v1/stop-points?sloids=${info.parentServicePointSloid}&fromDate=${validFrom}&toDate=${validTo}`
      ).then((response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

        const stopPoint = ReleaseApiUtils.getPrmObjectById(
          response.body,
          stopPointId,
          false,
          1
        );
        validate(stopPoint);
        etagVersion = stopPoint.etagVersion;
      });
    });

    it('Step-3: Update reduced Stop Point', () => {
      validTo = ReleaseApiUtils.tomorrowAsAtlasString();
      meansOfTransport = meansOfTransport.concat(ELEVATOR);
      CommonUtils.put(`/prm-directory/v1/stop-points/${stopPointId}`, {
        sloid: info.parentServicePointSloid,
        validFrom: validFrom,
        validTo: validTo, // Updated
        etagVersion: etagVersion,
        meansOfTransport: meansOfTransport,
        freeText: freeText,
        numberWithoutCheckDigit: info.numberWithoutCheckDigit,
      }).then((response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

        const stopPoint = ReleaseApiUtils.getPrmObjectById(
          response.body,
          stopPointId,
          true,
          1
        );
        validate(stopPoint);
        etagVersion = stopPoint.etagVersion;
      });
    });
  }
);

describe('PRM: New reduced Toilet', { testIsolation: false }, () => {
  let toiletId = -1;
  let toiletSloid = '';

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();

  const wheelchairToilet = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  let designation = 'öffentliches WC im Bf Basel Bad Bf';
  let additionalInformation = '';

  const validate = (toilet) => {
    validatePrmObject(toilet, validFrom, validTo);
    expect(toilet)
      .to.have.property('designation')
      .that.is.a('string')
      .and.to.equal(designation);
    expect(toilet)
      .to.have.property('wheelchairToilet')
      .that.is.a('string')
      .and.to.equal(wheelchairToilet);
    expect(toilet)
      .to.have.property('additionalInformation')
      .and.to.equal(additionalInformation);

    expect(toilet)
      .to.have.property('sloid')
      .that.is.a('string')
      .and.to.equal(toiletSloid);
    expect(toilet)
      .to.have.property('parentServicePointSloid')
      .that.is.a('string')
      .and.to.equal(info.parentServicePointSloid);
  };

  it('Step-1: New reduced Toilet', () => {
    toiletSloid = `${info.parentServicePointSloid}:TOILET1`; // Has to be initialized here, because before parentServicePointSloid is not known
    CommonUtils.post('/prm-directory/v1/toilets', {
      parentServicePointSloid: info.parentServicePointSloid,
      sloid: toiletSloid,
      validFrom: validFrom,
      validTo: validTo,
      numberWithoutCheckDigit: info.numberWithoutCheckDigit,
      designation: designation,
      wheelchairToilet: wheelchairToilet,
      additionalInformation: additionalInformation,
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );

      const toilet = response.body;
      validate(toilet);
      toiletId = toilet.id;
    });
  });

  it('Step-2: Get reduced Toilet', () => {
    CommonUtils.get(
      `/prm-directory/v1/toilets?parentServicePointSloids=${info.parentServicePointSloid}`
    ).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const toilet = ReleaseApiUtils.getPrmObjectById(
        response.body,
        toiletId,
        false,
        1
      );
      validate(toilet);
      etagVersion = toilet.etagVersion;
    });
  });

  it('Step-3: Change reduced Toilet', () => {
    designation = 'designation2';
    additionalInformation = 'additionalInformation2';

    CommonUtils.put(`/prm-directory/v1/toilets/${toiletId}`, {
      sloid: toiletSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersion,
      parentServicePointSloid: info.parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      wheelchairToilet: wheelchairToilet,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const toilet = ReleaseApiUtils.getPrmObjectById(
        response.body,
        toiletId,
        true,
        1
      );
      validate(toilet);
    });
  });
});

describe('PRM: New reduced Ticket Counter', { testIsolation: false }, () => {
  const referencePointElementType = 'TICKET_COUNTER';
  let ticketCounterSloid = '';
  let ticketCounterId = -1;

  let designation = referencePointElementType;
  let additionalInformation = null;

  const openingHours =
    'https://www.sbb.ch/de/bahnhof-services/am-bahnhof/bahnhoefe.html';
  const inductionLoop = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const wheelchairAccess = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();

  const validate = (ticketCounter) => {
    validatePrmObject(ticketCounter, validFrom, validTo);
    expect(ticketCounter)
      .to.have.property('designation')
      .that.is.a('string')
      .and.to.equal(designation);
    expect(ticketCounter)
      .to.have.property('wheelchairAccess')
      .that.is.a('string')
      .and.to.equal(wheelchairAccess);
    expect(ticketCounter)
      .to.have.property('additionalInformation')
      .and.to.equal(additionalInformation);

    expect(ticketCounter)
      .to.have.property('sloid')
      .that.is.a('string')
      .and.to.equal(ticketCounterSloid);
    expect(ticketCounter)
      .to.have.property('parentServicePointSloid')
      .that.is.a('string')
      .and.to.equal(info.parentServicePointSloid);

    expect(ticketCounter)
      .to.have.property('inductionLoop')
      .and.to.equal(inductionLoop);
    expect(ticketCounter)
      .to.have.property('openingHours')
      .and.to.equal(openingHours);
    expect(ticketCounter)
      .to.have.property('type')
      .and.to.equal(referencePointElementType);
  };

  it('Step-1: New reduced Ticket Counter', () => {
    ticketCounterSloid = `${info.parentServicePointSloid}:${referencePointElementType}1`; // Has to be initialized here, because before the input-variables are not known
    CommonUtils.post('/prm-directory/v1/contact-points', {
      sloid: ticketCounterSloid,
      validFrom: validFrom,
      validTo: validTo,
      parentServicePointSloid: info.parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      inductionLoop: inductionLoop,
      openingHours: openingHours,
      wheelchairAccess: wheelchairAccess,
      type: referencePointElementType,
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );
      const ticketCounter = response.body;
      validate(ticketCounter);
      ticketCounterId = ticketCounter.id;
    });
  });

  it('Step-2: Get reduced Ticket Counter', () => {
    CommonUtils.get(
      `/prm-directory/v1/contact-points?parentServicePointSloids=${info.parentServicePointSloid}`
    ).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
      const ticketCounter = ReleaseApiUtils.getPrmObjectById(
        response.body,
        ticketCounterId,
        false,
        1
      );
      validate(ticketCounter);
      etagVersion = ticketCounter.etagVersion;
    });
  });

  it('Step-3: Change reduced Ticket Counter', () => {
    designation = 'Ticket Counter 2';
    additionalInformation = 'Additional Information 2';

    CommonUtils.put(`/prm-directory/v1/contact-points/${ticketCounterId}`, {
      sloid: ticketCounterSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersion,
      parentServicePointSloid: info.parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      inductionLoop: inductionLoop,
      openingHours: openingHours,
      wheelchairAccess: wheelchairAccess,
      type: referencePointElementType,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const ticketCounter = ReleaseApiUtils.getPrmObjectById(
        response.body,
        ticketCounterId,
        true,
        1
      );
      validate(ticketCounter);
    });
  });
});

describe('PRM: New reduced Platform', { testIsolation: false }, () => {
  let platformId = -1;

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();

  let height = null;
  let inclinationLongitudinal = null;
  let additionalInformation = null;
  let wheelchairAreaLength = null;
  let wheelchairAreaWidth = null;
  let inductionLoop = null;
  let partialElevation = null;
  let tactileSystem = null;
  let vehicleAccess = null;
  let infoOpportunities = [];

  const validate = (platform) => {
    validatePrmObject(platform, validFrom, validTo);

    expect(platform).to.have.property('sloid').and.to.equal(trafficPointSloid);
    expect(platform)
      .to.have.property('parentServicePointSloid')
      .and.to.equal(info.parentServicePointSloid);
    expect(platform)
      .to.have.property('additionalInformation')
      .and.to.equal(additionalInformation);
    expect(platform).to.have.property('height').and.to.be.equal(height);
    expect(platform)
      .to.have.property('inclinationLongitudinal')
      .and.to.be.equal(inclinationLongitudinal);
    expect(platform)
      .to.have.property('infoOpportunities')
      .and.to.be.an('array')
      .and.to.deep.equal(infoOpportunities);
    expect(platform)
      .to.have.property('partialElevation')
      .and.to.be.equal(partialElevation);
    expect(platform)
      .to.have.property('tactileSystem')
      .and.to.be.equal(tactileSystem);
    expect(platform)
      .to.have.property('vehicleAccess')
      .and.to.be.equal(vehicleAccess);
    expect(platform)
      .to.have.property('wheelchairAreaLength')
      .and.to.be.equal(wheelchairAreaLength);
    expect(platform)
      .to.have.property('wheelchairAreaWidth')
      .and.to.be.equal(wheelchairAreaWidth);
  };

  it('Step-1: New reduced Platform', () => {
    CommonUtils.post('/prm-directory/v1/platforms', {
      sloid: trafficPointSloid,
      validFrom: validFrom,
      validTo: validTo,
      parentServicePointSloid: info.parentServicePointSloid,
      numberWithoutCheckDigit: info.numberWithoutCheckDigit, // From ServicePoint
      additionalInformation: additionalInformation,
      height: height,
      inclinationLongitudinal: inclinationLongitudinal,
      infoOpportunities: infoOpportunities,
      partialElevation: partialElevation,
      tactileSystem: tactileSystem,
      vehicleAccess: vehicleAccess,
      wheelchairAreaLength: wheelchairAreaLength,
      wheelchairAreaWidth: wheelchairAreaWidth,
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );

      const platform = response.body;
      validate(platform);
      platformId = platform.id;
    });
  });

  it('Step-2: Check reduced Platform', () => {
    CommonUtils.get(
      `/prm-directory/v1/platforms?sloids=${trafficPointSloid}`
    ).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const platform = ReleaseApiUtils.getPrmObjectById(
        response.body,
        platformId,
        false,
        1
      );
      validate(platform);
      etagVersion = platform.etagVersion;
    });
  });

  it('Step-3: Update reduced Platform', () => {
    height = ReleaseApiUtils.getRoundedRandomFloat(0, 100, 3);
    inclinationLongitudinal = ReleaseApiUtils.getRoundedRandomFloat(0, 100, 3);
    additionalInformation = 'additionalInformation2';
    wheelchairAreaLength = ReleaseApiUtils.getRoundedRandomFloat(0, 100, 3);
    wheelchairAreaWidth = ReleaseApiUtils.getRoundedRandomFloat(0, 100, 3);
    inductionLoop = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValuesAndNotApplicableAndPartially()
    );
    partialElevation = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.booleanValues()
    );
    tactileSystem = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValues()
    );
    vehicleAccess = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.vehicleAccessValues()
    );

    CommonUtils.put(`/prm-directory/v1/platforms/${platformId}`, {
      sloid: trafficPointSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersion,
      parentServicePointSloid: info.parentServicePointSloid,
      numberWithoutCheckDigit: info.numberWithoutCheckDigit,
      additionalInformation: additionalInformation,
      height: height,
      inclinationLongitudinal: inclinationLongitudinal,
      infoOpportunities: infoOpportunities,
      partialElevation: partialElevation,
      tactileSystem: tactileSystem,
      vehicleAccess: vehicleAccess,
      wheelchairAreaLength: wheelchairAreaLength,
      wheelchairAreaWidth: wheelchairAreaWidth,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const platform = ReleaseApiUtils.getPrmObjectById(
        response.body,
        platformId,
        true,
        1
      );
      validate(platform);
    });
  });
});

describe('PRM: New reduced Parking Lot', { testIsolation: false }, () => {
  let parkingLotId = -1;
  let parkingLotSloid = '';

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();

  let placesAvailable = 'TO_BE_COMPLETED';
  let prmPlacesAvailable = 'TO_BE_COMPLETED';
  let additionalInformation = null;
  let designation = 'null';

  const validate = (parkingLot) => {
    validatePrmObject(parkingLot, validFrom, validTo);

    expect(parkingLot).to.have.property('sloid').and.to.equal(parkingLotSloid);
    expect(parkingLot)
      .to.have.property('parentServicePointSloid')
      .and.to.equal(info.parentServicePointSloid);

    expect(parkingLot)
      .to.have.property('designation')
      .and.to.equal(designation);
    expect(parkingLot)
      .to.have.property('placesAvailable')
      .and.to.equal(placesAvailable);
    expect(parkingLot)
      .to.have.property('additionalInformation')
      .and.to.equal(additionalInformation);
    expect(parkingLot)
      .to.have.property('prmPlacesAvailable')
      .and.to.equal(prmPlacesAvailable);
  };

  it('Step-1: New reduced Parking Lot', () => {
    parkingLotSloid = `${info.parentServicePointSloid}:PARKING_LOT1`; // Has to be initialized here, because before parentServicePointSloid is not known

    CommonUtils.post('/prm-directory/v1/parking-lots', {
      sloid: parkingLotSloid,
      validFrom: validFrom,
      validTo: validTo,
      parentServicePointSloid: info.parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      placesAvailable: placesAvailable,
      prmPlacesAvailable: prmPlacesAvailable,
      numberWithoutCheckDigit: info.numberWithoutCheckDigit,
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );

      const parkingLot = response.body;
      validate(parkingLot);
      parkingLotId = parkingLot.id;
    });
  });

  it('Step-2: Get reduced Parking Lot', () => {
    CommonUtils.get(
      `/prm-directory/v1/parking-lots?parentServicePointSloids=${info.parentServicePointSloid}`
    ).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const parkingLot = ReleaseApiUtils.getPrmObjectById(
        response.body,
        parkingLotId,
        false,
        1
      );
      validate(parkingLot);
      etagVersion = parkingLot.etagVersion;
    });
  });

  it('Step-3: Change reduced Parking Lot', () => {
    designation = 'Updated Parking Lot';
    additionalInformation = 'Updated Information';

    placesAvailable = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValues()
    );
    prmPlacesAvailable = ReleaseApiUtils.extractOneRandomValue(
      PrmConstants.basicValues()
    );

    CommonUtils.put(`/prm-directory/v1/parking-lots/${parkingLotId}`, {
      sloid: parkingLotSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersion,
      parentServicePointSloid: info.parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      placesAvailable: placesAvailable,
      prmPlacesAvailable: prmPlacesAvailable,
      numberWithoutCheckDigit: info.numberWithoutCheckDigit,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const parkingLot = ReleaseApiUtils.getPrmObjectById(
        response.body,
        parkingLotId,
        true,
        1
      );
      validate(parkingLot);
    });
  });
});

describe('PRM: New reduced Information Desk', { testIsolation: false }, () => {
  let informationDeskSloid = '';
  let informationDeskId = -1;

  const referencePointElementType = 'INFORMATION_DESK';
  let designation = referencePointElementType;

  let additionalInformation = null;
  const openingHours =
    'https://www.sbb.ch/de/bahnhof-services/am-bahnhof/bahnhoefe.html';
  const inductionLoop = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const wheelchairAccess = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();

  const validate = (informationDesk) => {
    validatePrmObject(informationDesk, validFrom, validTo);
    expect(informationDesk)
      .to.have.property('designation')
      .that.is.a('string')
      .and.to.equal(designation);
    expect(informationDesk)
      .to.have.property('wheelchairAccess')
      .that.is.a('string')
      .and.to.equal(wheelchairAccess);
    expect(informationDesk)
      .to.have.property('additionalInformation')
      .and.to.equal(additionalInformation);

    expect(informationDesk)
      .to.have.property('sloid')
      .that.is.a('string')
      .and.to.equal(informationDeskSloid);
    expect(informationDesk)
      .to.have.property('parentServicePointSloid')
      .that.is.a('string')
      .and.to.equal(info.parentServicePointSloid);

    expect(informationDesk)
      .to.have.property('inductionLoop')
      .and.to.equal(inductionLoop);
    expect(informationDesk)
      .to.have.property('openingHours')
      .and.to.equal(openingHours);
    expect(informationDesk)
      .to.have.property('type')
      .and.to.equal(referencePointElementType);
  };

  it('Step-1: New reduced Information Desk', () => {
    informationDeskSloid = `${info.parentServicePointSloid}:${referencePointElementType}1`; // Has to be initialized here, because before the input-variables are not known
    CommonUtils.post('/prm-directory/v1/contact-points', {
      sloid: informationDeskSloid,
      validFrom: validFrom,
      validTo: validTo,
      parentServicePointSloid: info.parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      inductionLoop: inductionLoop,
      openingHours: openingHours,
      wheelchairAccess: wheelchairAccess,
      type: referencePointElementType,
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );
      const informationDesk = response.body;
      validate(informationDesk);
      informationDeskId = informationDesk.id;
    });
  });

  it('Step-2: Get reduced Information Desk', () => {
    CommonUtils.get(
      `/prm-directory/v1/contact-points?sloids=${informationDeskSloid}`
    ).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
      const informationDesk = ReleaseApiUtils.getPrmObjectById(
        response.body,
        informationDeskId,
        false,
        1
      );
      validate(informationDesk);
      etagVersion = informationDesk.etagVersion;
    });
  });

  it('Step-3: Change reduced Information Desk', () => {
    designation = 'Ticket Counter 2';
    additionalInformation = 'Additional Information 2';

    CommonUtils.put(`/prm-directory/v1/contact-points/${informationDeskId}`, {
      sloid: informationDeskSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersion,
      parentServicePointSloid: info.parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      inductionLoop: inductionLoop,
      openingHours: openingHours,
      wheelchairAccess: wheelchairAccess,
      type: referencePointElementType,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const informationDesk = ReleaseApiUtils.getPrmObjectById(
        response.body,
        informationDeskId,
        true,
        1
      );
      validate(informationDesk);
    });
  });
});

describe(
  'PRM: No Relation Tests for Toilet, Platform, and Parking Lot',
  { testIsolation: false },
  () => {
    const validateNoRelationResponse = (response, expectedTotalCount) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
      const body = response.body;
      expect(body).to.have.property('totalCount', expectedTotalCount);
      expect(body).to.have.property('objects').that.is.an('array');
      expect(body)
        .to.have.property('objects')
        .to.have.property('length')
        .which.equals(0);
    };

    it('Step-1: No TOILET-relation for sloid (reduced)', () => {
      CommonUtils.get(
        `/prm-directory/v1/relations?parentServicePointSloids=${info.parentServicePointSloid}&referencePointElementTypes=TOILET`
      ).then((response) => {
        validateNoRelationResponse(response, 0);
      });
    });

    it('Step-2: No PLATFORM-relation for sloid (reduced)', () => {
      CommonUtils.get(
        `/prm-directory/v1/relations?parentServicePointSloids=${info.parentServicePointSloid}&referencePointElementTypes=PLATFORM`
      ).then((response) => {
        validateNoRelationResponse(response, 0);
      });
    });

    it('Step-3: No PARKING_LOT-relation for sloid (reduced)', () => {
      CommonUtils.get(
        `/prm-directory/v1/relations?parentServicePointSloids=${info.parentServicePointSloid}&referencePointElementTypes=PARKING_LOT`
      ).then((response) => {
        validateNoRelationResponse(response, 0);
      });
    });

    it('Step-4: No relation for parent-sloid (reduced)', () => {
      CommonUtils.get(
        `/prm-directory/v1/relations?parentServicePointSloids=${info.parentServicePointSloid}`
      ).then((response) => {
        validateNoRelationResponse(response, 0);
      });
    });
  }
);
