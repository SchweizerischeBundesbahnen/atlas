import LidiUtils from '../support/util/lidi-utils';
import CommonUtils from '../support/util/common-utils';

/** Szenario 14: Linke Grenze ("Gültig von") auf gleichen Tag setzen, wie rechte Grenze ("Gültig bis")
 *
 * NEU:                                                             |
 * IST:      |------------------------------------------------------|
 * Version:                               1
 *
 * RESULTAT:                                                        |
 * Version:                                                         1
 */
describe('LiDi: Versioning Teillinie Scenario 14 - ATLAS-316', () => {
  const sublineVersion = LidiUtils.getFirstSublineVersion();
  const newValidFrom = '31.12.2000';
  let mainline: any;

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Add mainline', () => {
    mainline = LidiUtils.addMainLine();
  });

  it('Step-3: Navigate to Sublines', () => {
    LidiUtils.navigateToSublines();
    LidiUtils.checkHeaderTitle();
    LidiUtils.assertSublineTitle();
  });

  it('Step-4: Add Subline Version', () => {
    LidiUtils.clickOnAddNewSublinesLinieVersion();
    LidiUtils.fillSublineVersionForm(sublineVersion);
    CommonUtils.saveSubline();
  });

  it('Step-5: Update Subline Version', () => {
    CommonUtils.clickOnEdit();
    cy.get('[data-cy=validFrom]').clear().type(newValidFrom);
    CommonUtils.saveSubline();
  });

  it('Step-6: Assert version (current version)', () => {
    CommonUtils.assertSelectedVersion(1);
    sublineVersion.validFrom = newValidFrom;
    LidiUtils.assertContainsSublineVersion(sublineVersion);
  });

  it('Step-7: Navigate to Sublines', () => {
    CommonUtils.fromDetailBackToOverview();
    CommonUtils.navigateToHome();
    LidiUtils.navigateToSublines();
  });

  it('Step-8: Check the added is present on the table result and navigate to it ', () => {
    cy.contains(sublineVersion.swissSublineNumber).parents('tr').click();
    cy.contains(sublineVersion.swissSublineNumber);
  });

  it('Step-9: Delete the subline item ', () => {
    CommonUtils.deleteItems();
    LidiUtils.assertIsOnSublines();
  });

  it('Step-10: Delete the mainline item ', () => {
    LidiUtils.navigateToLine(mainline);
    cy.contains(mainline.swissLineNumber);
    LidiUtils.assertContainsLineVersion(mainline);

    CommonUtils.deleteItems();
    LidiUtils.assertIsOnLines();
  });
});
