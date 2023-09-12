import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ServicePointDetailComponent } from './service-point-detail.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { AuthService } from '../../../../core/auth/auth.service';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { BERN } from '../../service-point-test-data';
import { FormsModule } from '@angular/forms';
import { TextFieldComponent } from '../../../../core/form-components/text-field/text-field.component';
import { MeansOfTransportPickerComponent } from '../../means-of-transport-picker/means-of-transport-picker.component';
import { SelectComponent } from '../../../../core/form-components/select/select.component';
import { SwitchVersionComponent } from '../../../../core/components/switch-version/switch-version.component';
import { AtlasSlideToggleComponent } from '../../../../core/form-components/atlas-slide-toggle/atlas-slide-toggle.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasLabelFieldComponent } from '../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { AtlasFieldErrorComponent } from '../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { AtlasSpacerComponent } from '../../../../core/components/spacer/atlas-spacer.component';
import { Record } from '../../../../core/components/base-detail/record';
import { MockAtlasButtonComponent } from '../../../../app.testing.mocks';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { ApplicationRole, ServicePointsService } from '../../../../api';
import { NotificationService } from '../../../../core/notification/notification.service';
import { ServicePointType } from './service-point-type';
import { DisplayCantonPipe } from '../../../../core/cantons/display-canton.pipe';

const dialogServiceSpy = jasmine.createSpyObj('DialogService', ['confirm']);
const servicePointsServiceSpy = jasmine.createSpyObj('ServicePointService', ['updateServicePoint']);
const notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['success']);
const authServiceMock: Partial<AuthService> = {
  claims: { name: 'Test', email: 'test@test.ch', sbbuid: 'e123456', roles: [] },
  isAdmin: false,
  getPermissions: () => [],
  getApplicationUserPermission: (applicationType) => {
    return {
      application: applicationType,
      role: ApplicationRole.Writer,
      permissionRestrictions: [],
    };
  },
  logout: () => Promise.resolve(true),
};

describe('ServicePointDetailComponent', () => {
  let component: ServicePointDetailComponent;
  let fixture: ComponentFixture<ServicePointDetailComponent>;

  const activatedRouteMock = { parent: { data: of({ servicePoint: BERN }) } };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        ServicePointDetailComponent,
        TextFieldComponent,
        AtlasLabelFieldComponent,
        AtlasFieldErrorComponent,
        AtlasSpacerComponent,
        MeansOfTransportPickerComponent,
        SelectComponent,
        SwitchVersionComponent,
        AtlasSlideToggleComponent,
        MockAtlasButtonComponent,
        DisplayCantonPipe,
      ],
      imports: [AppTestingModule, FormsModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: ServicePointsService, useValue: servicePointsServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: TranslatePipe },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ServicePointDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize versioning correctly', () => {
    expect(component.showVersionSwitch).toBeTrue();
    expect(component.selectedVersion).toBeTruthy();

    expect((component.servicePointVersions[0] as Record).versionNumber).toBeTruthy();
  });

  it('should initialize form correctly', () => {
    expect(component.isNew).toBeFalse();
    expect(component.form.disabled).toBeTrue();
  });

  it('should switch to edit mode', () => {
    expect(component.form.disabled).toBeTrue();

    component.toggleEdit();
    expect(component.form.enabled).toBeTrue();
  });

  it('should switch to readonly mode when not dirty without confirmation', () => {
    component.form.enable();
    expect(component.form.enabled).toBeTrue();
    expect(component.form.dirty).toBeFalse();

    component.toggleEdit();
    expect(component.form.disabled).toBeTrue();
  });

  it('should switch to readonly mode when dirty with confirmation', () => {
    // given
    component.form.enable();
    expect(component.form.enabled).toBeTrue();

    component.form.controls.designationOfficial.setValue('Basel beste Sport');
    component.form.markAsDirty();
    expect(component.form.dirty).toBeTrue();

    dialogServiceSpy.confirm.and.returnValue(of(true));

    // when & then
    component.toggleEdit();
    expect(component.form.disabled).toBeTrue();
  });

  it('should stay in edit mode when confirmation canceled', () => {
    // given
    component.form.enable();
    expect(component.form.enabled).toBeTrue();

    component.form.controls.designationOfficial.setValue('Basel beste Sport');
    component.form.markAsDirty();
    expect(component.form.dirty).toBeTrue();

    dialogServiceSpy.confirm.and.returnValue(of(false));

    // when & then
    component.toggleEdit();
    expect(component.form.enabled).toBeTrue();
  });
});
