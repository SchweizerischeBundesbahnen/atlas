import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ServicePointDetailComponent } from './service-point-detail.component';
import { ActivatedRoute, Router } from '@angular/router';
import { BehaviorSubject, of } from 'rxjs';
import { FormGroup } from '@angular/forms';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { Record } from '../../../../core/components/base-detail/record';
import {
  adminPermissionServiceMock,
  MockAtlasButtonComponent,
  MockNavigationSepodiPrmComponent,
} from '../../../../app.testing.mocks';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import {
  Country,
  ReadServicePointVersion,
  ServicePointsService,
  Status,
} from '../../../../api';
import { NotificationService } from '../../../../core/notification/notification.service';
import { MapService } from '../../map/map.service';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { BERN } from '../../../../../test/data/service-point';
import { ValidityService } from '../../validity/validity.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { AddStopPointWorkflowDialogService } from '../../workflow/add-dialog/add-stop-point-workflow-dialog.service';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { NavigationSepodiPrmComponent } from '../../../../core/navigation-sepodi-prm/navigation-sepodi-prm.component';
import { GeographyComponent } from '../../geography/geography.component';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';
import { ServicePointFormComponent } from './service-point-form/service-point-form.component';
import { TerminationService } from './stop-point-termination/termination.service';
import moment from 'moment';
import { StopPointTerminationDialogService } from './stop-point-termination/stop-point-termination-dialog/stop-point-termination-dialog.service';
import SpyObj = jasmine.SpyObj;

const dialogServiceSpy = jasmine.createSpyObj('DialogService', ['confirm']);
const servicePointsServiceSpy = jasmine.createSpyObj('ServicePointService', [
  'updateServicePoint',
  'validateServicePoint',
  'revokeServicePoint',
]);

const notificationServiceSpy = jasmine.createSpyObj('NotificationService', [
  'success',
]);
const mapServiceSpy = jasmine.createSpyObj('MapService', [
  'placeMarkerAndFlyTo',
  'deselectServicePoint',
  'refreshMap',
]);
mapServiceSpy.mapInitialized = new BehaviorSubject<boolean>(false);

const addStopPointWorkflowDialogService = jasmine.createSpyObj(
  'addStopPointWorkflowDialogService',
  ['openDialog']
);

@Component({
  selector: 'service-point-form',
  template: '<h1>ServicePointFormMockComponent</h1>',
  standalone: true,
})
class ServicePointFormMockComponent {
  @Input() form?: FormGroup;
  @Input() currentVersion?: object;
  @Input() locationInformation?: object;
}

@Component({
  selector: 'sepodi-geography',
  template: '<h1>ServicePointGeographyMockComponent</h1>',
  standalone: true,
})
class ServicePointGeographyMockComponent {
  @Input() form?: FormGroup;
  @Input() editMode?: boolean;
  @Output() geographyChanged = new EventEmitter();
}

describe('ServicePointDetailComponent', () => {
  let component: ServicePointDetailComponent;
  let fixture: ComponentFixture<ServicePointDetailComponent>;
  let routerSpy: SpyObj<Router>;

  const activatedRouteMock = { parent: { data: of({ servicePoint: BERN }) } };

  let validityService: ValidityService;
  let terminationService: TerminationService;
  let stopPointTerminationDialogService: StopPointTerminationDialogService;

  beforeEach(async () => {
    routerSpy = jasmine.createSpyObj(['navigate']);
    routerSpy.navigate.and.returnValue(Promise.resolve(true));

    await TestBed.configureTestingModule({
      imports: [ServicePointDetailComponent, TranslateModule.forRoot()],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideMomentDateAdapter(),
        ValidityService,
        TerminationService,
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: ServicePointsService, useValue: servicePointsServiceSpy },
        { provide: NotificationService, useValue: notificationServiceSpy },
        { provide: TranslatePipe },
        { provide: MapService, useValue: mapServiceSpy },
        {
          provide: AddStopPointWorkflowDialogService,
          useValue: addStopPointWorkflowDialogService,
        },
        { provide: Router, useValue: routerSpy },
      ],
    })
      .overrideComponent(ServicePointDetailComponent, {
        remove: {
          imports: [
            AtlasButtonComponent,
            NavigationSepodiPrmComponent,
            GeographyComponent,
            ServicePointFormComponent,
          ],
        },
        add: {
          imports: [
            MockAtlasButtonComponent,
            MockNavigationSepodiPrmComponent,
            ServicePointGeographyMockComponent,
            ServicePointFormMockComponent,
          ],
        },
      })
      .compileComponents();

    fixture = TestBed.createComponent(ServicePointDetailComponent);
    component = fixture.componentInstance;
    validityService = TestBed.inject(ValidityService);
    terminationService = TestBed.inject(TerminationService);
    stopPointTerminationDialogService = TestBed.inject(
      StopPointTerminationDialogService
    );
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize versioning correctly', () => {
    expect(component.showVersionSwitch).toBeTrue();
    expect(component.selectedVersion).toBeTruthy();

    expect(
      (component.servicePointVersions[0] as Record).versionNumber
    ).toBeTruthy();
  });

  it('should initialize form correctly', () => {
    expect(component.form?.disabled).toBeTrue();
  });

  it('should switch to edit mode', () => {
    expect(component.form?.disabled).toBeTrue();

    component.toggleEdit();
    expect(component.form?.enabled).toBeTrue();
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
    component.servicePointVersions = [];
    fixture.detectChanges();
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
    component.servicePointVersions = [];
    fixture.detectChanges();
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

    fixture.detectChanges();
    component.initShowRevokeButton(version);
    expect(component.showRevokeButton).toBeTrue();
    component.servicePointVersions = [];
    fixture.detectChanges();
  });

  it('should switch to readonly mode when not dirty without confirmation', () => {
    component.form?.enable();

    expect(component.form?.enabled).toBeTrue();
    expect(component.form?.dirty).toBeFalse();

    component.toggleEdit();
    expect(component.form?.disabled).toBeTrue();
  });

  it('should switch to readonly mode when dirty with confirmation', () => {
    // given
    component.form?.enable();
    expect(component.form?.enabled).toBeTrue();

    component.form?.controls.designationOfficial.setValue('Basel beste Sport');
    component.form?.markAsDirty();
    expect(component.form?.dirty).toBeTrue();

    dialogServiceSpy.confirm.and.returnValue(of(true));

    // when & then
    component.toggleEdit();
    expect(component.form?.disabled).toBeTrue();
  });

  it('should stay in edit mode when confirmation canceled', () => {
    // given
    component.form?.enable();
    expect(component.form?.enabled).toBeTrue();

    component.form?.controls.designationOfficial.setValue('Basel beste Sport');
    component.form?.markAsDirty();
    expect(component.form?.dirty).toBeTrue();

    dialogServiceSpy.confirm.and.returnValue(of(false));

    // when & then
    component.toggleEdit();
    expect(component.form?.enabled).toBeTrue();
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

    expect(component.isAbbreviationAllowed).toBeTrue();

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

    expect(component.isLatestVersionSelected).toBeFalse();
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

  it('should update service point on save', () => {
    spyOn(validityService, 'initValidity').and.callThrough();
    spyOn(validityService, 'validateAndDisableCustom').and.callThrough();
    spyOn(validityService, 'confirmValidityDialog').and.returnValue(of(true));

    dialogServiceSpy.confirm.and.returnValue(of(true));
    servicePointsServiceSpy.updateServicePoint.and.returnValue(of(BERN));

    component.toggleEdit();
    component.form?.controls.designationOfficial.setValue('New YB Station');
    component.save();

    expect(servicePointsServiceSpy.updateServicePoint).toHaveBeenCalled();
  });

  it('should start termination on save', () => {
    //given
    spyOn(validityService, 'initValidity').and.callThrough();
    spyOn(terminationService, 'isStartingTermination').and.returnValue(true);
    spyOn(stopPointTerminationDialogService, 'openDialog').and.returnValue(
      of(true)
    );

    component.isLatestVersionSelected = true;

    component.toggleEdit();
    component.form?.controls.validTo.setValue(moment('2099-12-30'));
    fixture.detectChanges();
    //when
    component.save();
    //then
    expect(stopPointTerminationDialogService.openDialog).toHaveBeenCalled();
  });

  it('should open add workflow dialog', () => {
    component.addWorkflow();

    expect(addStopPointWorkflowDialogService.openDialog).toHaveBeenCalled();
  });
});
