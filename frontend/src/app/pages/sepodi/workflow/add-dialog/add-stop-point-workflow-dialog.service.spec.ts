import {MatDialog} from '@angular/material/dialog';
import {of} from 'rxjs';
import {TestBed} from '@angular/core/testing';
import {TranslateModule} from '@ngx-translate/core';
import {AddStopPointWorkflowDialogService,} from './add-stop-point-workflow-dialog.service';
import {BERN_WYLEREGG} from "../../../../../test/data/service-point";
import {DecisionType, JudgementType, StopPointPerson} from "../../../../api";

describe('AddStopPointWorkflowDialogService', () => {
  let service: AddStopPointWorkflowDialogService;

  const dialogSpy = jasmine.createSpyObj('dialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{ provide: MatDialog, useValue: dialogSpy }],
    });
    service = TestBed.inject(AddStopPointWorkflowDialogService);
  });

  it('should open new workflow', () => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) });
    const examinants: StopPointPerson[] = [
      {
        firstName: 'Thomas',
        lastName: 'Thomas',
        organisation: 'sbb',
        personFunction: 'PO',
        mail: 'thomas.thomas@fake.com',
        judgement: JudgementType.Yes,
        decisionType: DecisionType.Voted,
        id: 1,
      },
      {
        firstName: 'Judith',
        lastName: 'Judith',
        organisation: 'sbb',
        personFunction: 'PO',
        mail: 'judith.judith@fake.com',
        judgement: JudgementType.No,
        decisionType: DecisionType.Voted,
        id: 2,
      }];

    service
      .openDialog(BERN_WYLEREGG)
      .subscribe((result) => expect(result).toBeTrue());

    expect(dialogSpy.open).toHaveBeenCalled();
  });

});
