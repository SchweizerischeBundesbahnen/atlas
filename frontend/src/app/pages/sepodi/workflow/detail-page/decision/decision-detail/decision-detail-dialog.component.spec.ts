import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecisionDetailDialogComponent } from './decision-detail-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { DecisionDetailDialogData } from './decision-detail-dialog.service';
import { StopPointWorkflowDetailFormGroupBuilder } from '../../detail-form/stop-point-workflow-detail-form-group';
import { AppTestingModule } from '../../../../../../app.testing.module';
import { of } from 'rxjs';
import {
  DecisionType,
  JudgementType,
  ReadDecision,
  StopPointWorkflowService,
  WorkflowStatus,
} from '../../../../../../api';
import { DecisionFormComponent } from '../decision-form/decision-form.component';
import { CommentComponent } from '../../../../../../core/form-components/comment/comment.component';
import { AtlasFieldErrorComponent } from '../../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { TextFieldComponent } from '../../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { LoadingSpinnerComponent } from '../../../../../../core/components/loading-spinner/loading-spinner.component';
import { DialogContentComponent } from '../../../../../../core/components/dialog/content/dialog-content.component';
import { DialogCloseComponent } from '../../../../../../core/components/dialog/close/dialog-close.component';
import { DecisionOverrideComponent } from './override/decision-override.component';
import { DialogFooterComponent } from '../../../../../../core/components/dialog/footer/dialog-footer.component';
import { MockAtlasButtonComponent } from '../../../../../../app.testing.mocks';

const dialogRefSpy = jasmine.createSpyObj(['close']);
const dialogData: DecisionDetailDialogData = {
  title: '',
  message: '',
  workflowId: 123,
  workflowStatus: WorkflowStatus.Hearing,
  examinant: StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup(),
};

const dialogDataWithExisitingExaminant: DecisionDetailDialogData = {
  title: '',
  message: '',
  workflowId: 123,
  workflowStatus: WorkflowStatus.Hearing,
  examinant: StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup({
    judgement: JudgementType.Yes,
    organisation: 'Stadt Bern',
    mail: 'stadt@bern.be',
  }),
};

const dialogDataWithSpecialDecision: DecisionDetailDialogData = {
  title: '',
  message: '',
  workflowId: 123,
  workflowStatus: WorkflowStatus.Canceled,
  examinant: StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup({
    judgement: JudgementType.No,
    organisation: 'BAV',
    mail: 'bav@bern.be',
    decisionType: DecisionType.Canceled,
  }),
};

const existingDecision: ReadDecision = {
  judgement: JudgementType.Yes,
  motivation: 'Yep boiii',
};
const stopPointWorkflowService = jasmine.createSpyObj(
  'stopPointWorkflowService',
  {
    getDecision: of(existingDecision),
  }
);

describe('DecisionDetailDialogComponent', () => {
  let component: DecisionDetailDialogComponent;
  let fixture: ComponentFixture<DecisionDetailDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        DecisionDetailDialogComponent,
        DecisionOverrideComponent,
        DecisionFormComponent,
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
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: MAT_DIALOG_DATA, useValue: dialogData },
        {
          provide: StopPointWorkflowService,
          useValue: stopPointWorkflowService,
        },
      ],
    });
  });

  describe('without existing decision', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(DecisionDetailDialogComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();

      expect(component.existingDecision).toBeUndefined();
      expect(component.decisionForm.disabled).toBeTrue();
    });

    it('should close dialog', () => {
      component.close();

      expect(dialogRefSpy.close).toHaveBeenCalled();
    });
  });

  describe('with existing decision', () => {
    beforeEach(() => {
      TestBed.overrideProvider(MAT_DIALOG_DATA, {
        useValue: dialogDataWithExisitingExaminant,
      });
      fixture = TestBed.createComponent(DecisionDetailDialogComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should init', () => {
      expect(component).toBeTruthy();

      expect(stopPointWorkflowService.getDecision).toHaveBeenCalled();

      expect(component.existingDecision).toBeDefined();
      expect(component.decisionForm.controls.judgement.value).toEqual(
        JudgementType.Yes
      );
    });
  });

  describe('with cancel decision', () => {
    beforeEach(() => {
      TestBed.overrideProvider(MAT_DIALOG_DATA, {
        useValue: dialogDataWithSpecialDecision,
      });
      fixture = TestBed.createComponent(DecisionDetailDialogComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should init', () => {
      expect(component).toBeTruthy();

      expect(stopPointWorkflowService.getDecision).toHaveBeenCalled();

      expect(component.existingDecision).toBeDefined();
      expect(component.title).toEqual('WORKFLOW.STATUS.CANCELED');
      expect(component.specialDecision).toBeTrue();
    });
  });
});
