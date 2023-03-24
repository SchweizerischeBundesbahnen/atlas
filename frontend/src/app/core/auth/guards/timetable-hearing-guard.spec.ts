import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../auth.service';
import { Router } from '@angular/router';
import { TimetableHearingGuard } from './timetable-hearing-guard.service';

describe('TimetableHearingGuard', () => {
  let guard: TimetableHearingGuard;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        {
          provide: AuthService,
          useValue: jasmine.createSpyObj<AuthService>(['mayAccessTimetableHearing']),
        },
      ],
    });
    guard = TestBed.inject(TimetableHearingGuard);
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
