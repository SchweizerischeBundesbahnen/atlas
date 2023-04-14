import { Component, Inject, OnInit } from '@angular/core';
import { TimetableHearingService, TimetableHearingYear } from '../../../api';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { take } from 'rxjs';
import { FormControl, FormGroup } from '@angular/forms';

@Component({
  selector: 'dialog-manage-tth',
  templateUrl: './dialog-manage-tth.component.html',
  styleUrls: ['./dialog-manage-tth.component.scss'],
})
export class DialogManageTthComponent implements OnInit {
  readonly statementCreatableExternalCtrlName = 'statementCreatableExternal';
  readonly statementCreatableInternalCtrlName = 'statementCreatableInternal';
  readonly statementEditableCtrlName = 'statementEditable';

  private _showManageView = true;

  get showManageView(): boolean {
    return this._showManageView;
  }

  private readonly year: number;
  private readonly timetableHearingYear?: TimetableHearingYear;

  private readonly manageTthFormGroup: FormGroup = new FormGroup({
    [this.statementCreatableExternalCtrlName]: new FormControl<boolean>(false),
    [this.statementCreatableInternalCtrlName]: new FormControl<boolean>(false),
    [this.statementEditableCtrlName]: new FormControl<boolean>(false),
  });

  constructor(
    @Inject(MAT_DIALOG_DATA) private readonly matDialogData: number,
    private readonly tthService: TimetableHearingService
  ) {
    this.year = matDialogData;
  }

  ngOnInit() {
    console.log(this.year);
    this.tthService
      .getHearingYear(this.year)
      .pipe(take(1))
      .subscribe({
        next: (year) => {
          (this.timetableHearingYear as TimetableHearingYear | undefined) = year;
          this.manageTthFormGroup.setValue({
            statementCreatableExternal: !!year.statementCreatableExternal,
            statementCreatableInternal: !!year.statementCreatableInternal,
            statementEditable: !!year.statementEditable,
          });
        },
        error: (err) => console.error(err), // TODO: error handling
      });
  }

  handleManageViewCloseTthClick(): void {
    console.log(this.manageTthFormGroup);
    this._showManageView = false;
  }

  getFormCtrlValueOf(ctrlName: string): boolean {
    return this.manageTthFormGroup.value[ctrlName];
  }

  setFormCtrlValueOf(ctrlName: string, value: boolean): void {
    this.manageTthFormGroup.patchValue({
      [ctrlName]: value,
    });
    this.manageTthFormGroup.markAsTouched();
  }
}
