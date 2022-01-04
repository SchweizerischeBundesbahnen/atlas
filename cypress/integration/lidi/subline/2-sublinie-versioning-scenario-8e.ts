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

  const breadcrumbTitle = 'Linienverzeichnis';

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Add mainline', () => {
    mainline = LidiUtils.addMainLine();
  });

  it('Step-3: Navigate to Linienverzeichnis', () => {
    LidiUtils.navigateToLidi();
    cy.contains(breadcrumbTitle);
    cy.get('[data-cy=sublines-title]').invoke('text').should('eq', 'Teillinien');
  });

  it('Step-4: Add first Subline Version', () => {
    LidiUtils.clickOnAddNewSublinesLinieVersion();
    LidiUtils.fillSublineVersionForm(firstSublineVersion);
    CommonUtils.saveSubline();
    LidiUtils.readSlnidFromForm(firstSublineVersion);
  });

  it('Step-5: Add second Sibline Version (with gap)', () => {
    CommonUtils.clickOnEdit();
    LidiUtils.fillSublineVersionForm(secondSublineVersion);
    CommonUtils.saveSubline();
  });

  it('Step-6: update first Sibline Version', () => {
    CommonUtils.switchLeft();
    CommonUtils.clickOnEdit();
    cy.get('[data-cy=validFrom]').clear().type(editedFirstSublineVersion.validFrom);
    cy.get('[data-cy=validTo]').clear().type(editedFirstSublineVersion.validTo);
    cy.get('[data-cy=number]').clear().type(editedFirstSublineVersion.number);
    cy.get('[data-cy=longName]').clear().type(editedFirstSublineVersion.longName);
    CommonUtils.saveSubline();
  });

  it('Step-7: Check version display', () => {
    cy.get('[data-cy=switch-version-total-range]').contains(
      'Teillinien von 01.01.2000 bis 31.12.2002'
    );
  });

  it('Step-8: Assert third version (actual version)', () => {
    cy.get('[data-cy=switch-version-navigation-items]').contains('3 / 3');
    cy.get('[data-cy=switch-version-current-range]').contains('02.06.2002 bis 31.12.2002');

    secondSublineVersion.validFrom = '02.06.2002';
    secondSublineVersion.validTo = '31.12.2002';
    LidiUtils.assertContainsSublineVersion(secondSublineVersion);
  });

  it('Step-9: Assert second version', () => {
    CommonUtils.switchLeft();
    cy.get('[data-cy=switch-version-navigation-items]').contains('2 / 3');
    cy.get('[data-cy=switch-version-current-range]').contains('01.01.2002 bis 01.06.2002');

    secondSublineVersion.validFrom = '01.01.2002';
    secondSublineVersion.validTo = '01.06.2002';
    secondSublineVersion.number = editedFirstSublineVersion.number;
    secondSublineVersion.longName = editedFirstSublineVersion.longName;
    LidiUtils.assertContainsSublineVersion(secondSublineVersion);
  });

  it('Step-10: Assert first version', () => {
    CommonUtils.switchLeft();
    cy.get('[data-cy=switch-version-navigation-items]').contains('1 / 3');
    cy.get('[data-cy=switch-version-current-range]').contains('01.01.2000 bis 31.12.2001');

    firstSublineVersion.validFrom = '01.01.2000';
    firstSublineVersion.validTo = '31.12.2001';
    firstSublineVersion.number = editedFirstSublineVersion.number;
    firstSublineVersion.longName = editedFirstSublineVersion.longName;
    LidiUtils.assertContainsSublineVersion(firstSublineVersion);
  });

  it('Step-11: Navigate to Linienverzeichnis', () => {
    CommonUtils.navigateToHome();
    LidiUtils.navigateToLidi();
    cy.contains(breadcrumbTitle);
  });

  it('Step-12: Check the added is present on the table result and navigate to it ', () => {
    LidiUtils.navigateToSubline(firstSublineVersion);
    cy.contains(mainline.swissLineNumber);
    cy.contains(firstSublineVersion.swissSublineNumber);
  });

  it('Step-13: Delete the subline item ', () => {
    CommonUtils.deleteItems();
    LidiUtils.assertIsOnLiDiHome();
  });

  it('Step-14: Search and Navigate to the mainline item ', () => {
    LidiUtils.navigateToLine(mainline);
    cy.contains(mainline.swissLineNumber);
    LidiUtils.assertContainsLineVersion(mainline);
  });
  it('Step-15: Delete the mainline item ', () => {
    CommonUtils.deleteItems();
    LidiUtils.assertIsOnLiDiHome();
  });
});
