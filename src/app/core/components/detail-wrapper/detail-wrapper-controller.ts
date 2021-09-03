import { ActivatedRoute } from '@angular/router';
import { Directive, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

@Directive()
export abstract class DetailWrapperController<TYPE> implements OnInit {
  private readonly id: number;
  record!: TYPE;
  form = new FormGroup({});
  heading!: string | undefined;

  protected constructor(private route: ActivatedRoute) {
    this.id = parseInt(this.route.snapshot.paramMap.get('id')!);
  }

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

  getId() {
    return this.id;
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
    if (this.id) {
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
