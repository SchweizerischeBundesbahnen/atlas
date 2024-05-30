import {MatDialog} from '@angular/material/dialog';
import {of} from 'rxjs';
import {TestBed} from '@angular/core/testing';
import {TranslateModule} from '@ngx-translate/core';
import {AddStopPointWorkflowDialogService,} from './add-stop-point-workflow-dialog.service';
import {Status} from "../../../../api";

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
      .openDialog(
        {
          id: 123,
          validFrom: new Date(),
          validTo: new Date(),
          slnid: 'ch:1:slnid:1000003',
          businessOrganisation: 'ch:1:sboid:110000',
          status: Status.Draft,
          versionNumber: 0,
        },
      )
      .subscribe((result) => expect(result).toBeTrue());

    expect(dialogSpy.open).toHaveBeenCalled();
  });

});
