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
    cy.contains('Teillinien');
    CommonUtils.assertTableSearch(1, 0, 'Suche');
    CommonUtils.assertTableSearch(1, 1, 'Status');
    CommonUtils.assertTableSearch(1, 2, 'Teillinientyp');
    CommonUtils.assertTableSearch(1, 3, 'Gültig am');
    CommonUtils.assertTableHeader(1, 0, 'CH-Teilliniennummer');
    CommonUtils.assertTableHeader(1, 1, 'Teillinienbezeichnung');
    CommonUtils.assertTableHeader(1, 2, 'CH-Liniennummer (CHLNR)');
    CommonUtils.assertTableHeader(1, 3, 'Status');
    CommonUtils.assertTableHeader(1, 4, 'Teillinientyp');
    CommonUtils.assertTableHeader(1, 5, 'Geschäftsorganisation Konzessionär');
    CommonUtils.assertTableHeader(1, 6, 'SLNID');
    CommonUtils.assertTableHeader(1, 7, 'Gültig von');
    CommonUtils.assertTableHeader(1, 8, 'Gültig bis');
  });

  it('Step-5: Go to page Add new Version', () => {
    LidiUtils.clickOnAddNewSublinesLinieVersion();
    LidiUtils.fillSublineVersionForm(sublineVersion);
    CommonUtils.saveSubline();
    LidiUtils.readSlnidFromForm(sublineVersion);
  });

  it('Step-6: Navigate to Linienverzeichnis', () => {
    CommonUtils.navigateToHome();
    LidiUtils.navigateToLidi();
    cy.contains(breadcrumbTitle);
  });

  it('Step-7: Search for added element on the table and navigate to it', () => {
    cy.intercept('GET', '/line-directory/v1/sublines?**').as('searchSublines');
    cy.get('[data-cy="lidi-sublines"] [data-cy=table-search-chip-input]')
      .clear()
      .type(sublineVersion.swissSublineNumber)
      .type('{enter}');
    cy.wait('@searchSublines');

    cy.get('[data-cy=table-search-chip-input]').eq(1).type(sublineVersion.slnid).type('{enter}');
    cy.wait('@searchSublines');

    cy.get('table')
      .eq(1)
      .find('tbody tr')
      .each(($el) => {
        cy.wrap($el)
          .should('contain.text', sublineVersion.swissSublineNumber)
          .should('contain.text', sublineVersion.slnid);
      });
    cy.get('table')
      .eq(1)
      .find('tbody tr')
      .should('have.length', 1)
      .contains(sublineVersion.slnid)
      .click();
    cy.contains(sublineVersion.swissSublineNumber);
    LidiUtils.assertContainsSublineVersion(sublineVersion);
  });

  it('Step-8: Delete the subline item', () => {
    CommonUtils.deleteItems();
    cy.contains(breadcrumbTitle);
  });

  it('Step-9: Delete the mainline item', () => {
    cy.intercept('GET', '/line-directory/v1/lines?**').as('searchLines');
    cy.get('[data-cy="lidi-lines"] [data-cy=table-search-chip-input]')
      .clear()
      .type(mainline.swissLineNumber)
      .type('{enter}');
    cy.wait('@searchLines');

    cy.get('[data-cy=table-search-chip-input]').eq(0).type(mainline.slnid).type('{enter}');
    cy.wait('@searchLines');

    cy.get('table')
      .eq(0)
      .find('tbody tr')
      .should('have.length', 1)
      .contains(mainline.slnid)
      .click();
    cy.contains(mainline.swissLineNumber);
    CommonUtils.deleteItems();
    cy.contains(breadcrumbTitle);
  });
});
