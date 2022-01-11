import LidiUtils from '../../../support/util/lidi-utils';
import CommonUtils from '../../../support/util/common-utils';

/**
 * Szenario 4: Update, das über eine ganze Version hinausragt
 * NEU:             |___________________________________|
 * IST:      |-----------|----------------------|--------------------
 * Version:        1                 2                  3
 *
 *
 * RESULTAT: |------|_____|______________________|______|------------     NEUE VERSION EINGEFÜGT
 * Version:      1     4              2              5        3
 */

describe('LiDi: Versioning Linie Scenario 4', () => {
  const firstLinieVersion = LidiUtils.getFirstLineVersion();
  const secondLineVersion = LidiUtils.getSecondLineVersion();
  const thirdLineVersion = LidiUtils.getThirdLineVersion();
  const editedLineVersion = LidiUtils.getEditedLineVersion();

  const headerTitle = 'Linienverzeichnis';

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to Linienverzeichnis', () => {
    LidiUtils.navigateToLidi();
  });

  it('Step-3: Add first Linie Version', () => {
    LidiUtils.clickOnAddNewLinieVersion();
    LidiUtils.fillLineVersionForm(firstLinieVersion);
    CommonUtils.saveLine();
  });

  it('Step-4: Add second Linie Version', () => {
    CommonUtils.clickOnEdit();
    LidiUtils.fillLineVersionForm(secondLineVersion);
    CommonUtils.saveLine();
  });

  it('Step-5: Add third Linie Version', () => {
    CommonUtils.clickOnEdit();
    LidiUtils.fillLineVersionForm(thirdLineVersion);
    CommonUtils.saveLine();
  });

  it('Step-6: Add edited Linie Version to trigger versioning Scenario 4', () => {
    CommonUtils.clickOnEdit();
    cy.get('[data-cy=validFrom]').clear().type(editedLineVersion.validFrom);
    cy.get('[data-cy=validTo]').clear().type(editedLineVersion.validTo);
    cy.get('[data-cy=alternativeName]').clear().type(editedLineVersion.alternativeName);

    CommonUtils.saveLine();
    cy.get('[data-cy=switch-version-total-range]').contains('Linien von 01.01.2000 bis 31.12.2002');
  });

  it('Step-7: Assert fifth version (actual version)', () => {
    cy.get('[data-cy=switch-version-navigation-items]').contains('5 / 5');
    cy.get('[data-cy=switch-version-current-range]').contains('02.06.2002 bis 31.12.2002');

    thirdLineVersion.validFrom = '02.06.2002';
    thirdLineVersion.validTo = '31.12.2002';
    LidiUtils.assertContainsLineVersion(thirdLineVersion);
  });

  it('Step-8: Assert fourth version', () => {
    CommonUtils.switchLeft();
    cy.get('[data-cy=switch-version-navigation-items]').contains('4 / 5');
    cy.get('[data-cy=switch-version-current-range]').contains('01.01.2002 bis 01.06.2002');

    thirdLineVersion.validFrom = '01.01.2002';
    thirdLineVersion.validTo = '01.06.2002';
    thirdLineVersion.alternativeName = editedLineVersion.alternativeName;
    LidiUtils.assertContainsLineVersion(thirdLineVersion);
  });

  it('Step-9: Assert third version', () => {
    CommonUtils.switchLeft();
    cy.get('[data-cy=switch-version-navigation-items]').contains('3 / 5');
    cy.get('[data-cy=switch-version-current-range]').contains('01.01.2001 bis 31.12.2001');

    thirdLineVersion.validFrom = '01.01.2001';
    thirdLineVersion.validTo = '31.12.2001';
    thirdLineVersion.alternativeName = editedLineVersion.alternativeName;
    thirdLineVersion.businessOrganisation = 'SBB-1';
    thirdLineVersion.comment = 'Kommentar-1';
    LidiUtils.assertContainsLineVersion(thirdLineVersion);
  });

  it('Step-10: Assert second version', () => {
    CommonUtils.switchLeft();
    cy.get('[data-cy=switch-version-navigation-items]').contains('2 / 5');
    cy.get('[data-cy=switch-version-current-range]').contains('01.06.2000 bis 31.12.2000');

    secondLineVersion.validFrom = '01.06.2000';
    secondLineVersion.validTo = '31.12.2000';
    secondLineVersion.businessOrganisation = 'SBB';
    secondLineVersion.alternativeName = editedLineVersion.alternativeName;
    secondLineVersion.comment = firstLinieVersion.comment;
    LidiUtils.assertContainsLineVersion(secondLineVersion);
  });

  it('Step-11: Assert first version', () => {
    CommonUtils.switchLeft();
    cy.get('[data-cy=switch-version-navigation-items]').contains('1 / 5');
    cy.get('[data-cy=switch-version-current-range]').contains('01.01.2000 bis 31.05.2000');

    firstLinieVersion.validTo = '31.05.2000';
    LidiUtils.assertContainsLineVersion(firstLinieVersion);
  });

  it('Step-12: Delete the item ', () => {
    CommonUtils.deleteItems();
    cy.contains(headerTitle);
  });
});
