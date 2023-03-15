import LidiUtils from '../../../support/util/lidi-utils';
import CommonUtils from '../../../support/util/common-utils';
import BodiDependentUtils from '../../../support/util/bodi-dependent-utils';

describe('Linie', {testIsolation: false}, () => {
  const line = LidiUtils.getFirstLineVersion();

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent BusinessOrganisation Preparation Step', () => {
    BodiDependentUtils.createDependentBusinessOrganisation();
  });

  it('Step-2: Navigate to Linien', () => {
    LidiUtils.navigateToLines();
  });

  it('Step-3: Check the Linienverzeichnis Line Table is visible', () => {
    LidiUtils.checkHeaderTitle();

    CommonUtils.assertTableSearch(0, 0, 'Suche');
    CommonUtils.assertTableSearch(0, 1, 'Geschäftsorganisation');
    CommonUtils.assertTableSearch(0, 2, 'Linientyp');
    CommonUtils.assertTableSearch(0, 3, 'Status');
    CommonUtils.assertTableSearch(0, 4, 'Gültig am');

    CommonUtils.assertTableHeader(0, 0, 'Liniennummer');
    CommonUtils.assertTableHeader(0, 1, 'Linienbezeichnung');
    CommonUtils.assertTableHeader(0, 2, 'CH-Liniennummer (CHLNR)');
    CommonUtils.assertTableHeader(0, 3, 'Linientyp');
    CommonUtils.assertTableHeader(0, 4, 'SLNID');
    CommonUtils.assertTableHeader(0, 5, 'Status');
    CommonUtils.assertTableHeader(0, 6, 'Gültig von');
    CommonUtils.assertTableHeader(0, 7, 'Gültig bis');
  });

  it('PreStep-4: check if line already exists', () => {
    LidiUtils.checkIfLineAlreadyExists(line);
  });

  it('Step-4: Go to page Add new Version', () => {
    LidiUtils.clickOnAddNewLineVersion();
    LidiUtils.fillLineVersionForm(line);
    CommonUtils.saveLine();
    LidiUtils.readSlnidFromForm(line);
  });

  it('Step-5: Navigate to Linien', () => {
    CommonUtils.fromDetailBackToLinesOverview();
    CommonUtils.navigateToHomeViaHomeLogo();
    LidiUtils.navigateToLines();
    LidiUtils.checkHeaderTitle();
  });

  it('Step-6: Search added item in table and navigate to it', () => {
    LidiUtils.searchAndNavigateToLine(line);
    CommonUtils.getTotalRange().should('contain', '01.01.2000').should('contain', '31.12.2000');
  });

  it('Step-7: Delete the item', () => {
    CommonUtils.deleteItem();
    LidiUtils.assertIsOnLines();
    LidiUtils.checkHeaderTitle();
  });

  it('Dependent BusinessOrganisation Cleanup Step', () => {
    BodiDependentUtils.deleteDependentBusinessOrganisation();
  });
});
