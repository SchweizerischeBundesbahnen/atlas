import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AtlasButtonComponent } from './atlas-button.component';
import { AppTestingModule } from '../../../app.testing.module';
import { AuthService } from '../../auth/auth.service';
import { Role } from '../../auth/role';
import { ApplicationRole, ApplicationType, UserPermissionModel } from '../../../api';

let component: AtlasButtonComponent;
let fixture: ComponentFixture<AtlasButtonComponent>;

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
    for (let role of roles) {
      if (this.claims?.roles.includes(role)) return true;
    }
    return false;
  },
  hasRole(role: Role): boolean {
    return this.claims!.roles.includes(role);
  },
  get isAdmin(): boolean {
    return true;
  },
  hasPermissionsToWrite(): boolean {
    return true;
  },
  getApplicationUserPermission(applicationType: ApplicationType): UserPermissionModel {
    return { application: applicationType, role: ApplicationRole.Supervisor, sboids: [] };
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
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
