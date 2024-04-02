import PrmUtils from "../../support/util/prm-utils";
import SePoDiDependentUtils from "../../support/util/sepodi-dependent-utils";
import {DataCy} from "../../support/data-cy";
import CommonUtils from "../../support/util/common-utils";

describe('PRM use case: complete variant', {testIsolation: false}, () => {

  const stopPoint = PrmUtils.getCompleteStopPoint();

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent StopPoint Preparation Step', () => {
    SePoDiDependentUtils.createDependentStopPointWithTrafficPoint();
  });

  describe.skip('Use case 1: add base information', () => {

    it('Step-2: Navigate to Dependent StopPoint', () => {
      PrmUtils.navigateToPrm();
      PrmUtils.searchAndSelect(SePoDiDependentUtils.DEPENDENT_STOP_POINT_DESIGNATION);
    });

    it('Step-3: Select Means of Transport: Train', () => {
      cy.get(DataCy.MEANS_OF_TRANSPORT_TRAIN).click();
      cy.get('[data-cy="stepper-next"]').click()
    });

    it('Step-4: Fill complete form', () => {
      CommonUtils.getClearType(DataCy.PRM_FREE_TEXT, stopPoint.freeText);

      CommonUtils.getClearType(DataCy.VALID_FROM, stopPoint.validFrom, true);
      CommonUtils.getClearType(DataCy.VALID_TO, stopPoint.validTo, true);

      CommonUtils.getClearType(DataCy.PRM_ADDRESS, stopPoint.address);
      CommonUtils.getClearType(DataCy.PRM_ZIP_CODE, stopPoint.zipCode);
      CommonUtils.getClearType(DataCy.PRM_CITY, stopPoint.city);

      CommonUtils.selectItemFromDropDown(DataCy.PRM_VISUAL_INFO, 'Ja');
      CommonUtils.selectItemFromDropDown(DataCy.PRM_DYNAMIC_OPTIC_SYSTEM, 'Ja');
      CommonUtils.selectItemFromDropDown(DataCy.PRM_DYNAMIC_AUDIO_SYSTEM, 'Ja');

      CommonUtils.selectItemFromDropDown(DataCy.PRM_TICKET_MACHINE, 'Ja');
      CommonUtils.selectItemFromDropDown(DataCy.PRM_WHEELCHAIR_TICKET_MACHINE, 'Nein');
      CommonUtils.selectItemFromDropDown(DataCy.PRM_AUDIO_TICKET_MACHINE, 'Nein');

      CommonUtils.selectItemFromDropDown(DataCy.PRM_ASSISTANCE_REQUEST_FULFILLED, 'Ja');
      CommonUtils.selectItemFromDropDown(DataCy.PRM_ASSISTANCE_SERVICE, 'Nicht anwendbar');
      CommonUtils.selectItemFromDropDown(DataCy.PRM_ASSISTANCE_AVAILABILITY, 'Nicht anwendbar');

      CommonUtils.selectItemFromDropDown(DataCy.PRM_ALTERNATIVE_TRANSPORT, 'Nein');
    });

    it('Step-5: Save and assert tabs', () => {
      cy.get(DataCy.SAVE_ITEM).click().then(() => {
        cy.get(DataCy.EDIT).should('exist');
        cy.get(DataCy.CLOSE_DETAIL).should('exist');

        cy.get(DataCy.PRM_TAB_REFERENCE_POINTS).should('exist');
        cy.get(DataCy.PRM_TAB_PLATFORMS).should('exist');
        cy.get(DataCy.PRM_TAB_CONTACT_POINTS).should('exist');
        cy.get(DataCy.PRM_TAB_TOILETS).should('exist');
        cy.get(DataCy.PRM_TAB_PARKING_LOTS).should('exist');
      });
    });
  });

  describe.skip('Use case 2: add reference point', () => {

    it('Step-2: Navigate to Dependent StopPoint - ReferencePoint Tab', () => {
      PrmUtils.navigateToPrm();
      PrmUtils.searchAndSelect(SePoDiDependentUtils.DEPENDENT_STOP_POINT_DESIGNATION);
      cy.get(DataCy.PRM_TAB_REFERENCE_POINTS).should('exist').click();
    });

    it('Step-3: Click on new', () => {
      cy.get('[data-cy="new-reference-point"]').click();
    });

    it('Step-4: Fill reference point form', () => {
      CommonUtils.getClearType(DataCy.PRM_DESIGNATION, 'Seaside');

      CommonUtils.getClearType(DataCy.VALID_FROM, stopPoint.validFrom, true);
      CommonUtils.getClearType(DataCy.VALID_TO, stopPoint.validTo, true);

      CommonUtils.selectItemFromDropDown(DataCy.PRM_REFERENCE_POINT_TYPE, 'Haupteingang');
      cy.get(DataCy.PRM_MAIN_REFERENCE_POINT_CHECKBOX).click();

      CommonUtils.getClearType(DataCy.PRM_ADDITIONAL_INFORMATION, 'The sun always shine over happy people.');
    });

    it('Step-5: Save', () => {
      cy.get(DataCy.SAVE_ITEM).click().then(() => {
        cy.get(DataCy.EDIT).should('exist');
        cy.get(DataCy.CLOSE_DETAIL).should('exist');
      });
    });

  });

  describe('Use case 3: add complete platform', () => {

    it('Step-2: Navigate to Dependent StopPoint - Platform Tab', () => {
      PrmUtils.navigateToPrm();
      PrmUtils.searchAndSelect(SePoDiDependentUtils.DEPENDENT_STOP_POINT_DESIGNATION);
      cy.get(DataCy.PRM_TAB_PLATFORMS).should('exist').click();
    });

    it('Step-3: Click on platform', () => {
      // Platform table has length 1
      cy.get(DataCy.PRM_PLATFORM_TABLE + ' table tbody tr').should('have.length.greaterThan', 0);
      // Click on the item
      cy.contains('td', SePoDiDependentUtils.getDependentTrafficPointSloid()).parents('tr').click({force: true});
    });

    it('Step-4: Fill form', () => {
      CommonUtils.getClearType(DataCy.VALID_FROM, "15.01.2024", true);
      CommonUtils.getClearType(DataCy.VALID_TO, "31.12.9999", true);

      CommonUtils.getClearType(DataCy.PRM_ADDITIONAL_INFORMATION, "errare humanum est", true);

      CommonUtils.getClearType(DataCy.PRM_SUPERELEVATION, "10", true);
      CommonUtils.getClearType(DataCy.PRM_INCLINATION_WIDTH, "-10", true);
      CommonUtils.getClearType(DataCy.PRM_INCLINATION, "2", true);

      CommonUtils.selectItemFromDropDown(DataCy.PRM_CONTRASTING_AREAS, "Ja");

      CommonUtils.selectItemFromDropDown(DataCy.PRM_DYNAMIC_VISUAL, "Ja");
      CommonUtils.selectItemFromDropDown(DataCy.PRM_DYNAMIC_AUDIO, "Nein");

      CommonUtils.selectItemFromDropDown(DataCy.PRM_LEVEL_ACCESS_WHEELCHAIR, "Nein");
      CommonUtils.selectItemFromDropDown(DataCy.PRM_BOARDING_DEVICE, "Nein");
    });

    it('Step-5: Save', () => {
      cy.get(DataCy.SAVE_ITEM).click().then(() => {
        cy.get(DataCy.EDIT).should('exist');
        cy.get(DataCy.BACK).should('exist');

        cy.get(DataCy.PRM_TAB_RELATIONS).should('exist');
      });
    });
  });

  describe('Use case 4: edit relation', () => {

    it('Step-2: Navigate to Dependent StopPoint - Platform Tab', () => {
      PrmUtils.navigateToPrm();
      PrmUtils.searchAndSelect(SePoDiDependentUtils.DEPENDENT_STOP_POINT_DESIGNATION);
      cy.get(DataCy.PRM_TAB_PLATFORMS).should('exist').click();
    });

    it('Step-3: Click on platform - relation', () => {
      // Platform table has length 1
      cy.get(DataCy.PRM_PLATFORM_TABLE + ' table tbody tr').should('have.length.greaterThan', 0);
      // Click on the item
      cy.contains('td', SePoDiDependentUtils.getDependentTrafficPointSloid()).parents('tr').click({force: true});

      cy.get(DataCy.PRM_TAB_RELATIONS).should('exist').click();
      cy.get(DataCy.EDIT).should('exist');
    });

    it('Step-4: Fill relation form', () => {
      cy.get(DataCy.EDIT).click();
      cy.get(DataCy.SAVE_ITEM).should('exist');

      CommonUtils.selectItemFromDropDown(DataCy.PRM_STEP_FREE_ACCESS, "Ja mit Lift");
      CommonUtils.selectItemFromDropDown(DataCy.PRM_TACTILE_VISUAL_MARKS, "Teilweise");
      CommonUtils.selectItemFromDropDown(DataCy.PRM_CONTRASTING_AREAS, "Ja");
    });

    it('Step-5: Save relation', () => {
      cy.get(DataCy.SAVE_ITEM).click().then(() => {
        cy.get(DataCy.EDIT).should('exist');
      });
    });
  });

});
