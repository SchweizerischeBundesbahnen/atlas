import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';

import { PlatformReducedFormComponent } from './platform-reduced-form.component';
import { TranslatePipe } from '@ngx-translate/core';
import { SelectComponent } from '../../../../../../../core/form-components/select/select.component';
import { TextFieldComponent } from '../../../../../../../core/form-components/text-field/text-field.component';
import { MockAtlasFieldErrorComponent } from '../../../../../../../app.testing.mocks';
import { AtlasLabelFieldComponent, AtlasInfoIconComponent } from '@atlas/form';
import { AtlasSpacerComponent } from '../../../../../../../core/components/spacer/atlas-spacer.component';
import { AppTestingModule } from '../../../../../../../app.testing.module';
import { PlatformFormGroupBuilder } from '../platform-form-group';
import { InfoOpportunityAttributeType } from '../../../../../../../api';

describe('PlatformReducedFormComponent', () => {
  let component: PlatformReducedFormComponent;
  let fixture: ComponentFixture<PlatformReducedFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        PlatformReducedFormComponent,
        SelectComponent,
        AtlasInfoIconComponent,
        TextFieldComponent,
        MockAtlasFieldErrorComponent,
        AtlasLabelFieldComponent,
        AtlasSpacerComponent,
      ],
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
    expect(component.form.controls.infoOpportunities.value).toEqual([InfoOpportunityAttributeType.ToBeCompleted]);
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
