import LidiUtils from '../../../support/util/lidi-utils';
import CommonUtils from '../../../support/util/common-utils';

describe('Linie', () => {
  const line = LidiUtils.getFirstLineVersion();
  const headerTitle = 'Linienverzeichnis';

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to Linienverzeichnis', () => {
    LidiUtils.navigateToLidi();
  });

  it('Step-3: Check the Linienverzeichnis Line Table is visible', () => {
    cy.contains(headerTitle);
    CommonUtils.assertTableSearch(0, 0, 'Suche');
    CommonUtils.assertTableSearch(0, 1, 'Status');
    CommonUtils.assertTableSearch(0, 2, 'Linientyp');
    CommonUtils.assertTableSearch(0, 3, 'Gültig am');
    CommonUtils.assertTableHeader(0, 0, 'CH-Liniennummer (CHLNR)');
    CommonUtils.assertTableHeader(0, 1, 'Liniennummer');
    CommonUtils.assertTableHeader(0, 2, 'Linienbezeichnung');
    CommonUtils.assertTableHeader(0, 3, 'Status');
    CommonUtils.assertTableHeader(0, 4, 'Linientyp');
    CommonUtils.assertTableHeader(0, 5, 'Geschäftsorganisation Konzessionär');
    CommonUtils.assertTableHeader(0, 6, 'SLNID');
    CommonUtils.assertTableHeader(0, 7, 'Gültig von');
    CommonUtils.assertTableHeader(0, 8, 'Gültig bis');
  });

  it('Step-4: Go to page Add new Version', () => {
    LidiUtils.clickOnAddNewLinieVersion();
    LidiUtils.fillLineVersionForm(line);
    CommonUtils.saveLine();
    LidiUtils.readSlnidFromForm(line);
  });

  it('Step-5: Navigate to Linienverzeichnis', () => {
    CommonUtils.navigateToHome();
    LidiUtils.navigateToLidi();
    cy.contains(headerTitle);
  });

  it('Step-6: Search added item in table and navigate to it', () => {
    cy.intercept('GET', '/line-directory/v1/lines?**').as('searchLines');
    cy.get('[data-cy=table-search-chip-input]')
      .eq(0)
      .clear()
      .type(line.swissLineNumber)
      .type('{enter}');
    cy.wait('@searchLines');
    cy.get('[data-cy=table-search-chip-input]').eq(0).type(line.slnid).type('{enter}');
    cy.wait('@searchLines');
    cy.get('table').get('thead tr th').eq(6).click();
    cy.wait('@searchLines');
    cy.get('table')
      .eq(0)
      .find('tbody tr')
      .each(($el) => {
        cy.wrap($el)
          .should('contain.text', line.swissLineNumber)
          .should('contain.text', line.slnid);
      });
    cy.get('table').eq(0).find('tbody tr').first().click();
    cy.contains(line.swissLineNumber);
    LidiUtils.assertContainsLineVersion(line);
  });

  it('Step-7: Delete the item', () => {
    CommonUtils.deleteItems();
    cy.contains(headerTitle);
  });
});
