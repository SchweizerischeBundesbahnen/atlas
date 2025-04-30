import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReadStopPointWorkflow } from '../../../../../api';
import { StopPointWorkflowExaminantsTableComponent } from './stop-point-workflow-examinants-table.component';
import { MockAtlasButtonComponent } from '../../../../../app.testing.mocks';
import { DisplayDatePipe } from '../../../../../core/pipe/display-date.pipe';
import { AtlasSpacerComponent } from '../../../../../core/components/spacer/atlas-spacer.component';
import { AppTestingModule } from '../../../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { FormModule } from '../../../../../core/module/form.module';
import { StopPointWorkflowDetailFormGroupBuilder } from '../detail-form/stop-point-workflow-detail-form-group';

const workflow: ReadStopPointWorkflow = {
  versionId: 1,
  sloid: 'ch:1:sloid:8000',
  workflowComment: 'No comment',
};

describe('StopPointWorkflowExaminantsTableComponent', () => {
  let component: StopPointWorkflowExaminantsTableComponent;
  let fixture: ComponentFixture<StopPointWorkflowExaminantsTableComponent>;

  beforeEach(async () => {
    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        FormModule,
        StopPointWorkflowExaminantsTableComponent,
        MockAtlasButtonComponent,
        DisplayDatePipe,
        AtlasSpacerComponent,
      ],
      providers: [{ provide: TranslatePipe }],
    })
      .compileComponents()
      .then();

    fixture = TestBed.createComponent(
      StopPointWorkflowExaminantsTableComponent
    );
    component = fixture.componentInstance;

    component.form =
      StopPointWorkflowDetailFormGroupBuilder.buildFormGroup(workflow);
    component.examinants.push(
      StopPointWorkflowDetailFormGroupBuilder.buildExaminantFormGroup()
    );
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should add second examinant', () => {
    const firstExaminant = component.examinants.at(0);
    firstExaminant.controls.firstName.setValue('firstName');
    firstExaminant.controls.lastName.setValue('lastName');
    firstExaminant.controls.personFunction.setValue('personFunction');
    firstExaminant.controls.organisation.setValue('organisation');
    firstExaminant.controls.mail.setValue('mail@sbb.ch');

    component.addExaminant();
    expect(component.examinants.length).toBe(2);
  });

  it('should remove examinant', () => {
    const firstExaminant = component.examinants.at(0);
    firstExaminant.controls.firstName.setValue('firstName');
    firstExaminant.controls.lastName.setValue('lastName');
    firstExaminant.controls.personFunction.setValue('personFunction');
    firstExaminant.controls.organisation.setValue('organisation');
    firstExaminant.controls.mail.setValue('mail@sbb.ch');

    component.addExaminant();
    expect(component.examinants.length).toBe(2);

    component.examinants.at(0).disable();
    component.examinants.at(1).disable();
    component.removeExaminant(0);
    expect(component.examinants.length).toBe(1);
    component.removeExaminant(0);
    expect(component.examinants.length).toBe(0);
  });
});
