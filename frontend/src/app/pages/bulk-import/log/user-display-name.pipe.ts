import { Pipe, PipeTransform } from '@angular/core';
import { UserAdministrationService } from '../../../api';
import { map } from 'rxjs/operators';
import { Observable, of } from 'rxjs';

@Pipe({
  name: 'userDisplayName',
  standalone: true,
})
export class UserDisplayNamePipe implements PipeTransform {
  constructor(private readonly userAdministrationService: UserAdministrationService) {}

  transform(userId?: string): Observable<string | undefined> {
    if (!userId) return of();
    return this.userAdministrationService
      .getUserDisplayName(userId)
      .pipe(map((userInfo) => userInfo.displayName));
  }
}
