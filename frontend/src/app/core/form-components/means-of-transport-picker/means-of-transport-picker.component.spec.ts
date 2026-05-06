import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { FormControl, FormGroup } from '@angular/forms';
import { MeansOfTransportPickerComponent } from './means-of-transport-picker.component';
import { MeanOfTransport } from '../../../api';
import { By } from '@angular/platform-browser';
import { translateServiceProvider } from '../../../app.testing.mocks';
import { inputBinding, signal } from '@angular/core';

describe('MeansOfTransportPickerComponent', () => {
  let component: MeansOfTransportPickerComponent;
  let fixture: ComponentFixture<MeansOfTransportPickerComponent>;
  let formGroupInput: ReturnType<typeof signal<FormGroup>>;
  let multiSelectModeInput: ReturnType<typeof signal<boolean>>;
  let showSectorWarningInput: ReturnType<typeof signal<boolean>>;

  const getSectorWarningEl = (fixture: ComponentFixture<MeansOfTransportPickerComponent>) =>
    fixture.debugElement.query(By.css('.sector-warning'));

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [translateServiceProvider],
    });

    const formGroupInputName: keyof MeansOfTransportPickerComponent = 'formGroup';
    const controlNameInputName: keyof MeansOfTransportPickerComponent = 'controlName';
    const multiSelectModeInputName: keyof MeansOfTransportPickerComponent = 'multiSelectMode';
    const showSectorWarningInputName: keyof MeansOfTransportPickerComponent = 'showSectorWarning';
    formGroupInput = signal(
      new FormGroup({
        meansOfTransport: new FormControl([MeanOfTransport.Bus]),
      })
    );
    multiSelectModeInput = signal(true);
    showSectorWarningInput = signal(false);
    fixture = TestBed.createComponent(MeansOfTransportPickerComponent, {
      bindings: [
        inputBinding(formGroupInputName, formGroupInput),
        inputBinding(controlNameInputName, () => 'meansOfTransport'),
        inputBinding(multiSelectModeInputName, multiSelectModeInput),
        inputBinding(showSectorWarningInputName, showSectorWarningInput),
      ],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should add train on click (multi select mode)', () => {
    const trainImage = fixture.debugElement.query(By.css('[data-cy=TRAIN]'));
    trainImage.nativeElement.click();

    const currentMeans = component.formGroup().value.meansOfTransport;
    expect(currentMeans).toEqual([MeanOfTransport.Bus, MeanOfTransport.Train]);
  });

  it('should remove bus on click (multi select mode)', () => {
    const busImage = fixture.debugElement.query(By.css('[data-cy=BUS]'));
    busImage.nativeElement.click();

    const currentMeans = component.formGroup().value.meansOfTransport;
    expect(currentMeans).toEqual([]);
  });

  it('should switch to train on click (single select mode)', () => {
    multiSelectModeInput.set(false);
    fixture.detectChanges();
    const trainImage = fixture.debugElement.query(By.css('[data-cy=TRAIN]'));
    trainImage.nativeElement.click();

    const currentMeans = component.formGroup().value.meansOfTransport;
    expect(currentMeans).toEqual([MeanOfTransport.Train]);
  });

  it('should remove bus on click (single select mode)', () => {
    multiSelectModeInput.set(false);
    fixture.detectChanges();
    const busImage = fixture.debugElement.query(By.css('[data-cy=BUS]'));
    busImage.nativeElement.click();

    const currentMeans = component.formGroup().value.meansOfTransport;
    expect(currentMeans).toEqual([]);
  });

  it('should show sector warning on TRAIN removed', () => {
    formGroupInput.set(
      new FormGroup({
        meansOfTransport: new FormControl([MeanOfTransport.Train]),
      })
    );
    showSectorWarningInput.set(true);
    fixture.detectChanges();
    expect(getSectorWarningEl(fixture)).toBeNull();

    const train = fixture.debugElement.query(By.css('[data-cy=TRAIN]'));
    train.nativeElement.click();

    fixture.detectChanges();
    expect(getSectorWarningEl(fixture)).not.toBeNull();
  });
});
