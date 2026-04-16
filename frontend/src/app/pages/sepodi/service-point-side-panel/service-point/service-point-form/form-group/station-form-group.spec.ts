import { describe, expect, it } from 'vitest';
import { StationFormGroup } from './station-form-group';
import { FormControl } from '@angular/forms';
import { MeanOfTransport, StopPointType } from '../../../../../../api';

describe('Station Form Group', () => {
  it('Should not return validation errors', () => {
    //given
    const stopPointControl = new FormControl(StopPointType.OnDemand);
    const meansOfTransportControl = new FormControl(MeanOfTransport.OnDemand);
    //when
    StationFormGroup.validateMeansOfTranportOnDemand(stopPointControl, meansOfTransportControl);
    //then
    expect(stopPointControl.errors).toBeNull();
    expect(meansOfTransportControl.errors).toBeNull();
  });

  it('Should return validation error when stopPoint is not OnDemand', () => {
    //given
    const stopPointControl = new FormControl(StopPointType.Orderly);
    const meansOfTransportControl = new FormControl(MeanOfTransport.OnDemand);
    //when
    StationFormGroup.validateMeansOfTranportOnDemand(stopPointControl, meansOfTransportControl);
    //then
    expect(stopPointControl.errors).toBeNull();
    const sepodiOnDemand = meansOfTransportControl.errors?.['sepodiOnDemand'];
    expect(sepodiOnDemand).toBeDefined();
    expect(meansOfTransportControl.errors).toBeDefined();
  });

  it('Should not return validation error when Mean Of Transport is not OnDemand', () => {
    //given
    const stopPointControl = new FormControl(StopPointType.OnDemand);
    const meansOfTransportControl = new FormControl(MeanOfTransport.Train);
    //when
    StationFormGroup.validateMeansOfTranportOnDemand(stopPointControl, meansOfTransportControl);
    //then
    expect(stopPointControl.errors).toBeNull();
    expect(meansOfTransportControl.errors).toBeNull();
  });
});
