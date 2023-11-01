import BodiUtils from '../../support/util/bodi-utils';
import CommonUtils from '../../support/util/common-utils';
import { DataCy } from '../../support/data-cy';

describe('Company', { testIsolation: false }, () => {
  it('Step-1: ATLAS Login', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to Business Organisation', () => {
    BodiUtils.navigateToBusinessOrganisation();
  });

  it('Step-3: switch tab to Company Codes', () => {
    BodiUtils.switchTabToCompany();
  });

  it('Step-4: Check Company Table', () => {
    BodiUtils.checkHeaderTitle('Geschäftsorganisationen');

    CommonUtils.assertTableSearch(0, 0, 'Suche');

    CommonUtils.assertTableHeader(0, 0, 'Code');
    CommonUtils.assertTableHeader(0, 1, 'Kurzbezeichnung');
    CommonUtils.assertTableHeader(0, 2, 'Bezeichnung');
    CommonUtils.assertTableHeader(0, 3, 'Länderkürzel');
    CommonUtils.assertTableHeader(0, 4, 'URL');
  });

  it('Step-5: search and open first TU', () => {
    CommonUtils.typeSearchInput(
      '/business-organisation-directory/v1/companies?**',
      DataCy.TABLE_FILTER_CHIP_INPUT,
      'SBB-Passengers',
    );
    cy.get('table tbody')
      .find('tr')
      .first()
      .find('td')
      .first()
      .should(($tdElement) => {
        $tdElement.trigger('click');
      });
  });
});
