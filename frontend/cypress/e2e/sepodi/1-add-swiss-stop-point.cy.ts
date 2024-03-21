import SepodiUtils from "../../support/util/sepodi-utils";
import BodiDependentUtils from "../../support/util/bodi-dependent-utils";
import {DataCy} from "../../support/data-cy";
import CommonUtils from "../../support/util/common-utils";

describe('SePoDi use cases', {testIsolation: false}, () => {


  const neueDienststelleText = 'Neue Dienststelle';

  const servicePoint = SepodiUtils.getServicePointVersion();
  const trafficPoint = SepodiUtils.getTrafficPointVersion();

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent BusinessOrganisation Preparation Step', () => {
    BodiDependentUtils.createDependentBusinessOrganisation();
  });

  describe('Use case 1: Add a swiss stopPoint ', () => {

    it('Step-2: Navigate to Dienststellen', () => {
      SepodiUtils.navigateToServicePoint();
    });

    it('Step-3: Click on Neue Dienststelle', () => {
      cy.get(DataCy.SEPODI_SEARCH_FORM).should('exist');
      cy.get(DataCy.SEPODI_NEW_SERVICE_POINT_BUTTON).should('contain.text', neueDienststelleText).click();
      cy.get(DataCy.SEPODI_NEW_SERVICE_POINT_LABEL).should('contain.text', neueDienststelleText);
    });

    it('Step-4: Fill form and save', () => {
      CommonUtils.selectItemFromDropDown(DataCy.SEPODI_SELECT_COUNTRY, '85 - Schweiz');
      CommonUtils.getClearType(DataCy.SEPODI_DESIGNATION_OFFICIAL, servicePoint.designationOfficial);
      CommonUtils.getClearType(DataCy.SEPODI_DESIGNATION_LONG, servicePoint.designationLong);
      CommonUtils.getClearType(DataCy.VALID_FROM, servicePoint.validFrom, true);
      CommonUtils.getClearType(DataCy.VALID_TO, servicePoint.validTo, true);
      CommonUtils.typeAndSelectItemFromDropDown(
        DataCy.BUSINESS_ORGANISATION + ' ' + 'input',
        servicePoint.businessOrganisation
      );
      cy.get('[type="radio"]').check('STOP_POINT');
      cy.get(DataCy.SEPODI_CHECKBOX_STOP_POINT).check();
      cy.get(DataCy.SEPODI_CHECKBOX_STOP_POINT).should('be.checked');
      cy.get(DataCy.MEANS_OF_TRANSPORT_TRAIN).click();
      CommonUtils.getClearType(DataCy.SEPODI_GEOLOCATION_NORTH, servicePoint.north);
      CommonUtils.selectItemFromDropDown(DataCy.SEPODI_STOP_POINT_TYPE_SELECTION, 'Ordentliche Haltestelle');
      cy.get(DataCy.SEPODI_STOP_POINT_TYPE_SELECTION).type('{esc}')
      CommonUtils.getClearType(DataCy.SEPODI_GEOLOCATION_EAST, servicePoint.east);
      CommonUtils.selectItemFromDropDown(DataCy.SEPODI_CATEGORIES, 'Billettautomat SBB');
      cy.get(DataCy.SEPODI_CATEGORIES).type('{esc}');
      SepodiUtils.saveServicePoint();
    });

    it('Step-5: Search added StopPoint', () => {
      SepodiUtils.searchAddedServicePoint(servicePoint.designationOfficial);
    });

    it('Step-6: Validate added StopPoint', () => {
      CommonUtils.getTotalRange().should('contain', servicePoint.validFrom).should('contain', servicePoint.validTo);
      CommonUtils.assertVersionRange(1, servicePoint.validFrom, servicePoint.validTo);
      CommonUtils.assertVersionStatus(1, 'Entwurf');
      CommonUtils.assertItemValue(DataCy.SEPODI_DESIGNATION_OFFICIAL, servicePoint.designationOfficial);
      CommonUtils.assertItemValue(DataCy.SEPODI_DESIGNATION_LONG, servicePoint.designationLong);
      cy.get(DataCy.BUSINESS_ORGANISATION).should('contain.text', servicePoint.businessOrganisation);
      CommonUtils.assertItemValue(DataCy.VALID_FROM, servicePoint.validFrom);
      CommonUtils.assertItemValue(DataCy.VALID_TO, servicePoint.validTo);
      cy.get(DataCy.SEPODI_CHECKBOX_STOP_POINT).should('be.checked');
      cy.get(DataCy.SEPODI_STOP_POINT_TYPE_SELECTION + ' span').should('contain.text', 'Ordentliche Haltestelle')
      CommonUtils.assertItemValue(DataCy.SEPODI_GEOLOCATION_EAST, servicePoint.east);
      CommonUtils.assertItemValue(DataCy.SEPODI_GEOLOCATION_NORTH, servicePoint.north);
      CommonUtils.assertItemsFromDropdownAreChecked(DataCy.SEPODI_CATEGORIES, ['Billettautomat SBB'])
    });

  });

  describe('Use case 2: change geolocation', () => {

    it('Step-1: Search added StopPoint', () => {
      SepodiUtils.searchAddedServicePoint(servicePoint.designationOfficial);
    });

    it('Step-2: update geografie', () => {
      const newNorthGeolocation = '1201099';
      const newEastGeolocation = '2600783';
      cy.get(DataCy.EDIT).click();
      CommonUtils.getClearType(DataCy.SEPODI_GEOLOCATION_NORTH, newNorthGeolocation);
      cy.intercept('GET', 'service-point-directory/v1/geodata/reverse-geocode?east*').as('getGeodata');
      CommonUtils.getClearType(DataCy.SEPODI_GEOLOCATION_EAST, newEastGeolocation);
      cy.wait('@getGeodata').its('response.statusCode').should('eq', 200);
      SepodiUtils.saveServicePoint();
      CommonUtils.assertItemValue(DataCy.SEPODI_GEOLOCATION_EAST, newEastGeolocation);
      CommonUtils.assertItemValue(DataCy.SEPODI_GEOLOCATION_NORTH, newNorthGeolocation);
    });

  });

  describe('Use case 3: StopPoint cancellation/termination', () => {

    it('Step-1: Search added StopPoint', () => {
      SepodiUtils.searchAddedServicePoint(servicePoint.designationOfficial);
    });

    it('Step-2: Terminate StopPoint', () => {
      cy.get(DataCy.EDIT).click();
      const terminationDate = '31.12.2098';
      CommonUtils.getClearType(DataCy.VALID_TO, terminationDate, true);
      SepodiUtils.saveServicePoint();
      CommonUtils.assertItemValue(DataCy.VALID_TO, terminationDate);
      CommonUtils.assertVersionRange(1, servicePoint.validFrom, terminationDate);
      CommonUtils.getTotalRange().should('contain', servicePoint.validFrom).should('contain', terminationDate);
    });

  });

  describe('Use case 4: create TrafficPoint', () => {

    it('Step-1: Search added StopPoint', () => {
      SepodiUtils.searchAddedServicePoint(servicePoint.designationOfficial);
    });

    it('Step-2: navigate to Haltekante', () => {
      cy.get(DataCy.SEPODI_TRAFFIC_POINT_TAB).should('exist').click();
      cy.get(DataCy.SEPODI_NEW_TRAFFIC_POINT_BUTTON).should('exist').click();
      cy.get(DataCy.SEPODI_TRAFFIC_POINT_HEADER).should('contain.text', 'Haltekante / Gleis');
      cy.get(DataCy.SEPODI_TRAFFIC_POINT_HEADER_TITLE).should('contain.text', 'Haltestellenname Bern, Wyleregg');
    });

    it('Step-3: fill TrafficPoint form and save ', () => {
      CommonUtils.getClearType(DataCy.VALID_FROM, trafficPoint.validFrom, true);
      CommonUtils.getClearType(DataCy.VALID_TO, trafficPoint.validTo, true);
      CommonUtils.getClearType(DataCy.SEPODI_TRAFFIC_POINT_DESIGNATION, trafficPoint.designation);
      CommonUtils.getClearType(DataCy.SEPODI_TRAFFIC_POINT_DESIGNATION_OPERATIONAL, trafficPoint.designationOperational);
      CommonUtils.getClearType(DataCy.SEPODI_GEOLOCATION_EAST, trafficPoint.east);
      CommonUtils.getClearType(DataCy.SEPODI_GEOLOCATION_NORTH, trafficPoint.north);
      CommonUtils.getClearType(DataCy.SEPODI_TRAFFIC_POINT_LENGTH, trafficPoint.length);
      CommonUtils.getClearType(DataCy.SEPODI_TRAFFIC_POINT_BOARDING_AREA_LENGTH, trafficPoint.boardingAreaHeight);
      CommonUtils.getClearType(DataCy.SEPODI_TRAFFIC_POINT_COMPASS_DIRECTION, trafficPoint.compassDirection);
      SepodiUtils.saveTrafficPoint();
    });

    it('Step-4: Validate added TrafficPoint', () => {
      CommonUtils.getTotalRange().should('contain', trafficPoint.validFrom).should('contain', trafficPoint.validTo);
      CommonUtils.assertVersionRange(1, trafficPoint.validFrom, trafficPoint.validTo);
      CommonUtils.assertItemValue(DataCy.SEPODI_TRAFFIC_POINT_DESIGNATION, trafficPoint.designation);
      CommonUtils.assertItemValue(DataCy.SEPODI_GEOLOCATION_EAST, trafficPoint.east);
      CommonUtils.assertItemValue(DataCy.SEPODI_TRAFFIC_POINT_DESIGNATION_OPERATIONAL, trafficPoint.designationOperational);
      CommonUtils.assertItemValue(DataCy.SEPODI_TRAFFIC_POINT_LENGTH, trafficPoint.length);
      CommonUtils.assertItemValue(DataCy.SEPODI_GEOLOCATION_NORTH, trafficPoint.north);
      CommonUtils.assertItemValue(DataCy.SEPODI_TRAFFIC_POINT_BOARDING_AREA_LENGTH, trafficPoint.boardingAreaHeight);
      CommonUtils.assertItemValue(DataCy.SEPODI_TRAFFIC_POINT_COMPASS_DIRECTION, trafficPoint.compassDirection);
    });

  });

  describe('Use case 5: TrafficPoint change geolocation', () => {

    it('Step-1: Search added TrafficPoint', () => {
      SepodiUtils.searchAddedServicePoint(servicePoint.designationOfficial);
      cy.get(DataCy.SEPODI_TRAFFIC_POINT_TAB).should('exist').click();
      SepodiUtils.searchAndClickAddedTrafficPointOnTheTable(trafficPoint.designation)
      cy.get(DataCy.SEPODI_TRAFFIC_POINT_HEADER_TITLE).should('contain.text', 'Haltestellenname Bern, Wyleregg');
      CommonUtils.assertItemValue(DataCy.SEPODI_TRAFFIC_POINT_DESIGNATION, trafficPoint.designation);
    });

    it('Step-2: Update TrafficPoint geolocation', () => {
      cy.get(DataCy.EDIT).click();
      cy.intercept('GET', 'service-point-directory/v1/geodata/reverse-geocode?east*').as('getGeodata');
      cy.get(DataCy.SEPODI_MAP).should('exist').click(360,170);
      cy.wait('@getGeodata').its('response.statusCode').should('eq', 200);
      SepodiUtils.saveTrafficPoint();
    });

  });

  describe('Use case 6: TrafficPoint cancellation/termination', () => {

    it('Step-1: Terminate TrafficPoint', () => {
      cy.get(DataCy.EDIT).click();
      CommonUtils.getClearType(DataCy.VALID_FROM, trafficPoint.validFrom, true);
      CommonUtils.getClearType(DataCy.VALID_TO, '31.12.2050', true);
      SepodiUtils.saveTrafficPoint();
    });

    it('Step-2: Validate TrafficPoint termination', () => {
      CommonUtils.getTotalRange().should('contain', trafficPoint.validFrom).should('contain', '31.12.2050');
      CommonUtils.assertVersionRange(1, trafficPoint.validFrom, '31.12.2050');
    });

  });

});
