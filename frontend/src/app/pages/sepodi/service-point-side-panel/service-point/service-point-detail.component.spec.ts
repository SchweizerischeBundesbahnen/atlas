import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ServicePointDetailComponent } from './service-point-detail.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { AuthService } from '../../../../core/auth/auth.service';
import { ActivatedRoute } from '@angular/router';
import { BehaviorSubject, of } from 'rxjs';
import { BERN } from '../../service-point-test-data';
import { FormGroup, FormsModule } from '@angular/forms';
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
import {
  ApplicationRole,
  Country,
  ReadServicePointVersion,
  ServicePointsService,
  Status,
} from '../../../../api';
import { NotificationService } from '../../../../core/notification/notification.service';
import { DisplayCantonPipe } from '../../../../core/cantons/display-canton.pipe';
import { MapService } from '../../map/map.service';
import { Component, Input } from '@angular/core';

const dialogServiceSpy = jasmine.createSpyObj('DialogService', ['confirm']);
const servicePointsServiceSpy = jasmine.createSpyObj('ServicePointService', [
  'updateServicePoint',
  'validateServicePoint',
  'revokeServicePoint',
]);
const notificationServiceSpy = jasmine.createSpyObj('NotificationService', ['success']);
const mapServiceSpy = jasmine.createSpyObj('MapService', [
  'placeMarkerAndFlyTo',
  'deselectServicePoint',
  'refreshMap',
]);
mapServiceSpy.mapInitialized = new BehaviorSubject<boolean>(false);

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

@Component({
  selector: 'service-point-form',
  template: '<h1>ServicePointFormMockComponent</h1>',
})
class ServicePointFormMockComponent {
  @Input() form?: FormGroup;
  @Input() currentVersion?: object;
}

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
        ServicePointFormMockComponent,
      ],
      imports: [AppTestingModule, FormsModule],
      providers: [
        { provide: AuthService, useValue: authServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: ServicePointsService, useValue: servicePointsServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: TranslatePipe },
        { provide: MapService, useValue: mapServiceSpy },
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
    expect(component._selectedVersion).toBeTruthy();

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

  it('should set isAbbreviationAllowed based on selectedVersion.businessOrganisation', () => {
    component._selectedVersion = {
      businessOrganisation: 'ch:1:sboid:100016',
      designationOfficial: 'abcd',
      validFrom: new Date(2020 - 10 - 1),
      validTo: new Date(2099 - 10 - 1),
      number: {
        number: 123456,
        numberShort: 31,
        uicCountryCode: 0,
        checkDigit: 0,
      },
      status: 'VALIDATED',
      country: Country.Switzerland,
    };

    component.checkIfAbbreviationIsAllowed();

    expect(component.isAbbreviationAllowed).toBeTrue();

    component._selectedVersion = {
      businessOrganisation: 'falseBusinessOrganisation',
      designationOfficial: 'abcd',
      validFrom: new Date(2020 - 10 - 1),
      validTo: new Date(2099 - 10 - 1),
      number: {
        number: 123456,
        numberShort: 31,
        uicCountryCode: 0,
        checkDigit: 0,
      },
      status: 'VALIDATED',
      country: Country.Switzerland,
    };
    component.checkIfAbbreviationIsAllowed();
    expect(component.isAbbreviationAllowed).toBeFalse();
  });

  it('should set isLatestVersionSelected to true if selected version is the latest', () => {
    const selectedVersion: ReadServicePointVersion = {
      businessOrganisation: 'ch:1:sboid:100016',
      designationOfficial: 'abcd',
      validFrom: new Date(2001, 4, 1),
      validTo: new Date(2004, 11, 31),
      number: {
        number: 123456,
        numberShort: 31,
        uicCountryCode: 0,
        checkDigit: 0,
      },
      status: Status.Validated,
      country: Country.Switzerland,
    };

    const versions: ReadServicePointVersion[] = [
      {
        businessOrganisation: 'ch:1:sboid:100016',
        designationOfficial: 'efgh',
        validFrom: new Date(1999, 0, 1),
        validTo: new Date(2002, 0, 1),
        number: { number: 123457, numberShort: 32, uicCountryCode: 0, checkDigit: 0 },
        status: Status.Validated,
        country: Country.Switzerland,
      },
      selectedVersion,
    ];

    component.isSelectedVersionHighDate(versions, selectedVersion);

    expect(component.isLatestVersionSelected).toBeTrue();
  });

  it('should set isLatestVersionSelected to false if selected version is not the latest', () => {
    const selectedVersion: ReadServicePointVersion = {
      businessOrganisation: 'ch:1:sboid:100016',
      designationOfficial: 'abcd',
      validFrom: new Date(2001, 4, 1),
      validTo: new Date(2004, 11, 31),
      number: {
        number: 123456,
        numberShort: 31,
        uicCountryCode: 0,
        checkDigit: 0,
      },
      status: Status.Validated,
      country: Country.Switzerland,
    };

    const versions: ReadServicePointVersion[] = [
      {
        businessOrganisation: 'ch:1:sboid:100016',
        designationOfficial: 'efgh',
        validFrom: new Date(2020, 0, 1),
        validTo: new Date(2099, 0, 1),
        number: { number: 123457, numberShort: 32, uicCountryCode: 0, checkDigit: 0 },
        status: Status.Validated,
        country: Country.Switzerland,
      },
      selectedVersion,
    ];

    component.isSelectedVersionHighDate(versions, selectedVersion);

    expect(component.isLatestVersionSelected).toBeFalse();
  });

  it('should update service point on save', () => {
    dialogServiceSpy.confirm.and.returnValue(of(true));
    servicePointsServiceSpy.updateServicePoint.and.returnValue(of(BERN));

    component.toggleEdit();
    component.form.controls.designationOfficial.setValue('New YB Station');
    component.save();

    expect(servicePointsServiceSpy.updateServicePoint).toHaveBeenCalled();
  });

  it('should validate service point on validate', () => {
    dialogServiceSpy.confirm.and.returnValue(of(true));
    servicePointsServiceSpy.validateServicePoint.and.returnValue(of(BERN));

    component.validate();

    expect(servicePointsServiceSpy.validateServicePoint).toHaveBeenCalled();
  });

  it('should revoke service points on revoke', () => {
    dialogServiceSpy.confirm.and.returnValue(of(true));
    servicePointsServiceSpy.revokeServicePoint.and.returnValue(of(BERN));

    component.revoke();

    expect(servicePointsServiceSpy.revokeServicePoint).toHaveBeenCalled();
  });
});
