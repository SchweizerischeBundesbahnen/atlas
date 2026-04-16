import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { CreateStopPointComponent } from './create-stop-point.component';
import { MeanOfTransport } from '../../../../../api';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { FormControl, FormGroup } from '@angular/forms';
import { MeanOfTransportFormGroup, StopPointFormGroupBuilder } from '../form/stop-point-detail-form-group';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { of } from 'rxjs';
import { STOP_POINT, STOP_POINT_COMPLETE } from '../../../util/stop-point-test-data';
import { translateServiceProvider } from '../../../../../app.testing.mocks';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('CreateStopPointComponent', () => {
  let component: CreateStopPointComponent;
  let fixture: ComponentFixture<CreateStopPointComponent>;
  let dialogService: Mocked<Pick<DialogService, 'openDialogDataWithConfirmationResult'>>;

  beforeEach(() => {
    dialogService = {
      openDialogDataWithConfirmationResult: vi.fn(),
    };
    dialogService.openDialogDataWithConfirmationResult.mockReturnValue(of(true));

    TestBed.configureTestingModule({
      imports: [MatStepperModule, CreateStopPointComponent],
      providers: [
        { provide: DialogService, useValue: dialogService },
        translateServiceProvider,
        provideHttpClient(),
        provideHttpClientTesting(),
      ],
    });

    fixture = TestBed.createComponent(CreateStopPointComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    component.stepper = { selectedIndex: 0 } as MatStepper;
    component.stepper.previous = () => {};
    component.stepper.next = () => {};
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should backSelection', () => {
    component.selectedMeansOfTransport = [MeanOfTransport.Bus];

    component.backSelection();

    expect(component.isMeanOfTransportSelected).toBeTruthy();
    expect(component.isDataEditable).toBe(false);
  });

  it('should checkSelection when no previous meansOfTransport was selected', () => {
    component.formMeanOfTransport = new FormGroup<MeanOfTransportFormGroup>({
      meansOfTransport: new FormControl([MeanOfTransport.Bus]),
    });
    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);

    component.checkSelection();

    expect(component.formMeanOfTransport).toBeTruthy();
    expect(component.isReduced).toBeTruthy();
    expect(component.isDataEditable).toBeTruthy();
    expect(component.form.enabled).toBeTruthy();
  });

  it('should checkSelection when changed meansOfTransport selection from complete to reduced', () => {
    component.isPreviousSelectionReduced = true;
    component.isMeanOfTransportSelected = true;
    component.formMeanOfTransport = new FormGroup<MeanOfTransportFormGroup>({
      meansOfTransport: new FormControl([MeanOfTransport.Train]),
    });
    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT_COMPLETE);

    const resetDataFormSpy = vi.spyOn(component, 'resetDataForm').mockImplementation(() => {});
    const initFormSpy = vi.spyOn(component, 'initForm').mockImplementation(() => {});
    const addCompleteRecordingValidationSpy = vi.spyOn(StopPointFormGroupBuilder, 'addCompleteRecordingValidation');
    const removeCompleteRecordingValidationSpy = vi.spyOn(
      StopPointFormGroupBuilder,
      'removeCompleteRecordingValidation'
    );

    component.checkSelection();

    expect(dialogService.openDialogDataWithConfirmationResult).toHaveBeenCalled();
    expect(resetDataFormSpy).toHaveBeenCalled();
    expect(initFormSpy).toHaveBeenCalled();
    expect(addCompleteRecordingValidationSpy).toHaveBeenCalled();
    expect(removeCompleteRecordingValidationSpy).not.toHaveBeenCalled();
  });

  it('should checkSelection when changed meansOfTransport selection from reduced to complete', () => {
    component.isPreviousSelectionReduced = false;
    component.isMeanOfTransportSelected = true;
    component.formMeanOfTransport = new FormGroup<MeanOfTransportFormGroup>({
      meansOfTransport: new FormControl([MeanOfTransport.Bus]),
    });
    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);

    const resetDataFormSpy = vi.spyOn(component, 'resetDataForm').mockImplementation(() => {});
    const initFormSpy = vi.spyOn(component, 'initForm').mockImplementation(() => {});
    const addCompleteRecordingValidationSpy = vi.spyOn(StopPointFormGroupBuilder, 'addCompleteRecordingValidation');
    const removeCompleteRecordingValidationSpy = vi.spyOn(
      StopPointFormGroupBuilder,
      'removeCompleteRecordingValidation'
    );

    addCompleteRecordingValidationSpy.mockClear();
    removeCompleteRecordingValidationSpy.mockClear();

    component.checkSelection();

    expect(dialogService.openDialogDataWithConfirmationResult).toHaveBeenCalled();
    expect(resetDataFormSpy).toHaveBeenCalled();
    expect(initFormSpy).toHaveBeenCalled();
    expect(addCompleteRecordingValidationSpy).not.toHaveBeenCalled();
    expect(removeCompleteRecordingValidationSpy).toHaveBeenCalled();
  });

  it('should resetDataForm', () => {
    component.selectedMeansOfTransport = [MeanOfTransport.Metro];
    component.formMeanOfTransport = new FormGroup<MeanOfTransportFormGroup>({
      meansOfTransport: new FormControl([MeanOfTransport.Bus]),
    });
    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    const resetSpy = vi.spyOn(component.form, 'reset').mockImplementation(() => {});

    component.resetDataForm();

    expect(resetSpy).toHaveBeenCalled();
    expect(component.form.controls.meansOfTransport.value).toEqual([MeanOfTransport.Metro]);
    expect(component.form.controls.number.value).toEqual(STOP_POINT.number.number);
    expect(component.form.controls.sloid.value).toEqual(STOP_POINT.sloid);
  });
});
