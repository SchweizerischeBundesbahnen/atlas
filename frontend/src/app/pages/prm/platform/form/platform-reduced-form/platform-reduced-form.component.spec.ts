import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlatformReducedFormComponent } from './platform-reduced-form.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AtlasSpacerComponent } from '../../../../../core/components/spacer/atlas-spacer.component';
import {
  MockAtlasFieldErrorComponent,
  MockSelectComponent,
} from '../../../../../app.testing.mocks';
import { TextFieldComponent } from '../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { AppTestingModule } from '../../../../../app.testing.module';
import { PlatformFormGroupBuilder } from '../platform-form-group';
import { InfoOpportunityAttributeType } from '../../../../../api';

describe('PlatformReducedFormComponent', () => {
  let component: PlatformReducedFormComponent;
  let fixture: ComponentFixture<PlatformReducedFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        PlatformReducedFormComponent,
        MockSelectComponent,
        TextFieldComponent,
        MockAtlasFieldErrorComponent,
        AtlasLabelFieldComponent,
        AtlasSpacerComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }],
    });
    fixture = TestBed.createComponent(PlatformReducedFormComponent);
    component = fixture.componentInstance;
    component.form = PlatformFormGroupBuilder.buildReducedFormGroup();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should default infoOpportunities to ToBeCompleted', () => {
    component.form.controls.infoOpportunities.setValue([]);
    expect(component.form.controls.infoOpportunities.value).toEqual([
      InfoOpportunityAttributeType.ToBeCompleted,
    ]);
  });

  it('should remove ToBeCompleted from infoOpportunities on other select', () => {
    component.form.controls.infoOpportunities.setValue([
      InfoOpportunityAttributeType.ToBeCompleted,
      InfoOpportunityAttributeType.TextToSpeechComplete,
    ]);
    expect(component.form.controls.infoOpportunities.value).toEqual([
      InfoOpportunityAttributeType.TextToSpeechComplete,
    ]);
  });
});
