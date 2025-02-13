import { ComponentFixture, TestBed } from '@angular/core/testing';
import {
  BusinessOrganisationsService,
  ReadStopPointWorkflow,
} from '../../../../../api';
import { StopPointWorkflowExaminantsTableComponent } from './stop-point-workflow-examinants-table.component';
import { StringListComponent } from '../../../../../core/form-components/string-list/string-list.component';
import { MockAtlasButtonComponent } from '../../../../../app.testing.mocks';
import { DisplayDatePipe } from '../../../../../core/pipe/display-date.pipe';
import { SplitServicePointNumberPipe } from '../../../../../core/search-service-point/split-service-point-number.pipe';
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
      declarations: [
        StopPointWorkflowExaminantsTableComponent,
        StringListComponent,
        MockAtlasButtonComponent,
        DisplayDatePipe,
        SplitServicePointNumberPipe,
        AtlasSpacerComponent,
      ],
      imports: [AppTestingModule, FormModule],
      providers: [
        { provide: TranslatePipe },
        { provide: BusinessOrganisationsService },
      ],
    })
      .compileComponents()
      .then();

    fixture = TestBed.createComponent(
      StopPointWorkflowExaminantsTableComponent
    );
    component = fixture.componentInstance;

    component.form =
      StopPointWorkflowDetailFormGroupBuilder.buildFormGroup(workflow);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have one examinant per default', () => {
    expect(component.form.controls.examinants.length).toBe(1);
  });

  it('should add second examinant', () => {
    const firstExaminant = component.form.controls.examinants.at(0);
    firstExaminant.controls.firstName.setValue('firstName');
    firstExaminant.controls.lastName.setValue('lastName');
    firstExaminant.controls.personFunction.setValue('personFunction');
    firstExaminant.controls.organisation.setValue('organisation');
    firstExaminant.controls.mail.setValue('mail@sbb.ch');

    component.addExaminant();
    expect(component.form.controls.examinants.length).toBe(2);
  });

  it('should remove examinant', () => {
    const firstExaminant = component.form.controls.examinants.at(0);
    firstExaminant.controls.firstName.setValue('firstName');
    firstExaminant.controls.lastName.setValue('lastName');
    firstExaminant.controls.personFunction.setValue('personFunction');
    firstExaminant.controls.organisation.setValue('organisation');
    firstExaminant.controls.mail.setValue('mail@sbb.ch');

    component.addExaminant();
    expect(component.form.controls.examinants.length).toBe(2);

    component.form.controls.examinants.at(0).disable();
    component.form.controls.examinants.at(1).disable();
    component.removeExaminant(0);
    expect(component.form.controls.examinants.length).toBe(1);
    component.removeExaminant(0);
    expect(component.form.controls.examinants.length).toBe(0);
  });
});
