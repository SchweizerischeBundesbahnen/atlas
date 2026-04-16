import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import {
  ApplicationRole,
  ApplicationType,
  TimetableFieldNumber,
  TimetableHearingStatementV2,
  TransportCompany,
} from './api';
import { AtlasButtonType } from './core/components/button/atlas-button.type';
import { TableColumn } from './core/components/table/table-column';
import { TablePagination } from './core/components/table/table-pagination';
import { AtlasFieldCustomError } from './core/form-components/atlas-field-error/atlas-field-custom-error';
import { SelectionModel } from '@angular/cdk/collections';
import { TableFilter } from './core/components/table-filter/config/table-filter';
import { CreationEditionRecord } from './core/components/user-edit-info/creation-edition-record';
import { Record } from './core/model/record';
import { AuthService } from './core/auth/auth.service';
import { UserService } from './core/auth/user/user.service';
import { Observable, of, ReplaySubject } from 'rxjs';
import { PermissionService } from './core/auth/permission/permission.service';
import { PageService } from './core/pages/page.service';
import { Pages } from './pages/pages';
import { FieldExample } from './core/form-components/text-field/field-example';
import { TargetPageType } from './core/navigation-sepodi-prm/navigation-sepodi-prm.component';
import { Page } from './core/model/page';
import { provideTranslateService } from '@ngx-translate/core';
import { provideTranslateHttpLoader } from '@ngx-translate/http-loader';
import { Mocked, vi } from 'vitest';

@Component({
  selector: 'atlas-switch-version',
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
  selector: 'atlas-form-info-icon',
  template: '',
})
export class MockInfoIconComponent {
  @Input() infoTitle = '';
}

@Component({
  selector: 'atlas-bo-select',
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
  selector: 'atlas-tu-select',
  template: '<p>Mock TU Select Component</p>',
})
export class MockTuSelectComponent {
  @Input() valueExtraction = '';
  @Input() controlName!: string;
  @Input() formModus = true;
  @Input() formGroup!: FormGroup;
  @Output() selectedTransportCompanyChanged = new EventEmitter();
  @Output() tuSelectionChanged = new EventEmitter<TransportCompany>();

  transportCompanies: Observable<TransportCompany[]> = of([]);
}

@Component({
  selector: 'atlas-ttfn-select',
  template: '<p>Mock TTFN Select Component</p>',
})
export class MockTimetableFieldNumberSelectComponent {
  @Input() valueExtraction = 'ttfnid';
  @Input() controlName!: string;
  @Input() formModus = true;
  @Input() required = true;
  @Input() formGroup!: FormGroup;
  @Input() validOn: Date | undefined = undefined;
  @Input() disabled!: boolean;

  @Output() selectedTimetableFieldNumberChanged = new EventEmitter();
  @Output() ttfnSelectionChanged = new EventEmitter<TimetableFieldNumber>();
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
  selector: 'atlas-table',
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
  @Input() showPaginator = true;

  @Input() checkBoxSelection = new SelectionModel<TimetableHearingStatementV2>(true, []);
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
  selector: 'atlas-user-detail-info [record]',
  template: '',
})
export class MockUserDetailInfoComponent {
  @Input() record!: CreationEditionRecord;
}

@Component({
  selector: 'atlas-field-error',
  template: '',
})
export class MockAtlasFieldErrorComponent {
  @Input() controlName!: string;
  @Input() form: FormGroup = new FormGroup({});
  @Input() control!: FormControl;
  @Input() customError!: AtlasFieldCustomError;
}

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'mat-paginator',
  template: '',
})
export class MockMatPaginatorComponent {
  @Input() pageSizeOptions?: number[];
  @Input() length?: number;

  @Output() page = new EventEmitter();
}

@Component({
  selector: 'atlas-label-field',
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
  selector: 'atlas-navigation-sepodi-prm',
  template: '<h1>MockNavigationSepodiPrmComponent</h1>',
})
export class MockNavigationSepodiPrmComponent {
  @Input() targetPage!: TargetPageType;
  @Input() sloid?: string;
  @Input() number?: number;
  @Input() parentSloid?: string;
}

@Component({
  selector: 'atlas-prm-recording-obligation',
  template: '<h1>MockPrmRecordingObligationComponent</h1>',
})
export class MockPrmRecordingObligationComponent {
  @Input() sloid!: string;
  @Input() showToggle = true;
}

export type ActivatedRouteMockType = {
  /* eslint-disable  @typescript-eslint/no-explicit-any */
  parent?: any;
  data?: any;
  snapshot?: any;
  /* eslint-enable  @typescript-eslint/no-explicit-any */
};

export const adminUserServiceMock: Partial<UserService> = {
  currentUser: {
    name: 'Test (ITC)',
    email: 'test@test.ch',
    sbbuid: 'e123456',
    isAdmin: true,
    permissions: [],
  },
  userChanged: new ReplaySubject<void>(1),
  loggedIn: true,
  isAdmin: true,
  permissions: [],
};

export const adminPermissionServiceMock: Partial<PermissionService> = {
  isAdmin: true,
  hasPermissionsToCreate: () => true,
  isAtLeastSupervisor: () => true,
  isTthCanton: () => true,
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

type AuthServiceMock = Mocked<Pick<AuthService, 'login' | 'logout' | 'initAuth'>>;
export const authServiceMock: AuthServiceMock = {
  login: vi.fn(),
  logout: vi.fn(),
  initAuth: vi.fn(),
};

export const translateServiceProvider = provideTranslateService({
  loader: provideTranslateHttpLoader({
    prefix: './assets/i18n/',
    suffix: '.json',
    enforceLoading: true,
    useHttpBackend: true,
  }),
});

// Module only to declare mock components in Angular. Do not import. Declare the mocks in tests yourself
