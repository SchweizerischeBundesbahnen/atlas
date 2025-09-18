import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { routes } from './sepodi-routing';
import { provideHttpClient } from '@angular/common/http';

describe('SePoDi Routing', () => {
  it('should construct router with sepodi routes', async () => {
    TestBed.configureTestingModule({
      providers: [provideRouter(routes), provideHttpClient()],
    });
    const router = TestBed.inject(Router);
    expect(router).toBeTruthy();
  });
});
