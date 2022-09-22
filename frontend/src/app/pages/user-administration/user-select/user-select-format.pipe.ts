import { Pipe, PipeTransform } from '@angular/core';
import { UserModel } from '../../../api/model/userModel';

@Pipe({
  name: 'userSelectFormat',
  pure: true,
})
export class UserSelectFormatPipe implements PipeTransform {
  transform(user: UserModel, ...args: unknown[]): string {
    return `${user.displayName} (${user.mail})`;
  }
}
