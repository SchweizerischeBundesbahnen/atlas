import PrmUtils from "../../support/util/prm-utils";
import SePoDiDependentUtils, {SePoDependentInfo} from "../../support/util/sepodi-dependent-utils";
import {DataCy} from "../../support/data-cy";
import CommonUtils from "../../support/util/common-utils";
import {PrmDataCy} from "../../support/prm-data-cy";

describe('PRM use case: reduced variant', {testIsolation: false}, () => {

  const stopPoint = PrmUtils.getCompleteStopPoint();
  let dependentInfo: SePoDependentInfo;

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent StopPoint Preparation Step', () => {
    SePoDiDependentUtils.createDependentStopPointWithTrafficPoint('e2e-reduced-stop-point').then(info => dependentInfo = info);
  });

  describe('Use case 1: add base information', () => {

    it('Step-2: Navigate to Dependent StopPoint', () => {
      PrmUtils.navigateToPrm();
      PrmUtils.searchAndSelect(dependentInfo.designationOfficial);
    });

    it('Step-3: Select Means of Transport: Cable Car', () => {
      cy.get(DataCy.MEANS_OF_TRANSPORT_CABLE_CAR).click();
      cy.get('[data-cy="stepper-next"]').click()
    });

    it('Step-4: Fill reduced form', () => {
      CommonUtils.getClearType(PrmDataCy.FREE_TEXT, stopPoint.freeText);

      CommonUtils.getClearType(DataCy.VALID_FROM, stopPoint.validFrom, true);
      CommonUtils.getClearType(DataCy.VALID_TO, stopPoint.validTo, true);
    });

    it('Step-5: Save and assert tabs', () => {
      cy.get(DataCy.SAVE_ITEM).click().then(() => {
        cy.get(DataCy.EDIT).should('exist');
        cy.get(DataCy.CLOSE_DETAIL).should('exist');

        cy.get(PrmDataCy.TAB_REFERENCE_POINTS).should('not.exist');
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

  describe('Use case 2: add reduced platform', () => {

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

      CommonUtils.selectItemFromDropDown(PrmDataCy.VEHICLE_ACCESS, "Stufenloser Zugang; Ein-/Ausstieg durch Personalhilfestellung, keine Voranmeldung nötig.");
      CommonUtils.selectItemFromDropDown(PrmDataCy.TACTILE_SYSTEM, "Ja");

      CommonUtils.getClearType(PrmDataCy.ADDITIONAL_INFORMATION, "errare humanum est", true);

      CommonUtils.getClearType(PrmDataCy.INCLINATION_LONGITUDINAL, "10", true);

      CommonUtils.selectItemFromDropDown(PrmDataCy.INFO_OPPORTUNITIES, "elektronische Anzeige, nur mit Abfahrten");
      cy.get(PrmDataCy.INFO_OPPORTUNITIES).type('{esc}');

      CommonUtils.getClearType(PrmDataCy.HEIGHT, "22", true);
      CommonUtils.getClearType(PrmDataCy.WHEELCHAIR_AREA_LENGTH, "15", true);
      CommonUtils.getClearType(PrmDataCy.WHEELCHAIR_AREA_WIDTH, "4", true);
    });

    it('Step-5: Save reduced platform', () => {
      cy.get(DataCy.SAVE_ITEM).click().then(() => {
        cy.get(DataCy.EDIT).should('exist');
        cy.get(DataCy.BACK).should('exist');

        cy.get(PrmDataCy.TAB_RELATIONS).should('not.exist');
      });
    });

    it('Step-6: Assert platform', () => {
      CommonUtils.assertVersionRange(1, "15.01.2024", "31.12.9999");
      CommonUtils.assertItemValue(PrmDataCy.ADDITIONAL_INFORMATION, 'errare humanum est');
    });
  });

});
