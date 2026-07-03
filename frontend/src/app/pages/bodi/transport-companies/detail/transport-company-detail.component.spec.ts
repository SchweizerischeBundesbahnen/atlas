import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { BusinessOrganisation, TransportCompany, TransportCompanyBoRelation } from '../../../../api';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { TransportCompanyDetailFacade } from './transport-company-detail.facade';
import { TransportCompanyDetailComponent } from './transport-company-detail.component';
import { adminPermissionServiceMock, translateServiceProvider } from '../../../../app.testing.mocks';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { of } from 'rxjs';
import moment from 'moment';

const transportCompany: TransportCompany = {
  id: 1234,
  description: 'SBB',
};

const transportCompanyRelations: TransportCompanyBoRelation[] = [
  {
    id: 1,
    businessOrganisation: {
      said: '100',
      organisationNumber: 50,
      abbreviationDe: 'abbreviation',
      abbreviationIt: 'abbreviation',
      abbreviationEn: 'abbreviation',
      abbreviationFr: 'abbreviation',
      descriptionDe: 'description',
      descriptionEn: 'description',
      descriptionFr: 'description',
      descriptionIt: 'description',
      validFrom: new Date(),
      validTo: new Date(),
    },
    validFrom: new Date(),
    validTo: new Date(),
  },
  {
    id: 2,
    businessOrganisation: {
      said: '101',
      organisationNumber: 51,
      abbreviationDe: 'abbreviation',
      abbreviationIt: 'abbreviation',
      abbreviationEn: 'abbreviation',
      abbreviationFr: 'abbreviation',
      descriptionDe: 'description',
      descriptionEn: 'description',
      descriptionFr: 'description',
      descriptionIt: 'description',
      validFrom: new Date(),
      validTo: new Date(),
    },
    validFrom: new Date(),
    validTo: new Date(),
  },
];

describe('TransportCompanyDetailComponent', () => {
  let component: TransportCompanyDetailComponent;
  let fixture: ComponentFixture<TransportCompanyDetailComponent>;

  let facade: Mocked<
    Pick<
      TransportCompanyDetailFacade,
      'init' | 'save' | 'deleteRelation' | 'selectedRelation' | 'leaveEditMode' | 'isRelationSelected'
    >
  >;
  let dialogService: Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>>;
  let notificationService: Mocked<Pick<NotificationService, 'success'>>;

  const mockData = [transportCompany, transportCompanyRelations];

  beforeEach(() => {
    facade = {
      init: vi.fn(),
      save: vi.fn(),
      deleteRelation: vi.fn(),
      selectedRelation: vi.fn(),
      leaveEditMode: vi.fn(),
      isRelationSelected: vi.fn(),
    };
    facade.save.mockReturnValue(of([]));
    facade.deleteRelation.mockReturnValue(of([]));
    facade.isRelationSelected.mockReturnValue(false);

    dialogService = {
      openDialogDataWithConfirmationResult: vi.fn(),
    };
    dialogService.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

    notificationService = {
      success: vi.fn(),
    };

    TestBed.configureTestingModule({
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { data: { transportCompanyDetail: mockData } },
          },
        },
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        { provide: TransportCompanyDetailFacade, useValue: facade },
        { provide: DialogService, useValue: dialogService },
        { provide: NotificationService, useValue: notificationService },
        translateServiceProvider,
      ],
    }).overrideComponent(TransportCompanyDetailComponent, {
      set: { template: '' },
    });

    fixture = TestBed.createComponent(TransportCompanyDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should initialize facade with resolved route data', () => {
    expect(component).toBeTruthy();
    expect(facade.init).toHaveBeenCalledExactlyOnceWith(transportCompanyRelations, 1234);
  });

  it('should not save when relation form is invalid', () => {
    component.saveRelation();

    expect(facade.save).not.toHaveBeenCalled();
    expect(notificationService.success).not.toHaveBeenCalled();
  });

  it('should delegate valid save to facade and show success notification', () => {
    component.transportCompanyRelationForm().reset({
      businessOrganisation: { sboid: 'ch:1:sboid:100500' } as BusinessOrganisation,
      validFrom: moment('2020-05-05'),
      validTo: moment('2021-05-05'),
    });

    component.saveRelation();

    expect(facade.save).toHaveBeenCalledExactlyOnceWith({
      transportCompanyId: 1234,
      businessOrganisation: { sboid: 'ch:1:sboid:100500' },
      validFrom: moment('2020-05-05'),
      validTo: moment('2021-05-05'),
    });
    expect(notificationService.success).toHaveBeenCalledExactlyOnceWith('RELATION.ADD_SUCCESS_MSG');
  });

  it('should delegate delete action to facade and show success notification', () => {
    component.deleteRelation();

    expect(facade.deleteRelation).toHaveBeenCalledExactlyOnceWith();
    expect(notificationService.success).toHaveBeenCalledExactlyOnceWith('RELATION.DELETE_SUCCESS_MSG');
  });

  it('should preload relation form when update action is triggered', () => {
    const selectedRelation: TransportCompanyBoRelation = {
      id: 1,
      businessOrganisation: { sboid: 'ch:1:sboid:900' } as BusinessOrganisation,
      validFrom: new Date('2023-01-01'),
      validTo: new Date('2023-12-31'),
    };
    facade.selectedRelation.mockReturnValue(selectedRelation);

    component.updateRelation();

    expect(component.transportCompanyRelationForm.businessOrganisation().value()).toEqual(
      selectedRelation.businessOrganisation
    );
    expect(component.transportCompanyRelationForm.validFrom().value()?.isSame(moment(selectedRelation.validFrom))).toBe(
      true
    );
    expect(component.transportCompanyRelationForm.validTo().value()?.isSame(moment(selectedRelation.validTo))).toBe(
      true
    );
  });

  it('should leave edit mode directly when no unsaved changes are present', () => {
    component.leaveEditModeWithDialog();

    expect(dialogService.openDialogDataWithConfirmationResult).not.toHaveBeenCalled();
    expect(facade.leaveEditMode).toHaveBeenCalledExactlyOnceWith();
  });

  it('should confirm discard when form has unsaved changes and leave on confirmation', () => {
    component.transportCompanyRelationForm().markAsDirty();
    dialogService.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

    component.leaveEditModeWithDialog();

    expect(dialogService.openDialogDataWithConfirmationResult).toHaveBeenCalledExactlyOnceWith({
      title: 'DIALOG.DISCARD_CHANGES_TITLE',
      message: 'DIALOG.LEAVE_SITE',
    });
    expect(facade.leaveEditMode).toHaveBeenCalledExactlyOnceWith();
  });
});
