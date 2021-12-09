export default class LidiUtils {
  static navigateToLidi() {
    cy.get('#line-directory').click();
    cy.url().should('contain', '/line-directory');
  }

  static navigateToHome() {
    cy.get('#home').click();
  }

  static clickOnAddNewVersion() {
    cy.get('[data-cy=lidi-lines] [data-cy=new-item]').click();
    cy.get('[data-cy=save-item]').should('be.disabled');
    cy.get('[data-cy=edit-item]').should('not.exist');
    cy.get('[data-cy=delete-item]').should('not.exist');
    cy.contains('Neue Linie');
  }

  static fillVersionForm(version: any) {
    cy.get('[data-cy=validFrom]').clear().type(version.validFrom);
    cy.get('[data-cy=validTo]').clear().type(version.validTo);
    cy.get('[data-cy=swissLineNumber]').clear().type(version.swissLineNumber);
    cy.get('[data-cy=businessOrganisation]').clear().type(version.businessOrganisation);
    this.selectItemFromDropDown('[data-cy=type]', version.type);
    this.selectItemFromDropDown('[data-cy=paymentType]', version.paymentType);
    cy.get('[data-cy=colorFontRgb]').type(version.colorFontRgb);
    cy.get('[data-cy=colorBackRgb]').type(version.colorBackRgb);
    cy.get('[data-cy=colorFontCmyk]').type(version.colorFontCmyk);
    cy.get('[data-cy=colorBackCmyk]').type(version.colorBackCmyk);
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
  }

  static assertContainsVersion(version: any) {
    cy.get('[data-cy=validFrom]').invoke('val').should('eq', version.validFrom);
    cy.get('[data-cy=validTo]').invoke('val').should('eq', version.validTo);
    cy.get('[data-cy=swissLineNumber]').invoke('val').should('eq', version.swissLineNumber);
    cy.get('[data-cy=businessOrganisation]')
      .invoke('val')
      .should('eq', version.businessOrganisation);
    cy.get('[data-cy=type] .mat-select-value-text > .mat-select-min-line')
      .invoke('text')
      .should('eq', version.type);
    cy.get('[data-cy=paymentType] .mat-select-value-text > .mat-select-min-line')
      .invoke('text')
      .should('eq', version.paymentType);
    cy.get(
      '[data-cy=colorFontRgb] > .mat-form-field > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-infix > [data-cy=rgb-picker-input]'
    )
      .invoke('val')
      .should('eq', version.colorFontRgb);
    cy.get(
      '[data-cy=colorFontRgb] > .mat-form-field > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-infix > [data-cy=rgb-picker-input]'
    )
      .invoke('val')
      .should('eq', version.colorBackRgb);
    cy.get(
      '[data-cy=colorFontCmyk] > .mat-form-field > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-infix > [data-cy=cmyk-picker-input]'
    )
      .invoke('val')
      .should('eq', version.colorFontCmyk);
    cy.get(
      '[data-cy=colorBackCmyk] > .mat-form-field > .mat-form-field-wrapper > .mat-form-field-flex > .mat-form-field-infix > [data-cy=cmyk-picker-input]'
    )
      .invoke('val')
      .should('eq', version.colorBackCmyk);
    cy.get('[data-cy=description]').invoke('val').should('eq', version.description);
    cy.get('[data-cy=number]').invoke('val').should('eq', version.number);
    cy.get('[data-cy=alternativeName]').invoke('val').should('eq', version.alternativeName);
    cy.get('[data-cy=combinationName]').invoke('val').should('eq', version.combinationName);
    cy.get('[data-cy=longName]').invoke('val').should('eq', version.longName);
    cy.get('[data-cy=icon]').invoke('val').should('eq', version.icon);
    cy.get('[data-cy=comment]').invoke('val').should('eq', version.comment);
    cy.get('[data-cy=edit-item]').should('not.be.disabled');
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

  static getFirstVersion() {
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

  static getSecondVersion() {
    return {
      swissTimetableFieldNumber: '00.AAA',
      validFrom: '01.01.2001',
      validTo: '31.12.2002',
      businessOrganisation: 'SBB1',
      number: '1.1',
      name: 'Chur - Thusis / St. Moritz - Pontresina - Campocologno - Granze (Weiterfahrt nach Tirano/I)Z',
      comment: 'A new comment',
    };
  }
}
