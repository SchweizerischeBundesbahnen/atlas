import LidiUtils from '../../../support/util/lidi-utils';
import CommonUtils from '../../../support/util/common-utils';
import { DataCy } from '../../../support/data-cy';
import BodiDependentUtils from '../../../support/util/bodi-dependent-utils';

describe('Teillinie', {testIsolation: false}, () => {
  const sublineVersion = LidiUtils.getFirstSublineVersion();
  let mainline: any;

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent BusinessOrganisation Preparation Step', () => {
    BodiDependentUtils.createDependentBusinessOrganisation();
  });

  it('PreStep-2: check if subline and mainline already exists', () => {
    LidiUtils.navigateToSublines();
    LidiUtils.checkIfSublineAlreadyExists(sublineVersion);
    LidiUtils.navigateToLines();
    LidiUtils.checkIfLineAlreadyExists(LidiUtils.getMainLineVersion());
  });

  it('Step-2: Add mainline', () => {
    mainline = LidiUtils.addMainLine();
  });

  it('Step-3: Navigate to Sublines', () => {
    LidiUtils.navigateToSublines();
    LidiUtils.checkHeaderTitle();
    LidiUtils.assertSublineTitle();
  });

  it('Step-4: Check the Linienverzeichnis Line Table is visible', () => {
    CommonUtils.assertTableSearch(0, 0, 'Suche');
    CommonUtils.assertTableSearch(0, 1, 'Geschäftsorganisation');
    CommonUtils.assertTableSearch(0, 2, 'Teillinientyp');
    CommonUtils.assertTableSearch(0, 3, 'Status');
    CommonUtils.assertTableSearch(0, 4, 'Gültig am');

    CommonUtils.assertTableHeader(0, 0, 'Teilliniennummer');
    CommonUtils.assertTableHeader(0, 1, 'Teillinienbezeichnung');
    CommonUtils.assertTableHeader(0, 2, 'CH-Teilliniennummer');
    CommonUtils.assertTableHeader(0, 3, 'Gehört zu Linie (CHLNR)');
    CommonUtils.assertTableHeader(0, 4, 'Teillinientyp');
    CommonUtils.assertTableHeader(0, 5, 'SLNID');
    CommonUtils.assertTableHeader(0, 6, 'Status');
    CommonUtils.assertTableHeader(0, 7, 'Gültig von');
    CommonUtils.assertTableHeader(0, 8, 'Gültig bis');
  });

  it('Step-5: Go to page Add new Version', () => {
    LidiUtils.clickOnAddNewSublineVersion();
    LidiUtils.fillSublineVersionForm(sublineVersion);
    CommonUtils.saveSubline();
    LidiUtils.readSlnidFromForm(sublineVersion);
  });

  it('Step-6: Navigate to Sublines', () => {
    CommonUtils.fromDetailBackToSublinesOverview();
    CommonUtils.navigateToHomeViaHomeLogo();
    LidiUtils.navigateToSublines();
    LidiUtils.checkHeaderTitle();
  });

  it('Step-7: Search for added element on the table and navigate to it', () => {
    const pathToIntercept = '/line-directory/v1/sublines?**';

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_SUBLINES + ' ' + DataCy.TABLE_SEARCH_CHIP_INPUT,
      sublineVersion.swissSublineNumber
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_SUBLINES + ' ' + DataCy.TABLE_SEARCH_CHIP_INPUT,
      sublineVersion.slnid
    );

    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.LIDI_SUBLINES + ' ' + DataCy.TABLE_SEARCH_STATUS_INPUT,
      'Aktiv'
    );

    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.LIDI_SUBLINES + ' ' + DataCy.TABLE_SEARCH_SUBLINE_TYPE,
      sublineVersion.type
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_SUBLINES + ' ' + DataCy.TABLE_SEARCH_DATE_INPUT,
      sublineVersion.validTo
    );
    // Check that the table contains 1 result
    cy.get(DataCy.LIDI_SUBLINES + ' table tbody tr').should('have.length', 1);
    // Click on the item
    cy.contains('td', sublineVersion.swissSublineNumber).parents('tr').click({ force: true });
    CommonUtils.getTotalRange().should('contain', '01.01.2000').should('contain', '31.12.2000');
    LidiUtils.assertContainsSublineVersion(sublineVersion);
  });

  it('Step-8: Delete the subline item', () => {
    CommonUtils.deleteItem();
    LidiUtils.assertIsOnSublines();
  });

  it('Step-9: Navigate to the mainline item', () => {
    LidiUtils.changeLiDiTabToLines();
    LidiUtils.searchAndNavigateToLine(mainline);
    CommonUtils.getTotalRange().should('contain', '01.01.2000').should('contain', '31.12.2002');
  });

  it('Step-10: Delete the mainline item', () => {
    CommonUtils.deleteItem();
    LidiUtils.assertIsOnLines();
  });

  it('Dependent BusinessOrganisation Cleanup Step', () => {
    BodiDependentUtils.deleteDependentBusinessOrganisation();
  });
});
