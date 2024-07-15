import PrmUtils from '../../support/util/prm-utils';
import SePoDiDependentUtils, { SePoDependentInfo } from '../../support/util/sepodi-dependent-utils';
import { DataCy } from '../../support/data-cy';
import CommonUtils from '../../support/util/common-utils';
import { PrmDataCy } from '../../support/prm-data-cy';

describe('PRM use case: complete variant', { testIsolation: false }, () => {
  const completeStopPoint = PrmUtils.getCompleteStopPoint();
  let completeSePoDependentInfo: SePoDependentInfo;

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent StopPoint Preparation Step', () => {
    SePoDiDependentUtils.createDependentStopPointWithTrafficPoint('e2e-complete-stop-point').then(
      (info) => (completeSePoDependentInfo = info),
    );
  });

  describe('Use case 1: add base information (complete)', () => {
    it('Step-2: Navigate to Dependent StopPoint', () => {
      PrmUtils.navigateToPrm();
      PrmUtils.searchAndSelect(completeSePoDependentInfo.designationOfficial);
    });

    it('Step-3: Select Means of Transport: Train', () => {
      cy.get(DataCy.MEANS_OF_TRANSPORT_TRAIN).click();
      cy.get('[data-cy="stepper-next"]').click();
    });

    it('Step-4: Fill complete form', () => {
      CommonUtils.getClearType(PrmDataCy.FREE_TEXT, completeStopPoint.freeText);

      CommonUtils.getClearType(DataCy.VALID_FROM, completeStopPoint.validFrom, true);
      CommonUtils.getClearType(DataCy.VALID_TO, completeStopPoint.validTo, true);

      CommonUtils.getClearType(PrmDataCy.ADDRESS, completeStopPoint.address);
      CommonUtils.getClearType(PrmDataCy.ZIP_CODE, completeStopPoint.zipCode);
      CommonUtils.getClearType(PrmDataCy.CITY, completeStopPoint.city);

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

    it('Step-5: Save and assert complete tabs', () => {
      PrmUtils.saveItemAndAssertTabs().then(() => {
        // Assert Refence Point Hint Dialog
        cy.get(DataCy.DIALOG_CONFIRM_BUTTON).should('exist');
        cy.get(DataCy.DIALOG_CANCEL_BUTTON).should('exist').click();

        cy.get(PrmDataCy.TAB_REFERENCE_POINTS).should('exist');
      });
    });

    it('Step-6: Assert complete stop point', () => {
      CommonUtils.assertVersionRange(1, completeStopPoint.validFrom, completeStopPoint.validTo);
      CommonUtils.assertItemValue(PrmDataCy.FREE_TEXT, completeStopPoint.freeText);
    });
  });

  describe('Use case 2: add reference point', () => {
    it('Step-2: Navigate to Dependent StopPoint - ReferencePoint Tab', () => {
      PrmUtils.navigateToPrm();
      PrmUtils.searchAndSelect(completeSePoDependentInfo.designationOfficial);
      cy.get(PrmDataCy.TAB_REFERENCE_POINTS).should('exist').click();
    });

    it('Step-3: Click on new', () => {
      cy.get('[data-cy="new-reference-point"]').click();
    });

    it('Step-4: Fill reference point form', () => {
      CommonUtils.getClearType(PrmDataCy.DESIGNATION, 'Seaside');

      CommonUtils.getClearType(DataCy.VALID_FROM, completeStopPoint.validFrom, true);
      CommonUtils.getClearType(DataCy.VALID_TO, completeStopPoint.validTo, true);

      CommonUtils.selectItemFromDropDown(PrmDataCy.REFERENCE_POINT_TYPE, 'Haupteingang');
      cy.get(PrmDataCy.MAIN_REFERENCE_POINT_CHECKBOX).click();

      CommonUtils.getClearType(
        PrmDataCy.ADDITIONAL_INFORMATION,
        'The sun always shine over happy people.',
      );
    });

    it('Step-5: Save reference point', () => {
      cy.get(DataCy.SAVE_ITEM)
        .click()
        .then(() => {
          cy.get(DataCy.EDIT).should('exist');
          cy.get(DataCy.BACK).should('exist');
        });
    });

    it('Step-6: Assert reference point', () => {
      CommonUtils.assertVersionRange(1, completeStopPoint.validFrom, completeStopPoint.validTo);
      CommonUtils.assertItemValue(PrmDataCy.DESIGNATION, 'Seaside');
    });
  });

  describe('Use case 3: add complete platform', () => {
    it('Step-2: Navigate to Dependent StopPoint - Platform Tab', () => {
      PrmUtils.navigateToPrm();
      PrmUtils.searchAndSelect(completeSePoDependentInfo.designationOfficial);
      cy.get(PrmDataCy.TAB_PLATFORMS).should('exist').click();
    });

    it('Step-3: Click on platform', () => {
      PrmUtils.selectPlatformInTable(completeSePoDependentInfo.trafficPointSloids[0]);
    });

    it('Step-4: Fill complete platform form', () => {
      CommonUtils.getClearType(DataCy.VALID_FROM, '15.01.2024', true);
      CommonUtils.getClearType(DataCy.VALID_TO, '31.12.9999', true);

      CommonUtils.getClearType(PrmDataCy.ADDITIONAL_INFORMATION, 'errare humanum est', true);

      CommonUtils.getClearType(PrmDataCy.SUPERELEVATION, '10', true);
      CommonUtils.getClearType(PrmDataCy.INCLINATION_WIDTH, '-10', true);
      CommonUtils.getClearType(PrmDataCy.INCLINATION, '2', true);

      CommonUtils.selectItemFromDropDown(PrmDataCy.CONTRASTING_AREAS, 'Ja');

      CommonUtils.selectItemFromDropDown(PrmDataCy.DYNAMIC_VISUAL, 'Ja');
      CommonUtils.selectItemFromDropDown(PrmDataCy.DYNAMIC_AUDIO, 'Nein');

      CommonUtils.selectItemFromDropDown(PrmDataCy.LEVEL_ACCESS_WHEELCHAIR, 'Nein');
      CommonUtils.selectItemFromDropDown(PrmDataCy.BOARDING_DEVICE, 'Nein');
    });

    it('Step-5: Save complete platform', () => {
      cy.get(DataCy.SAVE_ITEM)
        .click()
        .then(() => {
          cy.get(DataCy.EDIT).should('exist');
          cy.get(DataCy.BACK).should('exist');

          cy.get(PrmDataCy.TAB_RELATIONS).should('exist');
        });
    });

    it('Step-6: Assert complete platform', () => {
      CommonUtils.assertVersionRange(1, '15.01.2024', '31.12.9999');
      CommonUtils.assertItemValue(PrmDataCy.ADDITIONAL_INFORMATION, 'errare humanum est');
    });
  });

  describe('Use case 4: edit relation', () => {
    it('Step-2: Navigate to Dependent StopPoint - Platform Tab', () => {
      PrmUtils.navigateToPrm();
      PrmUtils.searchAndSelect(completeSePoDependentInfo.designationOfficial);
      cy.get(PrmDataCy.TAB_PLATFORMS).should('exist').click();
    });

    it('Step-3: Click on platform - relation', () => {
      PrmUtils.selectPlatformInTable(completeSePoDependentInfo.trafficPointSloids[0]);

      cy.get(PrmDataCy.TAB_RELATIONS).should('exist').click();
      cy.get(DataCy.EDIT).should('exist');

      cy.get(PrmDataCy.STEP_FREE_ACCESS).should('contain.text', 'Zu vervollständigen');
      cy.get(PrmDataCy.TACTILE_VISUAL_MARKS).should('contain.text', 'Zu vervollständigen');
      cy.get(PrmDataCy.CONTRASTING_AREAS).should('contain.text', 'Zu vervollständigen');
    });

    it('Step-4: Fill relation form', () => {
      cy.get(DataCy.EDIT).click();
      cy.get(DataCy.SAVE_ITEM).should('exist');

      CommonUtils.selectItemFromDropDown(PrmDataCy.STEP_FREE_ACCESS, 'Ja mit Lift');
      CommonUtils.selectItemFromDropDown(PrmDataCy.TACTILE_VISUAL_MARKS, 'Teilweise');
      CommonUtils.selectItemFromDropDown(PrmDataCy.CONTRASTING_AREAS, 'Ja');
    });

    it('Step-5: Save relation', () => {
      cy.get(DataCy.SAVE_ITEM)
        .click()
        .then(() => {
          cy.get(DataCy.DIALOG_CONFIRM_BUTTON).click();
          cy.get(DataCy.EDIT).should('exist');
        });
    });

    it('Step-6: Assert relation', () => {
      CommonUtils.assertVersionRange(1, '15.01.2024', '31.12.9999');
    });
  });
});
