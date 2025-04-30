import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import {
  AffectedSublinesModel,
  LineType,
  LineVersionV2,
  LineVersionWorkflow,
  Status,
  WorkflowProcessingStatus,
} from '../../../../api';
import { LineDetailComponent } from './line-detail.component';
import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { adminPermissionServiceMock } from '../../../../app.testing.mocks';
import { FormModule } from '../../../../core/module/form.module';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { ValidityService } from '../../../sepodi/validity/validity.service';
import moment from 'moment';
import { Component, Input } from '@angular/core';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { LineService } from '../../../../api/service/lidi/line.service';
import { LineInternalService } from '../../../../api/service/lidi/line-internal.service';
import { SublineTableComponent } from './subline-table/subline-table.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';

@Component({
  selector: 'app-coverage',
  template: '<p>Mock Product Editor Component</p>',
})
class MockAppCoverageComponent {
  @Input() pageType!: Record;
  @Input() currentRecord!: Page;
}

@Component({
  selector: 'app-subline-table',
  template: '<p>Mock subline table Component</p>',
  imports: [FormModule],
})
export class MockSublineTableComponent {
  @Input() mainLineSlnid!: string;
  @Input() eventSubject!: Observable<boolean>;
}

@Component({
  selector: 'app-subline-detail',
  template: '<p>Mock subline table Component</p>',
  providers: [ValidityService],
  imports: [ReactiveFormsModule],
})
export class MockSublineDetailComponent {}

const lineVersion: LineVersionV2 = {
  lineConcessionType: 'CANTONALLY_APPROVED_LINE',
  offerCategory: 'ASC',
  id: 1234,
  slnid: 'slnid',
  number: 'name',
  description: 'asdf',
  status: 'VALIDATED',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  businessOrganisation: 'SBB',
  swissLineNumber: 'L1',
  lineType: LineType.Orderly,
  lineVersionWorkflows: new Set<LineVersionWorkflow>(),
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

let component: LineDetailComponent;
let fixture: ComponentFixture<LineDetailComponent>;
let router: Router;

const validityService = jasmine.createSpyObj<ValidityService>([
  'initValidity',
  'updateValidity',
  'validate',
]);
validityService.validate.and.returnValue(of(true));

const dialogService = jasmine.createSpyObj<DialogService>('DialogService', {
  confirm: of(true),
});

describe('LineDetailComponent for existing lineVersion', () => {
  const mockLineService = jasmine.createSpyObj('lineService', [
    'updateLineVersion',
  ]);
  const mockLineInternalService = jasmine.createSpyObj('lineInternalService', [
    'deleteLines',
    'checkAffectedSublines',
  ]);
  const mockData = {
    lineDetail: [lineVersion],
  };

  beforeEach(() => {
    setupTestBed(mockLineService, mockLineInternalService, mockData);
    fixture = TestBed.createComponent(LineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should disable form parts when in review', () => {
    lineVersion.status = Status.InReview;
    fixture.detectChanges();

    expect(component.form.enabled).toBeFalse();
    component.toggleEdit();
    expect(component.form.enabled).toBeTrue();

    expect(component.form.controls.validFrom.enabled).toBeFalse();
    expect(component.form.controls.validTo.enabled).toBeFalse();
    expect(component.form.controls.lineType.enabled).toBeFalse();
  });

  it('should not disable form parts when in draft/validated', () => {
    lineVersion.status = Status.Draft;
    fixture.detectChanges();

    component.toggleEdit();

    expect(component.form.controls.validFrom.enabled).toBeTrue();
    expect(component.form.controls.validTo.enabled).toBeTrue();
    expect(component.form.controls.lineType.enabled).toBeTrue();
  });

  it('should update LineVersion successfully', () => {
    mockLineService.updateLineVersion.and.returnValue(of(lineVersion));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    component.toggleEdit();
    component.form.controls.description.setValue('UpdatedDescription');

    component.save();
    fixture.detectChanges();

    const snackBarContainer = fixture.nativeElement.offsetParent.querySelector(
      'mat-snack-bar-container'
    );
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toBe(
      'LIDI.LINE.NOTIFICATION.EDIT_SUCCESS'
    );
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    mockLineService.updateLineVersion.and.returnValue(throwError(() => error));

    component.toggleEdit();
    component.form.controls.description.setValue('UpdatedDescription');

    component.save();
    fixture.detectChanges();

    expect(component.form.enabled).toBeTrue();
  });

  it('should delete LineVersion successfully', () => {
    mockLineInternalService.deleteLines.and.returnValue(of({}));
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));

    component.delete();
    fixture.detectChanges();

    const snackBarContainer = fixture.nativeElement.offsetParent.querySelector(
      'mat-snack-bar-container'
    );
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toBe(
      'LIDI.LINE.NOTIFICATION.DELETE_SUCCESS'
    );
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should update LineVersion with only shortening', () => {
    const affectedSublines: AffectedSublinesModel = {
      allowedSublines: ['1234'],
      notAllowedSublines: [],
    };
    spyOn(component, 'isOnlyValidityChangedToTruncation').and.returnValue(true);
    spyOn(component, 'openSublineShorteningDialog').and.returnValue(of(true));

    mockLineService.updateLineVersion.and.returnValue(of(lineVersion));
    mockLineInternalService.checkAffectedSublines.and.returnValue(
      of(affectedSublines)
    );
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    spyOn(component, 'updateLineVersion').and.callThrough();

    component.toggleEdit();
    component.form.controls.validTo.setValue(moment('2028-06-01'));
    component.updateLine(1, lineVersion);

    expect(component.updateLineVersion).toHaveBeenCalledWith(
      1,
      lineVersion,
      'LIDI.SUBLINE_SHORTENING.ALLOWED.SUCCESS'
    );
  });

  it('should update LineVersion with mixed sublines', () => {
    const affectedSublines: AffectedSublinesModel = {
      allowedSublines: ['1234'],
      notAllowedSublines: ['4321'],
      affectedSublinesEmpty: false,
      hasNotAllowedSublinesOnly: true,
      hasAllowedSublinesOnly: true,
    };
    spyOn(component, 'isOnlyValidityChangedToTruncation').and.returnValue(true);
    spyOn(component, 'openSublineShorteningDialog').and.returnValue(of(true));

    mockLineService.updateLineVersion.and.returnValue(of(lineVersion));
    mockLineInternalService.checkAffectedSublines.and.returnValue(
      of(affectedSublines)
    );
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    spyOn(component, 'updateLineVersion').and.callThrough();

    component.toggleEdit();
    component.form.controls.validTo.setValue(moment('2028-06-01'));
    component.updateLine(1, lineVersion);

    expect(component.updateLineVersion).toHaveBeenCalledWith(
      1,
      lineVersion,
      'LIDI.SUBLINE_SHORTENING.ALLOWED.SUCCESS'
    );
  });

  it('should update LineVersion with not allowed sublines', () => {
    const affectedSublines: AffectedSublinesModel = {
      allowedSublines: [],
      notAllowedSublines: ['4321'],
      affectedSublinesEmpty: false,
      hasAllowedSublinesOnly: false,
      hasNotAllowedSublinesOnly: true,
    };
    spyOn(component, 'isOnlyValidityChangedToTruncation').and.returnValue(true);
    spyOn(component, 'openSublineShorteningDialog').and.returnValue(of(true));

    mockLineService.updateLineVersion.and.returnValue(of(lineVersion));
    mockLineInternalService.checkAffectedSublines.and.returnValue(
      of(affectedSublines)
    );
    spyOn(router, 'navigate').and.returnValue(Promise.resolve(true));
    spyOn(component, 'updateLineVersion').and.callThrough();

    component.toggleEdit();
    component.form.controls.validTo.setValue(moment('2028-06-01'));
    component.updateLine(1, lineVersion);

    expect(component.updateLineVersion).toHaveBeenCalledWith(
      1,
      lineVersion,
      'LIDI.LINE.NOTIFICATION.EDIT_SUCCESS'
    );
  });
});

describe('LineDetailComponent for new lineVersion', () => {
  const mockLineService = jasmine.createSpyObj('lineService', [
    'createLineVersionV2',
  ]);
  const mockData = {
    lineDetail: [],
  };

  beforeEach(() => {
    setupTestBed(mockLineService, {} as LineInternalService, mockData);

    fixture = TestBed.createComponent(LineDetailComponent);
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
      mockLineService.createLineVersionV2.and.returnValue(of(lineVersion));

      component.form.patchValue({
        lineConcessionType: 'CANTONALLY_APPROVED_LINE',
        offerCategory: 'ASC',
        number: 'name',
        description: 'asdf',
        validFrom: moment(),
        validTo: moment(),
        businessOrganisation: 'SBB',
        swissLineNumber: 'L1',
        lineType: LineType.Orderly,
      });

      component.save();
      fixture.detectChanges();

      const snackBarContainer =
        fixture.nativeElement.offsetParent.querySelector(
          'mat-snack-bar-container'
        );
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent.trim()).toBe(
        'LIDI.LINE.NOTIFICATION.ADD_SUCCESS'
      );
      expect(snackBarContainer.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalled();
    });

    it('displaying error', () => {
      mockLineService.createLineVersionV2.and.returnValue(
        throwError(() => error)
      );
      component.save();
      fixture.detectChanges();

      expect(component.form.enabled).toBeTrue();
    });
  });

  describe('Show snapshot history', () => {
    it('Should show snapshot history without workflow but orderly and validated', () => {
      //given
      lineVersion.status = Status.Validated;
      lineVersion.lineType = LineType.Orderly;
      lineVersion.lineVersionWorkflows?.clear();
      fixture.componentInstance.selectedVersion = lineVersion;
      //when
      const result = fixture.componentInstance.showSnapshotHistoryLink();
      //then
      expect(result).toBeTruthy();
    });

    it('Should not show snapshot history without workflow when Temporary and validated', () => {
      //given
      lineVersion.status = Status.Validated;
      lineVersion.lineType = LineType.Temporary;
      lineVersion.lineVersionWorkflows?.clear();
      fixture.componentInstance.selectedVersion = lineVersion;
      //when
      const result = fixture.componentInstance.showSnapshotHistoryLink();
      //then
      expect(result).toBeFalsy();
    });

    it('Should not show snapshot history without workflow when Operational and validated', () => {
      //given
      lineVersion.status = Status.Validated;
      lineVersion.lineType = LineType.Operational;
      lineVersion.lineVersionWorkflows?.clear();
      fixture.componentInstance.selectedVersion = lineVersion;
      //when
      const result = fixture.componentInstance.showSnapshotHistoryLink();
      //then
      expect(result).toBeFalsy();
    });

    it('Should show snapshot history with workflow evaluated', () => {
      //given
      const lineWorkflow: LineVersionWorkflow = {
        workflowId: 1,
        workflowProcessingStatus: WorkflowProcessingStatus.Evaluated,
      };
      lineVersion.lineVersionWorkflows?.add(lineWorkflow);
      fixture.componentInstance.selectedVersion = lineVersion;

      //when
      const result = fixture.componentInstance.showSnapshotHistoryLink();
      //then
      expect(result).toBeTruthy();
    });

    it('Should show snapshot history with workflow in progress', () => {
      //given
      const lineWorkflow: LineVersionWorkflow = {
        workflowId: 1,
        workflowProcessingStatus: WorkflowProcessingStatus.InProgress,
      };
      lineVersion.lineVersionWorkflows?.add(lineWorkflow);
      fixture.componentInstance.selectedVersion = lineVersion;

      //when
      const result = fixture.componentInstance.showSnapshotHistoryLink();
      //then
      expect(result).toBeTruthy();
    });
  });
});

function setupTestBed(
  lineService: LineService,
  lineInternalService: LineInternalService,
  data: { lineDetail: string | LineVersionV2[] }
) {
  TestBed.configureTestingModule({
    imports: [LineDetailComponent, TranslateModule.forRoot()],
    providers: [
      provideHttpClient(),
      provideHttpClientTesting(),
      provideMomentDateAdapter(),
      { provide: FormBuilder },
      { provide: LineService, useValue: lineService },
      { provide: LineInternalService, useValue: lineInternalService },
      { provide: DialogService, useValue: dialogService },
      { provide: PermissionService, useValue: adminPermissionServiceMock },
      { provide: ActivatedRoute, useValue: { snapshot: { data: data } } },
      { provide: TranslatePipe },
    ],
  })
    .overrideComponent(LineDetailComponent, {
      remove: { imports: [SublineTableComponent] },
      add: { imports: [MockSublineTableComponent] },
    })
    .compileComponents()
    .then();
}
