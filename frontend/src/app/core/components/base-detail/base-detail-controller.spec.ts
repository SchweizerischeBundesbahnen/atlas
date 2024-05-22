import {BaseDetailController} from './base-detail-controller';
import {OnInit} from '@angular/core';
import {FormBuilder, FormGroup, ReactiveFormsModule} from '@angular/forms';
import {Record} from './record';
import {DialogService} from '../dialog/dialog.service';
import {TestBed} from '@angular/core/testing';
import {of} from 'rxjs';
import moment from 'moment';
import {Page} from '../../model/page';
import {NotificationService} from '../../notification/notification.service';
import {MAT_SNACK_BAR_DATA, MatSnackBarRef} from '@angular/material/snack-bar';
import {ActivatedRoute, Router, RouterModule} from '@angular/router';
import {HttpClientTestingModule} from '@angular/common/http/testing';
import {MaterialModule} from '../../module/material.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {TranslateFakeLoader, TranslateLoader, TranslateModule} from '@ngx-translate/core';
import {MatDialogRef} from '@angular/material/dialog';
import {ApplicationType} from 'src/app/api';
import {ValidityService} from "../../../pages/sepodi/validity/validity.service";
import {PermissionService} from "../../auth/permission.service";
import {adminPermissionServiceMock} from "../../../app.testing.mocks";

const dialogServiceSpy = jasmine.createSpyObj(['confirm']);
const dialogRefSpy = jasmine.createSpyObj(['close']);
const validityService = jasmine.createSpyObj<ValidityService>([
  'initValidity', 'updateValidity', 'validateAndDisableForm'
]);
describe('BaseDetailController', () => {
  const dummyController = jasmine.createSpyObj('controller', [
    'backToOverview',
    'createRecord',
    'revokeRecord',
    'deleteRecord',
    'updateRecord',
    'isDetailWorkflowable',
  ]);
  let record: Record;

  class DummyBaseDetailController extends BaseDetailController<Record> implements OnInit {
    constructor() {
      super(router, dialogService, notificationService, permissionService, activatedRoute, validityService);
    }

    getPageType(): Page {
      return dummyController.getPageType();
    }

    getApplicationType(): ApplicationType {
      return ApplicationType.Lidi;
    }

    getDetailHeading(): string {
      return '';
    }

    getDetailSubheading(): string {
      return '';
    }

    backToOverview(): void {
      dummyController.backToOverview();
    }

    createRecord(): void {
      dummyController.createRecord();
    }

    revokeRecord(): void {
      dummyController.revokeRecord();
    }

    deleteRecord(): void {
      dummyController.deleteRecord();
    }

    getFormGroup(value: Record): FormGroup {
      return new FormBuilder().group({
        value: [value.id],
      });
    }

    readRecord(): Record {
      return record;
    }

    updateRecord(): void {
      dummyController.updateRecord();
    }

    isWorkflowable(): boolean {
      return true;
    }
  }

  let controller: DummyBaseDetailController;
  let router: Router;
  let dialogService: DialogService;
  let notificationService: NotificationService;
  let permissionService: PermissionService;
  let activatedRoute: ActivatedRoute;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
        HttpClientTestingModule,
        ReactiveFormsModule,
        MaterialModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [
        ValidityService,
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: MatSnackBarRef, useValue: {} },
        { provide: MAT_SNACK_BAR_DATA, useValue: {} },
        { provide: PermissionService, useValue: adminPermissionServiceMock },
      ],
    });
    dialogService = TestBed.inject(DialogService);
    notificationService = TestBed.inject(NotificationService);
    router = TestBed.inject(Router);
    permissionService = TestBed.inject(PermissionService);
    activatedRoute = TestBed.inject(ActivatedRoute);
  });

  describe('existing record', () => {
    beforeEach(() => {
      record = { id: 1 };
      controller = new DummyBaseDetailController();
      controller.ngOnInit();
    });

    it('should create', () => {
      expect(controller).toBeTruthy();
    });

    it('should initialize form', () => {
      expect(controller.isNewRecord()).toBeFalse();
      expect(controller.isExistingRecord()).toBeTrue();
      expect(controller.form.enabled).toBeFalse();
    });

    it('should toggle edit form', () => {
      dialogServiceSpy.confirm.and.returnValue(of(true));

      expect(controller.form.enabled).toBeFalse();

      controller.toggleEdit();
      expect(controller.form.enabled).toBeTrue();
      expect(validityService.initValidity).toHaveBeenCalled()

      controller.toggleEdit();
      expect(controller.form.enabled).toBeFalse();
    });

    it('should update on save', () => {
      spyOn(controller, 'confirmBoTransfer').and.returnValue(of(true));

      controller.toggleEdit();
      controller.form.markAsDirty();
      controller.save();
      expect(validityService.validateAndDisableForm).toHaveBeenCalled();
    });

    it('should ask for confirmation to cancel when dirty', () => {
      dialogServiceSpy.confirm.and.returnValue(of(false));

      controller.toggleEdit();
      controller.form.markAsDirty();

      controller.toggleEdit();
      expect(controller.form.enabled).toBeTrue();
    });

    it('should delete on confirm', () => {
      dialogServiceSpy.confirm.and.returnValue(of(true));
      controller.delete();

      expect(dummyController.deleteRecord).toHaveBeenCalled();
    });
  });

  describe('new record', () => {
    beforeEach(() => {
      record = {};
      controller = new DummyBaseDetailController();
      controller.ngOnInit();
    });

    it('should initialize form', () => {
      expect(controller.isNewRecord()).toBeTrue();
      expect(controller.isExistingRecord()).toBeFalse();
      expect(controller.form.enabled).toBeTrue();
    });

    it('should create on save', () => {
      controller.form.markAsDirty();
      controller.save();

      expect(dummyController.createRecord).toHaveBeenCalled();
    });

    it('should go back to overview on cancel', () => {
      dialogServiceSpy.confirm.and.returnValue(of(true));

      controller.form.markAsDirty();
      controller.toggleEdit();

      expect(dummyController.backToOverview).toHaveBeenCalled();
    });
  });
});

describe('Get actual versioned record', () => {
  let controller: BaseDetailController<Record>;

  const activatedRouteMock: { snapshot?: object } = {};

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterModule.forRoot([]),
        HttpClientTestingModule,
        ReactiveFormsModule,
        MaterialModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [
        ValidityService,
        { provide: BaseDetailController },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: MatSnackBarRef, useValue: {} },
        { provide: MAT_SNACK_BAR_DATA, useValue: {} },
        { provide: PermissionService, useValue: adminPermissionServiceMock },
        { provide: ActivatedRoute, useValue: activatedRouteMock },
      ],
    });
    controller = TestBed.inject(BaseDetailController);
  });

  const firstRecord: Record = {
    id: 1,
    validFrom: moment('1.1.2000', 'DD.MM.YYYY').toDate(),
    validTo: moment('31.12.2000', 'DD.MM.YYYY').toDate(),
  };
  const secondRecord: Record = {
    id: 2,
    validFrom: moment('1.1.2001', 'DD.MM.YYYY').toDate(),
    validTo: moment('31.12.2001', 'DD.MM.YYYY').toDate(),
  };
  const thirdRecord: Record = {
    id: 3,
    validFrom: moment('1.1.2002', 'DD.MM.YYYY').toDate(),
    validTo: moment('31.12.2002', 'DD.MM.YYYY').toDate(),
  };

  it('should return the specified version when id parameter is found', () => {
    //given
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    const today = moment('1.2.2002', 'DD.MM.YYYY').toDate();
    jasmine.clock().mockDate(today);
    activatedRouteMock.snapshot = {
      queryParams: {
        id: 1,
      },
    };

    //when
    const record: Record = controller.evaluateSelectedRecord(records);

    //then
    expect(record.id).toBe(firstRecord.id);
  });

  it('should return the default version when id parameter is not found', () => {
    //given
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    const today = moment('1.2.2002', 'DD.MM.YYYY').toDate();
    jasmine.clock().mockDate(today);
    activatedRouteMock.snapshot = {
      queryParams: {
        id: 1000,
      },
    };

    //when
    const record: Record = controller.evaluateSelectedRecord(records);

    //then
    expect(record.id).toBe(thirdRecord.id);
  });
});
