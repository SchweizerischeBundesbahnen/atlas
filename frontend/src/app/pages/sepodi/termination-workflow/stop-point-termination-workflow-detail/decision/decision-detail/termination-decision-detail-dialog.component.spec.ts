import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TerminationDecisionDetailDialogComponent } from './termination-decision-detail-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TerminationDecisionDetailDialogData } from './termination-decision-detail-dialog.service';
import { AppTestingModule } from '../../../../../../app.testing.module';
import { of } from 'rxjs';
import { CommentComponent } from '../../../../../../core/form-components/comment/comment.component';
import { AtlasFieldErrorComponent } from '../../../../../../core/form-components/atlas-field-error/atlas-field-error.component';
import { TextFieldComponent } from '../../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { LoadingSpinnerComponent } from '../../../../../../core/components/loading-spinner/loading-spinner.component';
import { DialogContentComponent } from '../../../../../../core/components/dialog/content/dialog-content.component';
import { DialogCloseComponent } from '../../../../../../core/components/dialog/close/dialog-close.component';
import { DialogFooterComponent } from '../../../../../../core/components/dialog/footer/dialog-footer.component';
import { MockAtlasButtonComponent } from '../../../../../../app.testing.mocks';
import { TerminationDecision } from '../../../../../../api/model/terminationDecision';
import { FormControl, FormGroup } from '@angular/forms';
import { TerminationDecisionFormGroup } from '../../stop-point-termination-workflow-detail-form-group';
import moment from 'moment/moment';
import { WorkflowService } from '../../../../../../api/service/workflow/workflow.service';
import TerminationDecisionPersonEnum = TerminationDecision.TerminationDecisionPersonEnum;
import { TerminationWorkflowStatus } from '../../../../../../api/model/terminationWorkflowStatus';

const dialogRefSpy = jasmine.createSpyObj(['close']);
const terminationWorkflowService = jasmine.createSpyObj('WorkflowService', {
  decisionInfoPlus: of(),
  decisionNova: of(),
});
const decisionDialogData: TerminationDecisionDetailDialogData = {
  title: '',
  message: '',
  workflowId: 123,
  readOnly: false,
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
    terminationDecisionPerson: new FormControl(
      TerminationDecisionPersonEnum.InfoPlus
    ),
  }),
};

const dialogDataReadOnly: TerminationDecisionDetailDialogData = {
  title: '',
  message: '',
  workflowId: 123,
  readOnly: true,
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
    terminationDecisionPerson: new FormControl(
      TerminationDecisionPersonEnum.InfoPlus
    ),
  }),
};

describe('TerminationDecisionDetailDialogComponent', () => {
  let component: TerminationDecisionDetailDialogComponent;
  let fixture: ComponentFixture<TerminationDecisionDetailDialogComponent>;

  beforeEach(() => {
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
        { provide: MatDialogRef, useValue: dialogRefSpy },
        { provide: MAT_DIALOG_DATA, useValue: decisionDialogData },
        {
          provide: WorkflowService,
          useValue: terminationWorkflowService,
        },
      ],
    });
  });

  describe('while deciding', () => {
    beforeEach(() => {
      fixture = TestBed.createComponent(
        TerminationDecisionDetailDialogComponent
      );
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();

      expect(component.readOnly).toBeFalse();
    });

    it('should close dialog', () => {
      component.close();

      expect(dialogRefSpy.close).toHaveBeenCalled();
    });

    it('should decide', () => {
      component.decide();

      expect(terminationWorkflowService.decisionInfoPlus).toHaveBeenCalled();
    });
  });

  describe('while reading', () => {
    beforeEach(() => {
      TestBed.overrideProvider(MAT_DIALOG_DATA, {
        useValue: dialogDataReadOnly,
      });
      fixture = TestBed.createComponent(
        TerminationDecisionDetailDialogComponent
      );
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should init', () => {
      expect(component).toBeTruthy();

      expect(component.readOnly).toBeTrue();
      expect(component.form.disabled).toBeTrue();
    });
  });
});
