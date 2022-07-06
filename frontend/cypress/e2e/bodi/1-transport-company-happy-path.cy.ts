import BodiUtils from '../../support/util/bodi-utils';
import CommonUtils from '../../support/util/common-utils';
import { DataCy } from '../../support/data-cy';

describe('Transport Company', () => {
  it('Step-1: ATLAS Login', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to Business Organisation', () => {
    BodiUtils.navigateToBusinessOrganisation();
  });

  it('Step-3: switch tab to TU', () => {
    BodiUtils.switchTabToTU();
  });

  it('Step-4: Check TU Table is visible', () => {
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

  it('Step-5: search and open first TU', () => {
    CommonUtils.typeSearchInput(
      '/business-organisation-directory/v1/transport-companies?**',
      DataCy.TABLE_SEARCH_CHIP_INPUT,
      'Bern'
    );
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

  it('Step-6: add Relation with Business Organisation', () => {
    // search for 10 (attribute sboid) to make sure to find results
    CommonUtils.typeAndSelectItemFromDropDown(
      `${DataCy.BUSINESS_ORGANISATION_SEARCH_SELECT} input`,
      '10'
    );
    CommonUtils.getClearType(DataCy.VALID_FROM, '01.01.2020');
    CommonUtils.getClearType(DataCy.VALID_TO, '01.01.2021');
    BodiUtils.interceptGetTransportCompanyRelations(DataCy.TC_ADD_RELATION_BTN);
  });

  it('Step-7: remove created Relation', () => {
    cy.get('table tbody').find('tr').last().click();
    BodiUtils.interceptGetTransportCompanyRelations(DataCy.TC_DELETE_RELATION_BTN);
  });
});
