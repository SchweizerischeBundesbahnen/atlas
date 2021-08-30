describe('Fahrplanfeldnummer Application is available', () => {
  beforeEach(() => {
    const authToken = '***';
    sessionStorage.setItem('auth_token', authToken);
    cy.visit('/');
  });

  it('Visits the initial project page', () => {
    cy.contains('Fahrplanfeldnummer');
    cy.contains('This is a work in progress');
  });

  it('Changes to french', () => {
    cy.get('app-language-switcher').get('a').eq(1).click();
    !cy.contains('Fahrplanfeldnummer');
    cy.contains('Documentation des services TP suisses');
  });

  it('Changes to italian', () => {
    cy.get('app-language-switcher').get('a').eq(2).click();
    !cy.contains('Fahrplanfeldnummer');
    cy.contains('Documentazione dei servizi TP svizzeri');
  });

  it('Keeps selected language on refresh', () => {
    cy.get('app-language-switcher').get('a').eq(2).click();
    cy.reload();
    cy.contains('Documentazione dei servizi TP svizzeri');
  });
});
