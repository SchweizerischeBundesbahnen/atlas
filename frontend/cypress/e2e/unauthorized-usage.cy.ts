import {DataCy} from "../support/data-cy";
import LidiUtils from "../support/util/lidi-utils";
import SepodiUtils from "../support/util/sepodi-utils";
import CommonUtils from "../support/util/common-utils";

describe('Unauthorized usage', () => {

  it('Step-01: Check home', () => {
    cy.visit('/');

    // Check that login button is available
    cy.get('#login').should('contain.text', 'Login');

    // Pages for unauthorized user should be 4
    cy.get('.card').should('have.length', 4);
  });

  it('Step-02: Check Line', () => {
    cy.visit('/');
    LidiUtils.navigateToLines();
    LidiUtils.checkHeaderTitle();

    CommonUtils.navigateToHomeViaHomeLogo();
  });

  it('Step-03: Check Sepodi', () => {
    cy.visit('/');
    SepodiUtils.navigateToServicePoint();
    cy.get(DataCy.SEPODI_SEARCH_FORM).should('exist');
    cy.get(DataCy.SEPODI_NEW_SERVICE_POINT_BUTTON).should('not.exist');

    CommonUtils.navigateToHomeViaHomeLogo();
  });
});
