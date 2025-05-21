import { TestBed } from '@angular/core/testing';

import { TerminationService } from './termination.service';
import { ServicePointFormGroupBuilder } from '../service-point-detail-form-group';
import { BERN_WYLEREGG } from '../../../../../../test/data/service-point';
import { ReadServicePointVersion } from '../../../../../api';

let editedServicePoint!: ReadServicePointVersion;

describe('TerminationService', () => {
  let service: TerminationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TerminationService);
    editedServicePoint = JSON.parse(JSON.stringify(BERN_WYLEREGG));
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start termination', () => {
    //given
    const initialForm =
      ServicePointFormGroupBuilder.buildFormGroup(BERN_WYLEREGG);
    service.initTermination(initialForm);

    editedServicePoint.validTo = new Date('2020-03-31');
    const editedForm =
      ServicePointFormGroupBuilder.buildFormGroup(editedServicePoint);
    //when
    const result = service.isStartingTermination(editedForm);
    //then
    expect(result).toBeTrue();
  });

  it('should not start termination when edited validTo is in the past and other attributes are changed', () => {
    //given
    const initialForm =
      ServicePointFormGroupBuilder.buildFormGroup(BERN_WYLEREGG);
    service.initTermination(initialForm);

    editedServicePoint.validTo = new Date('2020-03-31');
    editedServicePoint.abbreviation = 'abb-2';
    const editedForm =
      ServicePointFormGroupBuilder.buildFormGroup(editedServicePoint);
    //when
    const result = service.isStartingTermination(editedForm);
    //then
    expect(result).toBeFalse();
  });

  it('should not start termination when status is not validated', () => {
    //given
    const initialForm =
      ServicePointFormGroupBuilder.buildFormGroup(BERN_WYLEREGG);
    service.initTermination(initialForm);

    editedServicePoint.validTo = new Date('2020-03-31');
    editedServicePoint.status = 'DRAFT';
    const editedForm =
      ServicePointFormGroupBuilder.buildFormGroup(editedServicePoint);
    //when
    const result = service.isStartingTermination(editedForm);
    //then
    expect(result).toBeFalse();
  });

  it('should not start termination when is not stopPoint', () => {
    //given
    const initialForm =
      ServicePointFormGroupBuilder.buildFormGroup(BERN_WYLEREGG);
    service.initTermination(initialForm);

    editedServicePoint.validTo = new Date('2020-03-31');
    editedServicePoint.stopPoint = false;
    const editedForm =
      ServicePointFormGroupBuilder.buildFormGroup(editedServicePoint);
    //when
    const result = service.isStartingTermination(editedForm);
    //then
    expect(result).toBeFalse();
  });

  it('should not start termination when edited validTo is in the feature', () => {
    //given
    const initialForm =
      ServicePointFormGroupBuilder.buildFormGroup(BERN_WYLEREGG);
    service.initTermination(initialForm);

    editedServicePoint.validTo = new Date('2022-03-31');
    const editedForm =
      ServicePointFormGroupBuilder.buildFormGroup(editedServicePoint);
    //when
    const result = service.isStartingTermination(editedForm);
    //then
    expect(result).toBeFalse();
  });
});
