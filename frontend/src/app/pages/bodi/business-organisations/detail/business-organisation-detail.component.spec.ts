import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { BusinessOrganisationsService, BusinessOrganisationVersion } from '../../../../api';
import { BusinessOrganisationDetailComponent } from './business-organisation-detail.component';
import { HttpErrorResponse } from '@angular/common/http';
import { AppTestingModule } from '../../../../app.testing.module';
import { ErrorNotificationComponent } from '../../../../core/notification/error/error-notification.component';
import { InfoIconComponent } from '../../../../core/form-components/info-icon/info-icon.component';
import {
  adminPermissionServiceMock,
  MockAppDetailWrapperComponent,
  MockSelectComponent,
} from '../../../../app.testing.mocks';
import { FormModule } from '../../../../core/module/form.module';
import { TranslatePipe } from '@ngx-translate/core';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { ValidityService } from '../../../sepodi/validity/validity.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';

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
        message: 'Number 111 already taken from 2020-12-12 to 2026-12-12 by ch:1:ttfnid:1001720',
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

describe('BusinessOrganisationDetailComponent for existing BusinessOrganisationVersion', () => {
  const mockBusinessOrganisationsService = jasmine.createSpyObj('businessOrganisationsService', [
    'updateBusinessOrganisationVersion',
    'deleteBusinessOrganisation',
  ]);

  const mockData = {
    businessOrganisationDetail: businessOrganisationVersion,
  };

  const validityService = jasmine.createSpyObj<ValidityService>('validityService', [
    'initValidity',
    'updateValidity',
    'validate',
    'validateAndDisableCustom',
    'confirmValidityDialog',
  ]);

  beforeEach(() => {
    setupTestBed(mockBusinessOrganisationsService, validityService, mockData);

    fixture = TestBed.createComponent(BusinessOrganisationDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should update BusinessOrganisationVersion successfully', () => {
    mockBusinessOrganisationsService.updateBusinessOrganisationVersion.and.returnValue(
      of(businessOrganisationVersion),
    );
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toEqual(
      'BODI.BUSINESS_ORGANISATION.NOTIFICATION.EDIT_SUCCESS',
    );
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    mockBusinessOrganisationsService.updateBusinessOrganisationVersion.and.returnValue(
      throwError(() => error),
    );
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    expect(component.form.enabled).toBeTrue();
  });

  it('should delete BusinessOrganisationVersion successfully', () => {
    mockBusinessOrganisationsService.deleteBusinessOrganisation.and.returnValue(of({}));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    fixture.componentInstance.deleteRecord();
    fixture.detectChanges();

    const snackBarContainer =
      fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toBe(
      'BODI.BUSINESS_ORGANISATION.NOTIFICATION.DELETE_SUCCESS',
    );
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });
});

describe('BusinessOrganisationDetailComponent for new BusinessOrganisationVersion', () => {
  const mockLinesService = jasmine.createSpyObj('businessOrganisationsService', [
    'createBusinessOrganisationVersion',
  ]);
  const mockData = {
    businessOrganisationDetail: 'add',
  };

  const validityService = jasmine.createSpyObj<ValidityService>('validityService', [
    'initValidity',
    'updateValidity',
    'validate',
  ]);
  beforeEach(() => {
    setupTestBed(mockLinesService, validityService, mockData);

    fixture = TestBed.createComponent(BusinessOrganisationDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('create new Version', () => {
    it('successfully', () => {
      spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
      mockLinesService.createBusinessOrganisationVersion.and.returnValue(
        of(businessOrganisationVersion),
      );
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector('mat-snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent.trim()).toBe(
        'BODI.BUSINESS_ORGANISATION.NOTIFICATION.ADD_SUCCESS',
      );
      expect(snackBarContainer.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalled();
    });

    it('displaying error', () => {
      mockLinesService.createBusinessOrganisationVersion.and.returnValue(throwError(() => error));
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      expect(component.form.enabled).toBeTrue();
    });
  });
});

function setupTestBed(
  businessOrganisationsService: BusinessOrganisationsService,
  validityService: ValidityService,
  data: { businessOrganisationDetail: string | BusinessOrganisationVersion },
) {
  TestBed.configureTestingModule({
    declarations: [
      BusinessOrganisationDetailComponent,
      MockAppDetailWrapperComponent,
      MockSelectComponent,
      ErrorNotificationComponent,
      InfoIconComponent,
      DetailPageContainerComponent,
      DetailFooterComponent,
    ],
    imports: [AppTestingModule, FormModule],
    providers: [
      { provide: FormBuilder },
      { provide: BusinessOrganisationsService, useValue: businessOrganisationsService },
      { provide: PermissionService, useValue: adminPermissionServiceMock },
      { provide: ValidityService, useValue: validityService },
      { provide: ActivatedRoute, useValue: { snapshot: { data: data } } },
      { provide: TranslatePipe },
    ],
  })
    .compileComponents()
    .then();
}
