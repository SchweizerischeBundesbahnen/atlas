import { Injectable } from '@angular/core';
import { OAuthStorage } from 'angular-oauth2-oidc';
import { CookieService } from 'ngx-cookie-service';

// See https://confluence.sbb.ch/x/MpfzpQ
@Injectable()
export class OAuthCookieStorage extends OAuthStorage {
  constructor(private cookieService: CookieService) {
    super();
  }

  getItem(key: string): string | null {
    return this.cookieService.get(key);
  }

  removeItem(key: string): void {
    this.cookieService.set(key, '', {
      sameSite: 'Strict',
      secure: true,
      path: '/',
      expires: -1,
    });
  }

  setItem(key: string, data: string): void {
    this.cookieService.set(key, data, {
      sameSite: 'Strict',
      secure: true,
      path: '/',
    });
  }
}
