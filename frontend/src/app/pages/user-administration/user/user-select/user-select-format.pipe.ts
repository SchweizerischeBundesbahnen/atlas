import { Pipe, PipeTransform } from '@angular/core';
import { User } from '../../../../api';
import { UserMailHelper } from '../../../../core/util/user-mail.helper';

@Pipe({
  name: 'userSelectFormat',
  pure: true,
})
export class UserSelectFormatPipe implements PipeTransform {
  transform(user: User): string {
    const effectiveMail = UserMailHelper.effectiveMail(user);
    return `${user.displayName} ${effectiveMail ? '(' + effectiveMail + ')' : ''}`;
  }
}
