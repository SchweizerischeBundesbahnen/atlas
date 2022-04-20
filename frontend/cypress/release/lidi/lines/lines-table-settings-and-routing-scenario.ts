import CommonUtils from '../../../support/util/common-utils';
import TtfnUtils from '../../../support/util/ttfn-utils';
import { DataCy } from '../../../support/data-cy';
import LidiUtils from '../../../support/util/lidi-utils';

describe('TTFN: TableSettings and Routing', () => {
  const minimalLine = LidiUtils.getFirstMinimalLineVersion();

  const firstValidDate = '01.01.1700';
  const statusAktiv = 'Aktiv';

  function assertAllTableFiltersAreFilled() {
    cy.get(DataCy.TABLE_SEARCH_STRINGS).contains(ttfnBernThun.swissTimetableFieldNumber);
    CommonUtils.assertItemsFromDropdownAreChecked(DataCy.TABLE_SEARCH_STATUS_INPUT, [statusAktiv]);
    CommonUtils.assertDatePickerIs(DataCy.TABLE_SEARCH_DATE_INPUT, firstValidDate);
    CommonUtils.assertNumberOfTableRows(DataCy.TTFN, 1);
  }

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to Lines', () => {
    LidiUtils.navigateToLines();
  });

  it('Step-3: Add new line', () => {
    LidiUtils.clickOnAddNewLinieVersion();
    LidiUtils.fillLineVersionForm(minimalLine);
    CommonUtils.saveLine();
    CommonUtils.fromDetailBackToLinesOverview();
  });

  it.skip('Step-4: Add another field number', () => {
    TtfnUtils.clickOnAddNewVersion();
    TtfnUtils.fillVersionForm(ttfnBernThun);
    CommonUtils.saveTtfn();
    CommonUtils.fromDetailBackToTtfnOverview();
  });

  it.skip('Step-5: Look for TTFN Bern - Thun', () => {
    CommonUtils.typeSearchInput(
      '/line-directory/v1/field-numbers?**',
      DataCy.TABLE_SEARCH_CHIP_INPUT,
      ttfnBernThun.swissTimetableFieldNumber
    );

    CommonUtils.typeSearchInput(
      '/line-directory/v1/field-numbers?**',
      DataCy.TABLE_SEARCH_DATE_INPUT,
      firstValidDate
    );

    CommonUtils.selectItemFromDropdownSearchItem(DataCy.TABLE_SEARCH_STATUS_INPUT, statusAktiv);

    // Check that the table contains 1 result
    CommonUtils.assertNumberOfTableRows(DataCy.TTFN, 1);

    assertAllTableFiltersAreFilled();
  });

  it.skip('Step-6: Click on add new TTFN Button and come back without actually creating it', () => {
    TtfnUtils.clickOnAddNewVersion();
    CommonUtils.clickCancelOnDetailViewBackToTtfn();

    assertAllTableFiltersAreFilled();
  });

  it.skip('Step-7: Edit Bern-Thun to Bern-Thun-Interlaken', () => {
    CommonUtils.assertNumberOfTableRows(DataCy.TTFN, 1);
    CommonUtils.clickFirstRowInTable(DataCy.TTFN);

    cy.get(DataCy.EDIT_ITEM).click();
    const newDescription = 'Bern - Thun - Interlaken';
    cy.get(DataCy.DESCRIPTION).clear().type(newDescription, { force: true });
    CommonUtils.saveTtfn();

    cy.intercept('GET', '/line-directory/v1/field-numbers?**').as('getTtfns');
    CommonUtils.fromDetailBackToTtfnOverview();
    cy.wait.skip('@getTtfns');

    // Search still present after edit
    assertAllTableFiltersAreFilled();
    // Change is already visible in table
    cy.get(DataCy.TTFN + ' .mat-row > .cdk-column-description').contains(newDescription);
  });

  it.skip('Step-8: Delete Line minimal1', () => {
    CommonUtils.clickFirstRowInTable(DataCy.TTFN);

    CommonUtils.deleteItem();
    cy.url().should('eq', Cypress.config().baseUrl + '/timetable-field-number');

    // Search still present after delete
    assertAllTableFiltersAreFilled();
    // No more items found
    CommonUtils.assertNoItemsInTable(DataCy.TTFN);
  });

  it.skip('Step-9: Cleanup other TTFN', () => {
    // Get rid of search filter by reload
    cy.reload();

    // Find other created item to clean up
    CommonUtils.typeSearchInput(
      '/line-directory/v1/field-numbers?**',
      DataCy.TABLE_SEARCH_CHIP_INPUT,
      churGrenze.swissTimetableFieldNumber
    );

    // Check that the table contains 1 result
    CommonUtils.assertNumberOfTableRows(DataCy.TTFN, 1);

    CommonUtils.clickFirstRowInTable(DataCy.TTFN);

    CommonUtils.deleteItem();
    cy.url().should('eq', Cypress.config().baseUrl + '/timetable-field-number');

    // Search still present after delete
    cy.get(DataCy.TABLE_SEARCH_STRINGS).contains(churGrenze.swissTimetableFieldNumber);
    // No more items found
    CommonUtils.assertNoItemsInTable(DataCy.TTFN);
  });
});
