import TtfnUtils from '../../support/util/ttfn-utils';
import CommonUtils from '../../support/util/common-utils';
import { DataCy } from '../../support/data-cy';
import BodiDependentUtils from '../../support/util/bodi-dependent-utils';

describe('Fahrplanfeldnummer', {testIsolation: false}, () => {
  const firstVersion = TtfnUtils.getFirstVersion();

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent BusinessOrganisation Preparation Step', () => {
    BodiDependentUtils.createDependentBusinessOrganisation();
  });

  it('Step-2: Navigate to Fahrplanfeldnummer', () => {
    TtfnUtils.navigateToTimetableFieldNumber();
    TtfnUtils.checkHeaderTitle();
  });

  it('Step-3: Check the Fahrplanfeldnummer Table is visible', () => {
    CommonUtils.assertTableSearch(0, 0, 'Suche');
    CommonUtils.assertTableSearch(0, 1, 'Geschäftsorganisation');
    CommonUtils.assertTableSearch(0, 2, 'Status');
    CommonUtils.assertTableSearch(0, 3, 'Gültig am');

    CommonUtils.assertTableHeader(0, 0, 'Fahrplanfeldnummer');
    CommonUtils.assertTableHeader(0, 1, 'CH-Fahrplanfeldnummer Bezeichnung');
    CommonUtils.assertTableHeader(0, 2, 'CH-Fahrplanfeldnummer');
    CommonUtils.assertTableHeader(0, 3, 'Fahrplanfeldnummer-ID');
    CommonUtils.assertTableHeader(0, 4, 'Status');
    CommonUtils.assertTableHeader(0, 5, 'Gültig von');
    CommonUtils.assertTableHeader(0, 6, 'Gültig bis');
  });

  it('PreStep-4: check if ttfn already exists', () => {
    TtfnUtils.checkIfTtfnAlreadyExists(firstVersion);
  });

  it('Step-4: Go to page Add new Version', () => {
    TtfnUtils.clickOnAddNewVersion();
    TtfnUtils.fillVersionForm(firstVersion);
    CommonUtils.saveTtfn();
    TtfnUtils.readTtfnidFromForm(firstVersion);
  });

  it('Step-5: Navigate to the Fahrplanfeldnummer', () => {
    CommonUtils.fromDetailBackToTtfnOverview();
    CommonUtils.navigateToHomeViaHomeLogo();
    TtfnUtils.navigateToTimetableFieldNumber();
    TtfnUtils.checkHeaderTitle();
  });

  it('Step-6: search for added item in table and select it', () => {
    const pathToIntercept = '/line-directory/v1/field-numbers?**';

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.TABLE_SEARCH_CHIP_INPUT,
      firstVersion.swissTimetableFieldNumber
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.TABLE_SEARCH_CHIP_INPUT,
      firstVersion.ttfnid
    );

    CommonUtils.selectItemFromDropdownSearchItem(DataCy.TABLE_SEARCH_STATUS_INPUT, 'Aktiv');

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.TABLE_SEARCH_DATE_INPUT,
      firstVersion.validTo
    );

    // Check that the table contains 1 result
    cy.get(DataCy.TTFN + ' table tbody tr').should('have.length', 1);
    // Click on the item
    cy.contains('td', firstVersion.swissTimetableFieldNumber).parents('tr').click({ force: true });
    CommonUtils.getTotalRange().should('contain', '01.01.2000').should('contain', '31.12.2000');
    TtfnUtils.assertContainsVersion(firstVersion);
  });

  it('Step-7: Delete added item', () => {
    CommonUtils.deleteItem();
    cy.url().should('contain', '/timetable-field-number');
    TtfnUtils.checkHeaderTitle();
  });

  it('Dependent BusinessOrganisation Cleanup Step', () => {
    BodiDependentUtils.deleteDependentBusinessOrganisation();
  });
});
