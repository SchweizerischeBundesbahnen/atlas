import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';
import { AffectedSublinesModel, LineType, LineVersionV2, LineVersionWorkflow, Status } from '../../../../api';
import { LineDetailComponent } from './line-detail.component';
import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { adminPermissionServiceMock, translateServiceProvider } from '../../../../app.testing.mocks';
import { FormModule } from '../../../../core/module/form.module';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import { ValidityService } from '../../../sepodi/validity/validity.service';
import moment from 'moment';
import { Component, input } from '@angular/core';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { LineService } from '../../../../api/service/lidi/line.service';
import { LineInternalService } from '../../../../api/service/lidi/line-internal.service';
import { SublineTableComponent } from './subline-table/subline-table.component';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideMomentDateAdapter } from '@angular/material-moment-adapter';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';

@Component({
  selector: 'atlas-subline-table',
  template: '<p>Mock subline table Component</p>',
  imports: [FormModule],
})
export class MockSublineTableComponent {
  readonly mainLineSlnid = input.required<string>();
  readonly eventSubject = input.required<Observable<boolean>>();
}

@Component({
  selector: 'atlas-subline-detail',
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

let validityService: Mocked<Pick<ValidityService, 'initValidity' | 'updateValidity' | 'validate'>>;
let dialogService: Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>>;

function createSharedMocks() {
  validityService = {
    initValidity: vi.fn(),
    updateValidity: vi.fn(),
    validate: vi.fn(),
  };
  validityService.validate.mockReturnValue(of(true));

  dialogService = {
    openDialogDataWithConfirmationResult: vi.fn(),
  };
  dialogService.openDialogDataWithConfirmationResult.mockReturnValue(of(true));
}

function setupTestBed(
  lineService: Partial<LineService>,
  lineInternalService: Partial<LineInternalService>,
  data: { lineDetail: string | LineVersionV2[] }
) {
  Element.prototype.scrollIntoView = vi.fn();

  TestBed.configureTestingModule({
    imports: [LineDetailComponent],
    providers: [
      translateServiceProvider,
      provideHttpClient(),
      provideHttpClientTesting(),
      provideMomentDateAdapter(),
      { provide: FormBuilder },
      { provide: LineService, useValue: lineService },
      { provide: LineInternalService, useValue: lineInternalService },
      { provide: DialogService, useValue: dialogService },
      { provide: PermissionService, useValue: adminPermissionServiceMock },
      { provide: ActivatedRoute, useValue: { snapshot: { data: data } } },
    ],
  })
    .overrideComponent(LineDetailComponent, {
      remove: { imports: [SublineTableComponent] },
      add: { imports: [MockSublineTableComponent] },
    })
    .compileComponents()
    .then();
}

describe('LineDetailComponent for existing lineVersion', () => {
  let component: LineDetailComponent;
  let fixture: ComponentFixture<LineDetailComponent>;
  let router: Router;
  let lineService: Mocked<Pick<LineService, 'updateLineVersion'>>;
  let lineInternalService: Mocked<Pick<LineInternalService, 'deleteLines' | 'checkAffectedSublines'>>;

  const mockData = { lineDetail: [lineVersion] };

  beforeEach(() => {
    createSharedMocks();

    lineService = { updateLineVersion: vi.fn() };
    lineInternalService = {
      deleteLines: vi.fn(),
      checkAffectedSublines: vi.fn(),
    };

    setupTestBed(lineService, lineInternalService, mockData);

    fixture = TestBed.createComponent(LineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  it('should disable form parts when in review', () => {
    lineVersion.status = Status.InReview;

    expect(component.form.enabled).toBe(false);
    component.toggleEdit();
    expect(component.form.enabled).toBe(true);

    expect(component.form.controls.validFrom.enabled).toBe(false);
    expect(component.form.controls.validTo.enabled).toBe(false);
    expect(component.form.controls.lineType.enabled).toBe(false);
  });

  it('should not disable form parts when in draft/validated', () => {
    lineVersion.status = Status.Draft;

    component.toggleEdit();

    expect(component.form.controls.validFrom.enabled).toBe(true);
    expect(component.form.controls.validTo.enabled).toBe(true);
    expect(component.form.controls.lineType.enabled).toBe(true);
  });

  it('should update LineVersion successfully', async () => {
    lineService.updateLineVersion.mockReturnValue(of([lineVersion]));
    const navigateSpy = vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));

    component.toggleEdit();
    component.form.controls.description.setValue('UpdatedDescription');
    component.save();
    fixture.whenStable();
    fixture.detectChanges();

    const snackBarContainer = document.body.querySelector('mat-snack-bar-container');

    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer!.textContent.trim()).toBe('LIDI.LINE.NOTIFICATION.EDIT_SUCCESS');
    expect(snackBarContainer!.classList).toContain('success');
    expect(navigateSpy).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    lineService.updateLineVersion.mockReturnValue(throwError(() => error));

    component.toggleEdit();
    component.form.controls.description.setValue('UpdatedDescription');
    component.save();
    fixture.detectChanges();

    expect(component.form.enabled).toBe(true);
  });

  it('should delete LineVersion successfully', async () => {
    lineInternalService.deleteLines.mockReturnValue(of(undefined));
    const navigateSpy = vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));

    component.delete();
    fixture.detectChanges();
    fixture.whenStable();
    fixture.detectChanges();

    const snackBarContainer = document.body.querySelector('mat-snack-bar-container');

    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer!.textContent.trim()).toBe('LIDI.LINE.NOTIFICATION.DELETE_SUCCESS');
    expect(snackBarContainer!.classList).toContain('success');
    expect(navigateSpy).toHaveBeenCalled();
  });

  it('should update LineVersion with only shortening', () => {
    const affectedSublines: AffectedSublinesModel = {
      allowedSublines: ['1234'],
      notAllowedSublines: [],
    };
    vi.spyOn(component, 'isOnlyValidityChangedToTruncation').mockReturnValue(true);
    vi.spyOn(component, 'openSublineShorteningDialog').mockReturnValue(of(true));

    lineService.updateLineVersion.mockReturnValue(of([lineVersion]));
    lineInternalService.checkAffectedSublines.mockReturnValue(of(affectedSublines));
    vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
    const updateLineVersionSpy = vi.spyOn(component, 'updateLineVersion');

    component.toggleEdit();
    component.form.controls.validTo.setValue(moment('2028-06-01'));
    component.updateLine(1, lineVersion);

    expect(updateLineVersionSpy).toHaveBeenCalledWith(1, lineVersion, 'LIDI.SUBLINE_SHORTENING.ALLOWED.SUCCESS');
  });

  it('should update LineVersion with mixed sublines', () => {
    const affectedSublines: AffectedSublinesModel = {
      allowedSublines: ['1234'],
      notAllowedSublines: ['4321'],
      affectedSublinesEmpty: false,
      hasNotAllowedSublinesOnly: true,
      hasAllowedSublinesOnly: true,
    };
    vi.spyOn(component, 'isOnlyValidityChangedToTruncation').mockReturnValue(true);
    vi.spyOn(component, 'openSublineShorteningDialog').mockReturnValue(of(true));

    lineService.updateLineVersion.mockReturnValue(of([lineVersion]));
    lineInternalService.checkAffectedSublines.mockReturnValue(of(affectedSublines));
    vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
    const updateLineVersionSpy = vi.spyOn(component, 'updateLineVersion');

    component.toggleEdit();
    component.form.controls.validTo.setValue(moment('2028-06-01'));
    component.updateLine(1, lineVersion);

    expect(updateLineVersionSpy).toHaveBeenCalledWith(1, lineVersion, 'LIDI.SUBLINE_SHORTENING.ALLOWED.SUCCESS');
  });

  it('should update LineVersion with not allowed sublines', () => {
    const affectedSublines: AffectedSublinesModel = {
      allowedSublines: [],
      notAllowedSublines: ['4321'],
      affectedSublinesEmpty: false,
      hasAllowedSublinesOnly: false,
      hasNotAllowedSublinesOnly: true,
    };
    vi.spyOn(component, 'isOnlyValidityChangedToTruncation').mockReturnValue(true);
    vi.spyOn(component, 'openSublineShorteningDialog').mockReturnValue(of(true));

    lineService.updateLineVersion.mockReturnValue(of([lineVersion]));
    lineInternalService.checkAffectedSublines.mockReturnValue(of(affectedSublines));
    vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
    const updateLineVersionSpy = vi.spyOn(component, 'updateLineVersion');

    component.toggleEdit();
    component.form.controls.validTo.setValue(moment('2028-06-01'));
    component.updateLine(1, lineVersion);

    expect(updateLineVersionSpy).toHaveBeenCalledWith(1, lineVersion, 'LIDI.LINE.NOTIFICATION.EDIT_SUCCESS');
  });
});

describe('LineDetailComponent for new lineVersion', () => {
  let component: LineDetailComponent;
  let fixture: ComponentFixture<LineDetailComponent>;
  let router: Router;
  let lineService: Mocked<Pick<LineService, 'createLineVersionV2'>>;

  const mockData = { lineDetail: [] };

  beforeEach(() => {
    createSharedMocks();
    lineService = { createLineVersionV2: vi.fn() };

    setupTestBed(lineService, {} as LineInternalService, mockData);

    fixture = TestBed.createComponent(LineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    router = TestBed.inject(Router);
  });

  describe('create new Version', () => {
    it('successfully', async () => {
      const navigateSpy = vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
      lineService.createLineVersionV2.mockReturnValue(of(lineVersion));

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
      fixture.whenStable();
      fixture.detectChanges();

      const snackBarContainer = document.body.querySelector('mat-snack-bar-container');

      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer!.textContent.trim()).toBe('LIDI.LINE.NOTIFICATION.ADD_SUCCESS');
      expect(snackBarContainer!.classList).toContain('success');
      expect(navigateSpy).toHaveBeenCalled();
    });

    it('displaying error', () => {
      lineService.createLineVersionV2.mockReturnValue(throwError(() => error));
      component.save();

      expect(component.form.enabled).toBe(true);
    });
  });
});
