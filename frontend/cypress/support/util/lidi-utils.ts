import CommonUtils from './common-utils';
import {DataCy} from "../data-cy";

export default class LidiUtils {

  private static LIDI_LINES_PATH = '/line-directory/lines';
  private static LIDI_SUBLINES_PATH = '/line-directory/sublines';

  static navigateToLines() {
    this.interceptLines('#line-directory');
  }

  static changeLiDiTabToLines() {
    this.interceptLines('a[href="' + LidiUtils.LIDI_LINES_PATH + '"]');
  }

  private static interceptLines(visitSelector: string) {
    cy.intercept('GET', '/line-directory/v1/lines?**').as('getLines');
    cy.get(visitSelector).click();
    cy.wait('@getLines').then((interception) => {
      cy.wrap(interception.response?.statusCode).should('eq', 200);
      cy.url().should('contain', LidiUtils.LIDI_LINES_PATH);
    });
  }

  static navigateToSublines() {
    cy.get('#line-directory').click();
    this.changeLiDiTabToSublines();
  }

  static changeLiDiTabToSublines() {
    cy.intercept('GET', '/line-directory/v1/sublines?**').as('getSublines');
    cy.get('a[href="' + LidiUtils.LIDI_SUBLINES_PATH + '"]').click();
    cy.wait('@getSublines').then((interception) => {
      cy.wrap(interception.response?.statusCode).should('eq', 200);
      cy.url().should('contain', LidiUtils.LIDI_SUBLINES_PATH);
    });
  }

  static checkHeaderTitle() {
    CommonUtils.assertHeaderTitle('Linien und Teillinien');
  }

  static assertSublineTitle() {
    cy.get(DataCy.SUBLINES_TITLE).invoke('text').should('contain', 'Teillinien');
  }

  static readSlnidFromForm(element: { slnid: string }) {
    cy.get(DataCy.SLNID)
    .invoke('val')
    .then((slnid) => (element.slnid = slnid ? slnid.toString() : ''));
  }

  static clickOnAddNewLinieVersion() {
    cy.get(DataCy.NEW_LINE).click();
    cy.get(DataCy.SAVE_ITEM).should('be.disabled');
    cy.get(DataCy.EDIT_ITEM).should('not.exist');
    cy.get(DataCy.DELETE_ITEM).should('not.exist');
    cy.contains('Neue Linie');
  }

  static clickOnAddNewSublinesLinieVersion() {
    cy.get(DataCy.NEW_SUBLINE).click();
    cy.get(DataCy.SAVE_ITEM).should('be.disabled');
    cy.get(DataCy.EDIT_ITEM).should('not.exist');
    cy.get(DataCy.DELETE_ITEM).should('not.exist');
    cy.contains('Neue Teillinie');
  }

  static assertIsOnLines() {
    cy.url().should('contain', LidiUtils.LIDI_LINES_PATH);
    cy.get(DataCy.LIDI_LINES).should('exist');
  }

  static assertIsOnSublines() {
    cy.url().should('contain', LidiUtils.LIDI_SUBLINES_PATH);
    cy.get(DataCy.LIDI_SUBLINES).should('exist');
  }

  static navigateToSubline(sublineVersion: any) {
    const itemToDeleteUrl = LidiUtils.LIDI_SUBLINES_PATH + '/' + sublineVersion.slnid;
    cy.visit({url: itemToDeleteUrl, method: 'GET'});
  }

  static navigateToLine(mainline: any) {
    const itemToDeleteUrl = LidiUtils.LIDI_LINES_PATH + '/' + mainline.slnid;
    cy.visit({url: itemToDeleteUrl, method: 'GET'});
  }

  static fillLineVersionForm(version: any) {
    // workaround for disabled input field error (https://github.com/cypress-io/cypress/issues/5830)
    cy.get(DataCy.VALID_FROM).clear().type(version.validFrom);
    cy.get(DataCy.VALID_TO).clear().type(version.validTo, {force: true});
    cy.get(DataCy.SWISS_LINE_NUMBER).clear().type(version.swissLineNumber, {force: true});
    cy.get(DataCy.BUSINESS_ORGANISATION).clear().type(version.businessOrganisation);
    CommonUtils.selectItemFromDropDown(DataCy.TYPE, version.type);
    CommonUtils.selectItemFromDropDown(DataCy.PAYMENT_TYPE, version.paymentType);
    cy.get('[data-cy=colorFontRgb] [data-cy=rgb-picker-input]')
    .type('{selectall}' + version.colorFontRgb, {force: true})
    .type('{selectall}' + version.colorFontRgb);
    cy.get('[data-cy=colorBackRgb] [data-cy=rgb-picker-input]').type(
      '{selectall}' + version.colorBackRgb
    );
    cy.get('[data-cy=colorFontCmyk] [data-cy=cmyk-picker-input]').type(
      '{selectall}' + version.colorFontCmyk
    );
    cy.get('[data-cy=colorBackCmyk] [data-cy=cmyk-picker-input]').type(
      '{selectall}' + version.colorBackCmyk
    );
    cy.get(DataCy.DESCRIPTION).clear().type(version.description);
    cy.get(DataCy.NUMBER).clear().type(version.number);
    cy.get(DataCy.ALTERNATIVE_NAME).clear().type(version.alternativeName);
    cy.get(DataCy.COMBINATION_NAME).clear().type(version.combinationName);
    cy.get(DataCy.LONG_NAME).clear().type(version.longName);
    cy.get(DataCy.ICON).clear().type(version.icon);
    cy.get(DataCy.COMMENT).clear().type(version.comment);
    cy.get(DataCy.SAVE_ITEM).should('not.be.disabled');
  }

  static typeAndSelectItemFromDropDown(selector: string, value: string) {
    cy.get(selector).type(value).wait(1000).type('{enter}');
  }

  static searchAndNavigateToLine(line: any) {
    const pathToIntercept = '/line-directory/v1/lines?**';

    CommonUtils.typeSearchInput(
      pathToIntercept,
      '[data-cy="lidi-lines"] [data-cy="table-search-chip-input"]',
      line.swissLineNumber
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      '[data-cy="lidi-lines"] [data-cy="table-search-chip-input"]',
      line.slnid
    );

    CommonUtils.selectItemFromDropdownSearchItem(
      '[data-cy="lidi-lines"] [data-cy="table-search-status-input"]',
      'Aktiv'
    );

    CommonUtils.selectItemFromDropdownSearchItem(
      '[data-cy="lidi-lines"] [data-cy="table-search-line-type"]',
      line.type
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      '[data-cy="lidi-lines"] [data-cy="table-search-date-input"]',
      line.validTo
    );
    // Check that the table contains 1 result
    cy.get('[data-cy="lidi-lines"] table tbody tr').should('have.length', 1);
    // Click on the item
    cy.contains('td', line.swissLineNumber).parents('tr').click({force: true});
    this.assertContainsLineVersion(line);
  }

  static assertContainsLineVersion(version: any) {
    CommonUtils.assertItemValue(DataCy.VALID_FROM, version.validFrom);
    CommonUtils.assertItemValue(DataCy.VALID_TO, version.validTo);
    CommonUtils.assertItemValue(DataCy.SWISS_LINE_NUMBER, version.swissLineNumber);
    CommonUtils.assertItemValue(DataCy.BUSINESS_ORGANISATION, version.businessOrganisation);
    CommonUtils.assertItemText(
      '[data-cy=type] .mat-select-value-text > .mat-select-min-line',
      version.type
    );
    CommonUtils.assertItemText(
      '[data-cy=paymentType] .mat-select-value-text > .mat-select-min-line',
      version.paymentType
    );
    CommonUtils.assertItemValue(
      '[data-cy=colorFontRgb] > .mat-form-field > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-infix > [data-cy=rgb-picker-input]',
      version.colorFontRgb
    );
    CommonUtils.assertItemValue(
      '[data-cy=colorFontRgb] > .mat-form-field > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-infix > [data-cy=rgb-picker-input]',
      version.colorBackRgb
    );
    CommonUtils.assertItemValue(
      '[data-cy=colorFontCmyk] > .mat-form-field > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-infix > [data-cy=cmyk-picker-input]',
      version.colorFontCmyk
    );
    CommonUtils.assertItemValue(
      '[data-cy=colorBackCmyk] > .mat-form-field > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-infix > [data-cy=cmyk-picker-input]',
      version.colorBackCmyk
    );
    CommonUtils.assertItemValue(DataCy.DESCRIPTION, version.description);
    CommonUtils.assertItemValue(DataCy.NUMBER, version.number);
    CommonUtils.assertItemValue(DataCy.ALTERNATIVE_NAME, version.alternativeName);
    CommonUtils.assertItemValue(DataCy.COMBINATION_NAME, version.combinationName);
    CommonUtils.assertItemValue(DataCy.LONG_NAME, version.longName);
    CommonUtils.assertItemValue(DataCy.ICON, version.icon);
    CommonUtils.assertItemValue(DataCy.COMMENT, version.comment);

    cy.get(DataCy.EDIT_ITEM).should('not.be.disabled');
  }

  static addMainLine() {
    const mainline = LidiUtils.getMainLineVersion();
    LidiUtils.navigateToLines();
    LidiUtils.clickOnAddNewLinieVersion();
    LidiUtils.fillLineVersionForm(mainline);
    CommonUtils.saveLine();
    LidiUtils.readSlnidFromForm(mainline);
    LidiUtils.assertContainsLineVersion(mainline);
    CommonUtils.fromDetailBackToOverview();
    CommonUtils.navigateToHome();
    return mainline;
  }

  static getMainLineVersion() {
    return {
      slnid: '',
      validFrom: '01.01.2000',
      validTo: '31.12.2002',
      swissLineNumber: 'b0.IC2',
      businessOrganisation: 'SBB',
      type: 'Betrieblich',
      paymentType: 'International',
      colorFontRgb: '#FFFFFF',
      colorBackRgb: '#FFFFFF',
      colorFontCmyk: '10,10,0,100',
      colorBackCmyk: '10,10,0,100',
      description: 'Mainline for sublines',
      number: 'IC2',
      alternativeName: 'IC2 alt',
      combinationName: 'IC2 comb',
      longName:
        'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z',
      icon: 'https://en.wikipedia.org/wiki/File:Icon_train.svg',
      comment: 'Kommentar',
    };
  }

  static getFirstLineVersion() {
    return {
      slnid: '',
      validFrom: '01.01.2000',
      validTo: '31.12.2000',
      swissLineNumber: 'b0.IC2',
      businessOrganisation: 'SBB',
      type: 'Betrieblich',
      paymentType: 'International',
      colorFontRgb: '#FFFFFF',
      colorBackRgb: '#FFFFFF',
      colorFontCmyk: '10,10,0,100',
      colorBackCmyk: '10,10,0,100',
      description: 'Lorem Ipus Linie',
      number: 'IC2',
      alternativeName: 'IC2 alt',
      combinationName: 'IC2 comb',
      longName:
        'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z',
      icon: 'https://en.wikipedia.org/wiki/File:Icon_train.svg',
      comment: 'Kommentar',
    };
  }

  static getSecondLineVersion() {
    return {
      validFrom: '01.01.2001',
      validTo: '31.12.2001',
      swissLineNumber: 'b0.IC2',
      businessOrganisation: 'SBB-1',
      type: 'Betrieblich',
      paymentType: 'International',
      colorFontRgb: '#FFFFFF',
      colorBackRgb: '#FFFFFF',
      colorFontCmyk: '10,10,0,100',
      colorBackCmyk: '10,10,0,100',
      description: 'Lorem Ipus Linie',
      number: 'IC2',
      alternativeName: 'IC2 alt',
      combinationName: 'IC2 comb',
      longName:
        'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z',
      icon: 'https://en.wikipedia.org/wiki/File:Icon_train.svg',
      comment: 'Kommentar-1',
    };
  }

  static getThirdLineVersion() {
    return {
      validFrom: '01.01.2002',
      validTo: '31.12.2002',
      swissLineNumber: 'b0.IC2',
      businessOrganisation: 'SBB-2',
      type: 'Betrieblich',
      paymentType: 'International',
      colorFontRgb: '#FFFFFF',
      colorBackRgb: '#FFFFFF',
      colorFontCmyk: '10,10,0,100',
      colorBackCmyk: '10,10,0,100',
      description: 'Lorem Ipus Linie',
      number: 'IC2',
      alternativeName: 'IC2 alt',
      combinationName: 'IC2 comb',
      longName:
        'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z',
      icon: 'https://en.wikipedia.org/wiki/File:Icon_train.svg',
      comment: 'Kommentar-2',
    };
  }

  static getEditedLineVersion() {
    return {
      validFrom: '01.06.2000',
      validTo: '01.06.2002',
      alternativeName: 'IC2 alt edit',
    };
  }

  static fillSublineVersionForm(version: any) {
    // workaround for disabled input field error with (https://github.com/cypress-io/cypress/issues/5830)
    cy.get(DataCy.VALID_FROM).clear().type(version.validFrom);
    cy.get(DataCy.VALID_TO).clear().type(version.validTo, {force: true});
    cy.get(DataCy.SWISS_SUBLINE_NUMBER)
    .clear()
    .type(version.swissSublineNumber, {force: true});
    this.typeAndSelectItemFromDropDown(DataCy.MAINLINE_SLNID, version.mainlineSlnid);
    cy.get(DataCy.BUSINESS_ORGANISATION).clear().type(version.businessOrganisation);
    CommonUtils.selectItemFromDropDown(DataCy.TYPE, version.type);
    CommonUtils.selectItemFromDropDown(DataCy.PAYMENT_TYPE, version.paymentType);
    cy.get(DataCy.DESCRIPTION).clear().type(version.description, {force: true});
    cy.get(DataCy.NUMBER).clear().type(version.number);
    cy.get(DataCy.LONG_NAME).clear().type(version.longName);
    cy.get(DataCy.SAVE_ITEM).should('not.be.disabled');
  }

  static assertContainsSublineVersion(version: any) {
    CommonUtils.assertItemValue(DataCy.VALID_FROM, version.validFrom);
    CommonUtils.assertItemValue(DataCy.VALID_TO, version.validTo);
    CommonUtils.assertItemValue(DataCy.SWISS_SUBLINE_NUMBER, version.swissSublineNumber);
    cy.get(DataCy.MAINLINE_SLNID).should('contain.text', version.mainlineSlnid);
    CommonUtils.assertItemValue(DataCy.BUSINESS_ORGANISATION, version.businessOrganisation);
    CommonUtils.assertItemText(
      '[data-cy=type] .mat-select-value-text > .mat-select-min-line',
      version.type
    );
    CommonUtils.assertItemText(
      '[data-cy=paymentType] .mat-select-value-text > .mat-select-min-line',
      version.paymentType
    );
    CommonUtils.assertItemValue(DataCy.DESCRIPTION, version.description);
    CommonUtils.assertItemValue(DataCy.NUMBER, version.number);
    CommonUtils.assertItemValue(DataCy.LONG_NAME, version.longName);

    cy.get(DataCy.EDIT_ITEM).should('not.be.disabled');
  }

  static getFirstSublineVersion() {
    return {
      slnid: '',
      validFrom: '01.01.2000',
      validTo: '31.12.2000',
      swissSublineNumber: 'b0.IC233',
      mainlineSlnid: 'b0.IC2',
      businessOrganisation: 'SBB-2',
      type: 'Technisch',
      paymentType: 'International',
      description: 'Lorem Ipus Linie',
      number: 'IC2',
      longName:
        'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z',
    };
  }

  static getSecondSublineVersion() {
    return {
      validFrom: '01.01.2002',
      validTo: '31.12.2002',
      swissSublineNumber: 'b0.IC233',
      mainlineSlnid: 'b0.IC2',
      businessOrganisation: 'SBB-2-update',
      type: 'Technisch',
      paymentType: 'International',
      description: 'Lorem Ipus Linie',
      number: 'IC2-update',
      longName:
        'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z',
    };
  }

  static getEditedFirstSublineVersion() {
    return {
      validFrom: '01.01.2000',
      validTo: '01.06.2002',
      number: 'IC2-Edit',
      longName:
        'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z - Edit',
    };
  }
}
