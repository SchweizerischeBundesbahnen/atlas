import {TestBed} from '@angular/core/testing';

import {StopPointRejectWorkflowDialogService} from './stop-point-reject-workflow-dialog.service';
import {TranslateModule} from "@ngx-translate/core";
import {MatDialog} from "@angular/material/dialog";
import {of} from "rxjs";

describe('StopPointRejectWorkflowDialogService', () => {
  let service: StopPointRejectWorkflowDialogService;

  const dialogSpy = jasmine.createSpyObj('dialog', ['open']);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [TranslateModule.forRoot()],
      providers: [{ provide: MatDialog, useValue: dialogSpy }],
    });
    service = TestBed.inject(StopPointRejectWorkflowDialogService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should open new workflow', () => {
    dialogSpy.open.and.returnValue({ afterClosed: () => of(true) });

    service
      .openDialog(123)
      .subscribe((result) => expect(result).toBeTrue());

    expect(dialogSpy.open).toHaveBeenCalled();
  });
});
