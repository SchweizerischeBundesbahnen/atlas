import LidiUtils from '../../../support/util/lidi-utils';
import CommonUtils from '../../../support/util/common-utils';

/** Szenario 8e: Letzte Version validTo und props updated
 *  NEU:      |_______________________________________|
 *  IST:      |----------------------|       |-------------------------|
 *  Version:             1                                 2
 *
 *  RESULTAT: |______________________________|________|----------------|
 *  Version:             1                        2            3
 */
describe('LiDi: Versioning Teillinie Scenario 4', () => {
  const firstSublineVersion = LidiUtils.getFirstSublineVersion();
  const secondSublineVersion = LidiUtils.getSecondSublineVersion();
  const editedFirstSublineVersion = LidiUtils.getEditedFirstSublineVersion();
  let mainline: any;

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Add mainline', () => {
    mainline = LidiUtils.addMainLine();
  });

  it('Step-3: Navigate to Linienverzeichnis', () => {
    LidiUtils.navigateToLidi();
    LidiUtils.checkHeaderTitle();
    cy.get('[data-cy=sublines-title]').invoke('text').should('eq', 'Teillinien');
  });

  it('Step-4: Add first Subline Version', () => {
    LidiUtils.clickOnAddNewSublinesLinieVersion();
    LidiUtils.fillSublineVersionForm(firstSublineVersion);
    CommonUtils.saveSubline();
    LidiUtils.readSlnidFromForm(firstSublineVersion);
  });

  it('Step-5: Add second Subline Version (with gap)', () => {
    CommonUtils.clickOnEdit();
    LidiUtils.fillSublineVersionForm(secondSublineVersion);
    CommonUtils.saveSubline();
  });

  it('Step-6: update first Subline Version', () => {
    CommonUtils.switchToVersion(1);
    CommonUtils.clickOnEdit();
    cy.get('[data-cy=validFrom]').clear().type(editedFirstSublineVersion.validFrom);
    cy.get('[data-cy=validTo]').clear().type(editedFirstSublineVersion.validTo);
    cy.get('[data-cy=number]').clear().type(editedFirstSublineVersion.number);
    cy.get('[data-cy=longName]').clear().type(editedFirstSublineVersion.longName);
    CommonUtils.saveSubline();
    cy.get('[data-cy=switch-version-total-range]').contains(
      'Teillinie von 01.01.2000 bis 31.12.2002'
    );
  });

  it('Step-7: Assert third version (actual version)', () => {
    cy.get('#version3').should('have.class', 'currentVersion');
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

  it('Step-10: Navigate to Linienverzeichnis', () => {
    CommonUtils.navigateToHome();
    LidiUtils.navigateToLidi();
    LidiUtils.checkHeaderTitle();
  });

  it('Step-11: Check the added is present on the table result and navigate to it ', () => {
    LidiUtils.navigateToSubline(firstSublineVersion);
    cy.contains(mainline.swissLineNumber);
    cy.contains(firstSublineVersion.swissSublineNumber);
  });

  it('Step-12: Delete the subline item ', () => {
    CommonUtils.deleteItems();
    LidiUtils.assertIsOnLiDiHome();
  });

  it('Step-13: Search and Navigate to the mainline item ', () => {
    LidiUtils.navigateToLine(mainline);
    cy.contains(mainline.swissLineNumber);
    LidiUtils.assertContainsLineVersion(mainline);
  });
  it('Step-14: Delete the mainline item ', () => {
    CommonUtils.deleteItems();
    LidiUtils.assertIsOnLiDiHome();
  });
});
