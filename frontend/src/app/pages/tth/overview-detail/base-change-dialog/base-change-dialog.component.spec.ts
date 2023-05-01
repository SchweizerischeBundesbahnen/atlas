import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BaseChangeDialogComponent } from './base-change-dialog.component';
import { DialogService } from '../../../../core/components/dialog/dialog.service';
import { of } from 'rxjs';
import { AppTestingModule } from '../../../../app.testing.module';
import { FormModule } from '../../../../core/module/form.module';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { TranslatePipe } from '@ngx-translate/core';
import { SwissCanton, TimetableHearingStatement } from '../../../../api';
import { FormControl, FormGroup } from '@angular/forms';
import { TthChangeStatusFormGroup } from '../tth-change-status-dialog/model/tth-change-status-form-group';
import { AtlasFieldLengthValidator } from '../../../../core/validation/field-lengths/atlas-field-length-validator';
import { WhitespaceValidator } from '../../../../core/validation/whitespace/whitespace-validator';
import { AtlasCharsetsValidator } from '../../../../core/validation/charsets/atlas-charsets-validator';
import { By } from '@angular/platform-browser';

const statement: TimetableHearingStatement = {
  id: 1,
  swissCanton: SwissCanton.Bern,
  statement: 'Luca is am yb match gsi',
  justification: 'Napoli ist besser als YB',
  statementSender: {
    email: 'luca@yb.ch',
  },
};

const dialogServiceSpy = jasmine.createSpyObj(DialogService, { confirmLeave: of({}) });
const dialogRefSpy = jasmine.createSpyObj(['close']);
describe('BaseChangeDialogComponent', () => {
  let component: BaseChangeDialogComponent;
  let fixture: ComponentFixture<BaseChangeDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BaseChangeDialogComponent],
      imports: [AppTestingModule, FormModule],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: {
            title: 'Title',
            message: 'message',
            tths: [statement],
            justification: 'Forza Napoli',
            type: 'SINGLE',
            id: 1,
          },
        },
        { provide: MatSnackBarRef, useValue: {} },
        { provide: MAT_SNACK_BAR_DATA, useValue: {} },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: TranslatePipe },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(BaseChangeDialogComponent);
    component = fixture.componentInstance;
    component.controlName = 'justification';
    component.formGroup = new FormGroup<TthChangeStatusFormGroup>({
      justification: new FormControl('', [
        AtlasFieldLengthValidator.statement,
        WhitespaceValidator.blankOrEmptySpaceSurrounding,
        AtlasCharsetsValidator.iso88591,
      ]),
    });
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close dialog when form is not dirty', () => {
    //when
    component.closeDialog();
    //then

    expect(dialogRefSpy.close).toHaveBeenCalled();
  });

  it('should render tth change status dialog', () => {
    component.formGroup.controls['justification'].setValue('Forza Napoli');

    const title = fixture.debugElement.query(By.css('div.dialog > div.mb-5 > span.font-bold-4xl'));
    expect(title.nativeElement.innerText).toBe('Title');

    const content = fixture.debugElement.query(By.css('div.dialog > div > span.message'));
    expect(content.nativeElement.innerText).toBe('message');

    const justification = fixture.debugElement.query(By.css('form-comment'));
    const justificationValue = justification.nativeNode.querySelector('textarea').value;
    expect(justificationValue).toBe('Forza Napoli');

    const cancelButton = fixture.debugElement.query(By.css('mat-dialog-actions button.me-3'));
    expect(cancelButton.nativeElement.innerText).toBe('DIALOG.CANCEL');

    const confirmButton = fixture.debugElement.query(
      By.css('mat-dialog-actions button.primary-color-btn')
    );
    expect(confirmButton.nativeElement.innerText).toBe('DIALOG.OK');
  });
});
