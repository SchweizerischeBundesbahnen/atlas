import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { AuthService } from '../auth.service';
import { TimetableHearingGuard } from './timetable-hearing-guard.service';
import { BehaviorSubject } from 'rxjs';

describe('TimetableHearingGuard', () => {
  let guard: TimetableHearingGuard;

  const authServiceSpy = jasmine.createSpyObj<AuthService>('authService', [
    'mayAccessTimetableHearing',
  ]);
  authServiceSpy.permissionsLoaded = new BehaviorSubject(false);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [
        {
          provide: AuthService,
          useValue: authServiceSpy,
        },
      ],
    });
    guard = TestBed.inject(TimetableHearingGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
