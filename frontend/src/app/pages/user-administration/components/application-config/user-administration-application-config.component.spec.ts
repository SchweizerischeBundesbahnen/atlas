import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationApplicationConfigComponent } from './user-administration-application-config.component';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { UserPermissionManager } from '../../service/user-permission-manager';
import { MaterialModule } from '../../../../core/module/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { BehaviorSubject, of } from 'rxjs';
import { ApplicationType } from '../../../../api';
import { SelectComponent } from '../../../../core/form-components/select/select.component';
import { AtlasSpacerComponent } from '../../../../core/components/spacer/atlas-spacer.component';
import { MockAtlasFieldErrorComponent } from '../../../../app.testing.mocks';
import SpyObj = jasmine.SpyObj;
import { AtlasLabelFieldComponent } from '../../../../core/form-components/atlas-label-field/atlas-label-field.component';

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
      },
    );
    await TestBed.configureTestingModule({
      declarations: [
        UserAdministrationApplicationConfigComponent,
        SelectComponent,
        AtlasLabelFieldComponent,
        AtlasSpacerComponent,
        MockAtlasFieldErrorComponent,
      ],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        MaterialModule,
        BrowserAnimationsModule,
      ],
      providers: [
        {
          provide: UserPermissionManager,
          useValue: userPermissionManagerSpy,
        },
        { provide: TranslatePipe },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationApplicationConfigComponent);
    component = fixture.componentInstance;
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
    expect(userPermissionManagerSpy.addSboidToPermission).not.toHaveBeenCalled();

    component.businessOrganisationForm.get(component.boFormCtrlName)?.setValue('test');
    component.application = 'TTFN';
    component.add();
    expect(userPermissionManagerSpy.addSboidToPermission).toHaveBeenCalledOnceWith('TTFN', 'test');
    expect(component.businessOrganisationForm.get(component.boFormCtrlName)?.value).toBe(null);
  });

  it('test removeBusinessOrganisation', () => {
    component.application = 'LIDI';
    component.selectedIndex = 0;
    component.remove();
    expect(userPermissionManagerSpy.removeSboidFromPermission).toHaveBeenCalledOnceWith('LIDI', 0);
    expect(component.selectedIndex).toBe(-1);
  });
});
