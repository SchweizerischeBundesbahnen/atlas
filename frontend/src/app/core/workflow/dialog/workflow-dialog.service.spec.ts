import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { TestBed } from '@angular/core/testing';
import { TranslateModule } from '@ngx-translate/core';
import {
  LineVersionWorkflow,
  Status,
  WorkflowProcessingStatus,
} from '../../../api';
import { WorkflowDialogService } from './workflow-dialog.service';

describe('WorkflowDialogService', () => {
  let service: WorkflowDialogService;

  const dialogSpy = jasmine.createSpyObj('dialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{ provide: MatDialog, useValue: dialogSpy }],
    });
    service = TestBed.inject(WorkflowDialogService);
  });

  it('should open new workflow', () => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    service
      .openNew(
        {
          id: 123,
          validFrom: new Date(),
          validTo: new Date(),
          slnid: 'ch:1:slnid:1000003',
          businessOrganisation: 'ch:1:sboid:110000',
          status: Status.Draft,
          versionNumber: 0,
        },
        'description'
      )
      .subscribe((result) => expect(result).toBeTrue());

    expect(dialogSpy.open).toHaveBeenCalled();
  });

  it('should open existing workflow', () => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    service
      .openExisting(
        {
          id: 123,
          validFrom: new Date(),
          validTo: new Date(),
          slnid: 'ch:1:slnid:1000003',
          businessOrganisation: 'ch:1:sboid:110000',
          status: Status.Draft,
          versionNumber: 0,
          lineVersionWorkflows: new Set<LineVersionWorkflow>([
            {
              workflowId: 1,
              workflowProcessingStatus: WorkflowProcessingStatus.InProgress,
            },
          ]),
        },
        'description'
      )
      .subscribe((result) => expect(result).toBeTrue());

    expect(dialogSpy.open).toHaveBeenCalled();
  });
});
