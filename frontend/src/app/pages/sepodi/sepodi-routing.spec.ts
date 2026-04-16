import { TestBed } from '@angular/core/testing';
import { describe, expect, it } from 'vitest';
import { provideRouter, Router } from '@angular/router';
import { routes } from './sepodi-routing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

describe('SePoDi Routing', () => {
  it('should construct router with sepodi routes', async () => {
    TestBed.configureTestingModule({
      providers: [provideRouter(routes), provideHttpClient(), provideHttpClientTesting()],
    });
    const router = TestBed.inject(Router);
    expect(router).toBeTruthy();
  });
});
