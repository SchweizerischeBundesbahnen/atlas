import CommonUtils from "./common-utils";
import BodiDependentUtils from "./bodi-dependent-utils";
import {DataCy} from "../data-cy";

export default class SepodiUtils {

  static navigateToServicePoint() {
    CommonUtils.navigateToHomeViaHomeLogo();
    cy.wait(1000);//I haven't found a better way to ensure that the DOM is actually rendered
    cy.get('#service-point-directory').click({ force: true });
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

  static saveTrafficPoint(){
    cy.get(DataCy.SAVE_ITEM).click().then(() =>
    {
      cy.get(DataCy.EDIT).should('exist');
      cy.get(DataCy.CLOSE_DETAIL).should('exist');
    })
  }

  static searchAddedServicePoint(designationOfficial: string){
    CommonUtils.navigateToHomeViaHomeLogo();
    this.navigateToServicePoint();
    cy.get(DataCy.SEPODI_SEARCH_SERVICE_POINT_SELECT + ' input')
      .type(designationOfficial)
      .then(() => {
        cy.intercept('POST', 'service-point-directory/v1/service-points/search').as('searchVersion');
        cy.wait('@searchVersion').its('response.statusCode').should('eq', 200);
        cy.get(DataCy.SEPODI_SEARCH_SERVICE_POINT_SELECT + ' .ng-option').click();
      });
  }

  static searchAndClickAddedTrafficPointOnTheTable(designation: string){
    // table
    cy.get(DataCy.SEPODI_TRAFFIC_POINT_ELEMENTS_TABLE + ' table tbody tr').should('have.length', 1);
    // Click on the item
    cy.contains('td', designation).parents('tr').click({force: true});
  }

  static getServicePointVersion() {
    return {
      designationLong: 'Bern, Wyleregg, the best place in Bern',
      designationOfficial: 'Bern, Wyleregg',
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,
      validFrom: '01.01.2000',
      validTo: '31.12.9999',
      north: '1201099.1',
      east: '2600783.1',
      height: '554.1'
    }
  }

  static getTrafficPointVersion() {
    return {
      designation: 'A',
      designationOperational: '51',
      validFrom: '15.01.2024',
      validTo: '31.12.2098',
      north: '1201099',
      east: '2600783',
      length: '15',
      boardingAreaHeight: '22',
      compassDirection:'266'
    }
  }

}
