import { Directive, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Record } from './record';
import { DialogService } from '../dialog/dialog.service';
import { Observable, of } from 'rxjs';

@Directive()
export abstract class DetailWrapperController<TYPE extends Record> implements OnInit {
  record!: TYPE;
  form = new FormGroup({});
  heading!: string | undefined;

  protected constructor(protected dialogService: DialogService) {}

  ngOnInit(): void {
    this.record = this.readRecord();
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
