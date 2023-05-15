import CommonUtils from "./common-utils";
import {DataCy} from "../data-cy";

export default class TthUtils {

  private static TTH_CH_PLANNED_PATH = '/timetable-hearing/ch/';

  static archiveHearingIfAlreadyActive() {
    cy.window().then((win) => {
      const cantonDropdown = win.document.querySelector(DataCy.SELECT_TTH_CANTON_DROPDOWN)
      if (cantonDropdown) {
        CommonUtils.selectItemFromDropDown(DataCy.SELECT_TTH_CANTON_DROPDOWN, ' Gesamtschweiz');
        cy.get(DataCy.TTH_MANAGE_TIMETABLE_HEARING).click();
        cy.get(DataCy.TTH_CLOSE_TTH_YEAR).click();
        cy.get(DataCy.TTH_CLOSE_TTH_TIMETABLE_HEARING).click();
      }
    })
  }

  static navigateToTimetableHearing() {
    CommonUtils.navigateToHomeViaHomeLogo();
    cy.get('#timetable-hearing').click();
  }

  static changeLiDiTabToTTH(hearingStatus: string) {
    cy.intercept('GET', '/line-directory/v1/timetable-hearing/years?statusChoices=' + hearingStatus).as('getRequest');
    cy.get('a[href="' + TthUtils.TTH_CH_PLANNED_PATH + hearingStatus.toLowerCase() + '"]').click();
    cy.wait('@getRequest').then((interception) => {
      cy.wrap(interception.response?.statusCode).should('eq', 200);
    });
  }

}
