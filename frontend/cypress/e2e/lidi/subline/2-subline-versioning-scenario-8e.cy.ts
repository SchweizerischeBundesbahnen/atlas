import LidiUtils from '../../../support/util/lidi-utils';
import CommonUtils from '../../../support/util/common-utils';
import { DataCy } from '../../../support/data-cy';
import BodiDependentUtils from '../../../support/util/bodi-dependent-utils';

/** Szenario 8e: Letzte Version validTo und props updated
 *  NEU:      |_______________________________________|
 *  IST:      |----------------------|       |-------------------------|
 *  Version:             1                                 2
 *
 *  RESULTAT: |______________________________|________|----------------|
 *  Version:             1                        2            3
 */
describe('LiDi: Versioning Teillinie Scenario 4', {testIsolation: false}, () => {
  const firstSublineVersion = LidiUtils.getFirstSublineVersion();
  const secondSublineVersion = LidiUtils.getSecondSublineVersion();
  const editedFirstSublineVersion = LidiUtils.getEditedFirstSublineVersion();
  let mainline: any;

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent BusinessOrganisation Preparation Step', () => {
    BodiDependentUtils.createDependentBusinessOrganisation();
  });

  it('PreStep-2: check if subline and mainline already exists', () => {
    LidiUtils.navigateToSublines();
    LidiUtils.checkIfSublineAlreadyExists(firstSublineVersion);
    LidiUtils.navigateToLines();
    LidiUtils.checkIfLineAlreadyExists(LidiUtils.getMainLineVersion());
  });

  it('Step-2: Add mainline', () => {
    mainline = LidiUtils.addMainLine();
  });

  it('Step-3: Navigate to Sublines', () => {
    LidiUtils.navigateToSublines();
    LidiUtils.checkHeaderTitle();
    LidiUtils.assertSublineTitle();
  });

  it('Step-4: Add first Subline Version', () => {
    LidiUtils.clickOnAddNewSublineVersion();
    LidiUtils.fillSublineVersionForm(firstSublineVersion);
    CommonUtils.saveSubline();
    LidiUtils.readSlnidFromForm(firstSublineVersion);
  });

  it('Step-5: Add second Subline Version (with gap)', () => {
    CommonUtils.clickOnEdit();
    LidiUtils.fillSublineVersionForm(secondSublineVersion, true);
    CommonUtils.saveSubline();
    CommonUtils.getTotalRange().should('contain', '01.01.2000').should('contain', '31.12.2002');
  });

  it('Step-6: update first Subline Version', () => {
    CommonUtils.switchToVersion(1);
    CommonUtils.clickOnEdit();
    cy.get(DataCy.VALID_FROM).clear().type(editedFirstSublineVersion.validFrom);
    cy.get(DataCy.VALID_TO).clear().type(editedFirstSublineVersion.validTo);
    cy.get(DataCy.NUMBER).clear().type(editedFirstSublineVersion.number);
    cy.get(DataCy.LONG_NAME).clear().type(editedFirstSublineVersion.longName);
    CommonUtils.saveSubline();
  });

  it('Step-7: Assert third version (actual version)', () => {
    CommonUtils.assertSelectedVersion(3);
    CommonUtils.assertVersionRange(3, '02.06.2002', '31.12.2002');

    secondSublineVersion.validFrom = '02.06.2002';
    secondSublineVersion.validTo = '31.12.2002';
    LidiUtils.assertContainsSublineVersion(secondSublineVersion);
  });

  it('Step-8: Assert second version', () => {
    CommonUtils.switchToVersion(2);
    CommonUtils.assertVersionRange(2, '01.01.2002', '01.06.2002');

    secondSublineVersion.validFrom = '01.01.2002';
    secondSublineVersion.validTo = '01.06.2002';
    secondSublineVersion.number = editedFirstSublineVersion.number;
    secondSublineVersion.longName = editedFirstSublineVersion.longName;
    LidiUtils.assertContainsSublineVersion(secondSublineVersion);
  });

  it('Step-9: Assert first version', () => {
    CommonUtils.switchToVersion(1);
    CommonUtils.assertVersionRange(1, '01.01.2000', '31.12.2001');

    firstSublineVersion.validFrom = '01.01.2000';
    firstSublineVersion.validTo = '31.12.2001';
    firstSublineVersion.number = editedFirstSublineVersion.number;
    firstSublineVersion.longName = editedFirstSublineVersion.longName;
    LidiUtils.assertContainsSublineVersion(firstSublineVersion);
  });

  it('Step-10: Navigate to Sublines', () => {
    CommonUtils.fromDetailBackToSublinesOverview();
    CommonUtils.navigateToHomeViaHomeLogo();
    LidiUtils.navigateToSublines();
    LidiUtils.checkHeaderTitle();
  });

  it('Step-11: Check the added is present on the table result and navigate to it ', () => {
    LidiUtils.navigateToSubline(firstSublineVersion);
    cy.contains(mainline.swissLineNumber);
    cy.get(DataCy.SWISS_SUBLINE_NUMBER)
    .invoke('val')
    .should('eq', firstSublineVersion.swissSublineNumber);
  });

  it('Step-12: Delete the subline item ', () => {
    CommonUtils.deleteItem();
    LidiUtils.assertIsOnSublines();
  });

  it('Step-13: Search and Navigate to the mainline item ', () => {
    LidiUtils.navigateToLine(mainline);
    cy.get(DataCy.SWISS_LINE_NUMBER).invoke('val').should('eq', mainline.swissLineNumber);
    LidiUtils.assertContainsLineVersion(mainline);
  });
  it('Step-14: Delete the mainline item ', () => {
    CommonUtils.deleteItem();
    LidiUtils.assertIsOnLines();
  });

  it('Dependent BusinessOrganisation Cleanup Step', () => {
    BodiDependentUtils.deleteDependentBusinessOrganisation();
  });
});
