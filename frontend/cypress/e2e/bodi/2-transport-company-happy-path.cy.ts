import BodiUtils from '../../support/util/bodi-utils';
import CommonUtils from '../../support/util/common-utils';
import { DataCy } from '../../support/data-cy';
import BodiDependentUtils from '../../support/util/bodi-dependent-utils';

describe('Transport Company', { testIsolation: false }, () => {
  it('Step-1: ATLAS Login', () => {
    cy.atlasLogin();
  });

  it('Dependent BusinessOrganisation Preparation Step', () => {
    BodiDependentUtils.createDependentBusinessOrganisation();
  });

  it('Step-2: Navigate to Business Organisation', () => {
    BodiUtils.navigateToBusinessOrganisation();
  });

  it('Step-4: switch tab to TU', () => {
    BodiUtils.switchTabToTU();
  });

  it('Step-5: Check TU Table is visible', () => {
    BodiUtils.checkHeaderTitle('Geschäftsorganisationen');

    CommonUtils.assertTableSearch(0, 0, 'Suche');
    CommonUtils.assertTableSearch(0, 1, 'Status');

    CommonUtils.assertTableHeader(0, 0, 'TU-Nummer');
    CommonUtils.assertTableHeader(0, 1, 'Initialen');
    CommonUtils.assertTableHeader(0, 2, 'Handelsregistername');
    CommonUtils.assertTableHeader(0, 3, 'Amtliche Bezeichnung');
    CommonUtils.assertTableHeader(0, 4, 'Unternehmens-ID (UID)');
    CommonUtils.assertTableHeader(0, 5, 'Status');
  });

  it('Step-6: search and open first TU', () => {
    CommonUtils.typeSearchInput(
      '/business-organisation-directory/v1/transport-companies?**',
      DataCy.TABLE_FILTER_CHIP_INPUT,
      'Bern'
    );
    cy.wait(500);
    cy.get('table tbody')
      .find('tr')
      .first()
      .find('td')
      .first()
      .then((tdElement) => {
        tdElement.trigger('click');
        CommonUtils.assertItemValue(DataCy.NUMBER, tdElement.text().trim());
        cy.get(DataCy.NUMBER).should('be.disabled');
      });
  });

  it('Step-7: add Relation with Business Organisation', () => {
    cy.get('app-relation').scrollIntoView();
    cy.get(DataCy.TC_ADD_RELATION_BTN).click();

    CommonUtils.typeAndSelectItemFromDropDown(
      `${DataCy.BUSINESS_ORGANISATION} input`,
      BodiDependentUtils.BO_DESCRIPTION
    );
    CommonUtils.getClearType(DataCy.VALID_FROM, '01.01.2020', true);
    CommonUtils.getClearType(DataCy.VALID_TO, '01.01.2021', true);
    BodiUtils.interceptGetTransportCompanyRelations(DataCy.SAVE_ITEM);
  });

  it('Step-8: remove created Relation', () => {
    cy.get('table tbody').find('tr').last().click();
    BodiUtils.interceptGetTransportCompanyRelations(DataCy.TC_DELETE_RELATION_BTN);
    BodiUtils.fromDetailBackToTransportCompanyOverview();
    BodiUtils.switchTabToBusinessOrganisations();
  });

  it('Dependent BusinessOrganisation Cleanup Step', () => {
    BodiDependentUtils.deleteDependentBusinessOrganisation();
  });
});
