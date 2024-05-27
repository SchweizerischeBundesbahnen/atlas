import {TestBed} from '@angular/core/testing';
import {OAuthCookieStorage} from "./oauth-cookie-storage";
import {CookieService} from "ngx-cookie-service";

const cookieService = jasmine.createSpyObj<CookieService>(['set', 'get', 'delete'])

describe('OAuthCookieStorage', () => {
  let oAuthCookieStorage: OAuthCookieStorage;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        OAuthCookieStorage,
        { provide: CookieService, useValue: cookieService }
      ],
    });
    oAuthCookieStorage = TestBed.inject(OAuthCookieStorage);
  });

  it('should set cookie', () => {
    oAuthCookieStorage.setItem('cookie', 'data');
    expect(cookieService.set).toHaveBeenCalled();
  });

  it('should get cookie', () => {
    oAuthCookieStorage.getItem('cookie');
    expect(cookieService.get).toHaveBeenCalled();
  });

  it('should delete cookie', () => {
    oAuthCookieStorage.removeItem('cookie');
    expect(cookieService.delete).toHaveBeenCalled();
  });
});
