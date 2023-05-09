import LidiUtils from "../../../support/util/lidi-utils";
import {DataCy} from "../../../support/data-cy";
import CommonUtils from "../../../support/util/common-utils";
import BodiDependentUtils from "../../../support/util/bodi-dependent-utils";

describe('Timetable Hearing', {testIsolation: false}, () => {

  let selectedHearingYear!: number;

  it('Step-1: Login on ATLAS', () => {
    cy.atlasLogin();
  });

  it('Dependent BusinessOrganisation Preparation Step', () => {
    BodiDependentUtils.createDependentBusinessOrganisation();
  });

  it('Step-2: Navigate to Fahrplananhörung', () => {
    LidiUtils.navigateToTimetableHearing();
  });

  it('Step-3: Navigate to Geplante Anhörungen', () => {
    cy.get(DataCy.TTH_SWISS_CANTON_CARD).click();
    LidiUtils.changeLiDiTabToTTH('PLANNED');
  });

  it('Step-4: Fahrplanjahr anlegen', () => {
    cy.get(DataCy.ADD_NEW_TIMETABLE_HEARING_BUTTON).click();
    CommonUtils.selectFirstItemFromDropDown(DataCy.ADD_NEW_TIMETABLE_HEARING_SELECT_YEAR_DROPDOWN);
    cy.get("[data-cy=timetableYear] .mat-mdc-select-value-text > .mat-mdc-select-min-line").then((elem) => {
      selectedHearingYear = Number(elem.text());
      const validFrom = '01.01.' + (Number(elem.text()) - 1);
      const validTo = '31.12.' + (Number(elem.text()) - 1);
      CommonUtils.getClearType(DataCy.HEARING_FROM, validFrom, true);
      CommonUtils.getClearType(DataCy.HEARING_TO, validTo, true);
    })
    cy.get(DataCy.DIALOG_CONFIRM_BUTTON).click();

  });

  it('Step-5: Fahrplanjahr Starten', () => {
    LidiUtils.changeLiDiTabToTTH('ACTIVE');
    LidiUtils.changeLiDiTabToTTH('PLANNED');
    cy.get(DataCy.START_TIMETABLE_HEARING_YEAR_BUTTON).click().then(() => {
      cy.get(DataCy.DIALOG_CONFIRM_BUTTON).click();
    })
  });

  it('Step-6: Stellungnahmen erfassen', () => {
    LidiUtils.changeLiDiTabToTTH('ACTIVE');
    CommonUtils.selectItemFromDropDown(DataCy.SELECT_TTH_CANTON_DROPDOWN, ' Tessin');
    cy.get(DataCy.NEW_STATEMENT_BUTTON).click();
    cy.get('.detail-page-container').scrollIntoView({offset: {top: 0, left: 0}});
    cy.get('.detail-page-container').scrollIntoView({offset: {top: 150, left: 0}});
    CommonUtils.getClearType(DataCy.STATEMENT_STOP_PLACE, 'Wiesenbach')
    CommonUtils.getClearType(DataCy.STATEMENT_FIRTS_NAME, 'Khvicha')
    CommonUtils.getClearType(DataCy.STATEMENT_LAST_NAME, 'Kvaratskhelia')
    CommonUtils.getClearType(DataCy.STATEMENT_ORGANISATION, 'SSC Calcio Napoli')
    CommonUtils.getClearType(DataCy.STATEMENT_ZIP, '8400')
    CommonUtils.getClearType(DataCy.STATEMENT_CITY, 'Napoli')
    CommonUtils.getClearType(DataCy.STATEMENT_STREET, 'San Paolo')
    CommonUtils.getClearType(DataCy.STATEMENT_EMAIL, 'k@k.sscnapoli')
    CommonUtils.getClearType(DataCy.STATEMENT_STATEMENT, 'Forza Napoli')
    CommonUtils.getClearType(DataCy.STATEMENT_JUSTIFICATION, 'Campioni in Italia')
    cy.get(DataCy.SAVE_ITEM).click();
    cy.get(DataCy.BACK_TO_OVERVIEW).click();

  });

  it('Step-7: Stellungnahmen editieren', () => {
    CommonUtils.clickFirstRowInTable(DataCy.TTH_TABLE);
    cy.get(DataCy.EDIT_BUTTON).click();
    CommonUtils.getClearType(DataCy.STATEMENT_ORGANISATION, 'SSC Calcio Napoli')
    cy.get(DataCy.SAVE_ITEM).click();
    cy.get(DataCy.BACK_TO_OVERVIEW).click();
  });

  it('Step-8: Sammelaktion -> Status ändern -> angenommen', () => {
    CommonUtils.selectFirstItemFromDropDown(DataCy.TTH_COLLECT_ACTION_TYPE);
    cy.get(DataCy.TTH_TABLE_CHECKBOX_ALL).click();
    CommonUtils.selectItemFromDropDown(DataCy.COLLECT_STATUS_CHANGE_ACTION_TYPE, 'angenommen');
    CommonUtils.getClearType(DataCy.STATEMENT_JUSTIFICATION, 'Campioni in Italia!!!Forza Napoli!!!');
    cy.get(DataCy.DIALOG_CONFIRM_BUTTON).click();
  });

  it('Step-8: Fahrplanjahr schliessen', () => {
    CommonUtils.selectItemFromDropDown(DataCy.SELECT_TTH_CANTON_DROPDOWN, ' Gesamtschweiz');
    cy.get(DataCy.TTH_MANAGE_TIMETABLE_HEARING).click();
    cy.get(DataCy.TTH_CLOSE_TTH_YEAR).click();
    cy.get(DataCy.TTH_CLOSE_TTH_TIMETABLE_HEARING).click();
  });

  it('Dependent BusinessOrganisation Cleanup Step', () => {
    console.log(selectedHearingYear);
    BodiDependentUtils.deleteDependentBusinessOrganisation();
  });
});
