import CommonUtils from './common-utils';
import {DataCy} from '../data-cy';
import BodiDependentUtils from './bodi-dependent-utils';
import AngularMaterialConstants from './angular-material-constants';

export default class LidiUtils {
  private static LIDI_LINES_PATH = '/line-directory/lines';
  private static LIDI_SUBLINES_PATH = '/line-directory/sublines';
  private static TTH_CH_PLANNED_PATH = '/timetable-hearing/ch/';

  private static MAINLINE_SWISS_LINE_NUMBER = 'b0.IC2-E2E';

  static navigateToLines() {
    CommonUtils.navigateToHomeViaHomeLogo();
    this.interceptLines('#line-directory');
  }

  static changeLiDiTabToLines() {
    this.interceptLines('a[href="' + LidiUtils.LIDI_LINES_PATH + '"]');
  }

  static navigateToSublines() {
    CommonUtils.navigateToHomeViaHomeLogo();
    cy.get('#line-directory').click();
    this.changeLiDiTabToSublines();
  }

  static navigateToTimetableHearing() {
    CommonUtils.navigateToHomeViaHomeLogo();
    cy.get('#timetable-hearing').click();
  }

  static changeLiDiTabToSublines() {
    cy.intercept('GET', '/line-directory/v1/sublines?**').as('getSublines');
    cy.get('a[href="' + LidiUtils.LIDI_SUBLINES_PATH + '"]').click();
    cy.wait('@getSublines').then((interception) => {
      cy.wrap(interception.response?.statusCode).should('eq', 200);
      cy.url().should('contain', LidiUtils.LIDI_SUBLINES_PATH);
    });
  }

  static changeLiDiTabToTTHPlanned() {
    cy.intercept('GET', '/line-directory/v1/timetable-hearing/years?statusChoices=PLANNED').as('getChPlanned');
    cy.get('a[href="' + LidiUtils.TTH_CH_PLANNED_PATH + '"]').click();
    cy.wait('@getChPlanned').then((interception) => {
      cy.wrap(interception.response?.statusCode).should('eq', 200);
      // cy.url().should('contain', LidiUtils.LIDI_SUBLINES_PATH);
    });
  }

  static changeLiDiTabToTTH(hearingStatus: string) {
    cy.intercept('GET', '/line-directory/v1/timetable-hearing/years?statusChoices=' + hearingStatus).as('getRequest');
    cy.get('a[href="' + LidiUtils.TTH_CH_PLANNED_PATH + hearingStatus.toLowerCase() + '"]').click();
    cy.wait('@getRequest').then((interception) => {
      cy.wrap(interception.response?.statusCode).should('eq', 200);
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

  static checkIfLineAlreadyExists(line: any) {
    const pathToIntercept = '/line-directory/v1/lines?**';

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_FILTER_CHIP_INPUT,
      line.swissLineNumber
    );

    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_FILTER_MULTI_SELECT(1, 2),
      'Aktiv'
    );

    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_FILTER_MULTI_SELECT(1, 1),
      line.type
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_FILTER_DATE_INPUT(1, 3),
      line.validTo
    );

    cy.get('tbody')
      .find('tr')
      .should('have.length', 1)
      .then(($el) => {
        if (!$el.hasClass(AngularMaterialConstants.TABLE_NOW_DATA_ROW_CLASS)) {
          $el.trigger('click');
          cy.get(DataCy.EDIT_ITEM).should('not.be.disabled');
          CommonUtils.deleteItem();
        }
      });
  }

  static checkIfSublineAlreadyExists(sublineVersion: any) {
    const pathToIntercept = '/line-directory/v1/sublines?**';

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_SUBLINES + ' ' + DataCy.TABLE_FILTER_CHIP_INPUT,
      sublineVersion.swissSublineNumber
    );

    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.LIDI_SUBLINES + ' ' + DataCy.TABLE_FILTER_MULTI_SELECT(1, 2),
      'Aktiv'
    );

    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.LIDI_SUBLINES + ' ' + DataCy.TABLE_FILTER_MULTI_SELECT(1, 1),
      sublineVersion.type
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_SUBLINES + ' ' + DataCy.TABLE_FILTER_DATE_INPUT(1, 3),
      sublineVersion.validTo
    );

    cy.get('tbody')
      .find('tr')
      .should('have.length', 1)
      .then(($el) => {
        if (!$el.hasClass(AngularMaterialConstants.TABLE_NOW_DATA_ROW_CLASS)) {
          $el.trigger('click');
          LidiUtils.assertContainsSublineVersion(sublineVersion);
          CommonUtils.deleteItem();
        }
      });
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
    CommonUtils.visit(itemToDeleteUrl);
  }

  static navigateToLine(mainline: any) {
    const itemToDeleteUrl = LidiUtils.LIDI_LINES_PATH + '/' + mainline.slnid;
    CommonUtils.visit(itemToDeleteUrl);
  }

  static fillLineVersionForm(version: any) {
    // force-workaround for disabled input field error (https://github.com/cypress-io/cypress/issues/5830)
    CommonUtils.getClearType(DataCy.VALID_FROM, version.validFrom, true);
    CommonUtils.getClearType(DataCy.VALID_TO, version.validTo, true);
    CommonUtils.getClearType(DataCy.SWISS_LINE_NUMBER, version.swissLineNumber, true);

    CommonUtils.typeAndSelectItemFromDropDown(
      DataCy.BUSINESS_ORGANISATION + ' ' + 'input',
      version.businessOrganisation
    );

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

  static searchAndNavigateToLine(line: any) {
    const pathToIntercept = '/line-directory/v1/lines?**';

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_FILTER_CHIP_INPUT,
      line.swissLineNumber
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_FILTER_CHIP_INPUT,
      line.slnid
    );

    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_FILTER_MULTI_SELECT(1, 2),
      'Aktiv'
    );

    CommonUtils.selectItemFromDropdownSearchItem(
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_FILTER_MULTI_SELECT(1, 1),
      line.type
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_LINES + ' ' + DataCy.TABLE_FILTER_DATE_INPUT(1, 3),
      line.validTo
    );
    // Check that the table contains 1 result
    cy.get(DataCy.LIDI_LINES + ' table tbody tr').should('have.length', 1);
    // Click on the item
    cy.contains('td', line.swissLineNumber).parents('tr').click({force: true});
    this.assertContainsLineVersion(line);
  }

  static assertContainsLineVersion(version: any) {
    CommonUtils.assertItemValue(DataCy.VALID_FROM, version.validFrom);
    CommonUtils.assertItemValue(DataCy.VALID_TO, version.validTo);
    CommonUtils.assertItemValue(DataCy.SWISS_LINE_NUMBER, version.swissLineNumber);
    cy.get(DataCy.BUSINESS_ORGANISATION).should('contain.text', version.businessOrganisation);
    CommonUtils.assertItemText(
      DataCy.TYPE + AngularMaterialConstants.MAT_SELECT_TEXT_DEEP_SELECT,
      version.type
    );
    CommonUtils.assertItemText(
      DataCy.PAYMENT_TYPE + AngularMaterialConstants.MAT_SELECT_TEXT_DEEP_SELECT,
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

  /** SLNID of mainline is implicitly updated */
  static addMainLine() {
    const mainline = LidiUtils.getMainLineVersion();
    this.addLine(mainline);
    return mainline;
  }

  /** SLNID of given line-object is implicitly updated */
  static addLine(line: any) {
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
      swissLineNumber: LidiUtils.MAINLINE_SWISS_LINE_NUMBER,
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,
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
      comment: 'Kommentar'
    };
  }

  static getFirstMinimalLineVersion() {
    return {
      slnid: '',
      validFrom: '01.01.1700',
      validTo: '01.01.1700',
      swissLineNumber: 'minimal1',
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,
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
      comment: ''
    };
  }

  static getSecondMinimalLineVersion() {
    return {
      slnid: '',
      validFrom: '31.12.9999',
      validTo: '31.12.9999',
      swissLineNumber: 'minimal2',
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,
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
      comment: ''
    };
  }

  static getFirstLineVersion() {
    return {
      slnid: '',
      validFrom: '01.01.2000',
      validTo: '31.12.2000',
      swissLineNumber: 'b0.IC2-E2E',
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,
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
      comment: 'Kommentar'
    };
  }

  static getSecondLineVersion() {
    return {
      validFrom: '01.01.2001',
      validTo: '31.12.2001',
      swissLineNumber: 'b0.IC2-E2E',
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,
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
      comment: 'Kommentar-1'
    };
  }

  static getThirdLineVersion() {
    return {
      validFrom: '01.01.2002',
      validTo: '31.12.2002',
      swissLineNumber: 'b0.IC2-E2E',
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,
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
      comment: 'Kommentar-2'
    };
  }

  static getEditedLineVersion() {
    return {
      validFrom: '01.06.2000',
      validTo: '01.06.2002',
      alternativeName: 'IC2 alt edit'
    };
  }

  static fillSublineVersionForm(version: any, skipMainline = false) {
    // workaround for disabled input field error with (https://github.com/cypress-io/cypress/issues/5830)
    CommonUtils.getClearType(DataCy.VALID_FROM, version.validFrom, true);
    CommonUtils.getClearType(DataCy.VALID_TO, version.validTo, true);
    cy.get(DataCy.SWISS_SUBLINE_NUMBER).clear().type(version.swissSublineNumber, {force: true});
    if (!skipMainline) {
      CommonUtils.typeAndSelectItemFromDropDown(DataCy.MAINLINE + ' ' + 'input', version.mainline);
    }

    CommonUtils.typeAndSelectItemFromDropDown(
      DataCy.BUSINESS_ORGANISATION + ' ' + 'input',
      version.businessOrganisation
    );

    CommonUtils.selectItemFromDropDown(DataCy.TYPE, version.type);
    CommonUtils.selectItemFromDropDown(DataCy.PAYMENT_TYPE, version.paymentType);
    cy.get(DataCy.DESCRIPTION).clear().type(version.description, {force: true});
    cy.get(DataCy.NUMBER).clear().type(version.number);
    cy.get(DataCy.LONG_NAME).clear().type(version.longName);
    cy.get(DataCy.SAVE_ITEM).should('not.be.disabled');
  }

  static searchAndNavigateToSubline(subline: any) {
    const pathToIntercept = '/line-directory/v1/sublines?**';

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_SUBLINES + ' ' + DataCy.TABLE_FILTER_CHIP_INPUT,
      subline.swissSublineNumber
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      DataCy.LIDI_SUBLINES + ' ' + DataCy.TABLE_FILTER_CHIP_INPUT,
      subline.slnid
    );

    // Check that the table contains 1 result
    cy.get(DataCy.LIDI_SUBLINES + ' table tbody tr').should('have.length', 1);
    // Click on the item
    cy.contains('td', subline.swissSublineNumber).parents('tr').click({force: true});
    this.assertContainsSublineVersion(subline);
  }

  static assertContainsSublineVersion(version: any) {
    CommonUtils.assertItemValue(DataCy.VALID_FROM, version.validFrom);
    CommonUtils.assertItemValue(DataCy.VALID_TO, version.validTo);
    CommonUtils.assertItemValue(DataCy.SWISS_SUBLINE_NUMBER, version.swissSublineNumber);
    cy.get(DataCy.MAINLINE).should('contain.text', version.mainline);
    cy.get(DataCy.BUSINESS_ORGANISATION).should('contain.text', version.businessOrganisation);
    CommonUtils.assertItemText(
      DataCy.TYPE + AngularMaterialConstants.MAT_SELECT_TEXT_DEEP_SELECT,
      version.type
    );
    CommonUtils.assertItemText(
      DataCy.PAYMENT_TYPE + AngularMaterialConstants.MAT_SELECT_TEXT_DEEP_SELECT,
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
      swissSublineNumber: 'b0.IC233-E2E',
      mainline: LidiUtils.MAINLINE_SWISS_LINE_NUMBER,
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,
      type: 'Kompensation',
      paymentType: 'International',
      description: 'Lorem Ipus Linie',
      number: 'IC2',
      longName:
        'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z'
    };
  }

  static getSecondSublineVersion() {
    return {
      validFrom: '01.01.2002',
      validTo: '31.12.2002',
      swissSublineNumber: 'b0.IC233-E2E',
      mainline: LidiUtils.MAINLINE_SWISS_LINE_NUMBER,
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,
      type: 'Technisch',
      paymentType: 'International',
      description: 'Lorem Ipus Linie',
      number: 'IC2-update',
      longName:
        'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z'
    };
  }

  static getEditedFirstSublineVersion() {
    return {
      validFrom: '01.01.2000',
      validTo: '01.06.2002',
      number: 'IC2-Edit',
      longName:
        'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z - Edit'
    };
  }

  static getFirstMinimalSubline() {
    return {
      slnid: '',
      number: '_31.001:a:',
      description: 'Thun Bahnhof - Gwatt Deltapark - Einigen - Spiez Bahnhof -',
      longName: 'Thun Bahnhof - Schadau - Gwatt Deltapark - Einigen - Spiez Bahnhof',
      mainline: 'minimal1',
      swissSublineNumber: 'r.31.001:x_',
      validFrom: '01.01.1700',
      validTo: '01.01.2000',
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,
      type: 'Kompensation',
      paymentType: 'Regional'
    };
  }

  static getSecondMinimalSubline() {
    return {
      slnid: '',
      number: '31.001:a',
      description: 'Das ist eine kurze Beschreibung auf deutsch.',
      longName: '- Thun Bahnhof - Schadau - Gwatt Deltapark - Einigen - Spiez Bahnhof',
      mainline: 'minimal1',
      swissSublineNumber: 'r.31.001:a_',
      validFrom: '01.01.2000',
      validTo: '31.12.2099',
      businessOrganisation: BodiDependentUtils.BO_DESCRIPTION,
      type: 'Konzession',
      paymentType: 'Lokal'
    };
  }

  private static interceptLines(visitSelector: string) {
    cy.intercept('GET', '/line-directory/v1/lines?**').as('getLines');
    cy.get(visitSelector)
      .should('be.visible')
      .should(($el) => expect(Cypress.dom.isFocusable($el)).to.be.true)
      .click();
    cy.wait('@getLines').then((interception) => {
      cy.wrap(interception.response?.statusCode).should('eq', 200);
      cy.url().should('contain', LidiUtils.LIDI_LINES_PATH);
    });
  }
}
