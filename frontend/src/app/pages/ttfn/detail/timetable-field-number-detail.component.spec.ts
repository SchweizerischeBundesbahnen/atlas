import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { TimetableFieldNumberDetailComponent } from './timetable-field-number-detail.component';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { TimetableFieldNumberVersion } from '../../../api';
import moment from 'moment';
import { of, throwError } from 'rxjs';
import { HomeComponent } from '../../home/home.component';
import { HttpErrorResponse } from '@angular/common/http';
import { adminPermissionServiceMock, translateServiceProvider } from '../../../app.testing.mocks';
import { PermissionService } from '../../../core/auth/permission/permission.service';
import { TimetableFieldNumberInternalService } from '../../../api/service/lidi/timetable-field-number-internal.service';
import { TimetableFieldNumberService } from '../../../api/service/lidi/timetable-field-number.service';
import { DateModule } from '../../../core/module/date.module';

const version: TimetableFieldNumberVersion = {
  id: 1,
  ttfnid: 'ttfnid',
  status: 'VALIDATED',
  validFrom: new Date('2021-06-01'),
  validTo: new Date('2029-06-01'),
  number: '1.1',
  businessOrganisation: 'sbb',
  descriptionOutwardLine1: 'desc',
  meanOfTransport: 'TRAIN',
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

const mockData = {
  timetableFieldNumberDetail: [version],
};

describe('TimetableFieldNumberDetailComponent detail page read version', () => {
  let component: TimetableFieldNumberDetailComponent;
  let fixture: ComponentFixture<TimetableFieldNumberDetailComponent>;
  let router: Router;
  let mockTimetableFieldNumberService: Mocked<Pick<TimetableFieldNumberService, 'updateVersionWithVersioning'>>;
  let mockTimetableFieldNumberInternalService: Mocked<
    Pick<TimetableFieldNumberInternalService, 'deleteVersions' | 'revokeTimetableFieldNumber'>
  >;

  beforeEach(() => {
    mockTimetableFieldNumberService = {
      updateVersionWithVersioning: vi.fn(),
    };
    mockTimetableFieldNumberInternalService = {
      deleteVersions: vi.fn(),
      revokeTimetableFieldNumber: vi.fn(),
    };

    TestBed.configureTestingModule({
      imports: [DateModule.forRoot()],
      providers: [
        {
          provide: TimetableFieldNumberInternalService,
          useValue: mockTimetableFieldNumberInternalService,
        },
        {
          provide: TimetableFieldNumberService,
          useValue: mockTimetableFieldNumberService,
        },
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        {
          provide: ActivatedRoute,
          useValue: { snapshot: { data: mockData } },
        },
        translateServiceProvider,
      ],
    });

    Element.prototype.scrollIntoView = vi.fn();
    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(TimetableFieldNumberDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.isNew).toBe(false);
    expect(component.editMode()).toBe(false);
  });

  it('should update Version successfully', () => {
    mockTimetableFieldNumberService.updateVersionWithVersioning.mockReturnValue(of([version]));
    vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    const snackBarContainer = fixture.nativeElement.parentElement.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toBe('TTFN.NOTIFICATION.EDIT_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should not update Version', () => {
    mockTimetableFieldNumberService.updateVersionWithVersioning.mockReturnValue(throwError(() => error));
    fixture.componentInstance.updateRecord();
    fixture.detectChanges();

    expect(component.editMode()).toBe(true);
  });

  it('should revoke Version successfully', () => {
    mockTimetableFieldNumberInternalService.revokeTimetableFieldNumber.mockReturnValue(of([version]));
    vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
    fixture.componentInstance.revoke();
    fixture.detectChanges();

    const snackBarContainer = fixture.nativeElement.parentElement.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toBe('TTFN.NOTIFICATION.REVOKE_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });

  it('should delete Version successfully', () => {
    mockTimetableFieldNumberInternalService.deleteVersions.mockReturnValue(of(undefined));
    vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
    fixture.componentInstance.deleteRecord();
    fixture.detectChanges();

    const snackBarContainer = fixture.nativeElement.parentElement.querySelector('mat-snack-bar-container');
    expect(snackBarContainer).toBeDefined();
    expect(snackBarContainer.textContent.trim()).toBe('TTFN.NOTIFICATION.DELETE_SUCCESS');
    expect(snackBarContainer.classList).toContain('success');
    expect(router.navigate).toHaveBeenCalled();
  });
});

describe('TimetableFieldNumberDetailComponent Detail page add new version', () => {
  let component: TimetableFieldNumberDetailComponent;
  let fixture: ComponentFixture<TimetableFieldNumberDetailComponent>;
  let mockTimetableFieldNumbersService: Mocked<Pick<TimetableFieldNumberService, 'createVersion'>>;
  let router: Router;

  beforeEach(() => {
    mockTimetableFieldNumbersService = {
      createVersion: vi.fn(),
    };

    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([{ path: '', component: HomeComponent }]), DateModule.forRoot()],
      providers: [
        {
          provide: TimetableFieldNumberService,
          useValue: mockTimetableFieldNumbersService,
        },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              data: { timetableFieldNumberDetail: [] },
            },
          },
        },
        {
          provide: PermissionService,
          useValue: adminPermissionServiceMock,
        },
        translateServiceProvider,
      ],
    });

    router = TestBed.inject(Router);
    fixture = TestBed.createComponent(TimetableFieldNumberDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(component.isNew).toBe(true);
    expect(component.editMode()).toBe(true);
  });

  describe('Create new Version', () => {
    it('should create successfully a new record', () => {
      vi.spyOn(router, 'navigate').mockReturnValue(Promise.resolve(true));
      mockTimetableFieldNumbersService.createVersion.mockReturnValue(of(version));
      component.formModel.update((model) => ({
        ...model,
        number: '1.1',
        businessOrganisation: 'sbb',
        descriptionOutwardLine1: 'desc',
        meanOfTransport: ['TRAIN'],
        validFrom: moment('2021-06-01'),
        validTo: moment('2029-06-01'),
      }));
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      const snackBarContainer = fixture.nativeElement.parentElement.querySelector('mat-snack-bar-container');
      expect(snackBarContainer).toBeDefined();
      expect(snackBarContainer.textContent.trim()).toBe('TTFN.NOTIFICATION.ADD_SUCCESS');
      expect(snackBarContainer.classList).toContain('success');
      expect(router.navigate).toHaveBeenCalled();
    });

    it('should not create a new record', () => {
      mockTimetableFieldNumbersService.createVersion.mockReturnValue(throwError(() => error));
      component.formModel.update((model) => ({
        ...model,
        number: '1.1',
        businessOrganisation: 'sbb',
        descriptionOutwardLine1: 'desc',
        meanOfTransport: ['TRAIN'],
        validFrom: moment('2021-06-01'),
        validTo: moment('2029-06-01'),
      }));
      fixture.componentInstance.createRecord();
      fixture.detectChanges();

      expect(component.editMode()).toBe(true);
    });
  });
});
