import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationApplicationConfigComponent } from './user-administration-application-config.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import SpyObj = jasmine.SpyObj;
import { UserPermissionManager } from '../user-permission-manager';
import { of } from 'rxjs';
import { BusinessOrganisation } from '../../../api';
import { MaterialModule } from '../../../core/module/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

describe('UserAdministrationApplicationConfigComponent', () => {
  let component: UserAdministrationApplicationConfigComponent;
  let fixture: ComponentFixture<UserAdministrationApplicationConfigComponent>;

  let applicationConfigManagerMock: SpyObj<UserPermissionManager>;

  beforeEach(async () => {
    applicationConfigManagerMock = jasmine.createSpyObj('UserPermissionManager', [
      'getCurrentRole',
      'addSboidToPermission',
      'removeSboidFromPermission',
    ]);
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
    component.relationComponent = {
      table: jasmine.createSpyObj(['renderRows']),
    } as any;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.currentRecords).toEqual([]);
    expect(component.selectedIndex).toBe(-1);
    expect(component.businessOrganisationForm).toBeDefined();
    expect(component.tableColumnDef).toBeDefined();
  });

  it('test getRoleOptions', () => {
    const roleOptions = component.getRoleOptions();
    expect(roleOptions).toContain('WRITER');
    expect(roleOptions).toContain('SUPER_USER');
    expect(roleOptions).toContain('ADMIN');
  });

  it('test isCurrentRoleWriter', () => {
    applicationConfigManagerMock.getCurrentRole.withArgs('TTFN').and.returnValue('WRITER');
    applicationConfigManagerMock.getCurrentRole.withArgs('LIDI').and.returnValue('SUPER_USER');
    component.application = 'TTFN';
    expect(component.isCurrentRoleWriter()).toBe(true);
    component.application = 'LIDI';
    expect(component.isCurrentRoleWriter()).toBe(false);
  });

  it('test addBusinessOrganisation', () => {
    component.addBusinessOrganisation();
    expect(applicationConfigManagerMock.addSboidToPermission).not.toHaveBeenCalled();

    component.businessOrganisationForm.get('businessOrganisation')?.setValue('test');
    applicationConfigManagerMock.addSboidToPermission.and.returnValue(
      of({
        sboid: 'ch:1:sboid:test',
      } as BusinessOrganisation)
    );
    component.application = 'TTFN';
    component.addBusinessOrganisation();
    expect(applicationConfigManagerMock.addSboidToPermission).toHaveBeenCalledOnceWith(
      'TTFN',
      'test'
    );
    expect(component.currentRecords).toEqual([
      {
        sboid: 'ch:1:sboid:test',
      } as BusinessOrganisation,
    ]);
    expect(component.relationComponent.table.renderRows).toHaveBeenCalledOnceWith();
  });

  it('test removeBusinessOrganisation', () => {
    component.currentRecords = [
      {
        sboid: 'ch:1:sboid:test',
      } as BusinessOrganisation,
    ];
    component.application = 'LIDI';
    component.selectedIndex = 0;
    component.removeBusinessOrganisation();
    expect(applicationConfigManagerMock.removeSboidFromPermission).toHaveBeenCalledOnceWith(
      'LIDI',
      0
    );
    expect(component.currentRecords).toEqual([]);
    expect(component.relationComponent.table.renderRows).toHaveBeenCalledOnceWith();
    expect(component.selectedIndex).toBe(-1);
  });
});
