import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { TestBed } from '@angular/core/testing';
import { TerminationDecisionDetailDialogService } from './termination-decision-detail-dialog.service';
import { FormControl, FormGroup } from '@angular/forms';
import { TerminationDecision } from '../../../../../../api/model/terminationDecision';
import TerminationDecisionPersonEnum = TerminationDecision.TerminationDecisionPersonEnum;
import { TerminationDecisionFormGroup } from '../../stop-point-termination-workflow-detail-form-group';
import moment from 'moment';

describe('TerminationDecisionDetailDialogService', () => {
  let service: TerminationDecisionDetailDialogService;

  const dialogSpy = jasmine.createSpyObj('dialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: MatDialog, useValue: dialogSpy }],
    });
    service = TestBed.inject(TerminationDecisionDetailDialogService);
  });

  it('should open dialog', (done) => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    service
      .openDialog(
        1,
        false,
        TerminationDecisionPersonEnum.InfoPlus,
        new FormGroup<TerminationDecisionFormGroup>({
          examinantMail: new FormControl(''),
          firstName: new FormControl(''),
          lastName: new FormControl(''),
          judgementIcon: new FormControl(''),
          organisation: new FormControl(''),
          judgement: new FormControl('YES'),
          motivation: new FormControl(),
          terminationDate: new FormControl(moment()),
          terminationDecisionPerson: new FormControl(
            TerminationDecisionPersonEnum.InfoPlus
          ),
        })
      )
      .subscribe((result) => {
        expect(result).toBeTrue();
        expect(dialogSpy.open).toHaveBeenCalled();
        done();
      });
  });
});
