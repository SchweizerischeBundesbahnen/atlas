import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LidiElementType, Line, ReadSublineVersionV2, SublineType } from '../../../../api';
import { SublineDetailComponent } from './subline-detail.component';
import { HttpErrorResponse, provideHttpClient } from '@angular/common/http';
import { adminPermissionServiceMock, translateServiceProvider } from '../../../../app.testing.mocks';
import { TranslatePipe } from '@ngx-translate/core';
import { ValidityService } from '../../../sepodi/validity/validity.service';
import { PermissionService } from '../../../../core/auth/permission/permission.service';
import moment from 'moment';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { SublineInternalService } from '../../../../api/service/lidi/subline-internal.service';
import { SublineService } from '../../../../api/service/lidi/subline.service';
import { LineService } from '../../../../api/service/lidi/line.service';
import { LineInternalService } from '../../../../api/service/lidi/line-internal.service';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { DateModule } from '../../../../core/module/date.module';
import { provideHttpClientTesting } from '@angular/common/http/testing';

const readSublineVersion: ReadSublineVersionV2 = {
  id: 1234,
  slnid: 'slnid',
  description: 'asdf',
  status: 'VALIDATED',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  businessOrganisation: 'SBB',
  swissSublineNumber: 'L1:2',
  mainlineSlnid: 'ch:1:slnid:1000',
  sublineType: SublineType.Technical,
  mainSwissLineNumber: 'L1',
  mainLineNumber: '1',
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

let component: SublineDetailComponent;
let fixture: ComponentFixture<SublineDetailComponent>;
let router: Router;
let validityService: Mocked<Pick<ValidityService, 'initValidity' | 'updateValidity' | 'validate'>>;
let lineService: Mocked<Pick<LineService, 'getLineVersionsV2'>>;
let lineInternalService: Mocked<Pick<LineInternalService, 'getLine' | 'getLines'>>;
let dialogService: Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>>;

function createSharedMocks(): void {
  validityService = {
    initValidity: vi.fn(),
    updateValidity: vi.fn(),
    validate: vi.fn(),
  };
  validityService.validate.mockReturnValue(of(true));

  lineService = {
    getLineVersionsV2: vi.fn(),
  };
  lineService.getLineVersionsV2.mockReturnValue(of([]));

  lineInternalService = {
    getLine: vi.fn(),
    getLines: vi.fn(),
  };
  lineInternalService.getLine.mockReturnValue(of({} as Line));

  dialogService = {
    openDialogDataWithConfirmationResult: vi.fn(),
  };
  dialogService.openDialogDataWithConfirmationResult.mockReturnValue(of(true));
}

describe('SublineDetailComponent for existing sublineVersion', () => {
  let sublineService: Mocked<Pick<SublineService, 'updateSublineVersionV2'>>;
  let sublineInternalService: Mocked<Pick<SublineInternalService, 'deleteSublines' | 'revokeSubline'>>;

  const mockData = { sublineDetail: [readSublineVersion] };

  beforeEach(() => {
    Element.prototype.scrollIntoView = vi.fn();
    createSharedMocks();

    sublineService = { updateSublineVersionV2: vi.fn() };
    sublineInternalService = {
      deleteSublines: vi.fn(),
      revokeSubline: vi.fn(),
    };

    setupTestBed(sublineService, sublineInternalService, mockData);

    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(SublineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should update SublineVersion successfully', async () => {
    sublineService.updateSublineVersionV2.mockReturnValue(of([readSublineVersion]));
    const navigateSpy = vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));

    component.toggleEdit();
    await fixture.whenStable();
    component.form.controls.description.setValue('NewDescription');
    component.save();
    await fixture.whenStable();
    fixture.detectChanges();

    expect(sublineService.updateSublineVersionV2).toHaveBeenCalled();
    const snackBarContainer = document.body.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).not.toBeNull();
    expect(snackBarContainer!.textContent!.trim()).toBe('LIDI.SUBLINE.NOTIFICATION.EDIT_SUCCESS');
    expect(snackBarContainer!.classList).toContain('success');
    expect(navigateSpy).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    sublineService.updateSublineVersionV2.mockReturnValue(throwError(() => error));

    component.toggleEdit();
    component.form.controls.description.setValue('NewDescription');
    component.save();

    expect(component.form.enabled).toBe(true);
  });

  it('should delete SublineVersion successfully', async () => {
    sublineInternalService.deleteSublines.mockReturnValue(of(undefined));
    const navigateSpy = vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));

    component.delete();
    await fixture.whenStable();
    fixture.detectChanges();

    const snackBarContainer = document.body.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).not.toBeNull();
    expect(snackBarContainer!.textContent!.trim()).toBe('LIDI.SUBLINE.NOTIFICATION.DELETE_SUCCESS');
    expect(snackBarContainer!.classList).toContain('success');
    expect(navigateSpy).toHaveBeenCalled();
  });

  it('should revoke SublineVersion successfully', async () => {
    sublineInternalService.revokeSubline.mockReturnValue(of(undefined));
    const navigateSpy = vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));

    component.revoke();
    await fixture.whenStable();
    fixture.detectChanges();

    const snackBarContainer = document.body.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).not.toBeNull();
    expect(snackBarContainer!.textContent!.trim()).toBe('LIDI.SUBLINE.NOTIFICATION.REVOKE_SUCCESS');
    expect(snackBarContainer!.classList).toContain('success');
    expect(navigateSpy).toHaveBeenCalled();
  });
});

describe('SublineDetailComponent for new sublineVersion', () => {
  let sublineService: Mocked<Pick<SublineService, 'createSublineVersionV2'>>;

  const mockData = { sublineDetail: [] };

  beforeEach(() => {
    Element.prototype.scrollIntoView = vi.fn();
    createSharedMocks();

    sublineService = { createSublineVersionV2: vi.fn() };

    setupTestBed(sublineService, {} as SublineInternalService, mockData);

    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(SublineDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  describe('create new Version', () => {
    it('successfully', async () => {
      const navigateSpy = vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
      sublineService.createSublineVersionV2.mockReturnValue(of(readSublineVersion));

      component.form.patchValue({
        mainlineSlnid: 'mainlineSlnid',
        sublineType: SublineType.Technical,
        description: 'description',
        businessOrganisation: 'sboid',
        validFrom: moment(),
        validTo: moment(),
        swissSublineNumber: 'slnr',
      });

      component.save();
      await fixture.whenStable();
      fixture.detectChanges();

      expect(sublineService.createSublineVersionV2).toHaveBeenCalled();
      const snackBarContainer = document.body.querySelector('mat-snack-bar-container');
      expect(snackBarContainer).not.toBeNull();
      expect(snackBarContainer!.textContent!.trim()).toBe('LIDI.SUBLINE.NOTIFICATION.ADD_SUCCESS');
      expect(snackBarContainer!.classList).toContain('success');
      expect(navigateSpy).toHaveBeenCalled();
    });
  });

  it('should handle mainLine selection of type orderly', () => {
    const mainLine: Line = {
      slnid: 'mainlineSlnid',
      lidiElementType: LidiElementType.Orderly,
    } as Line;
    component.mainLineChanged(mainLine);

    expect(component.TYPE_OPTIONS).toEqual([SublineType.Concession, SublineType.Technical]);
    expect(component.form.controls.sublineType.value).toBeNull();

    component.mainLineChanged(undefined);
    expect(component.TYPE_OPTIONS).toEqual([]);
    expect(component.currentMainlineSelection).toBeUndefined();
  });

  it('should handle mainLine selection of type disposition', () => {
    const mainLine: Line = {
      slnid: 'mainlineSlnid',
      lidiElementType: LidiElementType.Disposition,
    } as Line;
    component.mainLineChanged(mainLine);

    expect(component.TYPE_OPTIONS).toEqual([SublineType.Disposition]);
    expect(component.form.controls.sublineType.value).toBe(SublineType.Disposition);
  });

  it('should handle mainLine selection of type temporary', () => {
    const mainLine: Line = {
      slnid: 'mainlineSlnid',
      lidiElementType: LidiElementType.Temporary,
    } as Line;
    component.mainLineChanged(mainLine);

    expect(component.TYPE_OPTIONS).toEqual([SublineType.Temporary]);
    expect(component.form.controls.sublineType.value).toBe(SublineType.Temporary);
  });

  it('should handle mainLine selection of type operational', () => {
    const mainLine: Line = {
      slnid: 'mainlineSlnid',
      lidiElementType: LidiElementType.Operational,
    } as Line;
    component.mainLineChanged(mainLine);

    expect(component.TYPE_OPTIONS).toEqual([SublineType.Operational]);
    expect(component.form.controls.sublineType.value).toBe(SublineType.Operational);
  });
});

function setupTestBed(
  sublinesService: Partial<SublineService>,
  sublineInternalService: Partial<SublineInternalService>,
  data: { sublineDetail: ReadSublineVersionV2[] }
) {
  TestBed.configureTestingModule({
    imports: [DateModule.forRoot()],
    providers: [
      { provide: SublineService, useValue: sublinesService },
      { provide: SublineInternalService, useValue: sublineInternalService },
      { provide: LineService, useValue: lineService },
      { provide: LineInternalService, useValue: lineInternalService },
      { provide: DialogService, useValue: dialogService },
      { provide: PermissionService, useValue: adminPermissionServiceMock },
      { provide: ActivatedRoute, useValue: { snapshot: { data: data } } },
      translateServiceProvider,
      provideHttpClient(),
      provideHttpClientTesting(),
    ],
  }).overrideComponent(SublineDetailComponent, {
    set: {
      providers: [{ provide: ValidityService, useValue: validityService }, TranslatePipe],
    },
  });
}
