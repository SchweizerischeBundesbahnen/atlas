import {TestBed} from '@angular/core/testing';
import {OAuthCookieStorage} from "./oauth-cookie-storage";
import {CookieService} from "ngx-cookie-service";

describe('OAuthCookieStorage', () => {
  let oAuthCookieStorage: OAuthCookieStorage;
  let cookieService: CookieService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        OAuthCookieStorage, CookieService
      ],
    });
    oAuthCookieStorage = TestBed.inject(OAuthCookieStorage);
    cookieService = TestBed.inject(CookieService);
  });

  it('should set and delete cookie', () => {
    oAuthCookieStorage.setItem('cookie', 'data');
    expect(cookieService.check('cookie')).toBeTrue();
    expect(cookieService.get('cookie')).toEqual('data');
    expect(document.cookie).toBe('cookie=data');

    oAuthCookieStorage.removeItem('cookie');
    expect(cookieService.check('cookie')).toBeFalse();
    expect(cookieService.get('cookie')).toEqual('');
    expect(document.cookie).toBe('');
  });

  it('should set and delete doubled cookie', () => {
    oAuthCookieStorage.setItem('cookie', 'data');
    oAuthCookieStorage.setItem('cookie', 'data2');

    oAuthCookieStorage.removeItem('cookie');
    expect(cookieService.check('cookie')).toBeFalse();
  });
});
