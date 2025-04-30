import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { UserService } from '../../service/user.service';
import { ApplicationType, User } from '../../../../api';
import { SearchSelectComponent } from '../../../../core/form-components/search-select/search-select.component';
import { UserSelectFormatPipe } from './user-select-format.pipe';

@Component({
  selector: 'app-user-select',
  templateUrl: './user-select.component.html',
  imports: [SearchSelectComponent, ReactiveFormsModule, UserSelectFormatPipe],
})
export class UserSelectComponent {
  constructor(private readonly userService: UserService) {}

  @Input() form!: FormGroup;
  @Input() searchInAtlas?: boolean;
  @Input() applicationType?: ApplicationType;

  @Output() selectionChange: EventEmitter<User> = new EventEmitter<User>();
  userSearchResults$: Observable<User[]> = of([]);

  searchUser(searchQuery: string): void {
    if (!searchQuery) {
      return;
    }
    this.userSearchResults$ = this.userService.searchUsers(searchQuery);
  }

  searchUserInAtlas(searchQuery: string): void {
    if (!searchQuery) {
      return;
    }
    this.userSearchResults$ = this.userService.searchUsersInAtlas(
      searchQuery,
      this.applicationType!
    );
  }
}
