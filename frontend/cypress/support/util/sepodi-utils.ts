import CommonUtils from "./common-utils";
import BodiDependentUtils from "./bodi-dependent-utils";

export default class SepodiUtils {
  static navigateToServicePoint() {
    CommonUtils.navigateToHomeViaHomeLogo();
    cy.get('#service-point-directory').click();
  }

  static getServicePointVersion() {
    return {
      designationLong: 'Bern, Wyleregg, the best place in Bern',
      designationOfficial: 'Bern, Wyleregg',
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,validFrom: '01.01.2000',
      validTo: '31.12.9999',
      north: '1201099.0',
      east: '2600783.0',
      height: '555.0'
    }
  }

}
