import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute, Router, UrlTree } from '@angular/router';
import { of, throwError } from 'rxjs';
import { BusinessOrganisationVersion } from '../../../../api';
import { BusinessOrganisationDetailComponent } from './business-organisation-detail.component';
import { HttpErrorResponse } from '@angular/common/http';
import { AppTestingModule } from '../../../../app.testing.module';
import { ErrorNotificationComponent } from '../../../../core/notification/error/error-notification.component';
import { AtlasInfoIconComponent } from '@atlas/form';
import { adminPermissionServiceMock, MockSelectComponent, MockTableComponent } from '../../../../app.testing.mocks';
import { FormModule } from '../../../../core/module/form.module';
import { TranslatePipe } from '@ngx-translate/core';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { ValidityService } from '../../../sepodi/validity/validity.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { BusinessOrganisationInternalService } from '../../../../api/service/bodi/business-organisation-internal.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import moment from 'moment';
import { Pages } from '../../../pages';
import { TransportCompanyRelationInternalService } from '../../../../api/service/bodi/transport-company-relation-internal.service';
import { TableComponent } from '../../../../core/components/table/table.component';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

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
            { key: 'number', value: '111' },
            { key: 'validFrom', value: '2020-12-12' },
            { key: 'validTo', value: '2026-12-12' },
            { key: 'ttfnid', value: 'ch:1:ttfnid:1001720' },
          ],
        },
      },
    ],
  },
});

describe('BusinessOrganisationDetailComponent for existing BusinessOrganisationVersion', () => {
  let component: BusinessOrganisationDetailComponent;
  let fixture: ComponentFixture<BusinessOrganisationDetailComponent>;
  let router: Router;

  let mockBusinessOrganisationsService: Mocked<
    Pick<BusinessOrganisationInternalService, 'updateBusinessOrganisationVersion' | 'deleteBusinessOrganisation'>
  >;
  let mockTransportCompanyRelationInternalService: Mocked<
    Pick<TransportCompanyRelationInternalService, 'getBoTransportCompanyRelations'>
  >;
  let validityService: Mocked<
    Pick<
      ValidityService,
      'initValidity' | 'updateValidity' | 'validate' | 'validateAndDisableCustom' | 'confirmValidityDialog'
    >
  >;
  let dialogService: Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>>;

  const mockData = {
    businessOrganisationDetail: [businessOrganisationVersion],
  };

  beforeEach(() => {
    Element.prototype.scrollIntoView = vi.fn();

    dialogService = {
      openDialogDataWithConfirmationResult: vi.fn(),
    };
    dialogService.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

    validityService = {
      initValidity: vi.fn(),
      updateValidity: vi.fn(),
      validate: vi.fn(),
      validateAndDisableCustom: vi.fn(),
      confirmValidityDialog: vi.fn(),
    };

    mockBusinessOrganisationsService = {
      updateBusinessOrganisationVersion: vi.fn(),
      deleteBusinessOrganisation: vi.fn(),
    };

    mockTransportCompanyRelationInternalService = {
      getBoTransportCompanyRelations: vi.fn(),
    };

    mockTransportCompanyRelationInternalService.getBoTransportCompanyRelations.mockReturnValue(
      of([
        {
          validFrom: new Date('2026-01-01'),
          validTo: new Date('2026-01-05'),
          transportCompany: {
            id: 5,
            businessRegisterName: 'regName',
            abbreviation: 'RN',
          },
        },
      ])
    );

    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        FormModule,
        BusinessOrganisationDetailComponent,
        MockSelectComponent,
        ErrorNotificationComponent,
        AtlasInfoIconComponent,
        DetailPageContainerComponent,
        DetailFooterComponent,
      ],
      providers: [
        { provide: FormBuilder },
        {
          provide: BusinessOrganisationInternalService,
          useValue: mockBusinessOrganisationsService,
        },
        {
          provide: TransportCompanyRelationInternalService,
          useValue: mockTransportCompanyRelationInternalService,
        },
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        { provide: ValidityService, useValue: validityService },
        { provide: DialogService, useValue: dialogService },
        { provide: ActivatedRoute, useValue: { snapshot: { data: mockData } } },
        { provide: TranslatePipe },
      ],
    })
      .overrideComponent(BusinessOrganisationDetailComponent, {
        remove: { imports: [TableComponent] },
        add: { imports: [MockTableComponent] },
      })
      .compileComponents()
      .then();

    fixture = TestBed.createComponent(BusinessOrganisationDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
    expect(component.isNew).toBe(false);
  });

  it('should load tu relations', () => {
    expect(mockTransportCompanyRelationInternalService.getBoTransportCompanyRelations).toHaveBeenCalled();
  });

  it('should update BusinessOrganisationVersion successfully', () => {
    mockBusinessOrganisationsService.updateBusinessOrganisationVersion.mockReturnValue(
      of([businessOrganisationVersion])
    );
    vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));

    component.toggleEdit();
    expect(component.editMode()).toBe(true);

    component.formModel.update((form) => {
      return {
        ...form,
        descriptionDe: 'newDescription',
        validFrom: moment('2021-06-05'),
        validTo: moment('2029-06-01'),
      };
    });
    component.save();
    fixture.detectChanges();

    const snackBarContainer = document.body.querySelector('mat-snack-bar-container');

    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer!.textContent.trim()).toEqual('BODI.BUSINESS_ORGANISATION.NOTIFICATION.EDIT_SUCCESS');
    expect(snackBarContainer!.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    mockBusinessOrganisationsService.updateBusinessOrganisationVersion.mockReturnValue(throwError(() => error));

    component.toggleEdit();
    expect(component.editMode()).toBe(true);
    component.formModel.update((form) => {
      return {
        ...form,
        descriptionDe: 'newDescription',
        validFrom: moment('2021-06-05'),
        validTo: moment('2029-06-01'),
      };
    });
    component.save();
    fixture.detectChanges();

    expect(component.editMode()).toBe(true);
  });

  it('should delete BusinessOrganisationVersion successfully', () => {
    mockBusinessOrganisationsService.deleteBusinessOrganisation.mockReturnValue(of(undefined));
    vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));

    component.delete();
    fixture.detectChanges();

    const snackBarContainer = document.body.querySelector('mat-snack-bar-container');

    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer!.textContent.trim()).toBe('BODI.BUSINESS_ORGANISATION.NOTIFICATION.DELETE_SUCCESS');
    expect(snackBarContainer!.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should open transport company in new tab', () => {
    const transportCompanyId = 123;
    const mockUrl = '/bodi/transport-companies/123';

    vi.spyOn(router, 'createUrlTree').mockReturnValue({} as UrlTree);
    vi.spyOn(router, 'serializeUrl').mockReturnValue(mockUrl);
    vi.spyOn(window, 'open').mockImplementation(() => null);

    component.openInNewTab(transportCompanyId);

    expect(router.createUrlTree).toHaveBeenCalledExactlyOnceWith([
      Pages.BODI.path,
      Pages.TRANSPORT_COMPANIES.path,
      transportCompanyId,
    ]);
    expect(router.serializeUrl).toHaveBeenCalledExactlyOnceWith({} as UrlTree);
    expect(window.open).toHaveBeenCalledExactlyOnceWith(mockUrl, '_blank');
  });
});

describe('BusinessOrganisationDetailComponent for new BusinessOrganisationVersion', () => {
  let component: BusinessOrganisationDetailComponent;
  let fixture: ComponentFixture<BusinessOrganisationDetailComponent>;
  let router: Router;

  let mockBusinessOrganisationInternalService: Mocked<
    Pick<BusinessOrganisationInternalService, 'createBusinessOrganisationVersion'>
  >;
  let mockTransportCompanyRelationInternalService: Mocked<
    Pick<TransportCompanyRelationInternalService, 'getBoTransportCompanyRelations'>
  >;
  let validityService: Mocked<Pick<ValidityService, 'initValidity' | 'updateValidity' | 'validate'>>;
  let dialogService: Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>>;

  const mockData = {
    businessOrganisationDetail: [],
  };

  beforeEach(() => {
    Element.prototype.scrollIntoView = vi.fn();

    dialogService = {
      openDialogDataWithConfirmationResult: vi.fn(),
    };
    dialogService.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

    validityService = {
      initValidity: vi.fn(),
      updateValidity: vi.fn(),
      validate: vi.fn(),
    };

    mockBusinessOrganisationInternalService = {
      createBusinessOrganisationVersion: vi.fn(),
    };

    mockTransportCompanyRelationInternalService = {
      getBoTransportCompanyRelations: vi.fn(),
    };

    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        FormModule,
        BusinessOrganisationDetailComponent,
        MockSelectComponent,
        ErrorNotificationComponent,
        AtlasInfoIconComponent,
        DetailPageContainerComponent,
        DetailFooterComponent,
      ],
      providers: [
        { provide: FormBuilder },
        {
          provide: BusinessOrganisationInternalService,
          useValue: mockBusinessOrganisationInternalService,
        },
        {
          provide: TransportCompanyRelationInternalService,
          useValue: mockTransportCompanyRelationInternalService,
        },
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        { provide: ValidityService, useValue: validityService },
        { provide: DialogService, useValue: dialogService },
        { provide: ActivatedRoute, useValue: { snapshot: { data: mockData } } },
        { provide: TranslatePipe },
      ],
    })
      .overrideComponent(BusinessOrganisationDetailComponent, {
        remove: { imports: [TableComponent] },
        add: { imports: [MockTableComponent] },
      })
      .compileComponents()
      .then();

    fixture = TestBed.createComponent(BusinessOrganisationDetailComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
    expect(component.isNew).toBe(true);
  });

  it('should get tu relations ', () => {
    fixture.detectChanges();
    expect(mockTransportCompanyRelationInternalService.getBoTransportCompanyRelations).not.toHaveBeenCalled();
  });

  describe('create new Version', () => {
    it('successfully', () => {
      vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
      mockBusinessOrganisationInternalService.createBusinessOrganisationVersion.mockReturnValue(
        of(businessOrganisationVersion)
      );

      component.ngOnInit();
      component.formModel.update((form) => {
        return {
          ...form,
          organisationNumber: '1234',
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
        };
      });
      component.save();
      fixture.detectChanges();

      const snackBarContainer = document.body.querySelector('mat-snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer!.textContent.trim()).toBe('BODI.BUSINESS_ORGANISATION.NOTIFICATION.ADD_SUCCESS');
      expect(snackBarContainer!.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalled();
    });

    it('displaying error', () => {
      mockBusinessOrganisationInternalService.createBusinessOrganisationVersion.mockReturnValue(
        throwError(() => error)
      );
      component.ngOnInit();
      component.save();
      fixture.detectChanges();

      expect(component.editMode()).toBe(true);
    });
  });
});
