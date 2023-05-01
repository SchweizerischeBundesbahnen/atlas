import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TthChangeCantonDialogComponent } from './tth-change-canton-dialog.component';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../app.testing.module';
import { FormModule } from '../../../../core/module/form.module';
import { BaseChangeDialogComponent } from '../base-change-dialog/base-change-dialog.component';
import { SwissCanton, TimetableHearingStatement } from '../../../../api';
import { TranslatePipe } from '@ngx-translate/core';

const statement: TimetableHearingStatement = {
  id: 1,
  swissCanton: SwissCanton.Bern,
  statement: 'Luca is am yb match gsi',
  justification: 'Napoli ist besser als YB',
  statementSender: {
    email: 'luca@yb.ch',
  },
};
const dialogRefSpy = jasmine.createSpyObj(['close']);
describe('TthChangeCantonDialogComponent', () => {
  let component: TthChangeCantonDialogComponent;
  let fixture: ComponentFixture<TthChangeCantonDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TthChangeCantonDialogComponent, BaseChangeDialogComponent],
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

    fixture = TestBed.createComponent(TthChangeCantonDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
