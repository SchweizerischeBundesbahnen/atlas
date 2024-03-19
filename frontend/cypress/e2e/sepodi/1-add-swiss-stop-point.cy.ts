import SepodiUtils from "../../support/util/sepodi-utils";
import {DataCy} from "../../support/data-cy";
import CommonUtils from "../../support/util/common-utils";
import BodiDependentUtils from "../../support/util/bodi-dependent-utils";

describe('Use case: Add a swiss stopPoint ', {testIsolation: false}, () => {

  const servicePoint = SepodiUtils.getServicePointVersion();

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent BusinessOrganisation Preparation Step', () => {
    BodiDependentUtils.createDependentBusinessOrganisation();
  });


  it('Step-2: Navigate to Dienststellen', () => {
    SepodiUtils.navigateToServicePoint();
  });

  it('Step-3: Click on Neue Dienststelle', () => {
    cy.get(DataCy.SEPODI_NEW_SERVICE_POINT_BUTTON).click();
    cy.get(DataCy.SEPODI_NEW_SERVICE_POINT_LABEL).should('contain.text', 'Neue Dienststelle');
  });

  it('Step-4: Fill form', () => {
    CommonUtils.selectItemFromDropDown(DataCy.SEPODI_SELECT_COUNTRY, '85 - Schweiz');
    CommonUtils.getClearType(DataCy.SEPODI_DESIGNATION_OFFICIAL, servicePoint.designationOfficial);
    CommonUtils.getClearType(DataCy.SEPODI_DESIGNATION_LONG, servicePoint.designationLong);
    CommonUtils.getClearType(DataCy.VALID_FROM, servicePoint.validFrom, true);
    CommonUtils.getClearType(DataCy.VALID_TO, servicePoint.validTo, true);
    CommonUtils.typeAndSelectItemFromDropDown(
      DataCy.BUSINESS_ORGANISATION + ' ' + 'input',
      servicePoint.businessOrganisation
    );
    cy.get('[type="radio"]').check('STOP_POINT');
    cy.get(DataCy.SEPODI_CHECKBOX_STOP_POINT).check();
    cy.get(DataCy.SEPODI_CHECKBOX_STOP_POINT).should('be.checked');
    cy.get(DataCy.MEANS_OF_TRANSPORT_TRAIN).click();
    CommonUtils.getClearType(DataCy.SEPODI_GEOLOCATION_EAST, servicePoint.east);
    CommonUtils.getClearType(DataCy.SEPODI_GEOLOCATION_NORTH, servicePoint.north);
    CommonUtils.getClearType(DataCy.SEPODI_GEOLOCATION_HEIGHT, servicePoint.height);
    CommonUtils.selectItemFromDropDown(DataCy.SEPODI_CATEGORIES, 'Billettautomat SBB');
    cy.get(DataCy.SEPODI_CATEGORIES).type('{esc}')
    CommonUtils.saveVersionWithWait('service-point-directory/v1/service-points/versions/*')
  });

  it('Dependent BusinessOrganisation Cleanup Step', () => {
    BodiDependentUtils.deleteDependentBusinessOrganisation();
  });

});
