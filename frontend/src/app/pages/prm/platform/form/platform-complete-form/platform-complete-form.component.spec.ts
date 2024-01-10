import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlatformCompleteFormComponent } from './platform-complete-form.component';
import {
  MockAtlasFieldErrorComponent,
  MockSelectComponent,
} from '../../../../../app.testing.mocks';
import { TextFieldComponent } from '../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { AtlasSpacerComponent } from '../../../../../core/components/spacer/atlas-spacer.component';
import { AppTestingModule } from '../../../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { StopPointFormGroupBuilder } from '../../../tabs/stop-point/form/stop-point-detail-form-group';

describe('PlatformCompleteFormComponent', () => {
  let component: PlatformCompleteFormComponent;
  let fixture: ComponentFixture<PlatformCompleteFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        PlatformCompleteFormComponent,
        MockSelectComponent,
        TextFieldComponent,
        MockAtlasFieldErrorComponent,
        AtlasLabelFieldComponent,
        AtlasSpacerComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }],
    });
    fixture = TestBed.createComponent(PlatformCompleteFormComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.form =
      StopPointFormGroupBuilder.buildEmptyWithReducedValidationFormGroup();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
