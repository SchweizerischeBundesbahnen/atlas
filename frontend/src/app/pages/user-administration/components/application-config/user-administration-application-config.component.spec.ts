import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationApplicationConfigComponent } from './user-administration-application-config.component';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { UserPermissionManager } from '../../service/user-permission-manager';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BehaviorSubject, of } from 'rxjs';
import {
  ApplicationRole,
  ApplicationType,
  PermissionRestrictionType,
} from '../../../../api';
import { SelectComponent } from '../../../../core/form-components/select/select.component';
import { AtlasSpacerComponent } from '../../../../core/components/spacer/atlas-spacer.component';
import { MockAtlasFieldErrorComponent } from '../../../../app.testing.mocks';
import { AtlasLabelFieldComponent } from '../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import SpyObj = jasmine.SpyObj;

describe('UserAdministrationApplicationConfigComponent', () => {
  let component: UserAdministrationApplicationConfigComponent;
  let fixture: ComponentFixture<UserAdministrationApplicationConfigComponent>;

  let userPermissionManagerSpy: SpyObj<UserPermissionManager>;

  beforeEach(async () => {
    userPermissionManagerSpy = jasmine.createSpyObj(
      'UserPermissionManager',
      [
        'addSboidToPermission',
        'removeSboidFromPermission',
        'getAvailableApplicationRolesOfApplication',
        'getPermissionByApplication',
        'getRestrictionValues',
        'setPermissions',
      ],
      {
        boOfApplicationsSubject$: new BehaviorSubject<{
          [application in ApplicationType]: unknown[];
        }>({
          TTFN: [],
          LIDI: [],
          BODI: [],
          TIMETABLE_HEARING: [],
          SEPODI: [],
          PRM: [],
        }),
        boFormResetEvent$: of(),
      }
    );

    userPermissionManagerSpy.getPermissionByApplication.and.returnValue({
      role: ApplicationRole.Writer,
      application: ApplicationType.Sepodi,
      permissionRestrictions: [
        { type: PermissionRestrictionType.BulkImport, valueAsString: 'true' },
      ],
    });

    await TestBed.configureTestingModule({
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        BrowserAnimationsModule,
        UserAdministrationApplicationConfigComponent,
        SelectComponent,
        AtlasLabelFieldComponent,
        AtlasSpacerComponent,
        MockAtlasFieldErrorComponent,
      ],
      providers: [
        {
          provide: UserPermissionManager,
          useValue: userPermissionManagerSpy,
        },
        { provide: TranslatePipe },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(
      UserAdministrationApplicationConfigComponent
    );
    component = fixture.componentInstance;
    component.application = ApplicationType.Sepodi;

    component.novaTerminationVotePermission = false;
    component.infoPlusTerminationVotePermission = false;

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.selectedIndex).toBe(-1);
    expect(component.businessOrganisationForm).toBeDefined();
    expect(component.tableColumnDef).toBeDefined();
  });

  it('test addBusinessOrganisation', () => {
    component.add();
    expect(
      userPermissionManagerSpy.addSboidToPermission
    ).not.toHaveBeenCalled();

    component.businessOrganisationForm
      .get(component.boFormCtrlName)
      ?.setValue('test');
    component.application = 'TTFN';
    component.add();
    expect(
      userPermissionManagerSpy.addSboidToPermission
    ).toHaveBeenCalledOnceWith('TTFN', 'test');
    expect(
      component.businessOrganisationForm.get(component.boFormCtrlName)?.value
    ).toBe(null);
  });

  it('test removeBusinessOrganisation', () => {
    component.application = 'LIDI';
    component.selectedIndex = 0;
    component.remove();
    expect(
      userPermissionManagerSpy.removeSboidFromPermission
    ).toHaveBeenCalledOnceWith('LIDI', 0);
    expect(component.selectedIndex).toBe(-1);
  });

  it('test toggleNova', () => {
    component.infoPlusTerminationVotePermission = true;
    expect(component.infoPlusTerminationVotePermission).toBeTrue();
    expect(component.novaTerminationVotePermission).toBeFalse();

    component.onNovaToggle(true);

    expect(component.novaTerminationVotePermission).toBeTrue();
    expect(component.infoPlusTerminationVotePermission).toBeFalse();

    const permission = userPermissionManagerSpy.getPermissionByApplication(
      ApplicationType.Sepodi
    );
    const restriction = permission.permissionRestrictions.find(
      (r) => r.type === PermissionRestrictionType.NovaTerminationVote
    );
    expect(restriction).toBeDefined();
    expect(restriction?.valueAsString).toBe('true');
  });

  it('test toggleInfoPlus', () => {
    component.novaTerminationVotePermission = true;
    expect(component.novaTerminationVotePermission).toBeTrue();
    expect(component.infoPlusTerminationVotePermission).toBeFalse();

    component.onInfoPlusToggle(true);

    expect(component.infoPlusTerminationVotePermission).toBeTrue();
    expect(component.novaTerminationVotePermission).toBeFalse();

    const permission = userPermissionManagerSpy.getPermissionByApplication(
      ApplicationType.Sepodi
    );
    const restriction = permission.permissionRestrictions.find(
      (r) => r.type === PermissionRestrictionType.InfoPlusTerminationVote
    );
    expect(restriction).toBeDefined();
    expect(restriction?.valueAsString).toBe('true');
  });

  it('test setSboidAndCountryPermissions', () => {
    const returnedObj = {
      role: ApplicationRole.Writer,
      application: ApplicationType.Sepodi,
      permissionRestrictions: [
        { type: PermissionRestrictionType.Country, value: 'SWITZERLAND' },
        {
          type: PermissionRestrictionType.BusinessOrganisation,
          value: 'ch:1:sboid:abc',
        },
      ],
    };

    userPermissionManagerSpy.getPermissionByApplication.and.returnValue(
      returnedObj
    );

    const newBusinessRestrictions = [
      {
        type: PermissionRestrictionType.BusinessOrganisation,
        value: 'ch:1:sboid:123',
      },
    ];
    const newCountryRestrictions = [
      { type: PermissionRestrictionType.Country, value: 'SWITZERLAND' },
      { type: PermissionRestrictionType.Country, value: 'FRANCE' },
    ];
    const role = ApplicationRole.Writer;
    const application = ApplicationType.Sepodi;

    component.setSboidAndCountryPermissions(
      newBusinessRestrictions,
      newCountryRestrictions,
      role,
      application
    );

    expect(returnedObj.permissionRestrictions).toEqual([
      {
        type: PermissionRestrictionType.BusinessOrganisation,
        value: 'ch:1:sboid:abc',
      },
    ]);

    expect(userPermissionManagerSpy.setPermissions).toHaveBeenCalledWith([
      {
        application: application,
        role: role,
        permissionRestrictions: newBusinessRestrictions,
      },
      {
        application: application,
        role: role,
        permissionRestrictions: newCountryRestrictions,
      },
    ]);
  });
});
