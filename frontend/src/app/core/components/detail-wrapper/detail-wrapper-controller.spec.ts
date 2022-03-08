import { DetailWrapperController } from './detail-wrapper-controller';
import { OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Record } from './record';
import { DialogService } from '../dialog/dialog.service';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import moment from 'moment';
import { Page } from '../../model/page';
import { NotificationService } from '../../notification/notification.service';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { RouterModule } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MaterialModule } from '../../module/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';

describe('DetailWrapperController', () => {
  const dummyController = jasmine.createSpyObj('controller', [
    'backToOverview',
    'createRecord',
    'deleteRecord',
    'updateRecord',
  ]);
  let record: Record;

  class DummyWrapperController extends DetailWrapperController<Record> implements OnInit {
    constructor() {
      super(dialogService, notificationService);
    }

    getPageType(): Page {
      return dummyController.getPageType();
    }

    backToOverview(): void {
      dummyController.backToOverview();
    }

    createRecord(): void {
      dummyController.createRecord();
    }

    deleteRecord(): void {
      dummyController.deleteRecord();
    }

    getFormGroup(value: Record): FormGroup {
      return new FormBuilder().group({
        value: [value.id],
      });
    }

    getTitle(record: Record): string | undefined {
      return record.id ? +record.id + '' : undefined;
    }

    readRecord(): Record {
      return record;
    }

    updateRecord(): void {
      dummyController.updateRecord();
    }
  }

  let controller: DummyWrapperController;
  const dialogServiceSpy = jasmine.createSpyObj(['confirm']);
  let dialogService: DialogService;
  let notificationService: NotificationService;

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
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: MatSnackBarRef, useValue: {} },
        { provide: MAT_SNACK_BAR_DATA, useValue: {} },
      ],
    });
    dialogService = TestBed.inject(DialogService);
    notificationService = TestBed.inject(NotificationService);
  });

  describe('existing record', () => {
    beforeEach(() => {
      record = { id: 1 };
      controller = new DummyWrapperController();
      controller.ngOnInit();
    });

    it('should create', () => {
      expect(controller).toBeTruthy();
    });

    it('should initialize heading', () => {
      expect(controller.heading).toBe('1');
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

      controller.toggleEdit();
      expect(controller.form.enabled).toBeFalse();
    });

    it('should ask for confirmation to cancel when dirty', () => {
      dialogServiceSpy.confirm.and.returnValue(of(false));

      controller.toggleEdit();
      controller.form.markAsDirty();

      controller.toggleEdit();
      expect(controller.form.enabled).toBeTrue();
    });

    it('should update on save', () => {
      controller.toggleEdit();
      controller.form.markAsDirty();
      controller.save();

      expect(dummyController.updateRecord).toHaveBeenCalled();
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
      controller = new DummyWrapperController();
      controller.ngOnInit();
    });

    it('should initialize heading', () => {
      expect(controller.heading).toBeUndefined();
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
  let controller: DetailWrapperController<Record>;
  const dialogServiceSpy = jasmine.createSpyObj(['confirm']);

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
        { provide: DetailWrapperController },
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: MatSnackBarRef, useValue: {} },
        { provide: MAT_SNACK_BAR_DATA, useValue: {} },
      ],
    });
    controller = TestBed.inject(DetailWrapperController);
  });

  const firstRecord: Record = {
    id: 1,
    validFrom: moment('1.1.2000', 'DD.MM.YYYY').toDate(),
    validTo: moment('31.12.2000', 'DD.MM.YYYY').toDate(),
  };
  const secondRecord: Record = {
    id: 1,
    validFrom: moment('1.1.2001', 'DD.MM.YYYY').toDate(),
    validTo: moment('31.12.2001', 'DD.MM.YYYY').toDate(),
  };
  const thirdRecord: Record = {
    id: 1,
    validFrom: moment('1.1.2002', 'DD.MM.YYYY').toDate(),
    validTo: moment('31.12.2002', 'DD.MM.YYYY').toDate(),
  };

  it('should return the firstRecord version when today is the firstRecord range', () => {
    //given
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    const today = moment('1.2.2000').toDate();
    jasmine.clock().mockDate(today);

    //when
    const record: Record = controller.getActualRecord(records);

    //then
    expect(record.id).toBe(firstRecord.id);
  });

  it('should return the secondRecord version when today is the secondRecord range', () => {
    //given
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    const today = moment('1.2.2001').toDate();
    jasmine.clock().mockDate(today);

    //when
    const record: Record = controller.getActualRecord(records);

    //then
    expect(record.id).toBe(secondRecord.id);
  });

  it('should return the thirdRecord version when today is the thirdRecord range', () => {
    //given
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    const today = moment('1.2.2002').toDate();
    jasmine.clock().mockDate(today);

    //when
    const record: Record = controller.getActualRecord(records);

    //then
    expect(record.id).toBe(thirdRecord.id);
  });

  it('should return the firstRecord version when today is before all records', () => {
    //given
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    const today = moment('1.2.1999').toDate();
    jasmine.clock().mockDate(today);

    //when
    const record: Record = controller.getActualRecord(records);

    //then
    expect(record.id).toBe(firstRecord.id);
  });

  it('should return the thirdRecord version when today is after all records', () => {
    //given
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    const today = moment('1.2.2099').toDate();
    jasmine.clock().mockDate(today);

    //when
    const record: Record = controller.getActualRecord(records);

    //then
    expect(record.id).toBe(thirdRecord.id);
  });

  it('should return the thirdRecord version when today is after all records', () => {
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    const fourthRecord: Record = {
      id: 4,
      validFrom: moment('1.1.2004', 'DD.MM.YYYY').toDate(),
      validTo: moment('31.12.2004', 'DD.MM.YYYY').toDate(),
    };
    records.push(fourthRecord);
    //given
    const today = moment('1.2.2003').toDate();
    jasmine.clock().mockDate(today);

    //when
    const record: Record = controller.getActualRecord(records);

    //then
    expect(record.id).toBe(fourthRecord.id);
  });

  it('should return the record version with today', () => {
    const todayRecord: Record = {
      id: 1,
      validFrom: moment('1.1.2004', 'DD.MM.YYYY').toDate(),
      validTo: moment('1.1.2004', 'DD.MM.YYYY').toDate(),
    };
    const tomorrowRecord: Record = {
      id: 2,
      validFrom: moment('2.1.2004', 'DD.MM.YYYY').toDate(),
      validTo: moment('2.1.2004', 'DD.MM.YYYY').toDate(),
    };
    const records: Array<Record> = [todayRecord, tomorrowRecord];
    //given
    const today = moment('1.1.2004').toDate();
    jasmine.clock().mockDate(today);

    //when
    const record: Record = controller.getActualRecord(records);

    //then
    expect(record.id).toBe(todayRecord.id);
  });
});
