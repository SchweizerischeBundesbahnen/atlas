import {ComponentFixture, TestBed} from '@angular/core/testing';

import {DecisionFormComponent} from './decision-form.component';
import {DecisionFormGroupBuilder} from './decision-form-group';
import {AppTestingModule} from "../../../../../../app.testing.module";
import {CommentComponent} from "../../../../../../core/form-components/comment/comment.component";
import {AtlasFieldErrorComponent} from "../../../../../../core/form-components/atlas-field-error/atlas-field-error.component";
import {TextFieldComponent} from "../../../../../../core/form-components/text-field/text-field.component";
import {AtlasLabelFieldComponent} from "../../../../../../core/form-components/atlas-label-field/atlas-label-field.component";

describe('DecisionFormComponent', () => {
  let component: DecisionFormComponent;
  let fixture: ComponentFixture<DecisionFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DecisionFormComponent, CommentComponent, AtlasFieldErrorComponent, TextFieldComponent, AtlasLabelFieldComponent],
      imports: [AppTestingModule],
    }).compileComponents();

    fixture = TestBed.createComponent(DecisionFormComponent);
    component = fixture.componentInstance;
    component.form = DecisionFormGroupBuilder.buildFormGroup();

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return hasDecisionTypeVotedExpired', () => {
    component.hasDecisionTypeVotedExpired = true;
    component.hasOverride = false;
    fixture.detectChanges();
    expect(component.warningChipMessage).toBe('SEPODI.SERVICE_POINTS.WORKFLOW.VOTED_EXPIRATION')
  });

  it('should return hasOverride', () => {
    component.hasDecisionTypeVotedExpired = false;
    component.hasOverride = true;
    fixture.detectChanges();
    expect(component.warningChipMessage).toBe('SEPODI.SERVICE_POINTS.WORKFLOW.OVERRIDE_HAPPENED_INFO')
  });
});
