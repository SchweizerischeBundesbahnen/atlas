import CommonUtils from './common-utils';
import { DataCy } from '../data-cy';

export default class TthUtils {
  private static TTH_CH_PLANNED_PATH = '/timetable-hearing/ch/';

  static archiveHearingIfAlreadyActive() {
    TthUtils.changeTabToTTH('ACTIVE');
    cy.window().then((win) => {
      const cantonDropdown = win.document.querySelector(DataCy.SELECT_TTH_CANTON_DROPDOWN);
      if (cantonDropdown) {
        TthUtils.collectingActionStatusChangeToAccepted();
        CommonUtils.selectItemFromDropDown(DataCy.SELECT_TTH_CANTON_DROPDOWN, ' Gesamtschweiz');
        cy.get(DataCy.TTH_MANAGE_TIMETABLE_HEARING).click();
        cy.get(DataCy.TTH_CLOSE_TTH_YEAR).click();
        cy.get(DataCy.TTH_CLOSE_TTH_TIMETABLE_HEARING).click();
      }
    });
  }

  static collectingActionStatusChangeToAccepted() {
    cy.window().then((win) => {
      const collectingActionDropdown = win.document.querySelector(DataCy.TTH_COLLECT_ACTION_TYPE);
      if (
        collectingActionDropdown &&
        !collectingActionDropdown.getAttributeNode('aria-disabled')!.value
      ) {
        CommonUtils.selectFirstItemFromDropDown(DataCy.TTH_COLLECT_ACTION_TYPE);
        cy.get(DataCy.TTH_TABLE_CHECKBOX_ALL).click();
        CommonUtils.selectItemFromDropDown(DataCy.COLLECT_STATUS_CHANGE_ACTION_TYPE, 'angenommen');
        CommonUtils.getClearType(
          DataCy.STATEMENT_JUSTIFICATION,
          'Campioni in Italia!!!Forza Napoli!!!'
        );
        const updateStatementsPath = 'line-directory/v1/timetable-hearing/statements*';
        cy.intercept('GET', updateStatementsPath).as('updateStatementsPath');
        cy.get(DataCy.DIALOG_CONFIRM_BUTTON).click();
        cy.wait('@updateStatementsPath').its('response.statusCode').should('eq', 200);
      }
    });
  }

  static navigateToTimetableHearing() {
    CommonUtils.navigateToHomeViaHomeLogo();
    cy.get('#timetable-hearing').click();
  }

  static changeTabToTTH(hearingStatus: string) {
    cy.intercept(
      'GET',
      '/line-directory/v1/timetable-hearing/years?statusChoices=' + hearingStatus
    ).as('getRequest');
    cy.get('a[href="' + TthUtils.TTH_CH_PLANNED_PATH + hearingStatus.toLowerCase() + '"]').click();
    cy.wait('@getRequest').then((interception) => {
      cy.wrap(interception.response?.statusCode).should('eq', 200);
    });
  }
}
