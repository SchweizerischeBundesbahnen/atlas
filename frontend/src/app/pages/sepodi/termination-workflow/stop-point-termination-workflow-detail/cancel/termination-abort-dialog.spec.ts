import { TestBed } from '@angular/core/testing';

import { TerminationAbortDialogService } from './termination-abort-dialog.service';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { FormControl, FormGroup } from '@angular/forms';
import { TerminationAbortFormGroup } from '../stop-point-termination-workflow-detail-form-group';

describe('TerminationAbortDialog', () => {
  let service: TerminationAbortDialogService;
  const dialogSpy = jasmine.createSpyObj('dialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: MatDialog, useValue: dialogSpy }],
    });
    service = TestBed.inject(TerminationAbortDialogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should open dialog', (done) => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    service
      .openDialog(
        1,
        new FormGroup<TerminationAbortFormGroup>({
          abortComment: new FormControl(''),
        })
      )
      .subscribe((result) => {
        expect(result).toBeTrue();
        expect(dialogSpy.open).toHaveBeenCalled();
        done();
      });
  });
});
