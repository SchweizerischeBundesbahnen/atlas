import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { By } from '@angular/platform-browser';

import { StopPointCompleteFormComponent } from './stop-point-complete-form.component';
import { AppTestingModule } from '../../../../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { StopPointFormGroupBuilder } from '../stop-point-detail-form-group';
import { MockAtlasFieldErrorComponent, MockSelectComponent } from '../../../../../../app.testing.mocks';
import { TextFieldComponent } from '../../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent, InfoIconComponent } from '@atlas/form';
import { MeansOfTransportPickerComponent } from '../../../../../../core/form-components/means-of-transport-picker/means-of-transport-picker.component';
import { AtlasSpacerComponent } from '../../../../../../core/components/spacer/atlas-spacer.component';
import { PrmVariantInfoService } from '../../prm-variant-info.service';
import { MeanOfTransport } from '../../../../../../api';

describe('StopPointCompleteFormComponent', () => {
  let fixture: ComponentFixture<StopPointCompleteFormComponent>;
  let prmVariantInfoService: Mocked<Pick<PrmVariantInfoService, 'getPrmMeansOfTransportToShow'>>;

  beforeEach(() => {
    prmVariantInfoService = {
      getPrmMeansOfTransportToShow: vi.fn(),
    };
    prmVariantInfoService.getPrmMeansOfTransportToShow.mockReturnValue(Object.values(MeanOfTransport));

    TestBed.configureTestingModule({
      imports: [
        AppTestingModule,
        StopPointCompleteFormComponent,
        MockSelectComponent,
        TextFieldComponent,
        MockAtlasFieldErrorComponent,
        InfoIconComponent,
        AtlasLabelFieldComponent,
        MeansOfTransportPickerComponent,
        AtlasSpacerComponent,
      ],
      providers: [{ provide: TranslatePipe }, { provide: PrmVariantInfoService, useValue: prmVariantInfoService }],
    });

    fixture = TestBed.createComponent(StopPointCompleteFormComponent);
    fixture.componentInstance.form = StopPointFormGroupBuilder.buildEmptyWithReducedValidationFormGroup();
    fixture.detectChanges();
  });

  describe('should display complete fields', () => {
    it('should display meansOfTransport', () => {
      const meansOfTransport = fixture.debugElement.query(By.css('atlas-means-of-transport-picker'));
      expect(meansOfTransport.attributes['controlName']).toEqual('meansOfTransport');
    });

    it('should display interoperable', () => {
      const interoperable = fixture.debugElement.query(By.css('mat-checkbox'));
      expect(interoperable.attributes['formControlName']).toEqual('interoperable');
    });

    it('should display data-range', () => {
      expect(fixture.debugElement.query(By.css('atlas-form-date-range'))).toBeDefined();
    });

    it('should display formComments', () => {
      const formComments = fixture.debugElement.queryAll(By.css('atlas-form-comment'));
      const formCommentsControlName: string[] = [
        'freeText',
        'additionalInformation',
        'infoTicketMachine',
        'assistanceCondition',
        'alternativeTransportCondition',
      ];

      expect(formComments.length).toEqual(5);
      expect(formComments.map((value) => value.attributes['controlName'])).toEqual(
        expect.arrayContaining(formCommentsControlName)
      );
    });

    it('should display atlasSelects', () => {
      const atlasSelects = fixture.debugElement.queryAll(By.css('atlas-select'));
      const atlasSelectsControlName: string[] = [
        'visualInfo',
        'dynamicOpticSystem',
        'dynamicAudioSystem',
        'ticketMachine',
        'wheelchairTicketMachine',
        'audioTicketMachine',
        'assistanceRequestFulfilled',
        'assistanceService',
        'assistanceAvailability',
        'alternativeTransport',
        'shuttleService',
      ];

      expect(atlasSelects.length).toEqual(11);
      expect(atlasSelects.map((value) => value.attributes['controlName'])).toEqual(
        expect.arrayContaining(atlasSelectsControlName)
      );
    });

    it('should display atlas-text-fields', () => {
      const atlasTextFields = fixture.debugElement.queryAll(By.css('atlas-text-field'));
      const atlasTextFieldsControlName: string[] = ['url', 'address', 'zipCode', 'city'];

      expect(atlasTextFields.length).toEqual(4);
      expect(atlasTextFields.map((value) => value.attributes['controlName'])).toEqual(
        expect.arrayContaining(atlasTextFieldsControlName)
      );
    });
  });
});
