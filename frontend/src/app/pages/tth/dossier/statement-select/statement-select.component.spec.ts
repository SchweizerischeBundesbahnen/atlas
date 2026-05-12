import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { StatementSelectComponent } from './statement-select.component';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { SwissCanton, TimetableHearingStatementV2 } from '../../../../api';
import { TimetableHearingStatementInternalService } from '../../../../api/service/lidi/timetable-hearing-statement-internal.service';
import { FormatPipe } from '../../../../core/components/table/pipe/format.pipe';
import { TranslatePipe } from '@ngx-translate/core';
import { mock } from 'vitest-mock-extended';
import { translateServiceProvider } from '../../../../app.testing.mocks';

const statement: TimetableHearingStatementV2 = {
  id: 456,
  swissCanton: SwissCanton.Bern,
  statement: 'Mehr Bös pls',
  statementSender: {
    emails: new Set('me@sbb.ch'),
  },
  documents: [],
};
const timetableHearingStatementInternalService: Mocked<Pick<TimetableHearingStatementInternalService, 'getStatement'>> =
  {
    getStatement: vi.fn().mockReturnValue(of(statement)),
  };

describe('StatementSelectComponent', () => {
  let component: StatementSelectComponent;
  let fixture: ComponentFixture<StatementSelectComponent>;

  const activatedRoute = {
    snapshot: {
      data: {
        dossier: undefined,
      },
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        {
          provide: ActivatedRoute,
          useValue: activatedRoute,
        },
        {
          provide: TimetableHearingStatementInternalService,
          useValue: timetableHearingStatementInternalService,
        },
        {
          provide: TranslatePipe,
          useValue: mock<TranslatePipe>(),
        },
        translateServiceProvider,
        FormatPipe,
      ],
    });

    fixture = TestBed.createComponent(StatementSelectComponent);
    fixture.componentRef.setInput('selectedStatements', [1000]);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should remove statement', () => {
    component.selectedStatements.set([1000]);

    component.removeStatement({ id: 1000 } as TimetableHearingStatementV2);

    expect(component.selectedStatements()).toEqual([]);
  });

  it('should go to statement', () => {
    const windowOpenSpy = vi.spyOn(window, 'open').mockImplementation(() => null);

    component.goToStatement(statement);

    const expectedUrl = '/timetable-hearing/be/active/statements/456';
    expect(windowOpenSpy).toHaveBeenCalledWith(expectedUrl, '_blank');
  });
});
