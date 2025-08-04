import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TerminationCancelDetailDialogComponent } from './termination-cancel-detail-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormControl, FormGroup } from '@angular/forms';
import { TerminationCancelFormGroup } from '../../stop-point-termination-workflow-detail-form-group';
import { TerminationCancelDetailDialogData } from '../termination-cancel-dialog.service';
import { WorkflowService } from '../../../../../../api/service/workflow/workflow.service';
import { of } from 'rxjs';
import { TranslatePipe } from '@ngx-translate/core';
import { translateServiceProvider } from '../../../../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';

const dialogRefSpy = jasmine.createSpyObj(['close']);

const closeTerminationDialogData: TerminationCancelDetailDialogData = {
  title: '',
  message: '',
  workflowId: 123,
  cancelComment: new FormGroup<TerminationCancelFormGroup>({
    cancelComment: new FormControl(''),
  }),
};

const terminationWorkflowService = jasmine.createSpyObj('WorkflowService', {
  cancelTermination: of(),
});

describe('TerminationCancelDetailDialog', () => {
  let component: TerminationCancelDetailDialogComponent;
  let fixture: ComponentFixture<TerminationCancelDetailDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TerminationCancelDetailDialogComponent],
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

    fixture = TestBed.createComponent(TerminationCancelDetailDialogComponent);
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
    component.cancelTermination();

    expect(terminationWorkflowService.cancelTermination).toHaveBeenCalled();
  });
});
