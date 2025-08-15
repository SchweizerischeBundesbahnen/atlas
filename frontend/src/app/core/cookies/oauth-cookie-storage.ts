// import { OAuthStorage } from 'angular-oauth2-oidc';

// See https://confluence.sbb.ch/x/MpfzpQ
// @Injectable()
// export class OAuthCookieStorage extends OAuthStorage {
//   constructor(private cookieService: CookieService) {
//     super();
//   }
//
//   getItem(key: string): string | null {
//     return this.cookieService.get(key);
//   }
//
//   removeItem(key: string): void {
//     this.cookieService.delete(key, '/', undefined, true, 'Strict');
//   }
//
//   setItem(key: string, data: string): void {
//     this.cookieService.set(key, data, {
//       sameSite: 'Strict',
//       secure: true,
//       path: '/',
//     });
//   }
// }
