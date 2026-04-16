import { Component, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { Subject } from 'rxjs';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { NotificationService } from '../../../../core/notification/notification.service';
import { TthChangeCantonFormGroup } from './model/tth-change-canton-form-group';
import { ChangeCantonData } from './model/change-canton-data';
import { takeUntil } from 'rxjs/operators';
import { ValidationService } from 'src/app/core/validation/validation.service';
import { TimetableHearingStatementInternalService } from '../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { BaseChangeDialogComponent } from '../base-change-dialog/base-change-dialog.component';
import { Cantons } from '../../../../core/cantons/Cantons';
import { SelectComponent } from '../../../../core/form-components/select/select.component';
import { NgOptimizedImage } from '@angular/common';
import { Canton } from '../../../../core/cantons/Canton';

@Component({
  selector: 'atlas-tth-change-canton-dialog',
  templateUrl: './tth-change-canton-dialog.component.html',
  imports: [BaseChangeDialogComponent, ReactiveFormsModule, SelectComponent, NgOptimizedImage],
})
export class TthChangeCantonDialogComponent implements OnInit {
  formGroup!: FormGroup<TthChangeCantonFormGroup>;
  showSwissCantonDropdown = false;

  readonly CANTON_DROPDOWN_OPTIONS_WITHOUT_SWISS = Cantons.cantons;
  readonly extractEnumCanton = (option: Canton) => option.enumCanton;

  readonly extractShort = (option: Canton) => option.short;

  private ngUnsubscribe = new Subject<void>();

  constructor(
    public dialogRef: MatDialogRef<TthChangeCantonDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ChangeCantonData,
    private readonly notificationService: NotificationService,
    private readonly timetableHearingStatementsServiceV2: TimetableHearingStatementInternalService
  ) {}

  ngOnInit() {
    this.formGroup = new FormGroup<TthChangeCantonFormGroup>({
      cantonChangeComment: new FormControl('', [AtlasFieldLengthValidator.length_280]),
      swissCanton: new FormControl(this.data.swissCanton),
    });
    this.showSwissCantonDropdown = !!this.data.swissCanton;
  }

  onClick() {
    let comment: string | undefined;
    ValidationService.validateForm(this.formGroup);
    if (this.formGroup.valid) {
      if (this.formGroup.controls['cantonChangeComment'].value) {
        comment = this.formGroup.controls['cantonChangeComment'].value;
      }
      this.timetableHearingStatementsServiceV2
        .updateHearingCanton({
          ids: this.data.tths.map((value) => Number(value.id)),
          comment: comment,
          swissCanton: this.formGroup.controls.swissCanton.value!,
        })
        .pipe(takeUntil(this.ngUnsubscribe))
        .subscribe(() => {
          this.notificationService.success('TTH.NOTIFICATION.CANTON_CHANGE.SUCCESS');
          this.dialogRef.close(true);
        });
    }
  }
}
