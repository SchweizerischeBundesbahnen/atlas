import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { StatementSelectDialogComponent } from './statement-select-dialog.component';
import { AppTestingModule } from '../../../../../app.testing.module';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslatePipe } from '@ngx-translate/core';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { of } from 'rxjs';
import { SwissCanton, TimetableHearingStatementV2 } from '../../../../../api';
import {
  TimetableHearingStatementInternalService
} from '../../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { FormatPipe } from '../../../../../core/components/table/pipe/format.pipe';
import { Component, input, model } from '@angular/core';
import { StatementSelectComponent } from '../statement-select.component';
import { StatementSelectData } from '../../statement-select-data';

const dialogData: StatementSelectData = {
  title: 'TTH.DIALOG.STATUS_CHANGE',
  message: 'TTH.DIALOG.STATUS_CHANGE',
  cancelText: 'COMMON.CANCEL',
  confirmText: 'COMMON.APPLY',
  selectedStatements: [1000],
  swissCanton: SwissCanton.Bern,
  timetableHearingYear: 2020,
};

const dialogServiceSpy: Mocked<Pick<DialogService, 'confirmLeave'>> = {
  confirmLeave: vi.fn().mockReturnValue(of(true)),
};
const dialogRefSpy: Mocked<Pick<MatDialogRef<StatementSelectDialogComponent>, 'close'>> = {
  close: vi.fn(),
};

const statement: TimetableHearingStatementV2 = {
  id: 456,
  swissCanton: SwissCanton.Bern,
  statement: 'Mehr Bös pls',
  statementSender: {
    emails: new Set('me@sbb.ch'),
  },
  documents: [],
};
const timetableHearingStatementInternalService: Mocked<
  Pick<TimetableHearingStatementInternalService, 'getStatements' | 'getStatement'>
> = {
  getStatements: vi.fn().mockReturnValue(of({ objects: [], totalCount: 0 })),
  getStatement: vi.fn().mockReturnValue(of(statement)),
};

@Component({
  selector: 'atlas-statement-select',
  template: '<p>Mock statement selection</p>',
})
export class MockStatementSelectComponent {
  readonly selectedStatements = model.required<number[]>();
  readonly removeOptionEnabled = input(true);
}

describe('StatementSelectDialogComponent', () => {
  let component: StatementSelectDialogComponent;
  let fixture: ComponentFixture<StatementSelectDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppTestingModule, StatementSelectDialogComponent],
      providers: [
        { provide: DialogService, useValue: dialogServiceSpy },
        { provide: MatDialogRef, useValue: dialogRefSpy },
        {
          provide: MAT_DIALOG_DATA,
          useValue: dialogData,
        },
        {
          provide: TimetableHearingStatementInternalService,
          useValue: timetableHearingStatementInternalService,
        },
        { provide: TranslatePipe },
        { provide: FormatPipe },
      ],
    })
      .overrideComponent(StatementSelectDialogComponent, {
        remove: { imports: [StatementSelectComponent] },
        add: { imports: [MockStatementSelectComponent] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(StatementSelectDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should close dialog', () => {
    component.cancel();
    expect(dialogRefSpy.close).toHaveBeenCalledWith();
  });

  it('should confirm dialog', () => {
    expect(component.selectedStatements).toEqual([1000]);
    component.confirm();
    expect(dialogRefSpy.close).toHaveBeenCalledWith([1000]);
  });

  it('should add statement to selection dialog', () => {
    component.addStatement(statement);
    component.confirm();
    expect(dialogRefSpy.close).toHaveBeenCalledWith([1000, 456]);
  });

  it('should add statement unique to selection dialog', () => {
    component.addStatement(statement);
    component.addStatement(statement);
    component.confirm();
    expect(dialogRefSpy.close).toHaveBeenCalledWith([1000, 456]);
  });
});
