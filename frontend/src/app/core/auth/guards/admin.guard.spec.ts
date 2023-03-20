import { TestBed } from '@angular/core/testing';
import { AdminGuard } from './admin.guard';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';

describe('AdminGuard', () => {
  let guard: AdminGuard;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: jasmine.createSpyObj<AuthService>(['hasRole']) },
      ],
    });
    guard = TestBed.inject(AdminGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return UrlTree', () => {
    const canActivateResult = guard.canActivate();
    expect(canActivateResult).toBeDefined();
    expect(canActivateResult).toEqual(router.parseUrl('/'));
  });
});
