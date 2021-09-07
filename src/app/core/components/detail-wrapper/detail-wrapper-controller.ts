import { Directive, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Record } from './record';

@Directive()
export abstract class DetailWrapperController<TYPE extends Record> implements OnInit {
  record!: TYPE;
  form = new FormGroup({});
  heading!: string | undefined;

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
      this.form.disable();
    } else {
      this.form.enable();
    }
  }

  save() {
    this.form.disable();
    if (this.getId()) {
      this.updateRecord();
    } else {
      this.createRecord();
    }
  }

  abstract getTitle(record: TYPE): string | undefined;

  abstract readRecord(): TYPE;

  abstract getFormGroup(record: TYPE): FormGroup;

  abstract updateRecord(): void;

  abstract createRecord(): void;

  abstract deleteRecord(): void;
}
