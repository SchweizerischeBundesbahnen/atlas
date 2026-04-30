import { Pipe, PipeTransform } from '@angular/core';
import { map } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { UserAdministrationService } from '../../api/service/user-administration/user-administration.service';
import { inject } from '@angular/core';

@Pipe({
  name: 'userDisplayName',
  standalone: true,
})
export class UserDisplayNamePipe implements PipeTransform {
  private readonly userAdministrationService = inject(UserAdministrationService);

  transform(userId?: string): Observable<string | undefined> {
    if (!userId) {
      return of(undefined);
    }
    return this.userAdministrationService.getUserDisplayName(userId).pipe(map((userInfo) => userInfo.displayName));
  }
}
