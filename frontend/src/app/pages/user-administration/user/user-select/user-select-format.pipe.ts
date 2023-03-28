import { Pipe, PipeTransform } from '@angular/core';
import { User } from '../../../../api';

@Pipe({
  name: 'userSelectFormat',
  pure: true,
})
export class UserSelectFormatPipe implements PipeTransform {
  transform(user: User): string {
    return `${user.displayName} ${user.mail ? '(' + user.mail + ')' : ''}`;
  }
}
