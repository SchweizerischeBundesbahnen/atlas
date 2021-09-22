import { DetailWrapperController } from './detail-wrapper-controller';
import { OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Record } from './record';
import { DialogService } from '../dialog/dialog.service';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

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
      super(dialogService);
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

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: DialogService, useValue: dialogServiceSpy }],
    });
    dialogService = TestBed.inject(DialogService);
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
