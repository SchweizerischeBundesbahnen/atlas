import BodiDependentUtils from "./bodi-dependent-utils";

export default class SePoDiDependentUtils {

  static DEPENDENT_STOP_POINT_DESIGNATION = 'e2e-dependent-stop-point';

  static createDependentStopPointWithTrafficPoint() {
    return SePoDiDependentUtils.createDependentStopPoint().then(() => {
      cy.request({
        method: 'POST',
        failOnStatusCode: false,
        url: Cypress.env('API_URL') + '/service-point-directory/v1/traffic-point-elements',
        body: SePoDiDependentUtils.getDependentTrafficPointElement(),
        headers: {
          Authorization: `Bearer ${window.sessionStorage.getItem('access_token')}`
        },
      }).then((response) => {
        if (response.status === 409) {
          const number = response.body.details[0].displayInfo.parameters.filter((parameter: {
            key: string;
          }) => parameter.key == "trafficPointSloid")[0].value;
          window.sessionStorage.setItem('trafficPointSloid', number);
        } else {
          expect(response).property('status').to.equal(201);
          window.sessionStorage.setItem('trafficPointSloid', response.body.sloid);
        }
      });
    });
  }

  static createDependentStopPoint() {
    return BodiDependentUtils.createDependentBusinessOrganisation().then(() => {
      cy.request({
        method: 'POST',
        failOnStatusCode: false,
        url: Cypress.env('API_URL') + '/service-point-directory/v1/service-points',
        body: SePoDiDependentUtils.getDependentStopPoint(),
        headers: {
          Authorization: `Bearer ${window.sessionStorage.getItem('access_token')}`
        },
      }).then((response) => {
        if (response.status === 409) {
          const number = response.body.details[0].displayInfo.parameters.filter((parameter: {
            key: string;
          }) => parameter.key == "number")[0].value;
          window.sessionStorage.setItem('number', number);
        } else {
          expect(response).property('status').to.equal(201);
          window.sessionStorage.setItem('number', response.body.number);
        }
      });
    });
  }

  private static getDependentStopPoint() {
    return {
      country: "SWITZERLAND",
      designationOfficial: SePoDiDependentUtils.DEPENDENT_STOP_POINT_DESIGNATION,
      businessOrganisation: BodiDependentUtils.getDependentBusinessOrganisationSboid(),
      meansOfTransport: ["TRAIN"],
      validFrom: "2000-04-01",
      validTo: "9999-12-31",
      stopPointType: "ORDERLY",
    };
  }

  static getDependentServicePointNumber() {
    return window.sessionStorage.getItem('number');
  }

  static getDependentTrafficPointSloid() {
    return window.sessionStorage.getItem('trafficPointSloid');
  }

  private static getDependentTrafficPointElement() {
    return {
      designationOperational: "1",
      length: "10",
      boardingAreaHeight: "5",
      compassDirection: "0",
      designation: "1",
      validFrom: "2000-04-01",
      validTo: "9999-12-31",
      trafficPointElementType: "BOARDING_PLATFORM",
      numberWithoutCheckDigit: SePoDiDependentUtils.getDependentServicePointNumber()
    };
  }
}
