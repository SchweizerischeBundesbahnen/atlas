import BodiDependentUtils from '../../../support/util/bodi-dependent-utils';
import LidiUtils from "../../../support/util/lidi-utils";
import {DataCy} from "../../../support/data-cy";
import CommonUtils from "../../../support/util/common-utils";

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

  it('Step-5: Stellungnahmen erfassen', () => {
    LidiUtils.changeLiDiTabToTTH('ACTIVE');
  });

  it('Dependent BusinessOrganisation Cleanup Step', () => {
    console.log(selectedHearingYear);
    BodiDependentUtils.deleteDependentBusinessOrganisation();
  });
});
