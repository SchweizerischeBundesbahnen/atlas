import {TestBed} from '@angular/core/testing';
import {AtlasApiService} from '../atlas-api.service';
import {HttpClient} from '@angular/common/http';
import {UserService} from '../../../core/auth/user/user.service';
import {TrafficPointElementService} from "./traffic-point-element.service";
import {CreateTrafficPointElementVersion} from "../../model/createTrafficPointElementVersion";

describe('TrafficPointElementService', () => {
  let service: TrafficPointElementService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [TrafficPointElementService, AtlasApiService,
        {provide: HttpClient, useValue: {}},
        {provide: UserService, useValue: {}},
      ],
    });

    service = TestBed.inject(TrafficPointElementService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
    spyOn(apiService, 'put');
    spyOn(apiService, 'post');
  });

  it('should getTrafficPointElement', () => {
    service.getTrafficPointElement('ch:1:sloid:7000');

    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/service-point-directory/v1/traffic-point-elements/ch%3A1%3Asloid%3A7000',
    );
  });

  it('should createTrafficPoint', () => {
    service.createTrafficPoint( {} as CreateTrafficPointElementVersion);

    expect(apiService.post).toHaveBeenCalledOnceWith(
      '/service-point-directory/v1/traffic-point-elements',
      {}
    );
  });

  it('should updateTrafficPoint', () => {
    service.updateTrafficPoint(123, {} as CreateTrafficPointElementVersion);

    expect(apiService.put).toHaveBeenCalledOnceWith(
      '/service-point-directory/v1/traffic-point-elements/123',
      {}
    );
  });
});
