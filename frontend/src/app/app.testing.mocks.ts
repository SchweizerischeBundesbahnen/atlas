import {Component, EventEmitter, Input, NgModule, Output} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {ApplicationRole, ApplicationType, TimetableHearingStatement} from './api';
import {AtlasButtonType} from './core/components/button/atlas-button.type';
import {TableColumn} from './core/components/table/table-column';
import {TablePagination} from './core/components/table/table-pagination';
import {AtlasFieldCustomError} from './core/form-components/atlas-field-error/atlas-field-custom-error';
import {SelectionModel} from '@angular/cdk/collections';
import {TableFilter} from './core/components/table-filter/config/table-filter';
import {CreationEditionRecord} from './core/components/base-detail/user-edit-info/creation-edition-record';
import {BaseDetailController} from './core/components/base-detail/base-detail-controller';
import {Record} from './core/components/base-detail/record';
import {AuthService} from './core/auth/auth.service';
import {UserService} from './core/auth/user/user.service';
import {Observable, of, Subject } from 'rxjs';
import { PermissionService } from './core/auth/permission/permission.service';
import { PageService } from './core/pages/page.service';
import { Pages } from './pages/pages';
import { FieldExample } from './core/form-components/text-field/field-example';
import { TargetPageType } from './core/navigation-sepodi-prm/navigation-sepodi-prm.component';
import { Page } from './core/model/page';

@Component({
  selector: 'app-switch-version',
  template: '<h1>version switch mock</h1>',
})
export class MockSwitchVersionComponent {
  @Input() records!: Array<Record>;
  @Input() currentRecord!: Record;
  @Input() switchDisabled = false;
  @Input() showStatus = true;
  @Output() switchVersion = new EventEmitter<number>();
}

@Component({
  selector: 'app-detail-wrapper [controller][headingNew]',
  template: '<p>Mock Product Editor Component</p>',
})
export class MockAppDetailWrapperComponent {
  @Input() controller!: BaseDetailController<Record>;
  @Input() headingNew!: string;
}

@Component({
  selector: 'form-info-icon',
  template: '',
})
export class MockInfoIconComponent {
  @Input() infoTitle = '';
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
  @Input() optionsGroup?: any[] = [];
  /* eslint-disable  @typescript-eslint/no-explicit-any */
  @Input() value: any;
  @Input() valueExtractor: any;
  @Input() displayExtractor: any;
  /* eslint-enable  @typescript-eslint/no-explicit-any */
  @Input() disabled = false;
  @Input() isOptional = false;
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

@Component({
  selector: 'mat-paginator',
  template: '',
})
export class MockMatPaginatorComponent {
  @Input() pageSizeOptions?: number[];
  @Input() length?: number;

  @Output() page = new EventEmitter();
}

@Component({
  selector: 'app-atlas-label-field',
  template: '',
})
export class MockAtlasLabelFieldComponent {
  @Input() required!: boolean;
  @Input() fieldLabel!: string;
  @Input() infoIconTitle!: string;
  @Input() infoIconLink!: string;
  @Input() fieldExamples!: Array<FieldExample>;
}

@Component({
  selector: 'app-navigation-sepodi-prm',
  template: '<h1>MockNavigationSepodiPrmComponent</h1>',
})
export class MockNavigationSepodiPrmComponent {
  @Input() targetPage!: TargetPageType;
  @Input() sloid?: string;
  @Input() number?: number;
  @Input() parentSloid?: string;
}

// eslint-disable-next-line  @typescript-eslint/no-explicit-any
export type ActivatedRouteMockType = { data: any };

export const adminUserServiceMock: Partial<UserService> = {
  currentUser: {
    name: 'Test (ITC)',
    email: 'test@test.ch',
    sbbuid: 'e123456',
    isAdmin: true,
    permissions: [],
  },
  userChanged: new Subject<void>(),
  loggedIn: true,
  isAdmin: true,
  permissions: [],
};

export const adminPermissionServiceMock: Partial<PermissionService> = {
  isAdmin: true,
  hasPermissionsToCreate: () => true,
  isAtLeastSupervisor: () => true,
  hasPermissionsToWrite: () => true,
  hasWritePermissionsToForCanton: () => true,
  getApplicationUserPermission: (applicationType) => {
    return {
      application: applicationType,
      role: ApplicationRole.Supervisor,
      permissionRestrictions: [],
    };
  },
};

export const pageServiceMock: Partial<PageService> = {
  get enabledPages(): Observable<Page[]> {
    return of([...Pages.pages]);
  },
};

export const authServiceSpy = jasmine.createSpyObj<AuthService>(['login', 'logout']);

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
    MockInfoIconComponent,
    MockSwitchVersionComponent,
    MockMatPaginatorComponent,
    MockNavigationSepodiPrmComponent,
  ],
  exports: [
    MockSelectComponent
  ]
})
export class AppMockComponents {}
