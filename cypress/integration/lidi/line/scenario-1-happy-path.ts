import LidiUtils from '../../../support/util/lidi-utils';

describe('Linienverzeichnis', () => {
  const linie = LidiUtils.getFirstVersion();

  const headerTitle = 'Linienverzeichnis';

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to Linienverzeichnis', () => {
    LidiUtils.navigateToLidi();
  });

  it('Step-3: Check the Linienverzeichnis Line Table is visible', () => {
    cy.contains(headerTitle);
    LidiUtils.assertLidiTableHeader(1, 'CH-Liniennummer (CHLNR)');
    LidiUtils.assertLidiTableHeader(2, 'Liniennummer');
    LidiUtils.assertLidiTableHeader(3, 'Linienbezeichnung');
    LidiUtils.assertLidiTableHeader(4, 'Status');
    LidiUtils.assertLidiTableHeader(5, 'Geschäftsorganisation Konzessionär');
    LidiUtils.assertLidiTableHeader(6, 'SLNID');
    LidiUtils.assertLidiTableHeader(7, 'Gültig von');
    LidiUtils.assertLidiTableHeader(8, 'Gültig bis');
  });

  it('Step-4: Go to page Add new Version', () => {
    LidiUtils.clickOnAddNewVersion();
    LidiUtils.fillVersionForm(linie);
    LidiUtils.saveVersion();
  });

  it('Step-5: Navigate to Linienverzeichnis', () => {
    LidiUtils.navigateToHome();
    LidiUtils.navigateToLidi();
    cy.contains(headerTitle);
  });

  it('Step-6: Check the added is present on the table result and navigate to it ', () => {
    cy.contains(linie.swissLineNumber).parents('tr').click();
    cy.contains(linie.swissLineNumber);
    LidiUtils.assertContainsVersion(linie);
  });

  it('Step-7: Delete the item ', () => {
    LidiUtils.deleteItems();
    cy.contains(headerTitle);
  });
});
