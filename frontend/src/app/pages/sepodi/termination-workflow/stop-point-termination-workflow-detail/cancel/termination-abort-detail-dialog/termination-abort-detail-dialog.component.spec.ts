import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import {
  TerminationAbortDetailDialogComponent,
  TerminationAbortDetailDialogData,
} from './termination-abort-detail-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormControl, FormGroup } from '@angular/forms';
import { TerminationAbortFormGroup } from '../../stop-point-termination-workflow-detail-form-group';
import { StopPointTerminationWorkflowService } from '../../../../../../api/service/workflow/stop-point-termination-workflow.service';
import { EMPTY } from 'rxjs';
import { TranslatePipe } from '@ngx-translate/core';
import { translateServiceProvider } from '../../../../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('TerminationCancelDetailDialog', () => {
  let component: TerminationAbortDetailDialogComponent;
  let fixture: ComponentFixture<TerminationAbortDetailDialogComponent>;

  let dialogRefMock: Mocked<
    Pick<MatDialogRef<TerminationAbortDetailDialogComponent>, 'close'>
  >;
  let terminationWorkflowServiceMock: Mocked<
    Pick<StopPointTerminationWorkflowService, 'abortTermination'>
  >;

  beforeEach(async () => {
    dialogRefMock = {
      close: vi.fn(),
    };

    terminationWorkflowServiceMock = {
      abortTermination: vi.fn().mockReturnValue(EMPTY),
    };

    const closeTerminationDialogData: TerminationAbortDetailDialogData = {
      title: '',
      message: '',
      workflowId: 123,
      abortComment: new FormGroup<TerminationAbortFormGroup>({
        abortComment: new FormControl(''),
      }),
    };

    await TestBed.configureTestingModule({
      imports: [TerminationAbortDetailDialogComponent],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefMock },
        { provide: MAT_DIALOG_DATA, useValue: closeTerminationDialogData },
        {
          provide: StopPointTerminationWorkflowService,
          useValue: terminationWorkflowServiceMock,
        },
        TranslatePipe,
        translateServiceProvider,
        provideHttpClient(),
        provideHttpClientTesting(),
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

    expect(dialogRefMock.close).toHaveBeenCalled();
  });

  it('should cancel termination', () => {
    component.abortTermination();

    expect(terminationWorkflowServiceMock.abortTermination).toHaveBeenCalled();
  });
});
