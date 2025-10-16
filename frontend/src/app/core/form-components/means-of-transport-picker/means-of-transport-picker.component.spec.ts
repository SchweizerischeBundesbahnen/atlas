import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TranslatePipe } from '@ngx-translate/core';
import { FormControl, FormGroup, FormsModule } from '@angular/forms';
import { FormModule } from '../../module/form.module';
import { MeansOfTransportPickerComponent } from './means-of-transport-picker.component';
import { MeanOfTransport } from '../../../api';
import { By } from '@angular/platform-browser';
import { AtlasSpacerComponent } from '../../components/spacer/atlas-spacer.component';
import { InfoIconComponent } from '@atlas/form/info-icon/info-icon.component';
import { AtlasLabelFieldComponent } from '@atlas/form/atlas-label-field/atlas-label-field.component';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';

describe('MeansOfTransportPickerComponent', () => {
  let component: MeansOfTransportPickerComponent;
  let fixture: ComponentFixture<MeansOfTransportPickerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        FormModule,
        FormsModule,
        MeansOfTransportPickerComponent,
        InfoIconComponent,
        AtlasLabelFieldComponent,
        AtlasSpacerComponent,
      ],
      providers: [TranslatePipe, provideHttpClient(), translateServiceProvider],
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

  it('should show sector warning on TRAIN removed', () => {
    component.formGroup = new FormGroup({
      meansOfTransport: new FormControl([MeanOfTransport.Train]),
    });
    expect(component.sectorWarning).toBeFalse();
    expect(component.currentlySelectedMeans).toEqual([MeanOfTransport.Train]);

    component.clicked(MeanOfTransport.Train);
    expect(component.sectorWarning).toBeTrue();
  });
});
