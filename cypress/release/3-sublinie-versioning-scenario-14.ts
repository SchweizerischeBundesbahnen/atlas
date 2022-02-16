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

  it('Step-6: Check version display', () => {
    cy.get('[data-cy=switch-version-total-range]').contains(
      'Teillinien von ' + newValidFrom + ' bis 31.12.2000'
    );
  });

  it('Step-7: Assert version (current version)', () => {
    sublineVersion.validFrom = newValidFrom;
    LidiUtils.assertContainsSublineVersion(sublineVersion);
  });

  it('Step-8: Navigate to Linienverzeichnis', () => {
    CommonUtils.navigateToHome();
    LidiUtils.navigateToLidi();
    cy.contains(breadcrumbTitle);
  });

  it('Step-9: Check the added is present on the table result and navigate to it ', () => {
    cy.contains(sublineVersion.swissSublineNumber).parents('tr').click();
    cy.contains(sublineVersion.swissSublineNumber);
  });

  it('Step-10: Delete the subline item ', () => {
    CommonUtils.deleteItems();
    cy.contains(breadcrumbTitle);
  });

  it('Step-11: Delete the mainline item ', () => {
    CommonUtils.typeSearchInput(
      '/line-directory/v1/lines?**',
      '[data-cy="lidi-lines"] [data-cy=table-search-chip-input]',
      mainline.swissLineNumber
    );
    CommonUtils.typeSearchInput(
      '/line-directory/v1/lines?**',
      '[data-cy="lidi-lines"] [data-cy=table-search-chip-input]',
      mainline.slnid
    );
    cy.get('[data-cy=lidi-lines] tr').contains(mainline.swissLineNumber).click();
    cy.contains(mainline.swissLineNumber);
    CommonUtils.deleteItems();
    cy.contains(breadcrumbTitle);
  });
});
