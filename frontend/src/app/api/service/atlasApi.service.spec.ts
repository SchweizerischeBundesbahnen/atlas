import { AtlasApiService } from './atlasApi.service';
import { Injectable } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../core/auth/user/user.service';

describe('AtlasApiService', () => {

  @Injectable({ providedIn: 'root' })
  class AtlasApiServiceBase extends AtlasApiService {
    validate(prop1: any, prop2: any) {
      this.validateParams({ prop1, prop2 });
    }
  }

  let service: AtlasApiServiceBase;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AtlasApiServiceBase,
        { provide: HttpClient, useValue: {} },
        { provide: UserService, useValue: {} },
      ],
    });
    service = TestBed.inject(AtlasApiServiceBase);
  });

  it('should throw on validateParams', () => {
    expect(() => service.validate('123', undefined))
      .toThrowError('Required parameter \'prop2\' is null or undefined.');
    expect(() => service.validate('123', null))
      .toThrowError('Required parameter \'prop2\' is null or undefined.');
  });

  it('should not throw on validateParams', () => {
    expect(() => service.validate('123', 'test')).not.toThrow();
  });
});
