import { Component, output, input } from '@angular/core';
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
import { PageEvent } from '@angular/material/paginator';
import { UserPermissionProviderService } from './core/components/permissions/application-permission/user-permission-provider-service';
import {
  ApplicationPermission,
  ApplicationPermissionFormGroupBuilder,
} from './core/components/permissions/form/application-permission-form-group';

@Component({
  selector: 'atlas-switch-version',
  template: '<h1>version switch mock</h1>',
})
export class MockSwitchVersionComponent {
  readonly records = input.required<Array<Record>>();
  readonly currentRecord = input.required<Record>();
  readonly switchDisabled = input(false);
  readonly showStatus = input(true);
  readonly switchVersion = output<number>();
}

@Component({
  selector: 'atlas-form-info-icon',
  template: '',
})
export class MockInfoIconComponent {
  readonly infoTitle = input('');
}

@Component({
  selector: 'atlas-bo-select',
  template: '<p>Mock Business Organisation Select Component</p>',
})
export class MockBoSelectComponent {
  readonly valueExtraction = input('sboid');
  readonly controlName = input.required<string>();
  readonly formModus = input(true);
  readonly formGroup = input.required<FormGroup>();
  readonly sboidsRestrictions = input<string[]>([]);
}

@Component({
  selector: 'atlas-tu-select',
  template: '<p>Mock TU Select Component</p>',
})
export class MockTuSelectComponent {
  readonly valueExtraction = input('');
  readonly controlName = input.required<string>();
  readonly formModus = input(true);
  readonly formGroup = input.required<FormGroup>();
  readonly selectedTransportCompanyChanged = output();
  readonly tuSelectionChanged = output<TransportCompany>();

  transportCompanies: Observable<TransportCompany[]> = of([]);
}

@Component({
  selector: 'atlas-sepodi-geography',
  template: `<h1>Mock Geography Component</h1>`,
})
export class MockGeographyComponent {}

@Component({
  selector: 'atlas-ttfn-select',
  template: '<p>Mock TTFN Select Component</p>',
})
export class MockTimetableFieldNumberSelectComponent {
  readonly valueExtraction = input('ttfnid');
  readonly controlName = input.required<string>();
  readonly formModus = input(true);
  readonly required = input(true);
  readonly formGroup = input.required<FormGroup>();
  readonly validOn = input<Date>();
  readonly disabled = input.required<boolean>();

  readonly selectedTimetableFieldNumberChanged = output();
  readonly ttfnSelectionChanged = output<TimetableFieldNumber>();
}

@Component({
  selector: 'atlas-select',
  template: '<p>Mock Select Component</p>',
})
export class MockSelectComponent {
  readonly label = input<string>();
  readonly placeHolderLabel = input('FORM.DROPDOWN_PLACEHOLDER');
  readonly optionTranslateLabelPrefix = input<string>();
  readonly additionalLabelspace = input(true);
  readonly required = input(false);
  readonly multiple = input(false);
  readonly dataCy = input.required<string>();
  readonly controlName = input<string | null>(null);
  readonly formGroup = input.required<FormGroup>();
  readonly options = input([]);
  /* eslint-disable  @typescript-eslint/no-explicit-any */
  readonly optionsGroup = input<any[] | undefined>([]);
  /* eslint-disable  @typescript-eslint/no-explicit-any */
  readonly value = input<any>();
  readonly valueExtractor = input<any>();
  readonly displayExtractor = input<any>();
  /* eslint-enable  @typescript-eslint/no-explicit-any */
  readonly disabled = input(false);
  readonly isOptional = input(false);
  readonly selectChanged = output();
}

@Component({
  selector: 'atlas-table',
  template: '<p>Mock Table Component</p>',
})
export class MockTableComponent<DATATYPE> {
  readonly tableData = input<DATATYPE[]>([]);
  readonly tableFilterConfig = input<TableFilter<unknown>[][]>([]);
  readonly tableColumns = input.required<TableColumn<DATATYPE>[]>();
  readonly canEdit = input(true);
  readonly totalCount = input.required<number>();
  readonly pageSizeOptions = input<number[]>([5, 10, 25, 100]);
  readonly sortingDisabled = input(false);
  readonly showTableFilter = input(true);
  readonly checkBoxModeEnabled = input(false);
  readonly showPaginator = input(true);

  readonly checkBoxSelection = input(new SelectionModel<TimetableHearingStatementV2>(true, []));
  readonly editElementEvent = output<DATATYPE>();
  readonly getTableElementsEvent = output<TablePagination>();
}

@Component({
  selector: 'atlas-button',
  template: '',
})
export class MockAtlasButtonComponent {
  readonly applicationType = input.required<ApplicationType>();
  readonly businessOrganisation = input.required<string>();
  readonly businessOrganisations = input<string[]>([]);
  readonly canton = input.required<string>();
  readonly uicCountryCode = input<number>();
  readonly disabled = input.required<boolean>();

  readonly wrapperStyleClass = input.required<string>();
  readonly buttonDataCy = input.required<string>();
  readonly buttonType = input.required<AtlasButtonType>();
  readonly footerEdit = input(false);
  readonly submitButton = input.required<boolean>();
  readonly buttonText = input.required<string>();
}

@Component({
  selector: 'atlas-user-detail-info [record]',
  template: '',
})
export class MockUserDetailInfoComponent {
  readonly record = input.required<CreationEditionRecord>();
}

@Component({
  selector: 'atlas-field-error',
  template: '',
})
export class MockAtlasFieldErrorComponent {
  readonly controlName = input.required<string>();
  readonly form = input<FormGroup>(new FormGroup({}));
  readonly control = input.required<FormControl>();
  readonly customError = input.required<AtlasFieldCustomError>();
}

@Component({
  // eslint-disable-next-line @angular-eslint/component-selector
  selector: 'mat-paginator',
  template: '',
})
export class MockMatPaginatorComponent {
  readonly pageSizeOptions = input<number[]>();
  readonly length = input<number>();

  readonly page = output<Pick<PageEvent, 'pageSize' | 'pageIndex'>>();
}

@Component({
  selector: 'atlas-label-field',
  template: '',
})
export class MockAtlasLabelFieldComponent {
  readonly required = input.required<boolean>();
  readonly fieldLabel = input.required<string>();
  readonly infoIconTitle = input.required<string>();
  readonly infoIconLink = input.required<string>();
  readonly fieldExamples = input.required<Array<FieldExample>>();
}

@Component({
  selector: 'atlas-navigation-sepodi-prm',
  template: '<h1>MockNavigationSepodiPrmComponent</h1>',
})
export class MockNavigationSepodiPrmComponent {
  readonly targetPage = input.required<TargetPageType>();
  readonly sloid = input<string>();
  readonly number = input<number>();
  readonly parentSloid = input<string>();
}

@Component({
  selector: 'atlas-prm-recording-obligation',
  template: '<h1>MockPrmRecordingObligationComponent</h1>',
})
export class MockPrmRecordingObligationComponent {
  readonly sloid = input.required<string>();
  readonly showToggle = input(true);
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

export class MockUserPermissionProviderService extends UserPermissionProviderService {
  applicationPermissionFormGroup?: FormGroup<ApplicationPermission>;

  getCurrentForm(): FormGroup<ApplicationPermission> | undefined {
    return this.applicationPermissionFormGroup;
  }

  showAllSpecialPermissions(): boolean {
    return false;
  }

  loadFormGroup(): void {
    const formGroup = ApplicationPermissionFormGroupBuilder.buildFormGroup();
    formGroup.controls.application.setValue(ApplicationType.Ttfn);
    this.applicationPermissionFormGroup = formGroup;
  }
}
