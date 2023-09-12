import TthUtils from '../../support/util/tth-utils';
import {DataCy} from '../../support/data-cy';
import CommonUtils from '../../support/util/common-utils';
import AngularMaterialConstants from '../../support/util/angular-material-constants';

describe('Timetable Hearing', { testIsolation: false }, () => {
  let selectedHearingYear!: number;

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Step-2: Navigate to Fahrplananhörung', () => {
    TthUtils.navigateToTimetableHearing();
    cy.wait(2000);
    cy.get(DataCy.TTH_SWISS_CANTON_CARD).click();
  });

  it('Step-3: Navigate to Geplante Anhörungen', () => {
    TthUtils.changeTabToTTH('PLANNED');
  });

  it('Step-4: Fahrplanjahr anlegen', () => {
    cy.get(DataCy.ADD_NEW_TIMETABLE_HEARING_BUTTON).click();
    cy.wait(2000);
    CommonUtils.selectFirstItemFromDropDown(DataCy.ADD_NEW_TIMETABLE_HEARING_SELECT_YEAR_DROPDOWN);
    cy.get(
      DataCy.ADD_NEW_TIMETABLE_HEARING_SELECT_YEAR_DROPDOWN +
        AngularMaterialConstants.MAT_SELECT_TEXT_DEEP_SELECT
    ).then((elem) => {
      selectedHearingYear = Number(elem.text());
      const validFrom = '01.01.' + (Number(elem.text()) - 1);
      const validTo = '31.12.' + (Number(elem.text()) - 1);
      CommonUtils.getClearType(DataCy.HEARING_FROM, validFrom, true);
      CommonUtils.getClearType(DataCy.HEARING_TO, validTo, true);
    });
    cy.get(DataCy.DIALOG_CONFIRM_BUTTON).click();
  });

  it('Step-5: Fahrplanjahr Starten', () => {
    cy.get(DataCy.TTH_SELECT_YEAR).wait(1000).should('be.visible');
    CommonUtils.selectItemFromDropDown(DataCy.TTH_SELECT_YEAR, String(selectedHearingYear));
    cy.get(DataCy.START_TIMETABLE_HEARING_YEAR_BUTTON)
      .click()
      .then(() => {
        cy.get(DataCy.DIALOG_CONFIRM_BUTTON).click();
      });
  });

  it('Step-6: Stellungnahmen erfassen', () => {
    CommonUtils.visit('timetable-hearing/ch/active');
    CommonUtils.selectItemFromDropDown(DataCy.SELECT_TTH_CANTON_DROPDOWN, ' Tessin');
    cy.get(DataCy.NEW_STATEMENT_BUTTON).click();
    CommonUtils.selectItemFromDropDown(DataCy.TTH_SELECT_YEAR, String(selectedHearingYear));
    CommonUtils.getClearType(DataCy.STATEMENT_STOP_PLACE, 'Wiesenbach');
    CommonUtils.getClearType(DataCy.STATEMENT_FIRTS_NAME, 'Khvicha');
    CommonUtils.getClearType(DataCy.STATEMENT_LAST_NAME, 'Kvaratskhelia');
    CommonUtils.getClearType(DataCy.STATEMENT_ORGANISATION, 'SSC Calcio Napoli');
    CommonUtils.getClearType(DataCy.STATEMENT_ZIP, '8400');
    CommonUtils.getClearType(DataCy.STATEMENT_CITY, 'Napoli');
    CommonUtils.getClearType(DataCy.STATEMENT_STREET, 'San Paolo');
    CommonUtils.getClearType(DataCy.STATEMENT_EMAIL, 'k@k.it');
    CommonUtils.getClearType(DataCy.STATEMENT_STATEMENT, 'Forza Napoli');
    CommonUtils.getClearType(DataCy.STATEMENT_JUSTIFICATION, 'Campioni in Italia');
    cy.get(DataCy.SAVE_ITEM).click();
  });

  it('Step-7: Stellungnahmen editieren', () => {
    const getStatementsPath = 'line-directory/v1/timetable-hearing/statements*';
    cy.intercept('GET', getStatementsPath).as('getStatementsPath');
    cy.get(DataCy.BACK_TO_OVERVIEW).click();
    cy.wait('@getStatementsPath').its('response.statusCode').should('eq', 200);

    cy.get(DataCy.TTH_TABLE + ' table tbody tr td').should(
      'not.contain',
      'Es wurden keine Daten gefunden.'
    );

    CommonUtils.clickFirstRowInTable(DataCy.TTH_TABLE);
    cy.get(DataCy.EDIT_BUTTON).click();
    CommonUtils.getClearType(DataCy.STATEMENT_ORGANISATION, 'SSC Calcio Napoli');
    cy.get(DataCy.SAVE_ITEM).click();
    cy.get(DataCy.BACK_TO_OVERVIEW).click();
  });

  it('Step-8: Sammelaktion -> Status ändern -> angenommen', () => {
    cy.get(DataCy.TTH_TABLE + ' table tbody tr td').should(
      'not.contain',
      'Es wurden keine Daten gefunden.'
    );
    CommonUtils.selectFirstItemFromDropDown(DataCy.TTH_COLLECT_ACTION_TYPE);
    cy.get(DataCy.TTH_TABLE_CHECKBOX_ALL).click();
    CommonUtils.selectItemFromDropDown(DataCy.COLLECT_STATUS_CHANGE_ACTION_TYPE, 'angenommen');
    CommonUtils.getClearType(
      DataCy.STATEMENT_JUSTIFICATION,
      'Campioni in Italia!!!Forza Napoli!!!'
    );
    cy.get(DataCy.DIALOG_CONFIRM_BUTTON).click();
  });

  it('Step-9: Fahrplanjahr schliessen', () => {
    CommonUtils.visit('timetable-hearing/ch/active');
    cy.get(DataCy.TTH_TABLE).should('be.visible');
    cy.get(DataCy.TTH_MANAGE_TIMETABLE_HEARING).click();
    cy.get(DataCy.TTH_CLOSE_TTH_YEAR).click();
    cy.get(DataCy.TTH_CLOSE_TTH_TIMETABLE_HEARING).click();
  });

  it('Step-10: Archivierte Anhörungn kontrollieren', () => {
    TthUtils.changeTabToTTH('ARCHIVED');
    CommonUtils.selectItemFromDropDown(DataCy.TTH_SELECT_YEAR, String(selectedHearingYear));
    CommonUtils.assertNumberOfTableRows(DataCy.TTH_TABLE, 1);
  });
});
