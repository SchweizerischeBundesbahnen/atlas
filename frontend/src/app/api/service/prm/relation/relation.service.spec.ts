import {TestBed} from '@angular/core/testing';

import {RelationService} from './relation.service';
import {AtlasApiService} from "../../atlas-api.service";
import {StopPointService} from "../stop-point/stop-point.service";
import {provideHttpClient} from "@angular/common/http";
import {UserService} from "../../../../core/auth/user/user.service";

describe('RelationService', () => {
  let service: RelationService;
  let apiService: AtlasApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [StopPointService, AtlasApiService,
        provideHttpClient(),
        {provide: UserService, useValue: {}},
      ]
    });
    service = TestBed.inject(RelationService);
    apiService = TestBed.inject(AtlasApiService);
    spyOn(apiService, 'validateParams').and.callThrough();
    spyOn(apiService, 'get');
  });

  it('should getRelationsBySloid', () => {
    service.getRelationsBySloid('ch:1:sloid:7000');

    expect(apiService.validateParams).toHaveBeenCalledOnceWith({
      sloid: 'ch:1:sloid:7000'
    });
    expect(apiService.get).toHaveBeenCalledOnceWith(
      '/prm-directory/v1/relations/ch:1:sloid:7000',
    );
  });
});
