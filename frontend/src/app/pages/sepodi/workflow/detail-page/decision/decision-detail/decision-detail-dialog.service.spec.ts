import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import { DecisionDetailDialogService } from './decision-detail-dialog.service';
import { FormGroup } from '@angular/forms';
import { ExaminantFormGroup } from '../../detail-form/stop-point-workflow-detail-form-group';

describe('DecisionDetailDialogService', () => {
  let service: DecisionDetailDialogService;

  const dialogSpy = jasmine.createSpyObj('dialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{ provide: MatDialog, useValue: dialogSpy }],
    });
    service = TestBed.inject(DecisionDetailDialogService);
  });

  it('should open new workflow', () => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    // todo: finish test
    service
      .openDialog(1, new FormGroup({} as ExaminantFormGroup))
      .subscribe((result) => expect(result).toBeTrue());

    expect(dialogSpy.open).toHaveBeenCalled();
  });
});
