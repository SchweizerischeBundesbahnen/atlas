export default class LidiUtils {
  static navigateToLidi() {
    cy.get('#line-directory').click();
    cy.url().should('contain', '/line-directory');
  }

  static navigateToHome() {
    cy.get('#home').click();
  }

  static clickOnAddNewLinieVersion() {
    cy.get('[data-cy=lidi-lines] [data-cy=new-item]').click();
    cy.get('[data-cy=save-item]').should('be.disabled');
    cy.get('[data-cy=edit-item]').should('not.exist');
    cy.get('[data-cy=delete-item]').should('not.exist');
    cy.contains('Neue Linie');
  }

  static fillLineVersionForm(version: any) {
    cy.get('[data-cy=validFrom]').clear().type(version.validFrom);
    cy.get('[data-cy=validTo]').clear().type(version.validTo);
    cy.get('[data-cy=swissLineNumber]').clear().type(version.swissLineNumber);
    cy.get('[data-cy=businessOrganisation]').clear().type(version.businessOrganisation);
    this.selectItemFromDropDown('[data-cy=type]', version.type);
    this.selectItemFromDropDown('[data-cy=paymentType]', version.paymentType);
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

  static selectItemFromDropDown(selector: string, value: string) {
    cy.get(selector).first().click();
    // simulate click event on the drop down item (mat-option)
    cy.get('.mat-option-text').then((option) => {
      for (let i = 0; i < option.length; i++) {
        if (option[i].innerText === value) {
          option[i].click(); // this is jquery click() not cypress click()
        }
      }
    });
  }

  static saveVersion() {
    cy.get('[data-cy=save-item]').click();
    cy.get('[data-cy=edit-item]').should('be.visible');
    cy.get('[data-cy=delete-item]').should('be.visible');
    cy.wait(1000);
  }

  static assertContainsVersion(version: any) {
    this.assertItemValue('[data-cy=validFrom]', version.validFrom);
    this.assertItemValue('[data-cy=validTo]', version.validTo);
    this.assertItemValue('[data-cy=swissLineNumber]', version.swissLineNumber);
    this.assertItemValue('[data-cy=businessOrganisation]', version.businessOrganisation);
    this.assertItemText(
      '[data-cy=type] .mat-select-value-text > .mat-select-min-line',
      version.type
    );
    this.assertItemText(
      '[data-cy=paymentType] .mat-select-value-text > .mat-select-min-line',
      version.paymentType
    );
    this.assertItemValue(
      '[data-cy=colorFontRgb] > .mat-form-field > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-infix > [data-cy=rgb-picker-input]',
      version.colorFontRgb
    );
    this.assertItemValue(
      '[data-cy=colorFontRgb] > .mat-form-field > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-infix > [data-cy=rgb-picker-input]',
      version.colorBackRgb
    );
    this.assertItemValue(
      '[data-cy=colorFontCmyk] > .mat-form-field > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-infix > [data-cy=cmyk-picker-input]',
      version.colorFontCmyk
    );
    this.assertItemValue(
      '[data-cy=colorBackCmyk] > .mat-form-field > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-infix > [data-cy=cmyk-picker-input]',
      version.colorBackCmyk
    );
    this.assertItemValue('[data-cy=description]', version.description);
    this.assertItemValue('[data-cy=number]', version.number);
    this.assertItemValue('[data-cy=alternativeName]', version.alternativeName);
    this.assertItemValue('[data-cy=combinationName]', version.combinationName);
    this.assertItemValue('[data-cy=longName]', version.longName);
    this.assertItemValue('[data-cy=icon]', version.icon);
    this.assertItemValue('[data-cy=comment]', version.comment);

    cy.get('[data-cy=edit-item]').should('not.be.disabled');
  }

  private static assertItemValue(selector: string, value: string) {
    cy.get(selector).invoke('val').should('eq', value);
  }

  private static assertItemText(selector: string, value: string) {
    cy.get(selector).invoke('text').should('eq', value);
  }
  static deleteItems() {
    cy.get('[data-cy=delete-item]').click();
    cy.get('#mat-dialog-0').contains('Warnung!');
    cy.get('[data-cy=dialog-confirm-button]').should('exist');
    cy.get('[data-cy=dialog-cancel-button]').should('exist');
    cy.get('[data-cy=dialog-confirm-button]').click();
  }

  static switchLeft() {
    cy.get('[data-cy=switch-version-left]').click();
  }

  static assertTableHeader(columnHeaderNumber: number, columnHeaderContent: string) {
    cy.get('table')
      .get('thead tr th')
      .eq(columnHeaderNumber)
      .get('div')
      .contains(columnHeaderContent);
  }

  static clickOnEditLinie() {
    cy.get('[data-cy=edit-item]').click();
  }

  static getFirstLineVersion() {
    return {
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
}
