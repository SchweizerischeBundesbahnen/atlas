import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { UserService } from '../service/user.service';
import { User } from '../../../api';

@Component({
  selector: 'app-user-select',
  templateUrl: './user-select.component.html',
})
export class UserSelectComponent {
  constructor(private readonly userService: UserService) {}

  @Input() form!: FormGroup;
  @Output() selectionChange: EventEmitter<User> = new EventEmitter<User>();
  userSearchResults$: Observable<User[]> = of([]);

  searchUser(searchQuery: string): void {
    if (!searchQuery) {
      return;
    }
    this.userSearchResults$ = this.userService.searchUsers(searchQuery);
  }
}
