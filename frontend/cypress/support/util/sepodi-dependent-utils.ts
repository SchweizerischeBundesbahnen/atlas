import BodiDependentUtils from "./bodi-dependent-utils";

export type SePoDependentInfo = {
  designationOfficial: string,
  number: number,
  sboid: string,
  trafficPointSloids: string[]
}

export default class SePoDiDependentUtils {

  static createDependentStopPointWithTrafficPoint(stopPointDesignationOfficial: string): Promise<SePoDependentInfo> {
    return SePoDiDependentUtils.createDependentStopPoint(stopPointDesignationOfficial).then(stopPointInfo => {
      return cy.request({
        method: 'GET',
        failOnStatusCode: false,
        url: Cypress.env('API_URL') + '/service-point-directory/v1/traffic-point-elements?servicePointNumbers=' + stopPointInfo.number,
        headers: {
          Authorization: `Bearer ${window.sessionStorage.getItem('access_token')}`
        },
      }).then((response) => {
        const stopPointInfoWithTrafficPoint = stopPointInfo;
        if (response.body.totalCount > 0) {
          stopPointInfoWithTrafficPoint.trafficPointSloids.push(response.body.objects[0].sloid);
          return stopPointInfoWithTrafficPoint;
        } else {
          cy.request({
            method: 'POST',
            failOnStatusCode: false,
            url: Cypress.env('API_URL') + '/service-point-directory/v1/traffic-point-elements',
            body: SePoDiDependentUtils.getDependentTrafficPointElement(stopPointInfo.number),
            headers: {
              Authorization: `Bearer ${window.sessionStorage.getItem('access_token')}`
            },
          }).then((response) => {
            expect(response).property('status').to.equal(201);
            stopPointInfoWithTrafficPoint.trafficPointSloids.push(response.body.sloid);
            return stopPointInfoWithTrafficPoint;
          });
        }
      });
    });
  }

  static createDependentStopPoint(designationOfficial: string): Promise<SePoDependentInfo> {
    return BodiDependentUtils.createDependentBusinessOrganisation().then(sboid => {
      return cy.request({
        method: 'POST',
        failOnStatusCode: false,
        url: Cypress.env('API_URL') + '/service-point-directory/v1/service-points',
        body: SePoDiDependentUtils.getDependentStopPoint(designationOfficial),
        headers: {
          Authorization: `Bearer ${window.sessionStorage.getItem('access_token')}`
        },
      }).then((response) => {
        if (response.status === 409) {
          const number = response.body.details[0].displayInfo.parameters.filter((parameter: {
            key: string;
          }) => parameter.key == "number")[0].value;

          return {
            designationOfficial: designationOfficial,
            number: number,
            sboid: sboid,
            trafficPointSloids: []
          };
        } else {
          expect(response).property('status').to.equal(201);

          return {
            designationOfficial: designationOfficial,
            number: response.body.number.number,
            sboid: sboid,
            trafficPointSloids: []
          };
        }
      });
    });
  }

  private static getDependentStopPoint(designationOfficial: string) {
    return {
      country: "SWITZERLAND",
      designationOfficial: designationOfficial,
      businessOrganisation: BodiDependentUtils.getDependentBusinessOrganisationSboid(),
      meansOfTransport: ["TRAIN"],
      validFrom: "2000-04-01",
      validTo: "9999-12-31",
      stopPointType: "ORDERLY",
    };
  }

  private static getDependentTrafficPointElement(numberWithoutCheckDigit: number) {
    return {
      designationOperational: "1",
      length: "10",
      boardingAreaHeight: "5",
      compassDirection: "0",
      designation: "1",
      validFrom: "2000-04-01",
      validTo: "9999-12-31",
      trafficPointElementType: "BOARDING_PLATFORM",
      numberWithoutCheckDigit: numberWithoutCheckDigit
    };
  }
}
