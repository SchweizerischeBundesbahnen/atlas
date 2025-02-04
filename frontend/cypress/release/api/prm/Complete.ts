import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils from '../../../support/util/release-api-utils';
import PrmConstants from './PrmConstants';

// Documentation: PRM-Data-Fact-Matrix: https://confluence.sbb.ch/x/vgdpl

let parentServicePointSloid = '';
let referencePointSloid = '';
let numberWithoutCheckDigit = -1;
let trafficPointSloid = '';
let etagVersion = -1;

const statusForAllPRMObjects = 'VALIDATED';

// Means of Transport values
const TRAIN = 'TRAIN';
const METRO = 'METRO';
// The service-point needs to have a meansOfTransport, so that a PRM-stopPoint can be created.
// The meansOfTransport TRAIN leads to the complete recording variant
// Code: https://code.sbb.ch/projects/KI_ATLAS/repos/atlas/browse/base-atlas/src/main/java/ch/sbb/atlas/servicepoint/enumeration/MeanOfTransport.java
let meansOfTransport = [TRAIN];

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
    .and.to.equal(numberWithoutCheckDigit);
  expect(object)
    .to.have.property('status')
    .that.is.a('string')
    .and.to.equal(statusForAllPRMObjects);

  expect(object)
    .to.have.property('etagVersion')
    .that.is.a('number')
    .and.greaterThan(-1);
};

describe(
  'Create new ServicePoint and TrafficPoint for complete StopPoint',
  { testIsolation: false },
  () => {
    let sboid = '';

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
      CommonUtils.post('/service-point-directory/v1/service-points', {
        country: 'SWITZERLAND',
        designationOfficial: ReleaseApiUtils.today(),
        businessOrganisation: sboid,
        meansOfTransport: meansOfTransport,
        validFrom: '2019-06-18',
        validTo: '9999-12-31',
      }).then((response) => {
        expect(response.status).to.equal(
          CommonUtils.HTTP_REST_API_RESPONSE_CREATED
        );

        expect(response.body).to.have.property('sloid').that.is.a('string');
        parentServicePointSloid = response.body.sloid;

        expect(response.body)
          .property('number')
          .property('number')
          .that.is.a('number');
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
        trafficPointElementType: 'BOARDING_PLATFORM',
      }).then((response) => {
        expect(response.status).to.equal(
          CommonUtils.HTTP_REST_API_RESPONSE_CREATED
        );
        expect(response.body.parentSloid).to.equal(parentServicePointSloid);

        expect(response.body).to.have.property('sloid').that.is.a('string');
        trafficPointSloid = response.body.sloid;

        expect(response.body)
          .property('servicePointNumber')
          .property('number')
          .that.is.a('number')
          .and.equals(numberWithoutCheckDigit);
      });
    });
  }
);

describe('PRM: New complete Stop Point', { testIsolation: false }, () => {
  let stopPointId = -1;

  const validate = (stopPoint: any) => {
    validatePrmObject(stopPoint, validFrom, validTo);
    expect(stopPoint).to.have.property('reduced').to.be.false;
    expect(stopPoint)
      .to.have.property('sloid')
      .that.is.a('string')
      .and.to.equal(parentServicePointSloid);
    expect(stopPoint)
      .to.have.property('freeText')
      .that.is.a('string')
      .and.to.equal(freeText);

    expect(stopPoint).to.have.property('meansOfTransport').that.is.an('array');
    expect(stopPoint)
      .to.have.property('meansOfTransport')
      .to.have.property('length')
      .to.equal(meansOfTransport.length);
    meansOfTransport.forEach((mot) => {
      expect(stopPoint.meansOfTransport).to.include(mot);
    });

    expect(stopPoint)
      .to.have.property('alternativeTransport')
      .and.to.equal(alternativeTransport);
    expect(stopPoint)
      .to.have.property('alternativeTransportCondition')
      .and.to.equal(alternativeTransportCondition);
    expect(stopPoint)
      .to.have.property('assistanceAvailability')
      .and.to.equal(assistanceAvailability);
    expect(stopPoint)
      .to.have.property('assistanceCondition')
      .and.to.equal(assistanceCondition);
    expect(stopPoint)
      .to.have.property('assistanceService')
      .and.to.equal(assistanceService);
    expect(stopPoint)
      .to.have.property('audioTicketMachine')
      .and.to.equal(audioTicketMachine);
    expect(stopPoint)
      .to.have.property('dynamicAudioSystem')
      .and.to.equal(dynamicAudioSystem);
    expect(stopPoint)
      .to.have.property('dynamicOpticSystem')
      .and.to.equal(dynamicOpticSystem);
    expect(stopPoint)
      .to.have.property('infoTicketMachine')
      .and.to.equal(infoTicketMachine);
    expect(stopPoint).to.have.property('visualInfo').and.to.equal(visualInfo);
    expect(stopPoint)
      .to.have.property('wheelchairTicketMachine')
      .and.to.equal(wheelchairTicketMachine);
    expect(stopPoint)
      .to.have.property('assistanceRequestFulfilled')
      .and.to.equal(assistanceRequestFulfilled);
    expect(stopPoint)
      .to.have.property('ticketMachine')
      .and.to.equal(ticketMachine);
    expect(stopPoint)
      .to.have.property('additionalInformation')
      .and.to.equal(additionalInformation);

    expect(stopPoint).to.have.property('freeText').and.to.equal(freeText);
    expect(stopPoint).to.have.property('address').and.to.equal(address);
    expect(stopPoint).to.have.property('zipCode').and.to.equal(zipCode);
    expect(stopPoint).to.have.property('city').and.to.equal(city);

    // TODO: Uncomment, when ATLAS-2728 is fixed
    //       expect(stopPoint)
    //       .to.have.property('interoperable')
    //       .and.to.equal(interoperable);
    expect(stopPoint).to.have.property('url').and.to.equal(url);
  };

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();
  const additionalInformation = null;
  const alternativeTransport = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const assistanceAvailability = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const assistanceService = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const audioTicketMachine = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const dynamicAudioSystem = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const dynamicOpticSystem = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const visualInfo = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const wheelchairTicketMachine = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const assistanceRequestFulfilled = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValues()
  );
  const ticketMachine = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValues()
  );
  const freeText = 'freeText';
  const address = 'address';
  const zipCode = 'zipCode';
  const city = 'city';
  const alternativeTransportCondition = 'alternativeTransportCondition';
  const assistanceCondition = 'assistanceCondition';
  const infoTicketMachine = 'infoTicketMachine';
  const url = 'url';
  const interoperable = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.booleanValues()
  );

  it('Step-1: New complete Stop Point', () => {
    CommonUtils.post('/prm-directory/v1/stop-points', {
      sloid: parentServicePointSloid,
      validFrom: validFrom,
      validTo: validTo,
      meansOfTransport: meansOfTransport,
      freeText: freeText,
      address: address,
      zipCode: zipCode,
      city: city,
      alternativeTransport: alternativeTransport,
      alternativeTransportCondition: alternativeTransportCondition,
      assistanceAvailability: assistanceAvailability,
      assistanceCondition: assistanceCondition,
      assistanceService: assistanceService,
      audioTicketMachine: audioTicketMachine,
      additionalInformation: additionalInformation,
      dynamicAudioSystem: dynamicAudioSystem,
      dynamicOpticSystem: dynamicOpticSystem,
      infoTicketMachine: infoTicketMachine,
      interoperable: interoperable,
      url: url,
      visualInfo: visualInfo,
      wheelchairTicketMachine: wheelchairTicketMachine,
      assistanceRequestFulfilled: assistanceRequestFulfilled,
      ticketMachine: ticketMachine,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );

      const stopPoint = response.body;
      validate(stopPoint);
      stopPointId = stopPoint.id;
    });
  });

  it('Step-2: Check complete Stop Point', () => {
    CommonUtils.get(
      `/prm-directory/v1/stop-points?sloids=${parentServicePointSloid}&fromDate=${validFrom}&toDate=${validTo}`
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

  it('Step-3: Update complete Stop Point', () => {
    meansOfTransport = meansOfTransport.concat(METRO);

    CommonUtils.put(`/prm-directory/v1/stop-points/${stopPointId}`, {
      sloid: parentServicePointSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersion,
      meansOfTransport: meansOfTransport,
      freeText: freeText,
      address: address,
      zipCode: zipCode,
      city: city,
      alternativeTransport: alternativeTransport,
      alternativeTransportCondition: alternativeTransportCondition,
      assistanceAvailability: assistanceAvailability,
      assistanceCondition: assistanceCondition,
      assistanceService: assistanceService,
      audioTicketMachine: audioTicketMachine,
      additionalInformation: additionalInformation,
      dynamicAudioSystem: dynamicAudioSystem,
      dynamicOpticSystem: dynamicOpticSystem,
      infoTicketMachine: infoTicketMachine,
      url: url,
      visualInfo: visualInfo,
      wheelchairTicketMachine: wheelchairTicketMachine,
      assistanceRequestFulfilled: assistanceRequestFulfilled,
      ticketMachine: ticketMachine,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
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
});

describe('PRM: New complete Reference Point', { testIsolation: false }, () => {
  let designation = 'designation';
  let referencePointId = -1;

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();
  const referencePointType = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.referencePointTypeValues()
  );
  const additionalInformation = 'additional';
  const mainReferencePoint = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.booleanValues()
  );

  const validate = (referencePoint: any) => {
    validatePrmObject(referencePoint, validFrom, validTo);
    expect(referencePoint)
      .to.have.property('sloid')
      .and.to.equal(referencePointSloid);
    expect(referencePoint)
      .to.have.property('parentServicePointSloid')
      .and.to.equal(parentServicePointSloid);
    expect(referencePoint)
      .to.have.property('designation')
      .and.to.equal(designation);
    expect(referencePoint)
      .to.have.property('referencePointType')
      .and.to.equal(referencePointType);
    expect(referencePoint)
      .to.have.property('additionalInformation')
      .and.to.equal(additionalInformation);
    expect(referencePoint)
      .to.have.property('mainReferencePoint')
      .and.to.equal(mainReferencePoint);
  };

  it('Step-1: Create new complete Reference Point', () => {
    referencePointSloid = `${parentServicePointSloid}:referencePoint1`; // Has to be initialized here, because before parentServicePointSloid is not known

    CommonUtils.post('/prm-directory/v1/reference-points', {
      sloid: referencePointSloid,
      validFrom: validFrom,
      validTo: validTo,
      parentServicePointSloid: parentServicePointSloid,
      designation: designation,
      referencePointType: referencePointType,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
      additionalInformation: additionalInformation,
      mainReferencePoint: mainReferencePoint,
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );

      const referencePoint = response.body;
      validate(referencePoint);
      referencePointId = referencePoint.id;
    });
  });

  it('Step-2: Check complete Reference Point', () => {
    CommonUtils.get(
      `/prm-directory/v1/reference-points?sloids=${referencePointSloid}`
    ).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const referencePoint = ReleaseApiUtils.getPrmObjectById(
        response.body,
        referencePointId,
        false,
        1
      );
      validate(referencePoint);
      etagVersion = referencePoint.etagVersion;
    });
  });

  it('Step-3: Update complete Reference Point', () => {
    designation = 'designation-changed';

    CommonUtils.put(`/prm-directory/v1/reference-points/${referencePointId}`, {
      sloid: referencePointSloid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersion,
      parentServicePointSloid: parentServicePointSloid,
      designation: designation,
      referencePointType: referencePointType,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
      additionalInformation: additionalInformation,
      mainReferencePoint: mainReferencePoint,
    }).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      const referencePoint = ReleaseApiUtils.getPrmObjectById(
        response.body,
        referencePointId,
        true,
        1
      );
      validate(referencePoint);
      etagVersion = referencePoint.etagVersion;
    });
  });
});

describe('PRM: Check relations for sloid', { testIsolation: false }, () => {
  const NO_RELATIONSHIP_YET = 0;

  const checkResponse = (response) => {
    expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
    expect(response.body.totalCount).to.equal(NO_RELATIONSHIP_YET);
    expect(response.body)
      .to.have.property('objects')
      .that.is.an('array')
      .to.have.property('length')
      .to.equal(NO_RELATIONSHIP_YET);
  };

  it('Step-1: No relation for sloid', () => {
    CommonUtils.get(`/prm-directory/v1/relations/${referencePointSloid}`).then(
      (response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);
        expect(response.body)
          .to.be.an('array')
          .to.have.property('length')
          .to.equal(NO_RELATIONSHIP_YET);
      }
    );
  });

  it('Step-2: No TOILET-relation for sloid', () => {
    CommonUtils.get(
      `/prm-directory/v1/relations?sloids=${referencePointSloid}&referencePointElementTypes=TOILET`
    ).then((response) => {
      checkResponse(response);
    });
  });

  it('Step-3: No TICKET_COUNTER-relation for sloid', () => {
    CommonUtils.get(
      `/prm-directory/v1/relations?sloids=${referencePointSloid}&referencePointElementTypes=CONTACT_POINT`
    ).then((response) => {
      checkResponse(response);
    });
  });

  it('Step-4: No PLATFORM-relation for sloid', () => {
    CommonUtils.get(
      `/prm-directory/v1/relations?sloids=${referencePointSloid}&referencePointElementTypes=PLATFORM`
    ).then((response) => {
      checkResponse(response);
    });
  });

  it('Step-5: No PARKING_LOT-relation for sloid', () => {
    CommonUtils.get(
      `/prm-directory/v1/relations?sloids=${referencePointSloid}&referencePointElementTypes=PARKING_LOT`
    ).then((response) => {
      checkResponse(response);
    });
  });

  it('Step-6: No relation for parent-sloid', () => {
    CommonUtils.get(
      `/prm-directory/v1/relations?parentServicePointSloids=${parentServicePointSloid}`
    ).then((response) => {
      checkResponse(response);
    });
  });
});

describe('PRM: New complete Toilet', { testIsolation: false }, () => {
  let toiletId = -1;
  let relationId = -1;
  let toiletSloid = '';

  const validFrom = ReleaseApiUtils.todayAsAtlasString();
  const validTo = ReleaseApiUtils.todayAsAtlasString();
  const wheelchairToilet = ReleaseApiUtils.extractOneRandomValue(
    PrmConstants.basicValuesAndNotApplicableAndPartially()
  );
  const designation = 'öffentliches WC im Bf Basel Bad Bf';
  const additionalInformation = 'additionalInformation';
  const referencePointElementType = `TOILET`;

  const validate = (toilet) => {
    validatePrmObject(toilet, validFrom, validTo);
    expect(toilet)
      .to.have.property('sloid')
      .that.is.a('string')
      .and.to.equal(toiletSloid);
    expect(toilet)
      .to.have.property('parentServicePointSloid')
      .that.is.a('string')
      .and.to.equal(parentServicePointSloid);
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
  };

  const validateRelation = (relation) => {
    validatePrmObject(relation, validFrom, validTo);
    expect(relation)
      .to.have.property('referencePointSloid')
      .that.is.a('string')
      .and.to.equal(referencePointSloid);
    expect(relation)
      .to.have.property('elementSloid')
      .that.is.a('string')
      .and.to.equal(toiletSloid);
    expect(relation)
      .to.have.property('parentServicePointSloid')
      .that.is.a('string')
      .and.to.equal(parentServicePointSloid);
    expect(relation)
      .to.have.property('referencePointElementType')
      .that.is.a('string')
      .and.to.equal(referencePointElementType);
    expect(relation)
      .to.have.property('tactileVisualMarks')
      .that.is.a('string')
      .and.to.equal(CommonUtils.TO_BE_COMPLETED);
    expect(relation)
      .to.have.property('contrastingAreas')
      .that.is.a('string')
      .and.to.equal(CommonUtils.TO_BE_COMPLETED);
    expect(relation)
      .to.have.property('stepFreeAccess')
      .that.is.a('string')
      .and.to.equal(CommonUtils.TO_BE_COMPLETED);
  };

  it('Step-1: New complete Toilet', () => {
    toiletSloid = `${parentServicePointSloid}:${referencePointElementType}1`; // Initialisierung des sloid

    CommonUtils.post('/prm-directory/v1/toilets', {
      sloid: toiletSloid,
      validFrom: validFrom,
      validTo: validTo,
      parentServicePointSloid: parentServicePointSloid,
      designation: designation,
      additionalInformation: additionalInformation,
      wheelchairToilet: wheelchairToilet,
      numberWithoutCheckDigit: numberWithoutCheckDigit,
    }).then((response) => {
      expect(response.status).to.equal(
        CommonUtils.HTTP_REST_API_RESPONSE_CREATED
      );
      const toilet = response.body;
      validate(toilet);
      toiletId = toilet.id;
    });
  });

  it('Step-2: Get complete Toilet', () => {
    CommonUtils.get(`/prm-directory/v1/toilets?sloids=${toiletSloid}`).then(
      (response) => {
        expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

        const toilet = ReleaseApiUtils.getPrmObjectById(
          response.body,
          toiletId,
          false,
          1
        );
        validate(toilet);
        etagVersion = toilet.etagVersion;
      }
    );
  });

  it('Step-3: Get relations for sloid', () => {
    CommonUtils.get(
      `/prm-directory/v1/relations?sloids=${toiletSloid}&referencePointSloids=${referencePointSloid}`
    ).then((response) => {
      expect(response.status).to.equal(CommonUtils.HTTP_REST_API_RESPONSE_OK);

      expect(response.body)
        .to.have.property('objects')
        .that.is.an('array')
        .and.to.have.property('length')
        .which.equals(1);
      const toiletRelation = response.body.objects[0];
      validateRelation(toiletRelation);
      relationId = toiletRelation.id;
    });
  });
});
