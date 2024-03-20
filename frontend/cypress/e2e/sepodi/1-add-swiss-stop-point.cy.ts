import SepodiUtils from "../../support/util/sepodi-utils";
import CommonUtils from "../../support/util/common-utils";
import {DataCy} from "../../support/data-cy";
import BodiDependentUtils from "../../support/util/bodi-dependent-utils";

describe('SePoDi use cases', {testIsolation: false}, () => {

  const servicePoint = SepodiUtils.getServicePointVersion();

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent BusinessOrganisation Preparation Step', () => {
    BodiDependentUtils.createDependentBusinessOrganisation();
  });

  describe('Use case 1: Add a swiss stopPoint ', () => {

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
      CommonUtils.selectItemFromDropDown(DataCy.SEPODI_STOP_POINT_TYPE_SELECTION, 'Ordentliche Haltestelle');
      cy.get(DataCy.SEPODI_STOP_POINT_TYPE_SELECTION).type('{esc}')
      CommonUtils.selectItemFromDropDown(DataCy.SEPODI_CATEGORIES, 'Billettautomat SBB');
      cy.get(DataCy.SEPODI_CATEGORIES).type('{esc}')
      SepodiUtils.saveServicePoint();
    });

    it('Step-5: Search added StopPoint', () => {
      CommonUtils.navigateToHomeViaHomeLogo();
      SepodiUtils.navigateToServicePoint();
      cy.get(DataCy.SEPODI_SEARCH_SERVICE_POINT_SELECT + ' input')
        .type(servicePoint.designationOfficial)
        .then(() => {
          cy.intercept('POST', 'service-point-directory/v1/service-points/search').as('searchVersion');
          cy.wait('@searchVersion').its('response.statusCode').should('eq', 200);
          cy.get(DataCy.SEPODI_SEARCH_SERVICE_POINT_SELECT + ' .ng-option').click();
        })
    });

    it('Step-6: Validate added StopPoint', () => {
      CommonUtils.getTotalRange().should('contain', servicePoint.validFrom).should('contain', servicePoint.validTo);
      CommonUtils.assertVersionRange(1, servicePoint.validFrom, servicePoint.validTo);
      CommonUtils.assertVersionStatus(1, 'Entwurf');
      CommonUtils.assertItemValue(DataCy.SEPODI_DESIGNATION_OFFICIAL, servicePoint.designationOfficial);
      CommonUtils.assertItemValue(DataCy.SEPODI_DESIGNATION_LONG, servicePoint.designationLong);
      cy.get(DataCy.BUSINESS_ORGANISATION).should('contain.text', servicePoint.businessOrganisation);
      CommonUtils.assertItemValue(DataCy.VALID_FROM, servicePoint.validFrom);
      CommonUtils.assertItemValue(DataCy.VALID_TO, servicePoint.validTo);
      cy.get(DataCy.SEPODI_CHECKBOX_STOP_POINT).should('be.checked');
      cy.get(DataCy.SEPODI_STOP_POINT_TYPE_SELECTION + ' span').should('contain.text', 'Ordentliche Haltestelle')
      CommonUtils.assertItemValue(DataCy.SEPODI_GEOLOCATION_EAST, servicePoint.east);
      CommonUtils.assertItemValue(DataCy.SEPODI_GEOLOCATION_NORTH, servicePoint.north);
      CommonUtils.assertItemsFromDropdownAreChecked(DataCy.SEPODI_CATEGORIES, ['Billettautomat SBB'])
    });

  });

  describe('Use case 2: change geografie', () => {

    it('Step-1: update geografie', () => {
      cy.get(DataCy.EDIT).click();
      CommonUtils.getClearType(DataCy.SEPODI_GEOLOCATION_EAST, '2600783');
      CommonUtils.getClearType(DataCy.SEPODI_GEOLOCATION_NORTH, '1201099');
      CommonUtils.getClearType(DataCy.SEPODI_GEOLOCATION_HEIGHT, '555');
      SepodiUtils.saveServicePoint();
      CommonUtils.assertItemValue(DataCy.SEPODI_GEOLOCATION_EAST, '2600783');
      CommonUtils.assertItemValue(DataCy.SEPODI_GEOLOCATION_NORTH, '1201099');
      CommonUtils.assertItemValue(DataCy.SEPODI_GEOLOCATION_HEIGHT, '555');
    });

  });

  describe('Use case 3: StopPoint cancellation/termination', () => {

    it('Step-1: update validTo', () => {
      cy.get(DataCy.EDIT).click();
      const terminationDate = '31.12.2098';
      CommonUtils.getClearType(DataCy.VALID_TO, terminationDate, true);
      SepodiUtils.saveServicePoint();
      CommonUtils.assertItemValue(DataCy.VALID_TO, terminationDate);
      CommonUtils.assertVersionRange(1, servicePoint.validFrom, terminationDate);
      CommonUtils.getTotalRange().should('contain', servicePoint.validFrom).should('contain', terminationDate);
    });

  });

});
