import CommonUtils from './common-utils';

export default class LidiUtils {
  static navigateToLidi() {
    cy.get('#line-directory').click();
    cy.url().should('contain', '/line-directory');
  }

  static readSlnidFromForm(element: { slnid: string }) {
    cy.get('[data-cy=slnid]')
      .invoke('val')
      .then((slnid) => (element.slnid = slnid ? slnid.toString() : ''));
  }

  static clickOnAddNewLinieVersion() {
    cy.get('[data-cy=lidi-lines] [data-cy=new-item]').click();
    cy.get('[data-cy=save-item]').should('be.disabled');
    cy.get('[data-cy=edit-item]').should('not.exist');
    cy.get('[data-cy=delete-item]').should('not.exist');
    cy.contains('Neue Linie');
  }

  static clickOnAddNewSublinesLinieVersion() {
    cy.get('[data-cy=lidi-sublines] [data-cy=new-item]').click();
    cy.get('[data-cy=save-item]').should('be.disabled');
    cy.get('[data-cy=edit-item]').should('not.exist');
    cy.get('[data-cy=delete-item]').should('not.exist');
    cy.contains('Neue Teillinie');
  }

  static assertIsOnLiDiHome() {
    cy.url().should('contain', '/line-directory');
    cy.get('[data-cy="lidi-lines"]').should('exist');
    cy.get('[data-cy="lidi-sublines"]').should('exist');
    cy.contains('Teillinien');
  }

  static navigateToSubline(sublineVersion: any) {
    const itemToDeleteUrl = '/line-directory/sublines/' + sublineVersion.slnid;
    cy.visit({ url: itemToDeleteUrl, method: 'GET' });
  }

  static navigateToLine(mainline: any) {
    const itemToDeleteUrl = '/line-directory/lines/' + mainline.slnid;
    cy.visit({ url: itemToDeleteUrl, method: 'GET' });
  }

  static fillLineVersionForm(version: any) {
    cy.get('[data-cy=validFrom]').clear().type(version.validFrom);
    cy.get('[data-cy=validTo]').clear().type(version.validTo);
    cy.get('[data-cy=swissLineNumber]').clear().type(version.swissLineNumber);
    cy.get('[data-cy=businessOrganisation]').clear().type(version.businessOrganisation);
    CommonUtils.selectItemFromDropDown('[data-cy=type]', version.type);
    CommonUtils.selectItemFromDropDown('[data-cy=paymentType]', version.paymentType);
    cy.get('[data-cy=colorFontRgb] [data-cy=rgb-picker-input]').clear().type(version.colorFontRgb);
    cy.get('[data-cy=colorBackRgb] [data-cy=rgb-picker-input]').clear().type(version.colorBackRgb);
    cy.get('[data-cy=colorFontCmyk] [data-cy=cmyk-picker-input]')
      .clear()
      .type(version.colorFontCmyk);
    cy.get('[data-cy=colorBackCmyk] [data-cy=cmyk-picker-input]')
      .clear()
      .type(version.colorBackCmyk);
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
      '[data-cy="lidi-lines"] [data-cy=table-search-chip-input]',
      line.swissLineNumber
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      '[data-cy="lidi-lines"] [data-cy=table-search-chip-input]',
      line.slnid
    );

    CommonUtils.selectSearchStatus(
      '[data-cy="lidi-lines"] [data-cy=table-search-status-input]',
      'Aktiv'
    );

    CommonUtils.typeSearchInput(
      pathToIntercept,
      '[data-cy="lidi-lines"] [data-cy=table-search-date-input]',
      line.validTo
    );
    // Check that the table contains 1 result
    cy.get('[data-cy="lidi-lines"] table tbody tr').should('have.length', 1);
    // Click on the item
    cy.contains('td', line.swissLineNumber).parents('tr').click();
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

  static assertTableHeader(columnHeaderNumber: number, columnHeaderContent: string) {
    cy.get('table')
      .get('thead tr th')
      .eq(columnHeaderNumber)
      .get('div')
      .contains(columnHeaderContent);
  }

  static addMainLine() {
    const mainline = LidiUtils.getFirstLineVersion();
    LidiUtils.navigateToLidi();
    LidiUtils.clickOnAddNewLinieVersion();
    LidiUtils.fillLineVersionForm(mainline);
    CommonUtils.saveLine();
    LidiUtils.readSlnidFromForm(mainline);
    LidiUtils.assertContainsLineVersion(mainline);
    CommonUtils.navigateToHome();
    return mainline;
  }

  static getFirstLineVersion() {
    return {
      slnid: '',
      validFrom: '01.01.2000',
      validTo: '31.12.2000',
      swissLineNumber: 'b0.IC2',
      businessOrganisation: 'SBB',
      type: 'Temporär',
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
      type: 'Temporär',
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
      type: 'Temporär',
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
    cy.get('[data-cy=validFrom]').clear().type(version.validFrom);
    cy.get('[data-cy=validTo]').clear().type(version.validTo);
    cy.get('[data-cy=swissSublineNumber]').clear().type(version.swissSublineNumber);
    this.typeAndSelectItemFromDropDown('[data-cy=mainlineSlnid]', version.mainlineSlnid);
    cy.get('[data-cy=businessOrganisation]').clear().type(version.businessOrganisation);
    CommonUtils.selectItemFromDropDown('[data-cy=type]', version.type);
    CommonUtils.selectItemFromDropDown('[data-cy=paymentType]', version.paymentType);
    cy.get('[data-cy=description]').clear().type(version.description);
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
