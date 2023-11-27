import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateStopPointComponent } from './create-stop-point.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { MeanOfTransport } from '../../../../../api';
import { MatStepper, MatStepperModule } from '@angular/material/stepper';
import { FormControl, FormGroup } from '@angular/forms';
import {
  MeanOfTransportFormGroup,
  StopPointFormGroupBuilder,
} from '../form/stop-point-detail-form-group';
import { STOP_POINT, STOP_POINT_COMPLETE } from '../../../util/stop-point-test-data';
import { DialogService } from '../../../../../core/components/dialog/dialog.service';
import { of } from 'rxjs';
import SpyObj = jasmine.SpyObj;

describe('CreateStopPointComponent', () => {
  let component: CreateStopPointComponent;
  let fixture: ComponentFixture<CreateStopPointComponent>;
  let dialogService: SpyObj<DialogService>;

  beforeEach(() => {
    dialogService = jasmine.createSpyObj('dialogService', ['confirm']);
    dialogService.confirm.and.returnValue(of(true));
    TestBed.configureTestingModule({
      declarations: [CreateStopPointComponent],
      imports: [
        MatStepperModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [{ provide: DialogService, useValue: dialogService }],
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
    //given
    component.selectedMeansOfTransport = [MeanOfTransport.Bus];

    //when
    component.backSelection();

    //then
    expect(component.isMeanOfTransportSelected).toBeTruthy();
    expect(component.isDataEditable).toBeFalse();
  });

  it('should checkSelection when no previous meansOfTransport was selected', () => {
    //given
    component.formMeanOfTransport = new FormGroup<MeanOfTransportFormGroup>({
      meansOfTransport: new FormControl([MeanOfTransport.Bus]),
    });
    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);

    //when
    component.checkSelection();

    //then
    expect(component.formMeanOfTransport).toBeTruthy();
    expect(component.isReduced).toBeTruthy();
    expect(component.isDataEditable).toBeTruthy();
    expect(component.form.enabled).toBeTruthy();
  });

  it('should checkSelection when changed meansOfTransport selection from complete to reduced', () => {
    //given
    component.isPreviousSelectionReduced = true;
    component.isMeanOfTransportSelected = true;
    component.formMeanOfTransport = new FormGroup<MeanOfTransportFormGroup>({
      meansOfTransport: new FormControl([MeanOfTransport.Train]),
    });
    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT_COMPLETE);
    spyOn(component, 'resetDataForm');
    spyOn(component, 'initForm');
    const addCompleteRecordingValidationSpy = spyOn(
      StopPointFormGroupBuilder,
      'addCompleteRecordingValidation',
    ).and.callThrough();
    const removeCompleteRecordingValidationSpy = spyOn(
      StopPointFormGroupBuilder,
      'removeCompleteRecordingValidation',
    ).and.callThrough();
    //when
    component.checkSelection();

    //then
    expect(dialogService.confirm).toHaveBeenCalled();
    expect(component.resetDataForm).toHaveBeenCalled();
    expect(component.initForm).toHaveBeenCalled();
    expect(addCompleteRecordingValidationSpy).toHaveBeenCalled();
    expect(removeCompleteRecordingValidationSpy).not.toHaveBeenCalled();
  });

  it('should checkSelection when changed meansOfTransport selection from reduced to complete', () => {
    //given
    component.isPreviousSelectionReduced = false;
    component.isMeanOfTransportSelected = true;
    component.formMeanOfTransport = new FormGroup<MeanOfTransportFormGroup>({
      meansOfTransport: new FormControl([MeanOfTransport.Bus]),
    });
    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    spyOn(component, 'resetDataForm');
    spyOn(component, 'initForm');
    const addCompleteRecordingValidationSpy = spyOn(
      StopPointFormGroupBuilder,
      'addCompleteRecordingValidation',
    ).and.callThrough();
    const removeCompleteRecordingValidationSpy = spyOn(
      StopPointFormGroupBuilder,
      'removeCompleteRecordingValidation',
    ).and.callThrough();

    //when
    component.checkSelection();

    //then
    expect(dialogService.confirm).toHaveBeenCalled();
    expect(component.resetDataForm).toHaveBeenCalled();
    expect(component.initForm).toHaveBeenCalled();
    expect(addCompleteRecordingValidationSpy).not.toHaveBeenCalled();
    expect(removeCompleteRecordingValidationSpy).toHaveBeenCalled();
  });

  it('should resetDataForm', () => {
    //given
    component.selectedMeansOfTransport = [MeanOfTransport.Metro];
    component.formMeanOfTransport = new FormGroup<MeanOfTransportFormGroup>({
      meansOfTransport: new FormControl([MeanOfTransport.Bus]),
    });
    component.form = StopPointFormGroupBuilder.buildFormGroup(STOP_POINT);
    spyOn(component.form, 'reset');

    //when
    component.resetDataForm();

    //then
    expect(component.form.reset).toHaveBeenCalled();
    expect(component.form.controls.meansOfTransport.value).toEqual([MeanOfTransport.Metro]);
    expect(component.form.controls.number.value).toEqual(STOP_POINT.number.number);
    expect(component.form.controls.sloid.value).toEqual(STOP_POINT.sloid);
  });
});
