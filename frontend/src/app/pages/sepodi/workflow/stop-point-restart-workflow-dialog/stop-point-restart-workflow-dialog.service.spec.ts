import { TestBed } from '@angular/core/testing';

import { StopPointRestartWorkflowDialogService } from './stop-point-restart-workflow-dialog.service';
import {TranslateModule} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";
import {of} from "rxjs";

describe('StopPointRestartWorkflowDialogService', () => {
  let service: StopPointRestartWorkflowDialogService;

  const dialogSpy = jasmine.createSpyObj('dialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{provide: MatDialog, useValue: dialogSpy}],
    });
    service = TestBed.inject(StopPointRestartWorkflowDialogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should open new resart workflow', () => {
    dialogSpy.open.and.returnValue({afterClosed: () => of(true)});

    service
      .openDialog(123, 'RESTART')
      .subscribe((result) => expect(result).toBeTrue());

    expect(dialogSpy.open).toHaveBeenCalled();
  });
});
