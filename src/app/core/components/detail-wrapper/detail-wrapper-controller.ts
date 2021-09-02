import { ActivatedRoute } from '@angular/router';
import { Directive, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';

@Directive()
export abstract class DetailWrapperController implements OnInit {
  private readonly id: number;
  editable = false;

  protected constructor(private route: ActivatedRoute) {
    this.id = parseInt(this.route.snapshot.paramMap.get('id')!);

    if (this.isNewRecord()) {
      this.editable = true;
    }
  }

  ngOnInit(): void {
    if (this.isExistingRecord()) {
      this.readRecord();
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

  toggleEdit(detailForm: NgForm) {
    this.editable = !this.editable;
    if (!this.editable) {
      detailForm.reset();
    }
  }

  onSubmit() {
    this.editable = false;
    if (this.id) {
      this.updateRecord();
    } else {
      this.createRecord();
    }
  }

  abstract readRecord(): void;

  abstract updateRecord(): void;

  abstract createRecord(): void;

  abstract deleteRecord(): void;
}
