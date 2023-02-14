import { TestBed } from '@angular/core/testing';

import { MapOptionsService } from './map-options.service';
import { AuthService } from '../../../core/auth/auth.service';

const authService: Partial<AuthService> = {};

describe('MapOptionsService', () => {
  let service: MapOptionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: AuthService, useValue: authService }],
    });
    service = TestBed.inject(MapOptionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
