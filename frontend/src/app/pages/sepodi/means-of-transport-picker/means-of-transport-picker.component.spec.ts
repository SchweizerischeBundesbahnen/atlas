import { ComponentFixture, TestBed } from '@angular/core/testing';

import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { FormControl, FormGroup, FormsModule } from '@angular/forms';
import { FormModule } from '../../../core/module/form.module';
import { MeansOfTransportPickerComponent } from './means-of-transport-picker.component';
import { MeanOfTransport } from '../../../api';
import { MaterialModule } from '../../../core/module/material.module';
import { By } from '@angular/platform-browser';
import { AtlasSpacerComponent } from '../../../core/components/spacer/atlas-spacer.component';
import { InfoIconComponent } from '../../../core/form-components/info-icon/info-icon.component';
import { AtlasLabelFieldComponent } from '../../../core/form-components/atlas-label-field/atlas-label-field.component';

describe('MeansOfTransportPickerComponent', () => {
  let component: MeansOfTransportPickerComponent;
  let fixture: ComponentFixture<MeansOfTransportPickerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        MeansOfTransportPickerComponent,
        InfoIconComponent,
        AtlasLabelFieldComponent,
        AtlasSpacerComponent,
      ],
      imports: [
        FormModule,
        FormsModule,
        MaterialModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [{ provide: TranslatePipe }],
    }).compileComponents();

    fixture = TestBed.createComponent(MeansOfTransportPickerComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup({
      meansOfTransport: new FormControl([MeanOfTransport.Bus]),
    });
    component.controlName = 'meansOfTransport';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should add train on click', () => {
    const trainImage = fixture.debugElement.query(By.css('[data-cy=TRAIN]'));
    trainImage.nativeElement.click();

    const currentMeans = component.formGroup.value.meansOfTransport;
    expect(currentMeans).toEqual([MeanOfTransport.Bus, MeanOfTransport.Train]);
  });

  it('should remove bus on click', () => {
    const trainImage = fixture.debugElement.query(By.css('[data-cy=BUS]'));
    trainImage.nativeElement.click();

    const currentMeans = component.formGroup.value.meansOfTransport;
    expect(currentMeans).toEqual([]);
  });
});
