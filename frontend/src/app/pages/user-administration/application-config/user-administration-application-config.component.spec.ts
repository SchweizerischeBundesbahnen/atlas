import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationApplicationConfigComponent } from './user-administration-application-config.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { UserPermissionManager } from '../user-permission-manager';
import { MaterialModule } from '../../../core/module/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import SpyObj = jasmine.SpyObj;

describe('UserAdministrationApplicationConfigComponent', () => {
  let component: UserAdministrationApplicationConfigComponent;
  let fixture: ComponentFixture<UserAdministrationApplicationConfigComponent>;

  let applicationConfigManagerMock: SpyObj<UserPermissionManager>;

  beforeEach(async () => {
    applicationConfigManagerMock = jasmine.createSpyObj(
      'UserPermissionManager',
      [
        'getCurrentRole',
        'addSboidToPermission',
        'removeSboidFromPermission',
        'getAvailableApplicationRolesOfApplication',
      ],
      ['boOfApplicationsSubject$']
    );
    await TestBed.configureTestingModule({
      declarations: [UserAdministrationApplicationConfigComponent],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        MaterialModule,
        BrowserAnimationsModule,
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationApplicationConfigComponent);
    component = fixture.componentInstance;
    component.applicationConfigManager = applicationConfigManagerMock;
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
    expect(applicationConfigManagerMock.addSboidToPermission).not.toHaveBeenCalled();

    component.businessOrganisationForm.get('businessOrganisation')?.setValue('test');
    component.application = 'TTFN';
    component.add();
    expect(applicationConfigManagerMock.addSboidToPermission).toHaveBeenCalledOnceWith(
      'TTFN',
      'test'
    );
  });

  it('test removeBusinessOrganisation', () => {
    component.application = 'LIDI';
    component.selectedIndex = 0;
    component.remove();
    expect(applicationConfigManagerMock.removeSboidFromPermission).toHaveBeenCalledOnceWith(
      'LIDI',
      0
    );
    expect(component.selectedIndex).toBe(-1);
  });
});
