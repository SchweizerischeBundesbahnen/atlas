import {ComponentFixture, fakeAsync, TestBed, tick} from '@angular/core/testing';

import {DecisionDetailDialogComponent} from './decision-detail-dialog.component';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {DecisionDetailDialogData} from "./decision-detail-dialog.service";
import {StopPointWorkflowDetailFormGroupBuilder} from "../../detail-form/stop-point-workflow-detail-form-group";
import {AppTestingModule} from "../../../../../../app.testing.module";
import {of} from "rxjs";
import {JudgementType, ReadDecision, StopPointWorkflowService} from "../../../../../../api";

const dialogRefSpy = jasmine.createSpyObj(['close']);
const dialogData: DecisionDetailDialogData = {
  title: '',
  message: '',
  workflowId: 123,
  examinant: StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup()
}

const dialogDataWithExisitingExaminant: DecisionDetailDialogData = {
  title: '',
  message: '',
  workflowId: 123,
  examinant: StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup({
    judgement: JudgementType.Yes,
    organisation: 'Stadt Bern',
    mail: 'stadt@bern.be'
  })
}

const existingDecision: ReadDecision = {
  judgement: JudgementType.Yes,
  motivation: 'Yep boiii'
}
const stopPointWorkflowService = jasmine.createSpyObj('stopPointWorkflowService', {
  getDecision: of(existingDecision)
});

describe('DecisionDetailDialogComponent', () => {
  let component: DecisionDetailDialogComponent;
  let fixture: ComponentFixture<DecisionDetailDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [DecisionDetailDialogComponent, AppTestingModule],
      providers: [
        {provide: MatDialogRef, useValue: dialogRefSpy},
        {provide: MAT_DIALOG_DATA, useValue: dialogData},
        {provide: StopPointWorkflowService, useValue: stopPointWorkflowService},
      ]
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
      TestBed.overrideProvider(MAT_DIALOG_DATA, {useValue: dialogDataWithExisitingExaminant});
      fixture = TestBed.createComponent(DecisionDetailDialogComponent);
      component = fixture.componentInstance;
      fixture.detectChanges();
    });

    it('should init', () => {
      expect(component).toBeTruthy();

      expect(stopPointWorkflowService.getDecision).toHaveBeenCalled();

      expect(component.existingDecision).toBeDefined();
      expect(component.decisionForm.controls.judgement.value).toEqual(JudgementType.Yes);
    });
  });
});
