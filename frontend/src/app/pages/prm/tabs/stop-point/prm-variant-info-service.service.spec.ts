import {TestBed} from '@angular/core/testing';

import {PrmVariantInfoServiceService} from './prm-variant-info-service.service';
import {AuthService} from "../../../../core/auth/auth.service";
import {completeMeansOfTransport, reducedMeansOfTransport} from "../../util/prm-mean-of-transport-helper";

describe('PrmVariantInfoServiceService', () => {
  let service: PrmVariantInfoServiceService;

  const isAtLeastSupervisorMock: Partial<AuthService> = {
    isAtLeastSupervisor(): boolean {
      return true;
    }
  };
  const isNotAtLeastSupervisorMock: Partial<AuthService> = {
    isAtLeastSupervisor(): boolean {
      return false;
    }
  };

  describe("isAtLeastSupervisor", () => {
    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [
          { provide: AuthService, useValue: isAtLeastSupervisorMock },
        ]
      });
      service = TestBed.inject(PrmVariantInfoServiceService);
    });

    it('should be created', () => {
      expect(service).toBeTruthy();
    });

    it('should return reduced meansOfTransportToShow', () => {
      //when
      const res = service.getPrmMeansOfTransportToShow(['BUS']);
      //then
      expect(res).toBeUndefined();

    });

    it('should return complete meansOfTransportToShow', () => {
      //when
      const res = service.getPrmMeansOfTransportToShow(['TRAIN']);
      //then
      expect(res).toBeUndefined();

    });
  });

  describe("isNotAtLeastSupervisorMock", () => {
    beforeEach(() => {
      TestBed.configureTestingModule({
        providers: [
          { provide: AuthService, useValue: isNotAtLeastSupervisorMock },
        ]
      });
      service = TestBed.inject(PrmVariantInfoServiceService);
    });

    it('should return reduced meansOfTransportToShow', () => {
      //when
      const res = service.getPrmMeansOfTransportToShow(['BUS']);
      //then
      expect(res).toEqual(reducedMeansOfTransport);

    });

    it('should return complete meansOfTransportToShow', () => {
      //when
      const res = service.getPrmMeansOfTransportToShow(['TRAIN']);
      //then
      expect(res).toEqual(completeMeansOfTransport);

    });
  });

});
