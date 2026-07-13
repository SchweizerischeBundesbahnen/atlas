import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TransportCompany, TransportCompanyBoRelation, TransportCompanyStatus } from '../../../../api';
import { TransportCompanyDetailComponent } from './transport-company-detail.component';
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { ActivatedRoute, ActivatedRouteSnapshot } from '@angular/router';
import { TransportCompanyDetailFacade } from './transport-company-detail.facade';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { NotificationService } from '../../../../core/notification/notification.service';
import { of } from 'rxjs';
import { Component, input, signal, WritableSignal } from '@angular/core';
import { By } from '@angular/platform-browser';
import { DetailPageContainerComponent } from '../../../../core/components/detail-page-container/detail-page-container.component';
import { DetailPageContentComponent } from '../../../../core/components/detail-page-content/detail-page-content.component';
import { TextFieldSfComponent } from '../../../../core/form-components/text-field-sf/text-field-sf.component';
import { AtlasFormCommentSfComponent } from '../../../../core/form-components/comment-sf/atlas-form-comment-sf.component';
import { BoSelectSfComponent } from '../../../../core/form-components/bo-select-sf/bo-select-sf.component';
import { DateRangeSfComponent } from '../../../../core/form-components/date-range-sf/date-range-sf.component';
import { RelationComponent } from '../../../../core/components/relation/relation.component';
import { DetailFooterComponent } from '../../../../core/components/detail-footer/detail-footer.component';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { translateServiceProvider } from '../../../../app.testing.mocks';
import { Field } from '@angular/forms/signals';
import moment from 'moment';

const transportCompany: TransportCompany = {
  id: 1234,
  description: 'SBB',
  number: 'TC001',
  abbreviation: 'SBB',
  businessRegisterName: 'Swiss Federal Railways',
  businessRegisterNumber: 'CHE-123456',
  enterpriseId: 'ENT001',
  comment: 'Test comment',
  transportCompanyStatus: TransportCompanyStatus.Current,
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

// Mock child components
@Component({
  selector: 'atlas-detail-page-container',
  template: '<ng-content />',
  standalone: true,
})
class MockDetailPageContainerComponent {}

@Component({
  selector: 'atlas-detail-page-content',
  template: '<ng-content />',
  standalone: true,
})
class MockDetailPageContentComponent {}

@Component({
  selector: 'atlas-text-field-sf',
  template: '',
  standalone: true,
})
class MockTextFieldSfComponent {
  readonly field = input.required<Field<unknown>>();
  readonly fieldName = input<string>();
  readonly fieldLabel = input<string>();
}

@Component({
  selector: 'atlas-form-comment-sf',
  template: '',
  standalone: true,
})
class MockAtlasFormCommentSfComponent {
  readonly field = input.required<Field<unknown>>();
  readonly displayLabel = input<boolean>();
}

@Component({
  selector: 'atlas-bo-select-sf',
  template: '',
  standalone: true,
})
class MockBoSelectSfComponent {
  readonly field = input.required<Field<unknown>>();
  readonly disabled = input<boolean>();
  readonly valueExtraction = input<string>();
}

@Component({
  selector: 'atlas-date-range-sf',
  template: '',
  standalone: true,
})
class MockDateRangeSfComponent {
  readonly validFromField = input.required<Field<unknown>>();
  readonly validToField = input.required<Field<unknown>>();
}

@Component({
  selector: 'atlas-relation',
  template: '<ng-content />',
  standalone: true,
})
class MockRelationComponent {
  readonly editMode = input<boolean>();
  readonly editable = input<boolean>();
  readonly records = input<TransportCompanyBoRelation[]>();
  readonly selectedIndex = input<number>();
  readonly tableColumns = input<unknown[]>();
  readonly titleTranslationKey = input<string>();
}

@Component({
  selector: 'atlas-detail-footer',
  template: '<ng-content />',
  standalone: true,
})
class MockDetailFooterComponent {}

@Component({
  selector: 'atlas-button',
  template: '',
  standalone: true,
})
class MockAtlasButtonComponent {
  readonly footerEdit = input<boolean>();
  readonly buttonType = input<unknown>();
  readonly buttonDataCy = input<string>();
  readonly buttonText = input<string>();
  readonly disabled = input<boolean>();
  readonly submitButton = input<boolean>();
  readonly wrapperStyleClass = input<string>();
}

describe('TransportCompanyDetailComponent', () => {
  let component: TransportCompanyDetailComponent;
  let fixture: ComponentFixture<TransportCompanyDetailComponent>;

  let mockFacade: Record<keyof TransportCompanyDetailFacade, unknown>;
  let mockPermissionService: Partial<PermissionService>;
  let mockDialogService: Partial<DialogService>;
  let mockNotificationService: Partial<NotificationService>;
  let mockActivatedRoute: { snapshot: Pick<ActivatedRouteSnapshot, 'data'> };

  const fillRelationFormWithValidData = () => {
    (mockFacade.isEditMode as WritableSignal<boolean>).set(true);
    fixture.detectChanges();

    const boSelect = fixture.debugElement.query(By.directive(MockBoSelectSfComponent))?.componentInstance as
      MockBoSelectSfComponent | undefined;
    const dateRange = fixture.debugElement.query(By.directive(MockDateRangeSfComponent))?.componentInstance as
      MockDateRangeSfComponent | undefined;

    expect(boSelect).toBeDefined();
    expect(dateRange).toBeDefined();

    boSelect?.field()().value.set(transportCompanyRelations[0].businessOrganisation);
    boSelect?.field()().markAsDirty();
    dateRange?.validFromField()().value.set(moment('2024-01-01'));
    dateRange?.validToField()().value.set(moment('2024-12-31'));
  };

  beforeEach(() => {
    mockFacade = {
      init: vi.fn(),
      save: vi.fn().mockReturnValue(of({})),
      deleteRelation: vi.fn().mockReturnValue(of({})),
      leaveEditMode: vi.fn(),
      unselectRelation: vi.fn(),
      toggleEditMode: vi.fn(),
      selectRelation: vi.fn(),
      isEditMode: signal(false),
      isRelationSelected: signal(false),
      selectedRelation: signal(null),
      selectedRelationIndex: signal(-1),
      transportCompanyRelationsReadonly: signal([]),
    };

    mockPermissionService = {
      hasPermissionsToCreate: vi.fn().mockReturnValue(true),
    };

    mockDialogService = {
      openDialogDataWithConfirmationResult: vi.fn().mockReturnValue(of(true)),
    };

    mockNotificationService = {
      success: vi.fn(),
    };

    mockActivatedRoute = {
      snapshot: {
        data: {
          transportCompanyDetail: [transportCompany, transportCompanyRelations],
        },
      },
    };

    TestBed.configureTestingModule({
      imports: [TransportCompanyDetailComponent],
      providers: [
        { provide: PermissionService, useValue: mockPermissionService },
        { provide: DialogService, useValue: mockDialogService },
        { provide: NotificationService, useValue: mockNotificationService },
        { provide: ActivatedRoute, useValue: mockActivatedRoute },
        translateServiceProvider,
      ],
    }).overrideComponent(TransportCompanyDetailComponent, {
      remove: {
        providers: [TransportCompanyDetailFacade],
        imports: [
          DetailPageContainerComponent,
          DetailPageContentComponent,
          TextFieldSfComponent,
          AtlasFormCommentSfComponent,
          BoSelectSfComponent,
          DateRangeSfComponent,
          RelationComponent,
          DetailFooterComponent,
          AtlasButtonComponent,
        ],
      },
      add: {
        providers: [{ provide: TransportCompanyDetailFacade, useValue: mockFacade }],
        imports: [
          MockDetailPageContainerComponent,
          MockDetailPageContentComponent,
          MockTextFieldSfComponent,
          MockAtlasFormCommentSfComponent,
          MockBoSelectSfComponent,
          MockDateRangeSfComponent,
          MockRelationComponent,
          MockDetailFooterComponent,
          MockAtlasButtonComponent,
        ],
      },
    });

    fixture = TestBed.createComponent(TransportCompanyDetailComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeDefined();
  });

  it('should have edit permissions property computed correctly', () => {
    fixture.detectChanges();

    const relation = fixture.debugElement.query(By.directive(MockRelationComponent))?.componentInstance as
      MockRelationComponent | undefined;

    expect(relation).toBeDefined();
    expect(relation?.editable()).toBe(true);
  });

  describe('Initialization (ngOnInit)', () => {
    it('should initialize transport company form with data from activated route', () => {
      fixture.detectChanges();

      const textFieldComponents = fixture.debugElement
        .queryAll(By.directive(MockTextFieldSfComponent))
        .map((element) => element.componentInstance as MockTextFieldSfComponent);
      const fieldValueByName = (fieldName: string) => {
        const field = textFieldComponents.find((componentInstance) => componentInstance.fieldName() === fieldName);
        expect(field).toBeDefined();
        return field?.field()().value();
      };

      expect(fieldValueByName('number')).toBe(transportCompany.number);
      expect(fieldValueByName('abbreviation')).toBe(transportCompany.abbreviation);
      expect(fieldValueByName('description')).toBe(transportCompany.description);
      expect(fieldValueByName('enterpriseId')).toBe(transportCompany.enterpriseId);
      expect(fieldValueByName('businessRegisterName')).toBe(transportCompany.businessRegisterName);
      expect(fieldValueByName('businessRegisterNumber')).toBe(transportCompany.businessRegisterNumber);

      const commentField = fixture.debugElement.query(By.directive(MockAtlasFormCommentSfComponent))
        ?.componentInstance as MockAtlasFormCommentSfComponent | undefined;
      expect(commentField).toBeDefined();
      expect(commentField?.field()().value()).toBe(transportCompany.comment);
    });

    it('should initialize facade with relations and transport company id', () => {
      fixture.detectChanges();

      expect(mockFacade.init).toHaveBeenCalledWith(transportCompanyRelations, transportCompany.id);
    });
  });

  describe('conditional rendering', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should display N/A when status is null from activated route data', () => {
      mockActivatedRoute.snapshot.data.transportCompanyDetail = [
        {
          ...transportCompany,
          transportCompanyStatus: null,
        },
        transportCompanyRelations,
      ];

      fixture = TestBed.createComponent(TransportCompanyDetailComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();

      expect(fixture.debugElement.query(By.css('#status-value')).nativeElement.textContent).toBe('N/A');
    });
  });

  describe('Method: saveRelation', () => {
    beforeEach(() => {
      fixture.detectChanges();
      fillRelationFormWithValidData();
    });

    it('should call facade save when invoked', () => {
      component.saveRelation();
      expect(mockFacade.save).toHaveBeenCalled();
    });

    it('should show success notification on save', () => {
      component.saveRelation();
      expect(mockNotificationService.success).toHaveBeenCalled();
    });

    it('should show add success message when relation is not selected', () => {
      (mockFacade.isRelationSelected as WritableSignal<boolean>).set(false);
      component.saveRelation();
      expect(mockNotificationService.success).toHaveBeenCalledWith('RELATION.ADD_SUCCESS_MSG');
    });

    it('should show update success message when relation is selected', () => {
      (mockFacade.isRelationSelected as WritableSignal<boolean>).set(true);
      component.saveRelation();
      expect(mockNotificationService.success).toHaveBeenCalledWith('RELATION.UPDATE_SUCCESS_MSG');
    });
  });

  describe('Method: deleteRelation', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should call facade deleteRelation', () => {
      component.deleteRelation();
      expect(mockFacade.deleteRelation).toHaveBeenCalled();
    });

    it('should show delete success notification', () => {
      component.deleteRelation();
      expect(mockNotificationService.success).toHaveBeenCalledWith('RELATION.DELETE_SUCCESS_MSG');
    });
  });

  describe('Method: updateRelation', () => {
    beforeEach(() => {
      (mockFacade.isEditMode as WritableSignal<boolean>).set(true);
      fixture.detectChanges();
    });

    it('should populate form with selected relation data', () => {
      const selectedRelation = transportCompanyRelations[0];
      (mockFacade.selectedRelation as WritableSignal<TransportCompanyBoRelation | null>).set(selectedRelation);

      component.updateRelation();

      fixture.detectChanges();

      const boSelect = fixture.debugElement.query(By.directive(MockBoSelectSfComponent))?.componentInstance as
        MockBoSelectSfComponent | undefined;

      expect(boSelect).toBeDefined();
      expect(boSelect?.field()().value()).toEqual(selectedRelation.businessOrganisation);
    });
  });

  describe('Method: leaveEditModeWithDialog', () => {
    beforeEach(() => {
      fixture.detectChanges();
      fillRelationFormWithValidData();
    });

    it('should open dialog when form is dirty', () => {
      component.leaveEditModeWithDialog();
      expect(mockDialogService.openDialogDataWithConfirmationResult).toHaveBeenCalledWith(
        expect.objectContaining({
          title: 'DIALOG.DISCARD_CHANGES_TITLE',
          message: 'DIALOG.LEAVE_SITE',
        })
      );
    });

    it('should call facade leaveEditMode when dialog confirms', () => {
      mockDialogService.openDialogDataWithConfirmationResult = vi.fn().mockReturnValue(of(true));

      component.leaveEditModeWithDialog();

      expect(mockFacade.leaveEditMode).toHaveBeenCalled();
    });

    it('should not call leaveEditMode when dialog is dismissed', () => {
      mockDialogService.openDialogDataWithConfirmationResult = vi.fn().mockReturnValue(of(false));

      component.leaveEditModeWithDialog();

      expect(mockFacade.leaveEditMode).not.toHaveBeenCalled();
    });
  });

  describe('Form State', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('should compute dirty state from relation form', () => {
      expect(component.dirty()).toBe(false);

      fillRelationFormWithValidData();

      expect(component.dirty()).toBe(true);
    });

    it('should disable all transport company fields', () => {
      const textFieldComponents = fixture.debugElement
        .queryAll(By.directive(MockTextFieldSfComponent))
        .map((element) => element.componentInstance as MockTextFieldSfComponent);

      expect(textFieldComponents.length).toBeGreaterThan(0);
      textFieldComponents.forEach((textField) => {
        expect(textField.field()().disabled()).toBe(true);
      });

      const commentField = fixture.debugElement.query(By.directive(MockAtlasFormCommentSfComponent))
        ?.componentInstance as MockAtlasFormCommentSfComponent | undefined;
      expect(commentField).toBeDefined();
      expect(commentField?.field()().disabled()).toBe(true);
    });
  });
});
