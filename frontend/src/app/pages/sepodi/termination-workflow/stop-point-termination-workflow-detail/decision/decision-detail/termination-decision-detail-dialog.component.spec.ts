import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import {
  TerminationDecisionDetailDialogComponent,
  TerminationDecisionDetailDialogData,
} from './termination-decision-detail-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { AppTestingModule } from '../../../../../../app.testing.module';
import { EMPTY } from 'rxjs';
import { CommentComponent } from '../../../../../../core/form-components/comment/comment.component';
import { AtlasFieldErrorComponent } from '../../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { TextFieldComponent } from '../../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '@atlas/form';
import { LoadingSpinnerComponent } from '../../../../../../core/components/loading-spinner/loading-spinner.component';
import { DialogContentComponent } from '../../../../../../core/components/dialog/content/dialog-content.component';
import { DialogCloseComponent } from '../../../../../../core/components/dialog/close/dialog-close.component';
import { DialogFooterComponent } from '../../../../../../core/components/dialog/footer/dialog-footer.component';
import { MockAtlasButtonComponent } from '../../../../../../app.testing.mocks';
import { TerminationDecision } from '../../../../../../api/model/terminationDecision';
import { FormControl, FormGroup } from '@angular/forms';
import { TerminationDecisionFormGroup } from '../../stop-point-termination-workflow-detail-form-group';
import moment from 'moment/moment';
import { StopPointTerminationWorkflowService } from '../../../../../../api/service/workflow/stop-point-termination-workflow.service';
import { TerminationWorkflowStatus } from '../../../../../../api/model/terminationWorkflowStatus';
import TerminationDecisionPersonEnum = TerminationDecision.TerminationDecisionPersonEnum;

describe('TerminationDecisionDetailDialogComponent', () => {
  let component: TerminationDecisionDetailDialogComponent;
  let fixture: ComponentFixture<TerminationDecisionDetailDialogComponent>;

  let dialogRefMock: Mocked<Pick<MatDialogRef<TerminationDecisionDetailDialogComponent>, 'close'>>;
  let terminationWorkflowServiceMock: Mocked<
    Pick<StopPointTerminationWorkflowService, 'decisionInfoPlus' | 'decisionNova'>
  >;

  const buildDecisionDialogData = (readOnly: boolean): TerminationDecisionDetailDialogData => ({
    versionValidTo: new Date('9999-12-14'),
    title: '',
    message: '',
    workflowId: 123,
    readOnly,
    workflowStatus: TerminationWorkflowStatus.Started,
    examinant: TerminationDecisionPersonEnum.InfoPlus,
    decision: new FormGroup<TerminationDecisionFormGroup>({
      examinantMail: new FormControl(''),
      firstName: new FormControl(''),
      lastName: new FormControl(''),
      judgementIcon: new FormControl(''),
      organisation: new FormControl(''),
      judgement: new FormControl('YES'),
      motivation: new FormControl(),
      terminationDate: new FormControl(moment()),
      terminationDecisionPerson: new FormControl(TerminationDecisionPersonEnum.InfoPlus),
    }),
  });

  beforeEach(() => {
    dialogRefMock = {
      close: vi.fn(),
    };

    terminationWorkflowServiceMock = {
      decisionInfoPlus: vi.fn().mockReturnValue(EMPTY),
      decisionNova: vi.fn().mockReturnValue(EMPTY),
    };

    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        TerminationDecisionDetailDialogComponent,
        CommentComponent,
        AtlasFieldErrorComponent,
        TextFieldComponent,
        AtlasLabelFieldComponent,
        LoadingSpinnerComponent,
        DialogContentComponent,
        DialogCloseComponent,
        DialogFooterComponent,
        MockAtlasButtonComponent,
      ],
      providers: [
        { provide: MatDialogRef, useValue: dialogRefMock },
        { provide: MAT_DIALOG_DATA, useValue: buildDecisionDialogData(false) },
        {
          provide: StopPointTerminationWorkflowService,
          useValue: terminationWorkflowServiceMock,
        },
      ],
    });
  });

  describe('while deciding', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(TerminationDecisionDetailDialogComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();

      expect(component.readOnly).toBe(false);
    });

    it('should close dialog', () => {
      component.close();

      expect(dialogRefMock.close).toHaveBeenCalled();
    });

    it('should decide', () => {
      component.decide();

      expect(terminationWorkflowServiceMock.decisionInfoPlus).toHaveBeenCalled();
    });
  });

  describe('while reading', () => {
    beforeEach(() => {
      TestBed.overrideProvider(MAT_DIALOG_DATA, {
        useValue: buildDecisionDialogData(true),
      });
      fixture = TestBed.createComponent(TerminationDecisionDetailDialogComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should init', () => {
      expect(component).toBeTruthy();

      expect(component.readOnly).toBe(true);
      expect(component.form.disabled).toBe(true);
    });
  });
});
