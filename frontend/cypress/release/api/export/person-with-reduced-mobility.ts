import ReleaseApiUtils, {
  PrmExportType,
} from '../../../support/util/release-api/release-api-utils';

describe('Check latest exports in PRM', { testIsolation: false }, () => {
  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Check Contact Point', () => {
    const name = 'CONTACT_POINT_VERSION';
    ReleaseApiUtils.jsonExportChecker(470, name, PrmExportType.FULL);
    ReleaseApiUtils.jsonExportChecker(400, name, PrmExportType.ACTUAL);
    ReleaseApiUtils.jsonExportChecker(400, name, PrmExportType.FUTURE);
  });

  it('Step-3: Check Parking Lot', () => {
    const name = 'PARKING_LOT_VERSION';
    ReleaseApiUtils.jsonExportChecker(1000, name, PrmExportType.FULL);
    ReleaseApiUtils.jsonExportChecker(900, name, PrmExportType.ACTUAL);
    ReleaseApiUtils.jsonExportChecker(900, name, PrmExportType.FUTURE);
  });

  it('Step-4: Check Platform', () => {
    const name = 'PLATFORM_VERSION';
    ReleaseApiUtils.jsonExportChecker(80000, name, PrmExportType.FULL);
    ReleaseApiUtils.jsonExportChecker(50000, name, PrmExportType.ACTUAL);
    ReleaseApiUtils.jsonExportChecker(50000, name, PrmExportType.FUTURE);
  });

  it('Step-5: Check Reference Point', () => {
    const name = 'REFERENCE_POINT_VERSION';
    ReleaseApiUtils.jsonExportChecker(1600, name, PrmExportType.FULL);
    ReleaseApiUtils.jsonExportChecker(1600, name, PrmExportType.ACTUAL);
    ReleaseApiUtils.jsonExportChecker(1600, name, PrmExportType.FUTURE);
  });

  it('Step-6: Check Relation', () => {
    const name = 'RELATION_VERSION';
    ReleaseApiUtils.jsonExportChecker(6000, name, PrmExportType.FULL);
    ReleaseApiUtils.jsonExportChecker(5000, name, PrmExportType.ACTUAL);
    ReleaseApiUtils.jsonExportChecker(5000, name, PrmExportType.FUTURE);
  });

  it('Step-7: Check Stop Point', () => {
    const name = 'STOP_POINT_VERSION';
    ReleaseApiUtils.jsonExportChecker(28977, name, PrmExportType.FULL);
    ReleaseApiUtils.jsonExportChecker(23179, name, PrmExportType.ACTUAL);
    ReleaseApiUtils.jsonExportChecker(23189, name, PrmExportType.FUTURE);
  });

  it('Step-8: Check Toilet', () => {
    const name = 'TOILET_VERSION';
    ReleaseApiUtils.jsonExportChecker(600, name, PrmExportType.FULL);
    ReleaseApiUtils.jsonExportChecker(500, name, PrmExportType.ACTUAL);
    ReleaseApiUtils.jsonExportChecker(500, name, PrmExportType.FUTURE);
  });
});
