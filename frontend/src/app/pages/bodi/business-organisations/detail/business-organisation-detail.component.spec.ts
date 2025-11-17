import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { BusinessOrganisationVersion } from '../../../../api';
import { BusinessOrganisationDetailComponent } from './business-organisation-detail.component';
import { HttpErrorResponse } from '@angular/common/http';
import { AppTestingModule } from '../../../../app.testing.module';
import { ErrorNotificationComponent } from '../../../../core/notification/error/error-notification.component';
import { InfoIconComponent } from '@atlas/form/info-icon/info-icon.component';
import {
  adminPermissionServiceMock,
  MockSelectComponent,
} from '../../../../app.testing.mocks';
import { FormModule } from '../../../../core/module/form.module';
import { TranslatePipe } from '@ngx-translate/core';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { ValidityService } from '../../../sepodi/validity/validity.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { BusinessOrganisationInternalService } from '../../../../api/service/bodi/business-organisation-internal.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import moment from 'moment';

const businessOrganisationVersion: BusinessOrganisationVersion = {
  id: 1234,
  organisationNumber: 1234,
  sboid: 'sboid',
  descriptionDe: 'asdf',
  descriptionFr: 'asdf',
  descriptionIt: 'asdf',
  descriptionEn: 'asdf',
  abbreviationDe: 'asdf',
  abbreviationFr: 'asdf',
  abbreviationIt: 'asdf',
  abbreviationEn: 'asdf',
  status: 'VALIDATED',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
};

const error = new HttpErrorResponse({
  status: 404,
  error: {
    message: 'Not found',
    details: [
      {
        message:
          'Number 111 already taken from 2020-12-12 to 2026-12-12 by ch:1:ttfnid:1001720',
        field: 'number',
        displayInfo: {
          code: 'TTFN.CONFLICT.NUMBER',
          parameters: [
            {
              key: 'number',
              value: '111',
            },
            {
              key: 'validFrom',
              value: '2020-12-12',
            },
            {
              key: 'validTo',
              value: '2026-12-12',
            },
            {
              key: 'ttfnid',
              value: 'ch:1:ttfnid:1001720',
            },
          ],
        },
      },
    ],
  },
});

let component: BusinessOrganisationDetailComponent;
let fixture: ComponentFixture<BusinessOrganisationDetailComponent>;
let router: Router;

const dialogService = jasmine.createSpyObj<DialogService>('DialogService', {
  confirm: of(true),
});

describe('BusinessOrganisationDetailComponent for existing BusinessOrganisationVersion', () => {
  const mockBusinessOrganisationsService = jasmine.createSpyObj([
    'updateBusinessOrganisationVersion',
    'deleteBusinessOrganisation',
  ]);

  const mockData = {
    businessOrganisationDetail: [businessOrganisationVersion],
  };

  const validityService = jasmine.createSpyObj<ValidityService>(
    'validityService',
    [
      'initValidity',
      'updateValidity',
      'validate',
      'validateAndDisableCustom',
      'confirmValidityDialog',
    ]
  );

  beforeEach(() => {
    setupTestBed(mockBusinessOrganisationsService, validityService, mockData);

    fixture = TestBed.createComponent(BusinessOrganisationDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
    expect(component.isNew).toBeFalse();
  });

  it('should update BusinessOrganisationVersion successfully', () => {
    mockBusinessOrganisationsService.updateBusinessOrganisationVersion.and.returnValue(
      of(businessOrganisationVersion)
    );
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    component.toggleEdit();
    expect(component.form.enabled).toBeTrue();

    component.form.patchValue({
      descriptionDe: 'newDescription',
      validFrom: moment('2021-06-05'),
      validTo: moment('2029-06-01'),
    });
    component.save();
    fixture.detectChanges();

    const snackBarContainer = fixture.nativeElement.offsetParent.querySelector(
      'mat-snack-bar-container'
    );
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toEqual(
      'BODI.BUSINESS_ORGANISATION.NOTIFICATION.EDIT_SUCCESS'
    );
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    mockBusinessOrganisationsService.updateBusinessOrganisationVersion.and.returnValue(
      throwError(() => error)
    );

    component.toggleEdit();
    expect(component.form.enabled).toBeTrue();
    component.form.patchValue({
      descriptionDe: 'newDescription',
      validFrom: moment('2021-06-05'),
      validTo: moment('2029-06-01'),
    });
    component.save();
    fixture.detectChanges();

    expect(component.form.enabled).toBeTrue();
  });

  it('should delete BusinessOrganisationVersion successfully', () => {
    mockBusinessOrganisationsService.deleteBusinessOrganisation.and.returnValue(
      of({})
    );
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    component.delete();
    fixture.detectChanges();

    const snackBarContainer = fixture.nativeElement.offsetParent.querySelector(
      'mat-snack-bar-container'
    );
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toBe(
      'BODI.BUSINESS_ORGANISATION.NOTIFICATION.DELETE_SUCCESS'
    );
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });
});

describe('BusinessOrganisationDetailComponent for new BusinessOrganisationVersion', () => {
  const mockLinesService = jasmine.createSpyObj(
    'businessOrganisationsService',
    ['createBusinessOrganisationVersion']
  );
  const mockData = {
    businessOrganisationDetail: [],
  };

  const validityService = jasmine.createSpyObj<ValidityService>(
    'validityService',
    ['initValidity', 'updateValidity', 'validate']
  );
  beforeEach(() => {
    setupTestBed(mockLinesService, validityService, mockData);

    fixture = TestBed.createComponent(BusinessOrganisationDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.isNew).toBeTrue();
  });

  describe('create new Version', () => {
    it('successfully', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      mockLinesService.createBusinessOrganisationVersion.and.returnValue(
        of(businessOrganisationVersion)
      );

      component.form.patchValue({
        organisationNumber: 1234,
        descriptionDe: 'asdf',
        descriptionFr: 'asdf',
        descriptionIt: 'asdf',
        descriptionEn: 'asdf',
        abbreviationDe: 'asdf',
        abbreviationFr: 'asdf',
        abbreviationIt: 'asdf',
        abbreviationEn: 'asdf',
        validFrom: moment('2021-06-01'),
        validTo: moment('2029-06-01'),
      });
      component.save();
      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector(
          'mat-snack-bar-container'
        );
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent.trim()).toBe(
        'BODI.BUSINESS_ORGANISATION.NOTIFICATION.ADD_SUCCESS'
      );
      expect(snackBarContainer.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalled();
    });

    it('displaying error', () => {
      mockLinesService.createBusinessOrganisationVersion.and.returnValue(
        throwError(() => error)
      );
      component.save();
      fixture.detectChanges();

      expect(component.form.enabled).toBeTrue();
    });
  });
});

function setupTestBed(
  businessOrganisationInternalService: BusinessOrganisationInternalService,
  validityService: ValidityService,
  data: { businessOrganisationDetail: BusinessOrganisationVersion[] }
) {
  TestBed.configureTestingModule({
    imports: [
      AppTestingModule,
      FormModule,
      BusinessOrganisationDetailComponent,
      MockSelectComponent,
      ErrorNotificationComponent,
      InfoIconComponent,
      DetailPageContainerComponent,
      DetailFooterComponent,
    ],
    providers: [
      { provide: FormBuilder },
      {
        provide: BusinessOrganisationInternalService,
        useValue: businessOrganisationInternalService,
      },
      { provide: PermissionService, useValue: adminPermissionServiceMock },
      { provide: ValidityService, useValue: validityService },
      { provide: DialogService, useValue: dialogService },
      { provide: ActivatedRoute, useValue: { snapshot: { data: data } } },
      { provide: TranslatePipe },
    ],
  })
    .compileComponents()
    .then();
}
