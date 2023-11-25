import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointReducedFormComponent } from './stop-point-reduced-form.component';
import { StopPointFormGroupBuilder } from '../stop-point-detail-form-group';
import { TranslatePipe } from '@ngx-translate/core';
import {
  MockAtlasFieldErrorComponent,
  MockSelectComponent,
} from '../../../../../app.testing.mocks';
import { TextFieldComponent } from '../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { MeansOfTransportPickerComponent } from '../../../../sepodi/means-of-transport-picker/means-of-transport-picker.component';
import { AtlasSpacerComponent } from '../../../../../core/components/spacer/atlas-spacer.component';
import { AppTestingModule } from '../../../../../app.testing.module';

describe('StopPointReducedFormComponent', () => {
  let component: StopPointReducedFormComponent;
  let fixture: ComponentFixture<StopPointReducedFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        StopPointReducedFormComponent,
        MockSelectComponent,
        MockAtlasFieldErrorComponent,
        TextFieldComponent,
        AtlasLabelFieldComponent,
        MeansOfTransportPickerComponent,
        AtlasSpacerComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }],
    });
    fixture = TestBed.createComponent(StopPointReducedFormComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.form =
      StopPointFormGroupBuilder.buildEmptyWithReducedValidationFormGroup();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
