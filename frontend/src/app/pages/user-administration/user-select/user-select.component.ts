import { Component, EventEmitter, Output } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { UserService } from '../service/user.service';
import { UserModel } from '../../../api/model/userModel';

@Component({
  selector: 'app-user-select',
  templateUrl: './user-select.component.html',
  styleUrls: ['./user-select.component.scss'],
})
export class UserSelectComponent {
  constructor(private readonly userService: UserService) {}

  @Output() selectionChange: EventEmitter<UserModel> = new EventEmitter<UserModel>();

  userSearchResults$: Observable<UserModel[]> = of([]);

  readonly form: FormGroup = new FormGroup({
    userSearch: new FormControl<UserModel | null>(null),
  });

  readonly selectOption: (item: UserModel) => string = (item: UserModel): string =>
    `${item.displayName} (${item.mail})`;

  searchUser(searchQuery: string): void {
    if (!searchQuery) {
      return;
    }
    this.userSearchResults$ = this.userService.searchUsers(searchQuery);
  }
}
