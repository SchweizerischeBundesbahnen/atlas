import { Pipe, PipeTransform } from '@angular/core';
import { TranslatePipe } from '@ngx-translate/core';
import { UserModel } from '../../../../api/model/userModel';

@Pipe({
  name: 'editTitle',
  pure: true,
})
export class EditTitlePipe implements PipeTransform {
  constructor(private readonly translatePipe: TranslatePipe) {}

  transform(user: UserModel | undefined, ...args: unknown[]): string {
    if (user) {
      return `${user.firstName} ${user.lastName}`;
    }
    return this.translatePipe.transform('USER_ADMIN.NOT_FOUND');
  }
}
