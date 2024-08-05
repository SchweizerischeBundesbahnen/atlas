import {TestBed} from '@angular/core/testing';
import {ActivatedRouteSnapshot, convertToParamMap, RouterModule, RouterStateSnapshot} from "@angular/router";
import {adminUserServiceMock} from "../../../app.testing.mocks";
import {UserService} from "../user/user.service";
import {permissionsLoaded, PermissionsLoadedGuard} from "./permissions-loaded-guard";
import {Observable} from "rxjs";

describe('PermissionsLoadedGuard', () => {
  let guard: PermissionsLoadedGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterModule.forRoot([])],
      providers: [
        {
          provide: UserService,
          useValue: adminUserServiceMock,
        },
      ],
    });
    guard = TestBed.inject(PermissionsLoadedGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should wait for permissions to be loaded', () => {
    const mockRoute = {paramMap: convertToParamMap({id: '1234'})} as ActivatedRouteSnapshot;
    const result = TestBed.runInInjectionContext(
      () => permissionsLoaded(mockRoute, {} as RouterStateSnapshot)) as Observable<boolean>;

    expect(result).toBeDefined();
    result.subscribe(result => {
      expect(result).toBeTrue();
    })
  });

});
