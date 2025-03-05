import CommonUtils from '../../../support/util/common-utils';
import ReleaseApiUtils from '../../../support/util/release-api-utils';

// Documentation can be found at https://confluence.sbb.ch/x/pS4ynw
describe('SePo: Status Scenario', { testIsolation: false }, () => {
  let servicePointId: number;
  let sboid: string;
  let height: number = 0;
  let scenario: string;
  let north: number;
  let east: number;
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
  const validityBiggerThan60DaysCausesStatusToBeDraft = '12';
  const validitySmallerThan60DaysCausesStatusToBeValidated = '01';
  const changeMe = 'change me';
  const leaveMeUnchanged = 'leave me unchanged';
  const timeWithSeconds = new Date().toISOString().split('T')[1].split('.')[0]; // e.g. 16:25:20

  const setEtagVersion = (etagVersionContainer) => {
    expect(etagVersionContainer)
      .property('etagVersion')
      .to.be.a('number')
      .and.greaterThan(etagVersion);
    etagVersion = etagVersionContainer.etagVersion;
  };

  const checkSePoVersionStatus = (body, status: string) => {
    const sePoVersion = body.find(
      (sePoVersion) => String(sePoVersion.designationLong) === designationLong
    );
    expect(sePoVersion)
      .property('status')
      .to.be.a('string')
      .and.to.equal(status);
    return sePoVersion;
  };

  const checkStatusAndUpdateEtagVersion = (
    response: Cypress.Response<any>,
    status: string,
    responseStatus: number
  ) => {
    const body = checkResponseStatusAndGetResponseBody(
      response,
      responseStatus
    );
    checkSePoVersionStatus(body, status);

    // Always use the first version as basis for the other scenarios
    // so that the whole test works
    const firstSePoVersion = body.find(
      (sePoVersion) => sePoVersion.id === servicePointId
    );
    setEtagVersion(firstSePoVersion);
  };

  const checkResponseStatusAndGetResponseBody = (
    response: Cypress.Response<any>,
    responseStatus: number
  ) => {
    expect(response).property('status').to.equal(responseStatus);
    expect(response).to.have.property('body');
    return response.body;
  };

  const checkStatusAndUpdateEtagVersionPost = (
    response: Cypress.Response<any>,
    status: string,
    responseStatus: number
  ) => {
    const body = checkResponseStatusAndGetResponseBody(
      response,
      responseStatus
    );

    expect(body).property('status').to.be.a('string').and.to.equal(status);

    setEtagVersion(body);
  };

  const setServicePointAttributes = (
    heightToBeSet: number,
    designationOfficialPrefix: string,
    validToMonth: string,
    updateDesignationLong: boolean = true
  ) => {
    height = heightToBeSet;
    scenario = height.toString().padStart(2, '0');
    north = Number(`12051${scenario}.${scenario}`);
    east = Number(`26520${scenario}.${scenario}`);

    // If validFrom-validTo > 60 days -> status=DRAFT and
    // if validFrom-validTo < 60 days -> status=VALIDATED
    validFrom = `20${scenario}-01-01`;
    validTo = `20${scenario}-${validToMonth}-31`;

    designationOfficial = `${designationOfficialPrefix} ${timeWithSeconds}`;
    if (updateDesignationLong) {
      designationLong = `Scenario ${scenario} at ${timeWithSeconds}`;
    }
  };

  const getBody = (additionalAttributes: object) => {
    const body = {
      designationLong: designationLong,
      designationOfficial: designationOfficial,
      businessOrganisation: sboid,
      validFrom: validFrom,
      validTo: validTo,
      etagVersion: etagVersion,
    };
    Object.assign(body, additionalAttributes);
    return body;
  };

  const addSePoVersion = (additionalBodyAttributes: object) => {
    return CommonUtils.put(
      `/service-point-directory/v1/service-points/${servicePointId}`,
      getBody(additionalBodyAttributes)
    );
  };

  beforeEach(() => {
    // Height is the counter which is used for several dependent variables
    height++;
  });

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

  it(`Step-3: Create new Service Point - Scenario 1`, () => {
    setServicePointAttributes(
      1,
      changeMe,
      validityBiggerThan60DaysCausesStatusToBeDraft
    );

    CommonUtils.post(
      '/service-point-directory/v1/service-points',
      getBody({
        country: country,
        meansOfTransport: [meansOfTransport],
        servicePointGeolocation: {
          spatialReference: spatialReference,
          north: north,
          east: east,
          height: height,
        },
      })
    ).then((response) => {
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
    setServicePointAttributes(
      2,
      leaveMeUnchanged,
      validityBiggerThan60DaysCausesStatusToBeDraft
    );

    addSePoVersion({
      meansOfTransport: [meansOfTransport],
    }).then((response) => {
      checkStatusAndUpdateEtagVersion(
        response,
        statusValidated,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );
    });
  });

  it('Step-6: SePo-Status Scenario 3', () => {
    setServicePointAttributes(
      3,
      leaveMeUnchanged,
      validityBiggerThan60DaysCausesStatusToBeDraft
    );

    addSePoVersion({
      servicePointGeolocation: {
        spatialReference: spatialReference,
        north: north,
        east: east,
        height: height,
      },
    }).then((response) => {
      checkStatusAndUpdateEtagVersion(
        response,
        statusValidated,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );
    });
  });

  it(`Step-7: SePo-Status Scenario 4`, () => {
    setServicePointAttributes(
      4,
      leaveMeUnchanged,
      validitySmallerThan60DaysCausesStatusToBeValidated
    );

    addSePoVersion({
      meansOfTransport: [meansOfTransport],
      servicePointGeolocation: {
        spatialReference: spatialReference,
        north: north,
        east: east,
        height: height,
      },
    }).then((response) => {
      checkStatusAndUpdateEtagVersion(
        response,
        statusValidated,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );
    });
  });

  it(`Step-8: SePo-Status Scenario 5`, () => {
    setServicePointAttributes(
      5,
      changeMe,
      validityBiggerThan60DaysCausesStatusToBeDraft
    );

    addSePoVersion({
      meansOfTransport: [meansOfTransport],
    }).then((response) => {
      checkStatusAndUpdateEtagVersion(
        response,
        statusValidated,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );
    });
  });

  it(`Step-9: SePo-Status Scenario 6`, () => {
    setServicePointAttributes(
      6,
      changeMe,
      validityBiggerThan60DaysCausesStatusToBeDraft
    );

    addSePoVersion({
      servicePointGeolocation: {
        spatialReference: spatialReference,
        north: north,
        east: east,
        height: height,
      },
    }).then((response) => {
      checkStatusAndUpdateEtagVersion(
        response,
        statusValidated,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );
    });
  });

  it(`Step-10: SePo-Status Scenario 7`, () => {
    setServicePointAttributes(
      7,
      changeMe,
      validitySmallerThan60DaysCausesStatusToBeValidated
    );

    addSePoVersion({
      servicePointGeolocation: {
        spatialReference: spatialReference,
        north: north,
        east: east,
        height: height,
      },
    }).then((response) => {
      checkStatusAndUpdateEtagVersion(
        response,
        statusValidated,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );
    });
  });

  it(`Step-11: SePo-Status Scenario 8`, () => {
    setServicePointAttributes(
      8,
      leaveMeUnchanged,
      validityBiggerThan60DaysCausesStatusToBeDraft
    );

    addSePoVersion({}).then((response) => {
      checkStatusAndUpdateEtagVersion(
        response,
        statusValidated,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );
    });
  });

  it(`Step-12: SePo-Status Scenario 9`, () => {
    setServicePointAttributes(
      9,
      leaveMeUnchanged,
      validityBiggerThan60DaysCausesStatusToBeDraft
    );

    addSePoVersion({
      meansOfTransport: [meansOfTransport],
    }).then((response) => {
      checkStatusAndUpdateEtagVersion(
        response,
        statusValidated,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );
    });
  });

  it(`Step-13: SePo-Status Scenario 10`, () => {
    setServicePointAttributes(
      10,
      leaveMeUnchanged,
      validityBiggerThan60DaysCausesStatusToBeDraft
    );

    addSePoVersion({
      servicePointGeolocation: {
        spatialReference: spatialReference,
        north: north,
        east: east,
        height: height,
      },
    }).then((response) => {
      checkStatusAndUpdateEtagVersion(
        response,
        statusValidated,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );
    });
  });

  it(`Step-14: SePo-Status Scenario 11`, () => {
    setServicePointAttributes(
      11,
      leaveMeUnchanged,
      validitySmallerThan60DaysCausesStatusToBeValidated
    );

    addSePoVersion({}).then((response) => {
      checkStatusAndUpdateEtagVersion(
        response,
        statusValidated,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );
    });
  });

  it(`Step-15: SePo-Status Scenario 12`, () => {
    setServicePointAttributes(
      12,
      changeMe,
      validitySmallerThan60DaysCausesStatusToBeValidated
    );

    addSePoVersion({
      servicePointGeolocation: {
        spatialReference: spatialReference,
        north: north,
        east: east,
        height: height,
      },
    }).then((response) => {
      checkStatusAndUpdateEtagVersion(
        response,
        statusValidated,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );
    });
  });

  it(`Step-16: SePo-Status Scenario 13`, () => {
    setServicePointAttributes(
      13,
      changeMe,
      validitySmallerThan60DaysCausesStatusToBeValidated
    );

    addSePoVersion({
      meansOfTransport: [meansOfTransport],
    }).then((response) => {
      checkStatusAndUpdateEtagVersion(
        response,
        statusValidated,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );
    });
  });

  it(`Step-17: SePo-Status Scenario 14`, () => {
    setServicePointAttributes(
      14,
      changeMe,
      validityBiggerThan60DaysCausesStatusToBeDraft
    );

    addSePoVersion({}).then((response) => {
      checkStatusAndUpdateEtagVersion(
        response,
        statusValidated,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );
    });
  });

  it(`Step-18: SePo-Status Scenario 15`, () => {
    setServicePointAttributes(
      15,
      changeMe,
      validitySmallerThan60DaysCausesStatusToBeValidated
    );

    addSePoVersion({}).then((response) => {
      const body = checkResponseStatusAndGetResponseBody(
        response,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );

      const sePoVersion = checkSePoVersionStatus(body, statusValidated);

      // We need to start from version 15 with the next PUT,
      // because only then all versions are going to be changed,
      // otherwise the versioning cannot recognize our changes for all 15 versions.
      servicePointId = sePoVersion.id;
      setEtagVersion(sePoVersion);
    });
  });

  it(`Step-19: SePo-Status - Change all versions to DRAFT`, () => {
    setServicePointAttributes(
      99, // Change marker
      leaveMeUnchanged,
      validitySmallerThan60DaysCausesStatusToBeValidated,
      false
    );

    // Validity before first and after last version so that all 15 versions are enlarged
    // and hence all are longer than 60 days, because they all will be a year long
    validFrom = '1999-01-01';
    validTo = '2015-12-31';

    // designationLong has to be passed as an attribute, because otherwise this field is going to be deleted,
    // and hence the result will only be 2 versions and not 15, because of the merge.
    addSePoVersion({
      meansOfTransport: [meansOfTransport],
      servicePointGeolocation: {
        spatialReference: spatialReference,
        north: north,
        east: east,
        height: height,
      },
    }).then((response) => {
      const body = checkResponseStatusAndGetResponseBody(
        response,
        CommonUtils.HTTP_REST_API_RESPONSE_OK
      );

      expect(body).to.be.an('array').and.be.of.length(15);

      body.forEach((version) => {
        expect(version).to.have.property('status').which.equals(statusDraft);

        expect(version).to.have.property('validFrom').which.contains('01-01');
        expect(version).to.have.property('validTo').which.contains('12-31');

        expect(version)
          .property('servicePointGeolocation')
          .property('lv95')
          .property('north')
          .equals(north);
        expect(version)
          .property('servicePointGeolocation')
          .property('lv95')
          .property('east')
          .equals(east);
      });
    });
  });
});
