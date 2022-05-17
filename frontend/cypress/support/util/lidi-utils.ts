import CommonUtils from './common-utils';
import { DataCy } from '../data-cy';

export default class LidiUtils {
  private static LIDI_LINES_PATH = '/line-directory/lines';
  private static LIDI_SUBLINES_PATH = '/line-directory/sublines';

  static navigateToLines() {
    CommonUtils.navigateToHomeViaHomeLogo();
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
    CommonUtils.navigateToHomeViaHomeLogo();
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
    cy.get(DataCy.DETAIL_SUBHEADING_ID)
      .invoke('text')
      .then((slnid) => (element.slnid = slnid ? slnid.toString() : ''));
  }

  static clickOnAddNewLineVersion() {
    cy.get(DataCy.NEW_LINE).click();
    cy.get(DataCy.SAVE_ITEM).should('be.disabled');
    cy.get(DataCy.EDIT_ITEM).should('not.exist');
    cy.get(DataCy.DELETE_ITEM).should('not.exist');
    cy.contains('Neue Linie');
  }

  static clickOnAddNewSublineVersion() {
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
    cy.visit({ url: itemToDeleteUrl, method: 'GET' });
  }

  static navigateToLine(mainline: any) {
    const itemToDeleteUrl = LidiUtils.LIDI_LINES_PATH + '/' + mainline.slnid;
    cy.visit({ url: itemToDeleteUrl, method: 'GET' });
  }

  static fillLineVersionForm(version: any) {
    // force-workaround for disabled input field error (https://github.com/cypress-io/cypress/issues/5830)
    CommonUtils.getClearType(DataCy.VALID_FROM, version.validFrom);
    CommonUtils.getClearType(DataCy.VALID_TO, version.validTo, true);
    CommonUtils.getClearType(DataCy.SWISS_LINE_NUMBER, version.swissLineNumber, true);
    CommonUtils.getClearType(DataCy.BUSINESS_ORGANISATION, version.businessOrganisation);

    CommonUtils.selectItemFromDropDown(DataCy.TYPE, version.type);
    CommonUtils.selectItemFromDropDown(DataCy.PAYMENT_TYPE, version.paymentType);

    cy.get(DataCy.COLOR_FONT_RGB + ' ' + DataCy.RGB_PICKER_INPUT).type(
      '{selectall}' + version.colorFontRgb
    );
    cy.get(DataCy.COLOR_BACK_RGB + ' ' + DataCy.RGB_PICKER_INPUT).type(
      '{selectall}' + version.colorBackRgb
    );
    cy.get(DataCy.COLOR_FONT_CMYK + ' ' + DataCy.CMYK_PICKER_INPUT).type(
      '{selectall}' + version.colorFontCmyk
    );
    cy.get(DataCy.COLOR_BACK_CMYK + ' ' + DataCy.CMYK_PICKER_INPUT).type(
      '{selectall}' + version.colorBackCmyk
    );

    CommonUtils.getClearType(DataCy.DESCRIPTION, version.description);
    CommonUtils.getClearType(DataCy.NUMBER, version.number);
    CommonUtils.getClearType(DataCy.ALTERNATIVE_NAME, version.alternativeName);
    CommonUtils.getClearType(DataCy.COMBINATION_NAME, version.combinationName);
    CommonUtils.getClearType(DataCy.LONG_NAME, version.longName);
    CommonUtils.getClearType(DataCy.ICON, version.icon);
    CommonUtils.getClearType(DataCy.COMMENT, version.comment);

    cy.get(DataCy.SAVE_ITEM).should('not.be.disabled');
  }

  static typeAndSelectItemFromDropDown(selector: string, value: string) {
    cy.get(selector).type(value).wait(1000).type('{enter}');
  }

  static searchAndNavigateToLine(line: any) {
    const pathToIntercept = '/line-directory/v1/lines?**';

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_SEARCH_CHIP_INPUT,
      line.swissLineNumber
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_SEARCH_CHIP_INPUT,
      line.slnid
    );

    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_SEARCH_STATUS_INPUT,
      'Aktiv'
    );

    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_SEARCH_LINE_TYPE,
      line.type
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_SEARCH_DATE_INPUT,
      line.validTo
    );
    // Check that the table contains 1 result
    cy.get(DataCy.LIDI_LINES + ' table tbody tr').should('have.length', 1);
    // Click on the item
    cy.contains('td', line.swissLineNumber).parents('tr').click({ force: true });
    this.assertContainsLineVersion(line);
  }

  static assertContainsLineVersion(version: any) {
    CommonUtils.assertItemValue(DataCy.VALID_FROM, version.validFrom);
    CommonUtils.assertItemValue(DataCy.VALID_TO, version.validTo);
    CommonUtils.assertItemValue(DataCy.SWISS_LINE_NUMBER, version.swissLineNumber);
    CommonUtils.assertItemValue(DataCy.BUSINESS_ORGANISATION, version.businessOrganisation);
    CommonUtils.assertItemText(
      DataCy.TYPE + ' .mat-select-value-text > .mat-select-min-line',
      version.type
    );
    CommonUtils.assertItemText(
      DataCy.PAYMENT_TYPE + ' .mat-select-value-text > .mat-select-min-line',
      version.paymentType
    );
    CommonUtils.assertItemValue(
      DataCy.COLOR_FONT_RGB + ' ' + DataCy.RGB_PICKER_INPUT,
      version.colorFontRgb
    );
    CommonUtils.assertItemValue(
      DataCy.COLOR_BACK_RGB + ' ' + DataCy.RGB_PICKER_INPUT,
      version.colorBackRgb
    );
    CommonUtils.assertItemValue(
      DataCy.COLOR_FONT_CMYK + ' ' + DataCy.CMYK_PICKER_INPUT,
      version.colorFontCmyk
    );
    CommonUtils.assertItemValue(
      DataCy.COLOR_BACK_CMYK + ' ' + DataCy.CMYK_PICKER_INPUT,
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

  /** Update SLNID of mainline */
  static addMainLine() {
    const mainline = LidiUtils.getMainLineVersion();
    LidiUtils.navigateToLines();
    LidiUtils.clickOnAddNewLineVersion();
    LidiUtils.fillLineVersionForm(mainline);
    CommonUtils.saveLine();
    LidiUtils.readSlnidFromForm(mainline);
    LidiUtils.assertContainsLineVersion(mainline);
    CommonUtils.fromDetailBackToLinesOverview();
    return mainline;
  }

  /** Update SLNID of given line-object */
  static addLineFrom(line: any) {
    LidiUtils.navigateToLines();
    LidiUtils.clickOnAddNewLineVersion();
    LidiUtils.fillLineVersionForm(line);
    CommonUtils.saveLine();
    LidiUtils.readSlnidFromForm(line);
    LidiUtils.assertContainsLineVersion(line);
    CommonUtils.fromDetailBackToLinesOverview();
    return line;
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

  static getFirstMinimalLineVersion() {
    return {
      slnid: '',
      validFrom: '01.01.1700',
      validTo: '01.01.1700',
      swissLineNumber: 'minimal1',
      businessOrganisation: 'BO1',
      type: 'Ordentlich',
      paymentType: 'Regional',
      colorFontRgb: '#ABCDEF',
      colorBackRgb: '#123456',
      colorFontCmyk: '1,2,3,4',
      colorBackCmyk: '0,33,66,100',
      description: '',
      number: '',
      alternativeName: '',
      combinationName: '',
      longName: '',
      icon: '',
      comment: '',
    };
  }

  static getSecondMinimalLineVersion() {
    return {
      slnid: '',
      validFrom: '31.12.9999',
      validTo: '31.12.9999',
      swissLineNumber: 'minimal2',
      businessOrganisation: 'BO2',
      type: 'Temporär',
      paymentType: 'Keine',
      colorFontRgb: '#1FE23D',
      colorBackRgb: '#271611',
      colorFontCmyk: '1,2,3,4',
      colorBackCmyk: '0,33,66,100',
      description: '',
      number: '',
      alternativeName: '',
      combinationName: '',
      longName: '',
      icon: '',
      comment: '',
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
    cy.get(DataCy.VALID_TO).clear().type(version.validTo, { force: true });
    cy.get(DataCy.SWISS_SUBLINE_NUMBER).clear().type(version.swissSublineNumber, { force: true });
    this.typeAndSelectItemFromDropDown(DataCy.MAINLINE_SLNID, version.mainlineSlnid);
    cy.get(DataCy.BUSINESS_ORGANISATION).clear().type(version.businessOrganisation);
    CommonUtils.selectItemFromDropDown(DataCy.TYPE, version.type);
    CommonUtils.selectItemFromDropDown(DataCy.PAYMENT_TYPE, version.paymentType);
    cy.get(DataCy.DESCRIPTION).clear().type(version.description, { force: true });
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
      DataCy.TYPE + ' .mat-select-value-text > .mat-select-min-line',
      version.type
    );
    CommonUtils.assertItemText(
      DataCy.PAYMENT_TYPE + ' .mat-select-value-text > .mat-select-min-line',
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
      mainlineSlnid: 'minimal1',
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
      mainlineSlnid: 'minimal1',
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

  static getFirstMinimalSubline() {
    return {
      slnid: '',
      number: '_31.001:a:',
      description: 'Thun Bahnhof - Gwatt Deltapark - Einigen - Spiez Bahnhof -',
      longName: 'Thun Bahnhof - Schadau - Gwatt Deltapark - Einigen - Spiez Bahnhof',
      mainlineSlnid: 'minimal1',
      swissSublineNumber: 'r.31.001:x_',
      validFrom: '01.01.1700',
      validTo: '01.01.2000',
      businessOrganisation: '146 - STI',
      type: 'Kompensation',
      paymentType: 'Regional',
    };
  }

  static getSecondMinimalSubline() {
    return {
      slnid: '',
      number: '31.001:a',
      description: 'Das ist eine kurze Beschreibung auf deutsch.',
      longName: '- Thun Bahnhof - Schadau - Gwatt Deltapark - Einigen - Spiez Bahnhof',
      mainlineSlnid: 'minimal1',
      swissSublineNumber: 'r.31.001:a_',
      validFrom: '01.01.2000',
      validTo: '31.12.2099',
      businessOrganisation: 'abcdefghijklmnopqrstuvwxyabcdefghijklmnopqrstuvwxy',
      type: 'Konzession',
      paymentType: 'Lokal',
    };
  }
}
