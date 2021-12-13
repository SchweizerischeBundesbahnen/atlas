import LidiUtils from '../../../support/util/lidi-utils';

describe('Linienverzeichnis', () => {
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
    LidiUtils.assertTableHeader(1, 'CH-Liniennummer (CHLNR)');
    LidiUtils.assertTableHeader(2, 'Liniennummer');
    LidiUtils.assertTableHeader(3, 'Linienbezeichnung');
    LidiUtils.assertTableHeader(4, 'Status');
    LidiUtils.assertTableHeader(5, 'Geschäftsorganisation Konzessionär');
    LidiUtils.assertTableHeader(6, 'SLNID');
    LidiUtils.assertTableHeader(7, 'Gültig von');
    LidiUtils.assertTableHeader(8, 'Gültig bis');
  });

  it('Step-4: Go to page Add new Version', () => {
    LidiUtils.clickOnAddNewLinieVersion();
    LidiUtils.fillLineVersionForm(line);
    LidiUtils.saveVersion();
  });

  it('Step-5: Navigate to Linienverzeichnis', () => {
    LidiUtils.navigateToHome();
    LidiUtils.navigateToLidi();
    cy.contains(headerTitle);
  });

  it('Step-6: Check the added is present on the table result and navigate to it ', () => {
    cy.contains(line.swissLineNumber).parents('tr').click();
    cy.contains(line.swissLineNumber);
    LidiUtils.assertContainsVersion(line);
  });

  it('Step-7: Delete the item ', () => {
    LidiUtils.deleteItems();
    cy.contains(headerTitle);
  });
});
