import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TthChangeStatusDialogComponent } from './tth-change-status-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../app.testing.module';
import { SwissCanton, TimetableHearingStatement } from '../../../../api';

const statement: TimetableHearingStatement = {
  id: 1,
  swissCanton: SwissCanton.Bern,
  statement: 'Luca is am yb match gsi',
  justification: 'Napoli ist besser als YB',
  statementSender: {
    email: 'luca@yb.ch',
  },
};

describe('TthChangeStatusDialogComponent', () => {
  let component: TthChangeStatusDialogComponent;
  let fixture: ComponentFixture<TthChangeStatusDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TthChangeStatusDialogComponent],
      imports: [AppTestingModule],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: { title: 'Title', message: 'message', ths: statement, id: 1 },
        },
        { provide: MatDialogRef, useValue: {} },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TthChangeStatusDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
