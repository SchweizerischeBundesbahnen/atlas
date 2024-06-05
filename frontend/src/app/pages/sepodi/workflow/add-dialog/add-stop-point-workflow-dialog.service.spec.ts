import {MatDialog} from '@angular/material/dialog';
import {of} from 'rxjs';
import {TestBed} from '@angular/core/testing';
import {TranslateModule} from '@ngx-translate/core';
import {AddStopPointWorkflowDialogService,} from './add-stop-point-workflow-dialog.service';
import {BERN_WYLEREGG} from "../../../../../test/data/service-point";

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

    service
      .openDialog(BERN_WYLEREGG)
      .subscribe((result) => expect(result).toBeTrue());

    expect(dialogSpy.open).toHaveBeenCalled();
  });

});
