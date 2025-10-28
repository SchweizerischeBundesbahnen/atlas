import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TranslatePipe } from '@ngx-translate/core';
import { FormControl, FormGroup } from '@angular/forms';
import { MeansOfTransportPickerComponent } from './means-of-transport-picker.component';
import { MeanOfTransport } from '../../../api';
import { By } from '@angular/platform-browser';
import { AtlasLabelFieldComponent } from '@atlas/form/atlas-label-field/atlas-label-field.component';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';

describe('MeansOfTransportPickerComponent', () => {
  let component: MeansOfTransportPickerComponent;
  let fixture: ComponentFixture<MeansOfTransportPickerComponent>;

  const getSectorWarningEl = (
    fixture: ComponentFixture<MeansOfTransportPickerComponent>
  ) => fixture.debugElement.query(By.css('.sector-warning'));

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MeansOfTransportPickerComponent, AtlasLabelFieldComponent],
      providers: [TranslatePipe, translateServiceProvider, provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(MeansOfTransportPickerComponent);
    component = fixture.componentInstance;
    component.formGroup = new FormGroup({
      meansOfTransport: new FormControl([MeanOfTransport.Bus]),
    });
    component.controlName = 'meansOfTransport';
    fixture.detectChanges();
  });

  it('should add train on click (multi select mode)', () => {
    const trainImage = fixture.debugElement.query(By.css('[data-cy=TRAIN]'));
    trainImage.nativeElement.click();

    const currentMeans = component.formGroup.value.meansOfTransport;
    expect(currentMeans).toEqual([MeanOfTransport.Bus, MeanOfTransport.Train]);
  });

  it('should remove bus on click (multi select mode)', () => {
    const busImage = fixture.debugElement.query(By.css('[data-cy=BUS]'));
    busImage.nativeElement.click();

    const currentMeans = component.formGroup.value.meansOfTransport;
    expect(currentMeans).toEqual([]);
  });

  it('should switch to train on click (single select mode)', () => {
    component.multiSelectMode = false;
    const trainImage = fixture.debugElement.query(By.css('[data-cy=TRAIN]'));
    trainImage.nativeElement.click();

    const currentMeans = component.formGroup.value.meansOfTransport;
    expect(currentMeans).toEqual([MeanOfTransport.Train]);
  });

  it('should remove bus on click (single select mode)', () => {
    component.multiSelectMode = false;
    const busImage = fixture.debugElement.query(By.css('[data-cy=BUS]'));
    busImage.nativeElement.click();

    const currentMeans = component.formGroup.value.meansOfTransport;
    expect(currentMeans).toEqual([]);
  });

  it('should show sector warning on TRAIN removed', () => {
    component.formGroup = new FormGroup({
      meansOfTransport: new FormControl([MeanOfTransport.Train]),
    });
    component.showSectorWarning = true;
    fixture.detectChanges();
    expect(getSectorWarningEl(fixture)).toBeNull();

    const train = fixture.debugElement.query(By.css('[data-cy=TRAIN]'));
    train.nativeElement.click();

    fixture.detectChanges();
    expect(getSectorWarningEl(fixture)).not.toBeNull();
  });
});
