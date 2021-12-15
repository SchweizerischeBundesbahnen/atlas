import LidiUtils from '../../../support/util/lidi-utils';
import CommonUtils from '../../../support/util/common-utils';

describe('Teillinie', () => {
  const sublineVersion = LidiUtils.getFirstSublineVersion();
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

  it('Step-4: Check the Linienverzeichnis Line Table is visible', () => {
    CommonUtils.assertTableHeader(1, 'CH-Teilliniennummer');
    CommonUtils.assertTableHeader(2, 'Teillinienbezeichnung');
    CommonUtils.assertTableHeader(3, 'CH-Liniennummer (CHLNR)');
    CommonUtils.assertTableHeader(4, 'Status');
    CommonUtils.assertTableHeader(5, 'Teillinientyp');
    CommonUtils.assertTableHeader(6, 'Geschäftsorganisation Konzessionär');
    CommonUtils.assertTableHeader(7, 'SLNID');
    CommonUtils.assertTableHeader(8, 'Gültig von');
    CommonUtils.assertTableHeader(9, 'Gültig bis');
  });

  it('Step-5: Go to page Add new Version', () => {
    LidiUtils.clickOnAddNewSublinesLinieVersion();
    LidiUtils.fillSublineVersionForm(sublineVersion);
    CommonUtils.saveSubline();
  });

  it('Step-6: Navigate to Linienverzeichnis', () => {
    CommonUtils.navigateToHome();
    LidiUtils.navigateToLidi();
    cy.contains(breadcrumbTitle);
  });

  it('Step-7: Check the added is present on the table result and navigate to it ', () => {
    cy.contains(sublineVersion.swissSublineNumber).parents('tr').click();
    cy.contains(sublineVersion.swissSublineNumber);
    LidiUtils.assertContainsSublineVersion(sublineVersion);
  });

  it('Step-8: Delete the subline item ', () => {
    CommonUtils.deleteItems();
    cy.contains(breadcrumbTitle);
  });

  it('Step-9: Delete the mainline item ', () => {
    cy.get('[data-cy=lidi-lines]').contains(mainline.swissLineNumber).parents('tr').click();
    cy.contains(mainline.swissLineNumber);
    CommonUtils.deleteItems();
    cy.contains(breadcrumbTitle);
  });
});
