import { TestBed } from '@angular/core/testing';

import {
  ALLOWED_TERMINATION_COUNTRIES,
  TerminationService,
} from './termination.service';
import { ServicePointFormGroupBuilder } from '../service-point-detail-form-group';
import { BERN_WYLEREGG } from '../../../../../../test/data/service-point';
import { Country } from '../../../../../api';
import { environment } from '../../../../../../environments/environment';

function init(service: TerminationService) {
  TestBed.configureTestingModule({});
  service = TestBed.inject(TerminationService);
  return service;
}

describe('TerminationService', () => {
  describe('TerminationService - feature toggle enabled', () => {
    let service: TerminationService;

    beforeEach(() => {
      service = init(service);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should start termination', () => {
      //given
      const servicePoint = JSON.parse(JSON.stringify(BERN_WYLEREGG));
      const initialForm =
        ServicePointFormGroupBuilder.buildFormGroup(servicePoint);
      service.initTermination(initialForm);

      servicePoint.validTo = new Date('2020-03-31');
      const editedForm =
        ServicePointFormGroupBuilder.buildFormGroup(servicePoint);
      //when
      const result = service.isStartingTermination(editedForm);
      //then
      expect(result).toBeTrue();
    });

    describe('[allowed countries]', () => {
      ALLOWED_TERMINATION_COUNTRIES.forEach((country) => {
        it(`should start termination for country ${country}`, () => {
          //given
          const servicePoint = JSON.parse(JSON.stringify(BERN_WYLEREGG));
          servicePoint.country = country;
          const initialForm =
            ServicePointFormGroupBuilder.buildFormGroup(servicePoint);
          service.initTermination(initialForm);

          servicePoint.validTo = new Date('2020-03-30');
          const editedForm =
            ServicePointFormGroupBuilder.buildFormGroup(servicePoint);
          //when
          const result = service.isStartingTermination(editedForm);
          //then
          expect(result).toBeTrue();
        });
      });
    });

    it('should not start termination when Country is not Switzerland', () => {
      //given
      const servicePoint = JSON.parse(JSON.stringify(BERN_WYLEREGG));
      const initialForm =
        ServicePointFormGroupBuilder.buildFormGroup(servicePoint);
      service.initTermination(initialForm);

      servicePoint.validTo = new Date('2020-03-31');
      servicePoint.country = Country.Albania;
      const editedForm =
        ServicePointFormGroupBuilder.buildFormGroup(servicePoint);
      //when
      const result = service.isStartingTermination(editedForm);
      //then
      expect(result).toBeFalse();
    });

    it('should not start termination when is not stopPoint', () => {
      //given
      const servicePoint = JSON.parse(JSON.stringify(BERN_WYLEREGG));
      const initialForm =
        ServicePointFormGroupBuilder.buildFormGroup(servicePoint);
      service.initTermination(initialForm);

      servicePoint.validTo = new Date('2020-03-31');
      servicePoint.stopPoint = false;
      const editedForm =
        ServicePointFormGroupBuilder.buildFormGroup(servicePoint);
      //when
      const result = service.isStartingTermination(editedForm);
      //then
      expect(result).toBeFalse();
    });

    it('should not start termination when edited validTo is in the past and other attributes are changed', () => {
      //given
      const servicePoint = JSON.parse(JSON.stringify(BERN_WYLEREGG));
      const initialForm =
        ServicePointFormGroupBuilder.buildFormGroup(servicePoint);
      service.initTermination(initialForm);

      servicePoint.validTo = new Date('2020-03-31');
      servicePoint.abbreviation = 'abb-2';
      const editedForm =
        ServicePointFormGroupBuilder.buildFormGroup(servicePoint);
      //when
      const result = service.isStartingTermination(editedForm);
      //then
      expect(result).toBeFalse();
    });

    it('should not start termination when status is not validated', () => {
      //given
      const servicePoint = JSON.parse(JSON.stringify(BERN_WYLEREGG));
      const initialForm =
        ServicePointFormGroupBuilder.buildFormGroup(BERN_WYLEREGG);
      service.initTermination(initialForm);

      servicePoint.validTo = new Date('2020-03-31');
      servicePoint.status = 'DRAFT';
      const editedForm =
        ServicePointFormGroupBuilder.buildFormGroup(servicePoint);
      //when
      const result = service.isStartingTermination(editedForm);
      //then
      expect(result).toBeFalse();
    });

    it('should not start termination when is not stopPoint', () => {
      //given
      const servicePoint = JSON.parse(JSON.stringify(BERN_WYLEREGG));
      const initialForm =
        ServicePointFormGroupBuilder.buildFormGroup(BERN_WYLEREGG);
      service.initTermination(initialForm);

      servicePoint.validTo = new Date('2020-03-31');
      servicePoint.stopPoint = false;
      const editedForm =
        ServicePointFormGroupBuilder.buildFormGroup(servicePoint);
      //when
      const result = service.isStartingTermination(editedForm);
      //then
      expect(result).toBeFalse();
    });

    it('should not start termination when edited validTo is in the feature', () => {
      //given
      const servicePoint = JSON.parse(JSON.stringify(BERN_WYLEREGG));
      const initialForm =
        ServicePointFormGroupBuilder.buildFormGroup(BERN_WYLEREGG);
      service.initTermination(initialForm);

      servicePoint.validTo = new Date('2022-03-31');
      const editedForm =
        ServicePointFormGroupBuilder.buildFormGroup(servicePoint);
      //when
      const result = service.isStartingTermination(editedForm);
      //then
      expect(result).toBeFalse();
    });
  });

  describe('TerminationService feature toggle disabled', () => {
    let service: TerminationService;

    beforeEach(() => {
      service = init(service);
      environment.terminationWorkflowEnabled = false;
    });

    afterEach(() => {
      environment.terminationWorkflowEnabled = true;
    });

    it('should not start when feature is not enabled', () => {
      //given
      const servicePoint = JSON.parse(JSON.stringify(BERN_WYLEREGG));
      const initialForm =
        ServicePointFormGroupBuilder.buildFormGroup(BERN_WYLEREGG);
      service.initTermination(initialForm);

      servicePoint.validTo = new Date('2022-03-31');
      const editedForm =
        ServicePointFormGroupBuilder.buildFormGroup(servicePoint);
      //when
      const result = service.isStartingTermination(editedForm);
      //then
      expect(result).toBeFalse();
    });
  });
});
