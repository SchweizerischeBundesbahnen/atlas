import ReleaseApiUtils, {
  SwissExportType,
  WorldExportType,
} from '../../../support/util/release-api/release-api-utils';

describe('Check latest exports in SePoDi', { testIsolation: false }, () => {
  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Check Loading Point', () => {
    const name = 'LOADING_POINT_VERSION';
    ReleaseApiUtils.jsonExportChecker(3000, name, WorldExportType.FULL);
    ReleaseApiUtils.jsonExportChecker(2200, name, WorldExportType.ACTUAL);
    ReleaseApiUtils.jsonExportChecker(2200, name, WorldExportType.FUTURE);
  });

  it('Step-3: Check Traffic Points', () => {
    const name = 'TRAFFIC_POINT_ELEMENT_VERSION';
    ReleaseApiUtils.jsonExportChecker(91145, name, WorldExportType.FULL);
    ReleaseApiUtils.jsonExportChecker(59133, name, WorldExportType.ACTUAL);
    ReleaseApiUtils.jsonExportChecker(59276, name, WorldExportType.FUTURE);
  });

  it('Step-4: Check Service Points', () => {
    const name = 'SERVICE_POINT_VERSION';
    ReleaseApiUtils.jsonExportChecker(147738, name, SwissExportType.FULL);
    ReleaseApiUtils.jsonExportChecker(50000, name, SwissExportType.ACTUAL);
    ReleaseApiUtils.jsonExportChecker(50000, name, SwissExportType.FUTURE);

    ReleaseApiUtils.jsonExportChecker(359738, name, WorldExportType.FULL);
    ReleaseApiUtils.jsonExportChecker(120000, name, WorldExportType.ACTUAL);
    ReleaseApiUtils.jsonExportChecker(120000, name, WorldExportType.FUTURE);
  });
});
