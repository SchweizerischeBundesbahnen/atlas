import PrmUtils from "../../support/util/prm-utils";
import SePoDiDependentUtils, {SePoDependentInfo} from "../../support/util/sepodi-dependent-utils";
import {DataCy} from "../../support/data-cy";
import CommonUtils from "../../support/util/common-utils";
import {PrmDataCy} from "../../support/prm-data-cy";

describe('PRM use case: complete variant', {testIsolation: false}, () => {

  const stopPoint = PrmUtils.getCompleteStopPoint();
  let dependentInfo: SePoDependentInfo;

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent StopPoint Preparation Step', () => {
    SePoDiDependentUtils.createDependentStopPointWithTrafficPoint('e2e-complete-stop-point').then(info => dependentInfo = info);
  });

  describe('Use case 1: add base information', () => {

    it('Step-2: Navigate to Dependent StopPoint', () => {
      PrmUtils.navigateToPrm();
      PrmUtils.searchAndSelect(dependentInfo.designationOfficial);
    });

    it('Step-3: Select Means of Transport: Train', () => {
      cy.get(DataCy.MEANS_OF_TRANSPORT_TRAIN).click();
      cy.get('[data-cy="stepper-next"]').click()
    });

    it('Step-4: Fill complete form', () => {
      CommonUtils.getClearType(PrmDataCy.FREE_TEXT, stopPoint.freeText);

      CommonUtils.getClearType(DataCy.VALID_FROM, stopPoint.validFrom, true);
      CommonUtils.getClearType(DataCy.VALID_TO, stopPoint.validTo, true);

      CommonUtils.getClearType(PrmDataCy.ADDRESS, stopPoint.address);
      CommonUtils.getClearType(PrmDataCy.ZIP_CODE, stopPoint.zipCode);
      CommonUtils.getClearType(PrmDataCy.CITY, stopPoint.city);

      CommonUtils.selectItemFromDropDown(PrmDataCy.VISUAL_INFO, 'Ja');
      CommonUtils.selectItemFromDropDown(PrmDataCy.DYNAMIC_OPTIC_SYSTEM, 'Ja');
      CommonUtils.selectItemFromDropDown(PrmDataCy.DYNAMIC_AUDIO_SYSTEM, 'Ja');

      CommonUtils.selectItemFromDropDown(PrmDataCy.TICKET_MACHINE, 'Ja');
      CommonUtils.selectItemFromDropDown(PrmDataCy.WHEELCHAIR_TICKET_MACHINE, 'Nein');
      CommonUtils.selectItemFromDropDown(PrmDataCy.AUDIO_TICKET_MACHINE, 'Nein');

      CommonUtils.selectItemFromDropDown(PrmDataCy.ASSISTANCE_REQUEST_FULFILLED, 'Ja');
      CommonUtils.selectItemFromDropDown(PrmDataCy.ASSISTANCE_SERVICE, 'Nicht anwendbar');
      CommonUtils.selectItemFromDropDown(PrmDataCy.ASSISTANCE_AVAILABILITY, 'Nicht anwendbar');

      CommonUtils.selectItemFromDropDown(PrmDataCy.ALTERNATIVE_TRANSPORT, 'Nein');
    });

    it('Step-5: Save and assert tabs', () => {
      cy.get(DataCy.SAVE_ITEM).click().then(() => {
        cy.get(DataCy.EDIT).should('exist');
        cy.get(DataCy.CLOSE_DETAIL).should('exist');

        cy.get(PrmDataCy.TAB_REFERENCE_POINTS).should('exist');
        cy.get(PrmDataCy.TAB_PLATFORMS).should('exist');
        cy.get(PrmDataCy.TAB_CONTACT_POINTS).should('exist');
        cy.get(PrmDataCy.TAB_TOILETS).should('exist');
        cy.get(PrmDataCy.TAB_PARKING_LOTS).should('exist');
      });
    });

    it('Step-6: Assert stop point', () => {
      CommonUtils.assertVersionRange(1, stopPoint.validFrom, stopPoint.validTo);
      CommonUtils.assertItemValue(PrmDataCy.FREE_TEXT, stopPoint.freeText);
    });
  });

  describe('Use case 2: add reference point', () => {

    it('Step-2: Navigate to Dependent StopPoint - ReferencePoint Tab', () => {
      PrmUtils.navigateToPrm();
      PrmUtils.searchAndSelect(dependentInfo.designationOfficial);
      cy.get(PrmDataCy.TAB_REFERENCE_POINTS).should('exist').click();
    });

    it('Step-3: Click on new', () => {
      cy.get('[data-cy="new-reference-point"]').click();
    });

    it('Step-4: Fill reference point form', () => {
      CommonUtils.getClearType(PrmDataCy.DESIGNATION, 'Seaside');

      CommonUtils.getClearType(DataCy.VALID_FROM, stopPoint.validFrom, true);
      CommonUtils.getClearType(DataCy.VALID_TO, stopPoint.validTo, true);

      CommonUtils.selectItemFromDropDown(PrmDataCy.REFERENCE_POINT_TYPE, 'Haupteingang');
      cy.get(PrmDataCy.MAIN_REFERENCE_POINT_CHECKBOX).click();

      CommonUtils.getClearType(PrmDataCy.ADDITIONAL_INFORMATION, 'The sun always shine over happy people.');
    });

    it('Step-5: Save reference point', () => {
      cy.get(DataCy.SAVE_ITEM).click().then(() => {
        cy.get(DataCy.EDIT).should('exist');
        cy.get(DataCy.BACK).should('exist');
      });
    });

    it('Step-6: Assert reference point', () => {
      CommonUtils.assertVersionRange(1, stopPoint.validFrom, stopPoint.validTo);
      CommonUtils.assertItemValue(PrmDataCy.DESIGNATION, 'Seaside');
    });

  });

  describe('Use case 3: add complete platform', () => {

    it('Step-2: Navigate to Dependent StopPoint - Platform Tab', () => {
      PrmUtils.navigateToPrm();
      PrmUtils.searchAndSelect(dependentInfo.designationOfficial);
      cy.get(PrmDataCy.TAB_PLATFORMS).should('exist').click();
    });

    it('Step-3: Click on platform', () => {
      // Platform table has length 1
      cy.get(PrmDataCy.PLATFORM_TABLE + ' table tbody tr').should('have.length.greaterThan', 0);
      // Click on the item
      cy.contains('td', dependentInfo.trafficPointSloids[0]).parents('tr').click({force: true});
    });

    it('Step-4: Fill form', () => {
      CommonUtils.getClearType(DataCy.VALID_FROM, "15.01.2024", true);
      CommonUtils.getClearType(DataCy.VALID_TO, "31.12.9999", true);

      CommonUtils.getClearType(PrmDataCy.ADDITIONAL_INFORMATION, "errare humanum est", true);

      CommonUtils.getClearType(PrmDataCy.SUPERELEVATION, "10", true);
      CommonUtils.getClearType(PrmDataCy.INCLINATION_WIDTH, "-10", true);
      CommonUtils.getClearType(PrmDataCy.INCLINATION, "2", true);

      CommonUtils.selectItemFromDropDown(PrmDataCy.CONTRASTING_AREAS, "Ja");

      CommonUtils.selectItemFromDropDown(PrmDataCy.DYNAMIC_VISUAL, "Ja");
      CommonUtils.selectItemFromDropDown(PrmDataCy.DYNAMIC_AUDIO, "Nein");

      CommonUtils.selectItemFromDropDown(PrmDataCy.LEVEL_ACCESS_WHEELCHAIR, "Nein");
      CommonUtils.selectItemFromDropDown(PrmDataCy.BOARDING_DEVICE, "Nein");
    });

    it('Step-5: Save platform', () => {
      cy.get(DataCy.SAVE_ITEM).click().then(() => {
        cy.get(DataCy.EDIT).should('exist');
        cy.get(DataCy.BACK).should('exist');

        cy.get(PrmDataCy.TAB_RELATIONS).should('exist');
      });
    });

    it('Step-6: Assert platform', () => {
      CommonUtils.assertVersionRange(1, "15.01.2024", "31.12.9999");
      CommonUtils.assertItemValue(PrmDataCy.ADDITIONAL_INFORMATION, 'errare humanum est');
    });
  });

  describe('Use case 4: edit relation', () => {

    it('Step-2: Navigate to Dependent StopPoint - Platform Tab', () => {
      PrmUtils.navigateToPrm();
      PrmUtils.searchAndSelect(dependentInfo.designationOfficial);
      cy.get(PrmDataCy.TAB_PLATFORMS).should('exist').click();
    });

    it('Step-3: Click on platform - relation', () => {
      // Platform table has length 1
      cy.get(PrmDataCy.PLATFORM_TABLE + ' table tbody tr').should('have.length.greaterThan', 0);
      // Click on the item
      cy.contains('td', dependentInfo.trafficPointSloids[0]).parents('tr').click({force: true});

      cy.get(PrmDataCy.TAB_RELATIONS).should('exist').click();
      cy.get(DataCy.EDIT).should('exist');
    });

    it('Step-4: Fill relation form', () => {
      cy.get(DataCy.EDIT).click();
      cy.get(DataCy.SAVE_ITEM).should('exist');

      CommonUtils.selectItemFromDropDown(PrmDataCy.STEP_FREE_ACCESS, "Ja mit Lift");
      CommonUtils.selectItemFromDropDown(PrmDataCy.TACTILE_VISUAL_MARKS, "Teilweise");
      CommonUtils.selectItemFromDropDown(PrmDataCy.CONTRASTING_AREAS, "Ja");
    });

    it('Step-5: Save relation', () => {
      cy.get(DataCy.SAVE_ITEM).click().then(() => {
        cy.get(DataCy.EDIT).should('exist');
      });
    });

    it('Step-6: Assert relation', () => {
      CommonUtils.assertVersionRange(1, "15.01.2024", "31.12.9999");
    });
  });

});
