import { Component, EventEmitter, Input, NgModule, Output } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { ApplicationType, TimetableHearingStatement } from './api';
import { AtlasButtonType } from './core/components/button/atlas-button.type';
import { TableColumn } from './core/components/table/table-column';
import { TablePagination } from './core/components/table/table-pagination';
import { AtlasFieldCustomError } from './core/form-components/atlas-field-error/atlas-field-custom-error';
import { SelectionModel } from '@angular/cdk/collections';
import { TableFilter } from './core/components/table-filter/config/table-filter';
import { CreationEditionRecord } from './core/components/base-detail/user-edit-info/creation-edition-record';
import { BaseDetailController } from './core/components/base-detail/base-detail-controller';
import { Record } from './core/components/base-detail/record';

@Component({
  selector: 'app-detail-wrapper [controller][headingNew]',
  template: '<p>Mock Product Editor Component</p>',
})
export class MockAppDetailWrapperComponent {
  @Input() controller!: BaseDetailController<Record>;
  @Input() headingNew!: string;
}

@Component({
  selector: 'bo-select',
  template: '<p>Mock Business Organisation Select Component</p>',
})
export class MockBoSelectComponent {
  @Input() valueExtraction = 'sboid';
  @Input() controlName!: string;
  @Input() formModus = true;
  @Input() formGroup!: FormGroup;
  @Input() sboidsRestrictions: string[] = [];
}

@Component({
  selector: 'atlas-select',
  template: '<p>Mock Select Component</p>',
})
export class MockSelectComponent {
  @Input() label: string | undefined;
  @Input() placeHolderLabel = 'FORM.DROPDOWN_PLACEHOLDER';
  @Input() optionTranslateLabelPrefix: string | undefined;
  @Input() additionalLabelspace = true;
  @Input() required = false;
  @Input() multiple = false;
  @Input() dataCy!: string;
  @Input() controlName: string | null = null;
  @Input() formGroup!: FormGroup;
  @Input() options = [];
  /* eslint-disable  @typescript-eslint/no-explicit-any */
  @Input() value: any;
  @Input() valueExtractor: any;
  @Input() displayExtractor: any;
  /* eslint-enable  @typescript-eslint/no-explicit-any */
  @Input() disabled = false;
  @Output() selectChanged = new EventEmitter();
}

@Component({
  selector: 'app-table',
  template: '<p>Mock Table Component</p>',
})
export class MockTableComponent<DATATYPE> {
  @Input() tableData: DATATYPE[] = [];
  @Input() tableFilterConfig: TableFilter<unknown>[][] = [];
  @Input() tableColumns!: TableColumn<DATATYPE>[];
  @Input() canEdit = true;
  @Input() totalCount!: number;
  @Input() pageSizeOptions: number[] = [5, 10, 25, 100];
  @Input() sortingDisabled = false;
  @Input() showTableFilter = true;
  @Input() checkBoxModeEnabled = false;

  @Input() checkBoxSelection = new SelectionModel<TimetableHearingStatement>(true, []);
  @Output() editElementEvent = new EventEmitter<DATATYPE>();
  @Output() getTableElementsEvent = new EventEmitter<TablePagination>();
}

@Component({
  selector: 'atlas-button',
  template: '',
})
export class MockAtlasButtonComponent {
  @Input() applicationType!: ApplicationType;
  @Input() businessOrganisation!: string;
  @Input() businessOrganisations: string[] = [];
  @Input() canton!: string;
  @Input() uicCountryCode?: number;
  @Input() disabled!: boolean;

  @Input() wrapperStyleClass!: string;
  @Input() buttonDataCy!: string;
  @Input() buttonType!: AtlasButtonType;
  @Input() footerEdit = false;
  @Input() submitButton!: boolean;
  @Input() buttonText!: string;
}

@Component({
  selector: 'app-user-detail-info [record]',
  template: '',
})
export class MockUserDetailInfoComponent {
  @Input() record!: CreationEditionRecord;
}

@Component({
  selector: 'app-atlas-field-error',
  template: '',
})
export class MockAtlasFieldErrorComponent {
  @Input() controlName!: string;
  @Input() form: FormGroup = new FormGroup({});
  @Input() control!: FormControl;
  @Input() customError!: AtlasFieldCustomError;
}

// eslint-disable-next-line  @typescript-eslint/no-explicit-any
export type ActivatedRouteMockType = { data: any };

// Module only to declare mock components in Angular. Do not import. Declare the mocks in tests yourself
@NgModule({
  declarations: [
    MockAppDetailWrapperComponent,
    MockTableComponent,
    MockBoSelectComponent,
    MockSelectComponent,
    MockAtlasButtonComponent,
    MockUserDetailInfoComponent,
    MockAtlasFieldErrorComponent,
  ],
  exports: [
    MockBoSelectComponent,
    MockAtlasButtonComponent,
    MockTableComponent,
    MockSelectComponent,
  ],
})
export class AppMockComponents {}
