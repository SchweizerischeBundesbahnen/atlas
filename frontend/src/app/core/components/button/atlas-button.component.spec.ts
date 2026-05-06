import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { AtlasButtonComponent } from './atlas-button.component';
import { ApplicationRole, ApplicationType, Permission } from '../../../api';
import { AtlasButtonType } from './atlas-button.type';
import { By } from '@angular/platform-browser';
import { PermissionService } from '../../auth/permission/permission.service';
import { inputBinding, signal } from '@angular/core';
import { translateServiceProvider } from '../../../app.testing.mocks';

describe('AtlasButtonComponent', () => {
  let fixture: ComponentFixture<AtlasButtonComponent>;
  let buttonTypeInput: ReturnType<typeof signal<AtlasButtonType>>;
  let applicationTypeInput: ReturnType<typeof signal<ApplicationType>>;
  let businessOrganisationsInput: ReturnType<typeof signal<string[]>>;
  let businessOrganisationInput: ReturnType<typeof signal<string | undefined>>;
  let uicCountryCodeInput: ReturnType<typeof signal<number | undefined>>;

  let isAdmin = true;
  let isAtLeastSupervisor = true;
  let hasPermissionsToCreate = true;
  let hasPermissionsToWrite = true;
  let role = ApplicationRole.Reader;

  const permissionServiceMock: Partial<PermissionService> = {
    get isAdmin(): boolean {
      return isAdmin;
    },
    hasPermissionsToWrite(): boolean {
      return hasPermissionsToWrite;
    },
    hasPermissionsToCreate(): boolean {
      return hasPermissionsToCreate;
    },
    isAtLeastSupervisor(): boolean {
      return isAtLeastSupervisor;
    },
    getApplicationUserPermission(applicationType: ApplicationType): Permission {
      return {
        application: applicationType,
        role: role,
        permissionRestrictions: [],
      };
    },
  };

  beforeEach(() => {
    // Config
    TestBed.configureTestingModule({
      providers: [translateServiceProvider, { provide: PermissionService, useValue: permissionServiceMock }],
    });

    // Arrangement
    const buttonTypeInputName: keyof AtlasButtonComponent = 'buttonType';
    const applicationTypeInputName: keyof AtlasButtonComponent = 'applicationType';
    const businessOrganisationsInputName: keyof AtlasButtonComponent = 'businessOrganisations';
    const businessOrganisationInputName: keyof AtlasButtonComponent = 'businessOrganisation';
    const uicCountryCodeInputName: keyof AtlasButtonComponent = 'uicCountryCode';

    buttonTypeInput = signal(AtlasButtonType.DEFAULT_PRIMARY);
    applicationTypeInput = signal(ApplicationType.Bodi);
    businessOrganisationsInput = signal([]);
    businessOrganisationInput = signal(undefined);
    uicCountryCodeInput = signal(undefined);

    fixture = TestBed.createComponent(AtlasButtonComponent, {
      bindings: [
        inputBinding(buttonTypeInputName, buttonTypeInput),
        inputBinding(applicationTypeInputName, applicationTypeInput),
        inputBinding(businessOrganisationsInputName, businessOrganisationsInput),
        inputBinding(businessOrganisationInputName, businessOrganisationInput),
        inputBinding(uicCountryCodeInputName, uicCountryCodeInput),
      ],
    });

    isAdmin = true;
    isAtLeastSupervisor = true;
    hasPermissionsToCreate = true;
    hasPermissionsToWrite = true;
  });

  describe('Visibility', () => {
    it('should be visible for type CREATE', () => {
      buttonTypeInput.set(AtlasButtonType.CREATE);
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeTruthy();
    });

    it('should be visible for type CREATE_CHECKING_PERMISSION', () => {
      buttonTypeInput.set(AtlasButtonType.CREATE_CHECKING_PERMISSION);
      applicationTypeInput.set(ApplicationType.Bodi);
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeTruthy();
    });

    it('should not be visible for type CREATE_CHECKING_PERMISSION', () => {
      hasPermissionsToCreate = false;
      buttonTypeInput.set(AtlasButtonType.CREATE_CHECKING_PERMISSION);
      applicationTypeInput.set(ApplicationType.Bodi);
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeFalsy();
    });

    it('should be visible for type EDIT', () => {
      buttonTypeInput.set(AtlasButtonType.EDIT);
      applicationTypeInput.set(ApplicationType.Bodi);
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeTruthy();
    });

    it('should not be visible for type EDIT', () => {
      hasPermissionsToWrite = false;
      buttonTypeInput.set(AtlasButtonType.EDIT);
      applicationTypeInput.set(ApplicationType.Bodi);
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeFalsy();
    });

    it('should not be visible for type EDIT with uicCountryCode', () => {
      hasPermissionsToWrite = false;
      buttonTypeInput.set(AtlasButtonType.EDIT);
      applicationTypeInput.set(ApplicationType.Bodi);
      uicCountryCodeInput.set(85);
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeFalsy();
    });

    it('should be visible for type REVOKE', () => {
      buttonTypeInput.set(AtlasButtonType.REVOKE);
      applicationTypeInput.set(ApplicationType.Bodi);
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeTruthy();
    });

    it('should not be visible for type REVOKE', () => {
      isAdmin = false;
      isAtLeastSupervisor = false;
      role = ApplicationRole.Reader;
      buttonTypeInput.set(AtlasButtonType.REVOKE);
      applicationTypeInput.set(ApplicationType.Bodi);
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeFalsy();
    });

    it('should be visible for type DELETE', () => {
      buttonTypeInput.set(AtlasButtonType.DELETE);
      applicationTypeInput.set(ApplicationType.Bodi);
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeTruthy();
    });

    it('should not be visible for type DELETE', () => {
      isAdmin = false;
      buttonTypeInput.set(AtlasButtonType.DELETE);
      applicationTypeInput.set(ApplicationType.Bodi);
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeFalsy();
    });

    it('should not be visible for type EDIT_SERVICE_POINT_DEPENDENT', () => {
      hasPermissionsToWrite = false;
      buttonTypeInput.set(AtlasButtonType.EDIT_SERVICE_POINT_DEPENDENT);
      applicationTypeInput.set(ApplicationType.Sepodi);
      businessOrganisationsInput.set(['sboid']);
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeFalsy();
    });

    it('should be visible for type EDIT_SERVICE_POINT_DEPENDENT', () => {
      hasPermissionsToWrite = true;
      buttonTypeInput.set(AtlasButtonType.EDIT_SERVICE_POINT_DEPENDENT);
      applicationTypeInput.set(ApplicationType.Sepodi);
      businessOrganisationsInput.set(['sboid']);
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeTruthy();
    });
  });
});
