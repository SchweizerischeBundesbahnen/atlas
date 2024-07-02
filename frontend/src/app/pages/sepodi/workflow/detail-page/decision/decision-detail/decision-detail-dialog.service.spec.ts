import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { TestBed } from '@angular/core/testing';
import { DecisionDetailDialogService } from './decision-detail-dialog.service';
import { FormControl, FormGroup } from '@angular/forms';
import { ExaminantFormGroup } from '../../detail-form/stop-point-workflow-detail-form-group';
import {WorkflowStatus} from "../../../../../../api";

describe('DecisionDetailDialogService', () => {
  let service: DecisionDetailDialogService;

  const dialogSpy = jasmine.createSpyObj('dialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: MatDialog, useValue: dialogSpy }],
    });
    service = TestBed.inject(DecisionDetailDialogService);
  });

  it('should open dialog', (done) => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    service
      .openDialog(
        1,
        WorkflowStatus.Hearing,
        new FormGroup<ExaminantFormGroup>({
          mail: new FormControl(''),
          firstName: new FormControl(''),
          lastName: new FormControl(''),
          judgementIcon: new FormControl(''),
          organisation: new FormControl(''),
          personFunction: new FormControl(''),
          judgement: new FormControl('YES'),
          id: new FormControl(1),
        }),
      )
      .subscribe((result) => {
        expect(result).toBeTrue();
        expect(dialogSpy.open).toHaveBeenCalled();
        done();
      });
  });
});
