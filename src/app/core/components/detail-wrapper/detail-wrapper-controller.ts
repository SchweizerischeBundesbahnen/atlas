import { Directive, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Record } from './record';
import { DialogService } from '../dialog/dialog.service';
import { Observable, of } from 'rxjs';
import moment from 'moment/moment';

@Directive()
export abstract class DetailWrapperController<TYPE extends Record> implements OnInit {
  record!: TYPE;
  form = new FormGroup({});
  heading!: string | undefined;

  protected constructor(protected dialogService: DialogService) {}

  ngOnInit(): void {
    const records = this.readRecord();
    if (Array.isArray(records)) {
      console.log('is array');
      this.record = this.getActualRecord(records);
    } else {
      console.log('is not array');
      this.record = records;
    }
    this.form = this.getFormGroup(this.record);

    if (this.isExistingRecord()) {
      this.form.disable();
      this.heading = this.getTitle(this.record);
    } else {
      this.form.enable();
    }
  }

  getId(): number {
    return this.record.id!;
  }

  isNewRecord() {
    return !this.getId();
  }

  isExistingRecord() {
    return !this.isNewRecord();
  }

  toggleEdit() {
    if (this.form.enabled) {
      this.confirmLeave().subscribe((confirmed) => {
        if (confirmed) {
          if (this.isNewRecord()) {
            this.backToOverview();
          } else {
            this.form.disable();
            this.ngOnInit();
          }
        }
      });
    } else {
      this.form.enable();
    }
  }

  save() {
    this.validateAllFormFields(this.form);
    if (this.form.valid) {
      this.form.disable();
      if (this.getId()) {
        this.updateRecord();
      } else {
        this.createRecord();
      }
    }
  }

  delete() {
    this.dialogService
      .confirm({
        title: 'DIALOG.WARNING',
        message: 'DIALOG.DELETE',
      })
      .subscribe((confirmed) => {
        if (confirmed) {
          this.deleteRecord();
        }
      });
  }

  getActualRecord(records: Array<TYPE>): TYPE {
    if (records.length == 1) {
      return records[0];
    }
    const now = moment();
    const matchedRecord = records.filter((record) => {
      const currentValidFrom = moment(record.validFrom);
      const currentValidTo = moment(record.validTo);
      return now.isBetween(currentValidFrom, currentValidTo);
    });

    console.log(matchedRecord);
    if (matchedRecord.length == 1) {
      return matchedRecord[0];
    } else if (matchedRecord.length > 1) {
      console.log('Something went wrong. Found more than one Record.');
    } else if (matchedRecord.length == 0 && records.length > 1) {
      //get next in future
      const firstIndexValidFrom = records[0].validFrom;
      if (now.isBefore(firstIndexValidFrom)) {
        return records[0];
      }
      //get last in passt
      const lastIndexValidTo = records[matchedRecord.length - 1].validTo;
      if (now.isAfter(lastIndexValidTo)) {
        return matchedRecord[matchedRecord.length - 1];
      }
    }
    return records[0];
  }

  abstract getTitle(record: TYPE): string | undefined;

  abstract readRecord(): TYPE;

  abstract getFormGroup(record: TYPE): FormGroup;

  abstract updateRecord(): void;

  abstract createRecord(): void;

  abstract deleteRecord(): void;

  abstract backToOverview(): void;

  private confirmLeave(): Observable<boolean> {
    if (this.form.dirty) {
      return this.dialogService.confirm({
        title: 'DIALOG.DISCARD_CHANGES_TITLE',
        message: 'DIALOG.LEAVE_SITE',
      });
    }
    return of(true);
  }

  private validateAllFormFields(formGroup: FormGroup) {
    Object.keys(formGroup.controls).forEach((field) => {
      const control = formGroup.get(field);
      if (control instanceof FormControl) {
        control.markAsTouched({ onlySelf: true });
      } else if (control instanceof FormGroup) {
        this.validateAllFormFields(control);
      }
    });
  }
}
