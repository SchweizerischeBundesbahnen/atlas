import CommonUtils from "./common-utils";
import BodiDependentUtils from "./bodi-dependent-utils";
import {DataCy} from "../data-cy";

export default class SepodiUtils {
  static navigateToServicePoint() {
    CommonUtils.navigateToHomeViaHomeLogo();
    cy.get('#service-point-directory').click();
  }

  static saveServicePoint(){
    cy.get(DataCy.SAVE_ITEM).click().then(() =>
    {
      cy.intercept('GET', 'service-point-directory/v1/service-points/85*').as('saveAndGetVersion');
      cy.wait('@saveAndGetVersion').its('response.statusCode').should('eq', 200);
      cy.get(DataCy.EDIT).should('exist');
      cy.get(DataCy.CLOSE_DETAIL).should('exist');
      cy.get(DataCy.REVOKE_ITEM).should('exist');
      cy.get(DataCy.SKIP_WORKFLOW).should('exist');
    })
  }


  static getServicePointVersion() {
    return {
      designationLong: 'Bern, Wyleregg, the best place in Bern',
      designationOfficial: 'Bern, Wyleregg',
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,validFrom: '01.01.2000',
      validTo: '31.12.9999',
      north: '1201099.1',
      east: '2600783.1',
      height: '554.1'
    }
  }

}
