import {ComponentFixture, TestBed} from '@angular/core/testing';

import {StopPointReducedFormComponent} from './stop-point-reduced-form.component';
import {StopPointFormGroupBuilder} from '../stop-point-detail-form-group';
import {TranslatePipe} from '@ngx-translate/core';
import {MockAtlasFieldErrorComponent, MockSelectComponent,} from '../../../../../../app.testing.mocks';
import {TextFieldComponent} from '../../../../../../core/form-components/text-field/text-field.component';
import {AtlasLabelFieldComponent} from '../../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import {
  MeansOfTransportPickerComponent
} from '../../../../../sepodi/means-of-transport-picker/means-of-transport-picker.component';
import {AtlasSpacerComponent} from '../../../../../../core/components/spacer/atlas-spacer.component';
import {AppTestingModule} from '../../../../../../app.testing.module';
import {By} from '@angular/platform-browser';
import {InfoIconComponent} from '../../../../../../core/form-components/info-icon/info-icon.component';
import {MeanOfTransport} from "../../../../../../api";
import {PrmVariantInfoServiceService} from "../../prm-variant-info-service.service";

describe('StopPointReducedFormComponent', () => {
  let component: StopPointReducedFormComponent;
  let fixture: ComponentFixture<StopPointReducedFormComponent>;

  const prmVariantInfoServiceService = jasmine.createSpyObj(
    'prmVariantInfoServiceService',
    ['getPrmMeansOfTransportToShow'],
  );
  prmVariantInfoServiceService.getPrmMeansOfTransportToShow.and.returnValue(Object.values(MeanOfTransport))

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [
        StopPointReducedFormComponent,
        MockSelectComponent,
        MockAtlasFieldErrorComponent,
        TextFieldComponent,
        InfoIconComponent,
        AtlasLabelFieldComponent,
        MeansOfTransportPickerComponent,
        AtlasSpacerComponent,
      ],
      imports: [AppTestingModule],
      providers: [
        { provide: TranslatePipe },
        { provide: PrmVariantInfoServiceService, useValue: prmVariantInfoServiceService }
      ]
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

  it('should display reduced fields', () => {
    expect(fixture.debugElement.query(By.css('means-of-transport-picker'))).toBeDefined();
    expect(fixture.debugElement.query(By.css('form-comment'))).toBeDefined();
    expect(fixture.debugElement.query(By.css('form-date-range'))).toBeDefined();
  });
});
