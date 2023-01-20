import { TestBed } from '@angular/core/testing';

import { MapOptionsService } from './map-options.service';
import { AuthService } from '../../core/auth/auth.service';

describe('GeodataService', () => {
  let service: MapOptionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: AuthService, useValue: {} }],
    });
    service = TestBed.inject(MapOptionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
