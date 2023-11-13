import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AtlasButtonComponent } from './atlas-button.component';
import { AppTestingModule } from '../../../app.testing.module';
import { AuthService } from '../../auth/auth.service';
import { Role } from '../../auth/role';
import { ApplicationRole, ApplicationType, Permission } from '../../../api';
import { AtlasButtonType } from './atlas-button.type';
import { By } from '@angular/platform-browser';

let component: AtlasButtonComponent;
let fixture: ComponentFixture<AtlasButtonComponent>;

let isAdmin = true;
let isAtLeastSupervisor = true;
let hasPermissionsToCreate = true;
let hasPermissionsToWrite = true;
let role = ApplicationRole.Reader;
const authServiceMock: Partial<AuthService> = {
  claims: {
    name: 'Test (ITC)',
    email: 'test@test.ch',
    sbbuid: 'e123456',
    roles: ['lidi-admin', 'lidi-writer'],
  },
  logout: () => Promise.resolve(true),
  login: () => Promise.resolve(true),
  hasAnyRole(roles: Role[]): boolean {
    for (const role of roles) {
      if (this.claims?.roles.includes(role)) return true;
    }
    return false;
  },
  hasRole(role: Role): boolean {
    return this.claims!.roles.includes(role);
  },
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
    return { application: applicationType, role: role, permissionRestrictions: [] };
  },
};

describe('AtlasButtonComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AtlasButtonComponent],
      imports: [AppTestingModule],
      providers: [{ provide: AuthService, useValue: authServiceMock }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AtlasButtonComponent);
    component = fixture.componentInstance;

    isAdmin = true;
    isAtLeastSupervisor = true;
    hasPermissionsToCreate = true;
    hasPermissionsToWrite = true;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('Visibility', () => {
    it('should be visible for type CREATE', () => {
      component.buttonType = AtlasButtonType.CREATE;
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeTruthy();
    });

    it('should be visible for type CREATE_CHECKING_PERMISSION', () => {
      component.buttonType = AtlasButtonType.CREATE_CHECKING_PERMISSION;
      component.applicationType = ApplicationType.Bodi;
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeTruthy();
    });

    it('should not be visible for type CREATE_CHECKING_PERMISSION', () => {
      hasPermissionsToCreate = false;
      component.buttonType = AtlasButtonType.CREATE_CHECKING_PERMISSION;
      component.applicationType = ApplicationType.Bodi;
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeFalsy();
    });

    it('should be visible for type EDIT', () => {
      component.buttonType = AtlasButtonType.EDIT;
      component.applicationType = ApplicationType.Bodi;
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeTruthy();
    });

    it('should not be visible for type EDIT', () => {
      hasPermissionsToWrite = false;
      component.buttonType = AtlasButtonType.EDIT;
      component.applicationType = ApplicationType.Bodi;
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeFalsy();
    });

    it('should not be visible for type EDIT with uicCountryCode', () => {
      hasPermissionsToWrite = false;
      component.buttonType = AtlasButtonType.EDIT;
      component.applicationType = ApplicationType.Bodi;
      component.uicCountryCode = 85;
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeFalsy();
    });

    it('should be visible for type REVOKE', () => {
      component.buttonType = AtlasButtonType.REVOKE;
      component.applicationType = ApplicationType.Bodi;
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeTruthy();
    });

    it('should not be visible for type REVOKE', () => {
      isAdmin = false;
      isAtLeastSupervisor = false;
      role = ApplicationRole.Reader;
      component.buttonType = AtlasButtonType.REVOKE;
      component.applicationType = ApplicationType.Bodi;
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeFalsy();
    });

    it('should be visible for type DELETE', () => {
      component.buttonType = AtlasButtonType.DELETE;
      component.applicationType = ApplicationType.Bodi;
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeTruthy();
    });

    it('should not be visible for type DELETE', () => {
      isAdmin = false;
      component.buttonType = AtlasButtonType.DELETE;
      component.applicationType = ApplicationType.Bodi;
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeFalsy();
    });

    it('should not be visible for type EDIT_SERVICE_POINT_DEPENDENT', () => {
      hasPermissionsToWrite = false;
      component.buttonType = AtlasButtonType.EDIT_SERVICE_POINT_DEPENDENT;
      component.applicationType = ApplicationType.Sepodi;
      component.businessOrganisations = ['sboid'];
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeFalsy();
    });

    it('should be visible for type EDIT_SERVICE_POINT_DEPENDENT', () => {
      hasPermissionsToWrite = true;
      component.buttonType = AtlasButtonType.EDIT_SERVICE_POINT_DEPENDENT;
      component.applicationType = ApplicationType.Sepodi;
      component.businessOrganisations = ['sboid'];
      fixture.detectChanges();

      const button = fixture.debugElement.query(By.css('button'));
      expect(button).toBeTruthy();
    });
  });
});
