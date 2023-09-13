import CommonUtils from '../../../support/util/common-utils';
import { DataCy } from '../../../support/data-cy';
import LidiUtils from '../../../support/util/lidi-utils';
import BodiDependentUtils from '../../../support/util/bodi-dependent-utils';

describe('Lines: TableSettings and Routing', { testIsolation: false }, () => {
  const minimalLine1 = LidiUtils.getFirstMinimalLineVersion();
  const minimalLine2 = LidiUtils.getSecondMinimalLineVersion();

  const firstValidDate = '01.01.1700';
  const statusEntwurf = 'Entwurf';

  const lineDirectoryUrlPath = '/line-directory/lines';
  const lineDirectoryUrlPathToIntercept = '/line-directory/v1/lines?**';

  function deleteFirstFoundLineInTable() {
    CommonUtils.clickFirstRowInTable(DataCy.LIDI_LINES);

    CommonUtils.deleteItem();
    cy.url().should('eq', Cypress.config().baseUrl + lineDirectoryUrlPath);
  }

  function assertAllTableFiltersAreFilled() {
    cy.get(DataCy.TABLE_FILTER_CHIP_DIV).contains(minimalLine1.swissLineNumber);

    CommonUtils.assertItemsFromDropdownAreChecked(DataCy.TABLE_FILTER_MULTI_SELECT(1, 2), [
      statusEntwurf,
    ]);

    CommonUtils.assertItemsFromDropdownAreChecked(DataCy.TABLE_FILTER_MULTI_SELECT(1, 1), [
      minimalLine1.type,
    ]);
    CommonUtils.assertDatePickerIs(DataCy.TABLE_FILTER_DATE_INPUT(1, 3), firstValidDate);

    // Check that the table contains 1 result
    CommonUtils.assertNumberOfTableRows(DataCy.LIDI_LINES, 1);
  }

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent BusinessOrganisation Preparation Step', () => {
    BodiDependentUtils.createDependentBusinessOrganisation();
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
    // Set all table-filters (for now: except the business-organisation)
    // ---------------------
    CommonUtils.typeSearchInput(
      lineDirectoryUrlPathToIntercept,
      DataCy.TABLE_FILTER_CHIP_INPUT,
      minimalLine1.swissLineNumber
    );

    // Set Status=Entwurf
    CommonUtils.chooseOneValueFromMultiselect(
      DataCy.TABLE_FILTER_MULTI_SELECT(1, 2),
      statusEntwurf
    );

    //
    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.TABLE_FILTER_MULTI_SELECT(1, 1),
      minimalLine1.type
    );

    // Set validOn=firstValidDate
    CommonUtils.typeSearchInput(
      lineDirectoryUrlPathToIntercept,
      DataCy.TABLE_FILTER_DATE_INPUT(1, 3),
      firstValidDate
    );

    // Check that all table-filters are filled
    // ---------------------------------------
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
    cy.get(DataCy.LIDI_LINES + ' .cdk-column-swissLineNumber').contains(newCHLNR);
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
      DataCy.TABLE_FILTER_CHIP_INPUT,
      minimalLine2.swissLineNumber
    );

    // Check that the table contains 1 result
    CommonUtils.assertNumberOfTableRows(DataCy.LIDI_LINES, 1);

    deleteFirstFoundLineInTable();

    // Search still present after delete
    cy.get(DataCy.TABLE_FILTER_CHIP_DIV).contains(minimalLine2.swissLineNumber);
    // No more items found
    CommonUtils.assertNoItemsInTable(DataCy.LIDI_LINES);
  });

  it('Dependent BusinessOrganisation Cleanup Step', () => {
    BodiDependentUtils.deleteDependentBusinessOrganisation();
  });
});
