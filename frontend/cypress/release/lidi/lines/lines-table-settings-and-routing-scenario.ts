import CommonUtils from '../../../support/util/common-utils';
import { DataCy } from '../../../support/data-cy';
import LidiUtils from '../../../support/util/lidi-utils';

describe('Lines: TableSettings and Routing', () => {
  const minimalLine1 = LidiUtils.getFirstMinimalLineVersion();
  const minimalLine2 = LidiUtils.getSecondMinimalLineVersion();

  const firstValidDate = '01.01.1700';
  const statusAktiv = 'Aktiv';

  const lineDirectoryUrlPath = '/line-directory/lines';
  const lineDirectoryUrlPathToIntercept = '/line-directory/v1/lines?**';

  function deleteFirstFoundLineInTable() {
    CommonUtils.clickFirstRowInTable(DataCy.LIDI_LINES);

    CommonUtils.deleteItem();
    cy.url().should('eq', Cypress.config().baseUrl + lineDirectoryUrlPath);
  }

  function assertAllTableFiltersAreFilled() {
    cy.get(DataCy.TABLE_SEARCH_STRINGS).contains(minimalLine1.swissLineNumber);
    CommonUtils.assertItemsFromDropdownAreChecked(DataCy.TABLE_SEARCH_STATUS_INPUT, [statusAktiv]);

    CommonUtils.assertItemsFromDropdownAreChecked(DataCy.TABLE_SEARCH_LINE_TYPE, [
      minimalLine1.type
    ]);
    CommonUtils.assertDatePickerIs(DataCy.TABLE_SEARCH_DATE_INPUT, firstValidDate);

    // Check that the table contains 1 result
    CommonUtils.assertNumberOfTableRows(DataCy.LIDI_LINES, 1);
  }

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to Lines', () => {
    LidiUtils.navigateToLines();
  });

  it('Step-3: Add new line', () => {
    LidiUtils.clickOnAddNewLineVersion();
    LidiUtils.fillLineVersionForm(minimalLine1);
    CommonUtils.saveLine();
    CommonUtils.fromDetailBackToLinesOverview();
  });

  it('Step-4: Add another line', () => {
    LidiUtils.clickOnAddNewLineVersion();
    LidiUtils.fillLineVersionForm(minimalLine2);
    CommonUtils.saveLine();
    CommonUtils.fromDetailBackToLinesOverview();
  });

  it('Step-5: Look for line minimal1', () => {
    CommonUtils.typeSearchInput(
      lineDirectoryUrlPathToIntercept,
      DataCy.TABLE_SEARCH_CHIP_INPUT,
      minimalLine1.swissLineNumber
    );

    CommonUtils.selectItemFromDropdownSearchItem(DataCy.TABLE_SEARCH_STATUS_INPUT, statusAktiv);
    CommonUtils.selectItemFromDropdownSearchItem(DataCy.TABLE_SEARCH_LINE_TYPE, minimalLine1.type);

    CommonUtils.typeSearchInput(
      lineDirectoryUrlPathToIntercept,
      DataCy.TABLE_SEARCH_DATE_INPUT,
      firstValidDate
    );

    assertAllTableFiltersAreFilled();
  });

  it('Step-6: Click on add new Line Button and come back without actually creating it', () => {
    LidiUtils.clickOnAddNewLineVersion();
    CommonUtils.clickCancelOnDetailViewBackToLines();

    assertAllTableFiltersAreFilled();
  });

  it('Step-7: Change CHLNR of line from minimal1 to minimal1-changed', () => {
    CommonUtils.assertNumberOfTableRows(DataCy.LIDI_LINES, 1);
    CommonUtils.clickFirstRowInTable(DataCy.LIDI_LINES);

    cy.get(DataCy.EDIT_ITEM).click();
    const newCHLNR = 'minimal1-changed';
    cy.get(DataCy.SWISS_LINE_NUMBER).clear().type(newCHLNR, { force: true });
    CommonUtils.saveLine();

    cy.intercept('GET', lineDirectoryUrlPathToIntercept).as('getLines');
    CommonUtils.fromDetailBackToLinesOverview();
    cy.wait('@getLines');

    // Search still present after edit
    assertAllTableFiltersAreFilled();
    // Change is already visible in table
    cy.get(DataCy.LIDI_LINES + ' .mat-row > .cdk-column-swissLineNumber').contains(newCHLNR);
  });

  it('Step-8: Delete Line minimal1', () => {
    deleteFirstFoundLineInTable();

    // Search still present after delete
    assertAllTableFiltersAreFilled();
    // No more items found
    CommonUtils.assertNoItemsInTable(DataCy.LIDI_LINES);
  });

  it('Step-9: Cleanup other Line', () => {
    // Get rid of search filter by reload
    CommonUtils.visit('/line-directory/lines');

    // Find other created item to clean up
    CommonUtils.typeSearchInput(
      lineDirectoryUrlPathToIntercept,
      DataCy.TABLE_SEARCH_CHIP_INPUT,
      minimalLine2.swissLineNumber
    );

    // Check that the table contains 1 result
    CommonUtils.assertNumberOfTableRows(DataCy.LIDI_LINES, 1);

    deleteFirstFoundLineInTable();

    // Search still present after delete
    cy.get(DataCy.TABLE_SEARCH_STRINGS).contains(minimalLine2.swissLineNumber);
    // No more items found
    CommonUtils.assertNoItemsInTable(DataCy.LIDI_LINES);
  });
});
