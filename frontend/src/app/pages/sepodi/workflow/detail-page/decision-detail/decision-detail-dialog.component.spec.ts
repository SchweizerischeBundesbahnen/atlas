import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DecisionDetailDialogComponent} from './decision-detail-dialog.component';
import {MAT_DIALOG_DATA, MatDialogRef} from "@angular/material/dialog";
import {AppTestingModule} from "../../../../../app.testing.module";
import {DecisionDetailDialogData} from "./decision-detail-dialog.service";
import {StopPointWorkflowDetailFormGroupBuilder} from "../../detail-form/stop-point-workflow-detail-form-group";

const dialogRefSpy = jasmine.createSpyObj(['close']);
const dialogData: DecisionDetailDialogData = {
  title: '',
  message: '',
  workflowId: 123,
  examinant: StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup()
}

describe('DecisionDetailDialogComponent', () => {
  let component: DecisionDetailDialogComponent;
  let fixture: ComponentFixture<DecisionDetailDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecisionDetailDialogComponent, AppTestingModule],
      providers: [
        {provide: MatDialogRef, useValue: dialogRefSpy},
        {provide: MAT_DIALOG_DATA, useValue: dialogData}]
    })
      .compileComponents();

    fixture = TestBed.createComponent(DecisionDetailDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
