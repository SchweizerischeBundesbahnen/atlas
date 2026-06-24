import {ComponentFixture, TestBed} from '@angular/core/testing';
import {beforeEach, describe, expect, it, type Mocked, vi} from 'vitest';
import {ServicePointDetailComponent} from './service-point-detail.component';
import {ActivatedRoute, Router} from '@angular/router';
import {BehaviorSubject, of} from 'rxjs';
import {FormGroup} from '@angular/forms';
import {TranslatePipe} from '@ngx-translate/core';
import {Record} from '../../../../core/model/record';
import {
  adminPermissionServiceMock,
  MockAtlasButtonComponent,
  MockNavigationSepodiPrmComponent,
  translateServiceProvider,
} from '../../../../app.testing.mocks';
import {DialogService} from '../../../../core/components/dialog/dialog.service';
import {Country, ReadServicePointVersion, Status, StopPointType} from '../../../../api';
import {NotificationService} from '../../../../core/notification/notification.service';
import {MapService} from '../../map/map.service';
import {Component, input, output} from '@angular/core';
import {BERN} from '../../../../../test/data/service-point';
import {ValidityService} from '../../validity/validity.service';
import {PermissionService} from '../../../../core/auth/permission/permission.service';
import {AtlasButtonComponent} from '../../../../core/components/button/atlas-button.component';
import {NavigationSepodiPrmComponent} from '../../../../core/navigation-sepodi-prm/navigation-sepodi-prm.component';
import {GeographyComponent} from '../../geography/geography.component';
import {provideHttpClient} from '@angular/common/http';
import {provideHttpClientTesting} from '@angular/common/http/testing';
import {provideMomentDateAdapter} from '@angular/material-moment-adapter';
import {ServicePointFormComponent} from './service-point-form/service-point-form.component';
import {TerminationService} from './stop-point-termination/termination.service';
import moment from 'moment';
import {ServicePointService} from '../../../../api/service/sepodi/service-point.service';
import {ServicePointInternalService} from '../../../../api/service/sepodi/service-point-internal.service';

@Component({
  selector: 'atlas-service-point-form',
  template: '<h1>ServicePointFormMockComponent</h1>',
  standalone: true,
})
class ServicePointFormMockComponent {
  readonly form = input<FormGroup>();
  readonly currentVersion = input<object>();
  readonly locationInformation = input<object>();
  readonly selectableStopPointTypes = input.required<StopPointType[]>();
}

@Component({
  selector: 'atlas-sepodi-geography',
  template: '<h1>ServicePointGeographyMockComponent</h1>',
  standalone: true,
})
class ServicePointGeographyMockComponent {
  readonly form = input<FormGroup>();
  readonly editMode = input<boolean>();
  readonly geographyChanged = output();
}

describe('ServicePointDetailComponent', () => {
  let component: ServicePointDetailComponent;
  let fixture: ComponentFixture<ServicePointDetailComponent>;
  let routerSpy: Mocked<Pick<Router, 'navigate'>>;

  let dialogServiceSpy: Mocked<
    Pick<DialogService, 'openDialogDataWithConfirmationResult' | 'openDialogDataWithCustomResult' | 'openWithoutResult'>
  >;
  let servicePointServiceSpy: Mocked<Pick<ServicePointService, 'updateServicePoint'>>;
  let servicePointInternalServiceSpy: Mocked<
    Pick<ServicePointInternalService, 'validateServicePoint' | 'revokeServicePoint'>
  >;
  let notificationServiceSpy: Mocked<Pick<NotificationService, 'success'>>;
  let mapServiceSpy: Mocked<
    Pick<MapService, 'placeMarkerAndFlyTo' | 'deselectServicePoint' | 'refreshMap'> & {
      mapInitialized: BehaviorSubject<boolean>;
    }
  >;

  const activatedRouteMock = { parent: { data: of({ servicePoint: BERN }) } };

  let validityService: ValidityService;
  let terminationService: TerminationService;

  beforeEach(() => {
    Element.prototype.scrollIntoView = vi.fn();

    dialogServiceSpy = {
      openDialogDataWithConfirmationResult: vi.fn(),
      openDialogDataWithCustomResult: vi.fn().mockReturnValue(of(true)),
      openWithoutResult: vi.fn(),
    };
    servicePointServiceSpy = { updateServicePoint: vi.fn() };
    servicePointInternalServiceSpy = {
      validateServicePoint: vi.fn(),
      revokeServicePoint: vi.fn(),
    };
    notificationServiceSpy = { success: vi.fn() };
    mapServiceSpy = {
      placeMarkerAndFlyTo: vi.fn(),
      deselectServicePoint: vi.fn(),
      refreshMap: vi.fn(),
      mapInitialized: new BehaviorSubject<boolean>(false),
    };
    routerSpy = { navigate: vi.fn() };
    routerSpy.navigate.mockReturnValue(Promise.resolve(true));

    TestBed.configureTestingModule({
      imports: [ServicePointDetailComponent],
      providers: [
        translateServiceProvider,
        provideHttpClient(),
        provideHttpClientTesting(),
        provideMomentDateAdapter(),
        ValidityService,
        TerminationService,
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: ServicePointService, useValue: servicePointServiceSpy },
        {
          provide: ServicePointInternalService,
          useValue: servicePointInternalServiceSpy,
        },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: TranslatePipe },
        { provide: MapService, useValue: mapServiceSpy },
        { provide: Router, useValue: routerSpy },
      ],
    }).overrideComponent(ServicePointDetailComponent, {
      remove: {
        imports: [AtlasButtonComponent, NavigationSepodiPrmComponent, GeographyComponent, ServicePointFormComponent],
      },
      add: {
        imports: [
          MockAtlasButtonComponent,
          MockNavigationSepodiPrmComponent,
          ServicePointGeographyMockComponent,
          ServicePointFormMockComponent,
        ],
      },
    });

    fixture = TestBed.createComponent(ServicePointDetailComponent);
    component = fixture.componentInstance;
    validityService = TestBed.inject(ValidityService);
    terminationService = TestBed.inject(TerminationService);
    fixture.detectChanges();
  });

  it('should initialize versioning correctly', () => {
    expect(component.showVersionSwitch).toBe(true);
    expect(component.selectedVersion).toBeTruthy();

    expect((component.servicePointVersions[0] as Record).versionNumber).toBeTruthy();
  });

  it('should initialize form correctly', () => {
    expect(component.form?.disabled).toBe(true);
  });

  it('should switch to edit mode', () => {
    expect(component.form?.disabled).toBe(true);

    component.toggleEdit();
    expect(component.form?.enabled).toBe(true);
  });

  it('should not show revoke button when status in review', () => {
    const version: ReadServicePointVersion = {
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
      status: 'IN_REVIEW',
      country: Country.Switzerland,
    };
    component.servicePointVersions.push(version);

    component.initShowRevokeButton(version);
    expect(component.showRevokeButton).toBeFalsy();
  });

  it('should not show revoke button when status in revoked', () => {
    const version: ReadServicePointVersion = {
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
      status: 'REVOKED',
      country: Country.Switzerland,
    };
    component.servicePointVersions.push(version);
    component.initShowRevokeButton(version);

    expect(component.showRevokeButton).toBeFalsy();
  });

  it('should show revoke button', () => {
    const version: ReadServicePointVersion = {
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
    component.servicePointVersions = [];
    component.servicePointVersions.push(version);
    component.initShowRevokeButton(version);

    expect(component.showRevokeButton).toBe(true);
  });

  it('should switch to readonly mode when not dirty without confirmation', () => {
    component.form?.enable();

    expect(component.form?.enabled).toBe(true);
    expect(component.form?.dirty).toBe(false);

    component.toggleEdit();
    expect(component.form?.disabled).toBe(true);
  });

  it('should switch to readonly mode when dirty with confirmation', () => {
    // given
    component.form?.enable();
    expect(component.form?.enabled).toBe(true);

    component.form?.controls.designationOfficial.setValue('Basel beste Sport');
    component.form?.markAsDirty();
    expect(component.form?.dirty).toBe(true);

    dialogServiceSpy.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

    // when & then
    component.toggleEdit();
    expect(component.form?.disabled).toBe(true);
  });

  it('should stay in edit mode when confirmation canceled', () => {
    // given
    component.form?.enable();
    expect(component.form?.enabled).toBe(true);

    component.form?.controls.designationOfficial.setValue('Basel beste Sport');
    component.form?.markAsDirty();
    expect(component.form?.dirty).toBe(true);

    dialogServiceSpy.openDialogDataWithConfirmationResult.mockReturnValue(of(false));

    // when & then
    component.toggleEdit();
    expect(component.form?.enabled).toBe(true);
  });

  it('should set isAbbreviationAllowed based on selectedVersion.businessOrganisation', () => {
    component.selectedVersion = {
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

    expect(component.isAbbreviationAllowed).toBe(true);

    component.selectedVersion = {
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

    expect(component.isAbbreviationAllowed).toBe(false);
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
        number: {
          number: 123457,
          numberShort: 32,
          uicCountryCode: 0,
          checkDigit: 0,
        },
        status: Status.Validated,
        country: Country.Switzerland,
      },
      selectedVersion,
    ];

    component.isSelectedVersionHighDate(versions, selectedVersion);

    expect(component.isLatestVersionSelected).toBe(true);
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
        number: {
          number: 123457,
          numberShort: 32,
          uicCountryCode: 0,
          checkDigit: 0,
        },
        status: Status.Validated,
        country: Country.Switzerland,
      },
      selectedVersion,
    ];

    component.isSelectedVersionHighDate(versions, selectedVersion);

    expect(component.isLatestVersionSelected).toBe(false);
  });

  it('should validate service point on validate', () => {
    dialogServiceSpy.openDialogDataWithConfirmationResult.mockReturnValue(of(true));
    servicePointInternalServiceSpy.validateServicePoint.mockReturnValue(of(BERN[0]));

    component.validate();

    expect(servicePointInternalServiceSpy.validateServicePoint).toHaveBeenCalled();
  });

  it('should revoke service points on revoke', () => {
    dialogServiceSpy.openDialogDataWithConfirmationResult.mockReturnValue(of(true));
    servicePointInternalServiceSpy.revokeServicePoint.mockReturnValue(of(BERN));

    component.revoke();

    expect(servicePointInternalServiceSpy.revokeServicePoint).toHaveBeenCalled();
  });

  it('should update service point on save', () => {
    vi.spyOn(validityService, 'initValidity');
    vi.spyOn(validityService, 'validateAndDisableCustom');
    vi.spyOn(validityService, 'confirmValidityDialog').mockReturnValue(of(true));

    dialogServiceSpy.openDialogDataWithConfirmationResult.mockReturnValue(of(true));
    servicePointServiceSpy.updateServicePoint.mockReturnValue(of(BERN[0]));

    component.toggleEdit();
    component.form?.controls.designationOfficial.setValue('New YB Station');
    component.save();

    expect(servicePointServiceSpy.updateServicePoint).toHaveBeenCalled();
  });

  it('should start termination on save', () => {
    //given
    vi.spyOn(validityService, 'initValidity');
    vi.spyOn(terminationService, 'isStartingTermination').mockReturnValue(true);
    vi.spyOn(dialogServiceSpy, 'openDialogDataWithCustomResult').mockReturnValue(of(true));

    component.isLatestVersionSelected = true;

    component.toggleEdit();
    component.form?.controls.validityGroup.controls.validTo.setValue(moment('2099-12-30'));
    //when
    component.save();
    //then
    expect(dialogServiceSpy.openDialogDataWithCustomResult).toHaveBeenCalledTimes(1);
  });

  it('should open add workflow dialog', () => {
    component.addWorkflow();

    expect(dialogServiceSpy.openWithoutResult).toHaveBeenCalledTimes(1);
  });
});
