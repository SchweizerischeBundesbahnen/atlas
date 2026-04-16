import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { By } from '@angular/platform-browser';

import { StopPointReducedFormComponent } from './stop-point-reduced-form.component';
import { StopPointFormGroupBuilder } from '../stop-point-detail-form-group';
import { TranslatePipe } from '@ngx-translate/core';
import { MockAtlasFieldErrorComponent, MockSelectComponent } from '../../../../../../app.testing.mocks';
import { TextFieldComponent } from '../../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent, InfoIconComponent } from '@atlas/form';
import { MeansOfTransportPickerComponent } from '../../../../../../core/form-components/means-of-transport-picker/means-of-transport-picker.component';
import { AtlasSpacerComponent } from '../../../../../../core/components/spacer/atlas-spacer.component';
import { AppTestingModule } from '../../../../../../app.testing.module';
import { MeanOfTransport } from '../../../../../../api';
import { PrmVariantInfoService } from '../../prm-variant-info.service';

describe('StopPointReducedFormComponent', () => {
  let component: StopPointReducedFormComponent;
  let fixture: ComponentFixture<StopPointReducedFormComponent>;
  let prmVariantInfoService: Mocked<Pick<PrmVariantInfoService, 'getPrmMeansOfTransportToShow'>>;

  beforeEach(() => {
    prmVariantInfoService = {
      getPrmMeansOfTransportToShow: vi.fn(),
    };
    prmVariantInfoService.getPrmMeansOfTransportToShow.mockReturnValue(Object.values(MeanOfTransport));

    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        StopPointReducedFormComponent,
        MockSelectComponent,
        MockAtlasFieldErrorComponent,
        TextFieldComponent,
        InfoIconComponent,
        AtlasLabelFieldComponent,
        MeansOfTransportPickerComponent,
        AtlasSpacerComponent,
      ],
      providers: [{ provide: TranslatePipe }, { provide: PrmVariantInfoService, useValue: prmVariantInfoService }],
    });

    fixture = TestBed.createComponent(StopPointReducedFormComponent);
    component = fixture.componentInstance;
    fixture.componentInstance.form = StopPointFormGroupBuilder.buildEmptyWithReducedValidationFormGroup();
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
