import { ComponentFixture, TestBed } from '@angular/core/testing';

import { StopPointCompleteFormComponent } from './stop-point-complete-form.component';
import { AppTestingModule } from '../../../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { StopPointFormGroupBuilder } from '../stop-point-detail-form-group';
import {
  MockAtlasFieldErrorComponent,
  MockSelectComponent,
} from '../../../../../app.testing.mocks';
import { TextFieldComponent } from '../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { MeansOfTransportPickerComponent } from '../../../../sepodi/means-of-transport-picker/means-of-transport-picker.component';
import { AtlasSpacerComponent } from '../../../../../core/components/spacer/atlas-spacer.component';
import { UserDetailInfoComponent } from '../../../../../core/components/base-detail/user-edit-info/user-detail-info.component';

describe('StopPointCompleteFormComponent', () => {
  let component: StopPointCompleteFormComponent;
  let fixture: ComponentFixture<StopPointCompleteFormComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        StopPointCompleteFormComponent,
        MockAtlasFieldErrorComponent,
        TextFieldComponent,
        AtlasLabelFieldComponent,
        MeansOfTransportPickerComponent,
        AtlasSpacerComponent,
        UserDetailInfoComponent,
        MockSelectComponent,
      ],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }],
    });
    fixture = TestBed.createComponent(StopPointCompleteFormComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.form =
      StopPointFormGroupBuilder.buildEmptyWithReducedValidationFormGroup();
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
