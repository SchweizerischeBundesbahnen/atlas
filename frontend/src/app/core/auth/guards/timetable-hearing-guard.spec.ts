import {TestBed} from '@angular/core/testing';
import {AuthService} from '../auth.service';
import {TimetableHearingGuard} from './timetable-hearing-guard.service';
import {BehaviorSubject} from 'rxjs';
import {RouterModule} from "@angular/router";

describe('TimetableHearingGuard', () => {
  let guard: TimetableHearingGuard;

  const authServiceSpy = jasmine.createSpyObj<AuthService>('authService', [
    'mayAccessTimetableHearing',
  ]);
  authServiceSpy.permissionsLoaded = new BehaviorSubject(false);

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
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
