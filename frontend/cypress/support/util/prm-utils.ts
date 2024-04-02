import CommonUtils from "./common-utils";
import {DataCy} from "../data-cy";

export default class PrmUtils {

  static navigateToPrm() {
    CommonUtils.navigateToHomeViaHomeLogo();
    cy.wait(1000);//I haven't found a better way to ensure that the DOM is actually rendered
    cy.get('#prm-directory').click({force: true});
  }

  static searchAndSelect(searchString: string) {
    cy.get(DataCy.SEPODI_SEARCH_SERVICE_POINT_SELECT + ' input')
      .type(searchString)
      .then(() => {
        cy.get(DataCy.SEPODI_SEARCH_SERVICE_POINT_SELECT + ' .ng-option')
          .should('contain', searchString).click({force: true});
      });
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
