import CommonUtils from './common-utils';

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
    cy.get('[data-cy=sublines-title]').invoke('text').should('contain', 'Teillinien');
  }

  static readSlnidFromForm(element: { slnid: string }) {
    cy.get('[data-cy=slnid]')
    .invoke('val')
    .then((slnid) => (element.slnid = slnid ? slnid.toString() : ''));
  }

  static clickOnAddNewLinieVersion() {
    cy.get('[data-cy=new-line]').click();
    cy.get('[data-cy=save-item]').should('be.disabled');
    cy.get('[data-cy=edit-item]').should('not.exist');
    cy.get('[data-cy=delete-item]').should('not.exist');
    cy.contains('Neue Linie');
  }

  static clickOnAddNewSublinesLinieVersion() {
    cy.get('[data-cy=new-subline]').click();
    cy.get('[data-cy=save-item]').should('be.disabled');
    cy.get('[data-cy=edit-item]').should('not.exist');
    cy.get('[data-cy=delete-item]').should('not.exist');
    cy.contains('Neue Teillinie');
  }

  static assertIsOnLines() {
    cy.url().should('contain', LidiUtils.LIDI_LINES_PATH);
    cy.get('[data-cy="lidi-lines"]').should('exist');
  }

  static assertIsOnSublines() {
    cy.url().should('contain', LidiUtils.LIDI_SUBLINES_PATH);
    cy.get('[data-cy="lidi-sublines"]').should('exist');
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
    cy.get('[data-cy=validFrom]').clear().type(version.validFrom);
    cy.get('[data-cy=validTo]').clear().type(version.validTo, {force: true});
    cy.get('[data-cy=swissLineNumber]').clear().type(version.swissLineNumber, {force: true});
    cy.get('[data-cy=businessOrganisation]').clear().type(version.businessOrganisation);
    CommonUtils.selectItemFromDropDown('[data-cy=type]', version.type);
    CommonUtils.selectItemFromDropDown('[data-cy=paymentType]', version.paymentType);
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
    cy.get('[data-cy=description]').clear().type(version.description);
    cy.get('[data-cy=number]').clear().type(version.number);
    cy.get('[data-cy=alternativeName]').clear().type(version.alternativeName);
    cy.get('[data-cy=combinationName]').clear().type(version.combinationName);
    cy.get('[data-cy=longName]').clear().type(version.longName);
    cy.get('[data-cy=icon]').clear().type(version.icon);
    cy.get('[data-cy=comment]').clear().type(version.comment);
    cy.get('[data-cy=save-item]').should('not.be.disabled');
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
    CommonUtils.assertItemValue('[data-cy=validFrom]', version.validFrom);
    CommonUtils.assertItemValue('[data-cy=validTo]', version.validTo);
    CommonUtils.assertItemValue('[data-cy=swissLineNumber]', version.swissLineNumber);
    CommonUtils.assertItemValue('[data-cy=businessOrganisation]', version.businessOrganisation);
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
    CommonUtils.assertItemValue('[data-cy=description]', version.description);
    CommonUtils.assertItemValue('[data-cy=number]', version.number);
    CommonUtils.assertItemValue('[data-cy=alternativeName]', version.alternativeName);
    CommonUtils.assertItemValue('[data-cy=combinationName]', version.combinationName);
    CommonUtils.assertItemValue('[data-cy=longName]', version.longName);
    CommonUtils.assertItemValue('[data-cy=icon]', version.icon);
    CommonUtils.assertItemValue('[data-cy=comment]', version.comment);

    cy.get('[data-cy=edit-item]').should('not.be.disabled');
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
    cy.get('[data-cy=validFrom]').clear().type(version.validFrom);
    cy.get('[data-cy=validTo]').clear().type(version.validTo, {force: true});
    cy.get('[data-cy=swissSublineNumber]')
    .clear()
    .type(version.swissSublineNumber, {force: true});
    this.typeAndSelectItemFromDropDown('[data-cy=mainlineSlnid]', version.mainlineSlnid);
    cy.get('[data-cy=businessOrganisation]').clear().type(version.businessOrganisation);
    CommonUtils.selectItemFromDropDown('[data-cy=type]', version.type);
    CommonUtils.selectItemFromDropDown('[data-cy=paymentType]', version.paymentType);
    cy.get('[data-cy=description]').clear().type(version.description, {force: true});
    cy.get('[data-cy=number]').clear().type(version.number);
    cy.get('[data-cy=longName]').clear().type(version.longName);
    cy.get('[data-cy=save-item]').should('not.be.disabled');
  }

  static assertContainsSublineVersion(version: any) {
    CommonUtils.assertItemValue('[data-cy=validFrom]', version.validFrom);
    CommonUtils.assertItemValue('[data-cy=validTo]', version.validTo);
    CommonUtils.assertItemValue('[data-cy=swissSublineNumber]', version.swissSublineNumber);
    cy.get('[data-cy=mainlineSlnid]').should('contain.text', version.mainlineSlnid);
    CommonUtils.assertItemValue('[data-cy=businessOrganisation]', version.businessOrganisation);
    CommonUtils.assertItemText(
      '[data-cy=type] .mat-select-value-text > .mat-select-min-line',
      version.type
    );
    CommonUtils.assertItemText(
      '[data-cy=paymentType] .mat-select-value-text > .mat-select-min-line',
      version.paymentType
    );
    CommonUtils.assertItemValue('[data-cy=description]', version.description);
    CommonUtils.assertItemValue('[data-cy=number]', version.number);
    CommonUtils.assertItemValue('[data-cy=longName]', version.longName);

    cy.get('[data-cy=edit-item]').should('not.be.disabled');
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
