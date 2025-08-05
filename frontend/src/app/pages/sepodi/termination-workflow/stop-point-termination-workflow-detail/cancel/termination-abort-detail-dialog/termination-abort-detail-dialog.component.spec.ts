import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TerminationAbortDetailDialogComponent } from './termination-abort-detail-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormControl, FormGroup } from '@angular/forms';
import { TerminationAbortFormGroup } from '../../stop-point-termination-workflow-detail-form-group';
import { TerminationAbortDetailDialogData } from '../termination-abort-dialog.service';
import { WorkflowService } from '../../../../../../api/service/workflow/workflow.service';
import { of } from 'rxjs';
import { TranslatePipe } from '@ngx-translate/core';
import { translateServiceProvider } from '../../../../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';

const dialogRefSpy = jasmine.createSpyObj(['close']);

const closeTerminationDialogData: TerminationAbortDetailDialogData = {
  title: '',
  message: '',
  workflowId: 123,
  abortComment: new FormGroup<TerminationAbortFormGroup>({
    abortComment: new FormControl(''),
  }),
};

const terminationWorkflowService = jasmine.createSpyObj('WorkflowService', {
  abortTermination: of(),
});

describe('TerminationCancelDetailDialog', () => {
  let component: TerminationAbortDetailDialogComponent;
  let fixture: ComponentFixture<TerminationAbortDetailDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TerminationAbortDetailDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: MAT_DIALOG_DATA, useValue: closeTerminationDialogData },
        {
          provide: WorkflowService,
          useValue: terminationWorkflowService,
        },
        TranslatePipe,
        translateServiceProvider,
        provideHttpClient(),
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TerminationAbortDetailDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should close dialog', () => {
    component.close();

    expect(dialogRefSpy.close).toHaveBeenCalled();
  });

  it('should cancel termination', () => {
    component.abortTermination();

    expect(terminationWorkflowService.abortTermination).toHaveBeenCalled();
  });
});
