import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlatformCompleteFormComponent } from './platform-complete-form.component';
import { AppTestingModule } from '../../../../../../app.testing.module';
import { TranslatePipe } from '@ngx-translate/core';
import { StopPointFormGroupBuilder } from '../stop-point-detail-form-group';
import {
  MockAtlasFieldErrorComponent,
  MockSelectComponent,
} from '../../../../../../app.testing.mocks';
import { TextFieldComponent } from '../../../../../../core/form-components/text-field/text-field.component';
import { AtlasLabelFieldComponent } from '../../../../../../core/form-components/atlas-label-field/atlas-label-field.component';
import { MeansOfTransportPickerComponent } from '../../../../../sepodi/means-of-transport-picker/means-of-transport-picker.component';
import { AtlasSpacerComponent } from '../../../../../../core/components/spacer/atlas-spacer.component';
import { By } from '@angular/platform-browser';
import arrayContaining = jasmine.arrayContaining;

describe('StopPointCompleteFormComponent', () => {
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
        MeansOfTransportPickerComponent,
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

  describe('should display complete fields', () => {
    it('should display meansOfTransport', () => {
      const meansOfTransport = fixture.debugElement.query(By.css('means-of-transport-picker'));
      expect(meansOfTransport.attributes['controlName']).toEqual('meansOfTransport');
    });
    it('should display interoperable', () => {
      const interoperable = fixture.debugElement.query(By.css('mat-checkbox'));
      expect(interoperable.attributes['formControlName']).toEqual('interoperable');
    });
    it('should display data-range', () => {
      expect(fixture.debugElement.query(By.css('form-date-range'))).toBeDefined();
    });
    it('should display formComments', () => {
      const formComments = fixture.debugElement.queryAll(By.css('form-comment'));
      const formCommentsControlName: string[] = [
        'freeText',
        'additionalInformation',
        'infoTicketMachine',
        'assistanceCondition',
        'alternativeTransportCondition',
      ];
      expect(formComments.length).toEqual(5);
      expect(formComments.map((value) => value.attributes['controlName'])).toEqual(
        arrayContaining(formCommentsControlName),
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
      ];
      expect(atlasSelects.length).toEqual(10);
      expect(atlasSelects.map((value) => value.attributes['controlName'])).toEqual(
        arrayContaining(atlasSelectsControlName),
      );
    });
    it('should display atlas-text-fields', () => {
      const atlasTextFields = fixture.debugElement.queryAll(By.css('atlas-text-field'));
      const atlasTextFieldsControlName: string[] = ['url', 'address', 'zipCode', 'city'];
      expect(atlasTextFields.length).toEqual(4);
      expect(atlasTextFields.map((value) => value.attributes['controlName'])).toEqual(
        arrayContaining(atlasTextFieldsControlName),
      );
    });
  });
});
