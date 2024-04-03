import CommonUtils from "./common-utils";
import {DataCy} from "../data-cy";
import {PrmDataCy} from "../prm-data-cy";
import SepodiUtils from "./sepodi-utils";

export default class PrmUtils {

  static navigateToPrm() {
    CommonUtils.navigateToHomeViaHomeLogo();
    cy.wait(1000);//I haven't found a better way to ensure that the DOM is actually rendered
    cy.get('#prm-directory').click({force: true});
  }

  static searchAndSelect(searchString: string) {
    SepodiUtils.searchServicePoint(searchString);
  }

  static saveItemAndAssertTabs() {
    return cy.get(DataCy.SAVE_ITEM).click().then(() => {
      cy.get(DataCy.EDIT).should('exist');
      cy.get(DataCy.CLOSE_DETAIL).should('exist');

      cy.get(PrmDataCy.TAB_PLATFORMS).should('exist');
      cy.get(PrmDataCy.TAB_CONTACT_POINTS).should('exist');
      cy.get(PrmDataCy.TAB_TOILETS).should('exist');
      cy.get(PrmDataCy.TAB_PARKING_LOTS).should('exist');
    });
  }

  static selectPlatformInTable(sloid: string) {
    // Platform table has length 1
    cy.get(PrmDataCy.PLATFORM_TABLE + ' table tbody tr').should('have.length.greaterThan', 0);
    // Click on the item
    cy.contains('td', sloid).parents('tr').click({force: true});
  }

  static getCompleteStopPoint() {
    return {
      validFrom: "15.01.2024",
      validTo: "31.12.9999",
      meansOfTransport: ["TRAIN"],
      freeText: "Ich bin ein rollstuhlgängiger Test.",
      address: "Wylerstrasse 123",
      zipCode: "3000",
      city: "Bern",
    }
  }

  static getReducedStopPoint() {
    return {
      validFrom: "15.01.2024",
      validTo: "31.12.9999",
      meansOfTransport: ["TRAIN"],
      freeText: "Ich bin ein rollstuhlgängiger Test.",
    }
  }

}
