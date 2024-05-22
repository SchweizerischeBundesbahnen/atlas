import {TestBed} from '@angular/core/testing';
import {TimetableHearingGuard} from './timetable-hearing-guard.service';
import {RouterModule} from "@angular/router";
import {adminPermissionServiceMock, adminUserServiceMock} from "../../../app.testing.mocks";
import {PermissionService} from "../permission.service";
import {UserService} from "../user.service";

describe('TimetableHearingGuard', () => {
  let guard: TimetableHearingGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [
        {
          provide: UserService,
          useValue: adminUserServiceMock,
        },
        {
          provide: PermissionService,
          useValue: adminPermissionServiceMock,
        },
      ],
    });
    guard = TestBed.inject(TimetableHearingGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
