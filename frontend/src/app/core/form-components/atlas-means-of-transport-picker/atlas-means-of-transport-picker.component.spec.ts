import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { Field, form } from '@angular/forms/signals';
import { signal } from '@angular/core';
import { beforeEach, describe, expect, it } from 'vitest';
import { AtlasMeansOfTransportPickerComponent } from './atlas-means-of-transport-picker.component';
import { MeanOfTransport } from '../../../api';
import { translateServiceProvider } from '../../../app.testing.mocks';

describe('AtlasMeansOfTransportPickerComponent', () => {
  let component: AtlasMeansOfTransportPickerComponent;
  let fixture: ComponentFixture<AtlasMeansOfTransportPickerComponent>;

  const createField = (initialValue: MeanOfTransport[]): Field<MeanOfTransport[]> =>
    TestBed.runInInjectionContext(() => {
      const model = signal({ value: initialValue });
      const testForm = form(model);
      return testForm.value;
    });

  const clickMean = (mean: MeanOfTransport) => {
    const meanImage = fixture.debugElement.query(By.css(`[data-cy=${mean}]`));
    meanImage.nativeElement.click();
    fixture.detectChanges();
  };

  const getSelectedMeans = () => component.field()().value();

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AtlasMeansOfTransportPickerComponent],
      providers: [translateServiceProvider],
    });

    fixture = TestBed.createComponent(AtlasMeansOfTransportPickerComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('field', createField([MeanOfTransport.Bus]));
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show all means of transport by default', () => {
    const buttons = fixture.debugElement.queryAll(By.css('.means-of-transport-wrapper button'));
    expect(buttons.length).toBe(Object.values(MeanOfTransport).length);
  });

  it('should only show the means passed via meansOfTransportToShow', () => {
    fixture.componentRef.setInput('meansOfTransportToShow', [MeanOfTransport.Bus, MeanOfTransport.Train]);
    fixture.detectChanges();

    const buttons = fixture.debugElement.queryAll(By.css('.means-of-transport-wrapper button'));
    expect(buttons.length).toBe(2);
  });

  it('should add train on click (multi select mode)', () => {
    clickMean(MeanOfTransport.Train);

    expect(getSelectedMeans()).toEqual([MeanOfTransport.Bus, MeanOfTransport.Train]);
  });

  it('should remove bus on click (multi select mode)', () => {
    clickMean(MeanOfTransport.Bus);

    expect(getSelectedMeans()).toEqual([]);
  });

  it('should switch to train on click (single select mode)', () => {
    fixture.componentRef.setInput('multiSelectMode', false);
    fixture.detectChanges();

    clickMean(MeanOfTransport.Train);

    expect(getSelectedMeans()).toEqual([MeanOfTransport.Train]);
  });

  it('should remove bus on click (single select mode)', () => {
    fixture.componentRef.setInput('multiSelectMode', false);
    fixture.detectChanges();

    clickMean(MeanOfTransport.Bus);

    expect(getSelectedMeans()).toEqual([]);
  });

  it('should mark field as dirty and touched on selection', () => {
    clickMean(MeanOfTransport.Train);

    expect(component.field()().dirty()).toBe(true);
    expect(component.field()().touched()).toBe(true);
  });
});
