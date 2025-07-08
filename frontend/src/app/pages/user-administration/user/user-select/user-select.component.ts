import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { ApplicationType, User } from '../../../../api';
import { SearchSelectComponent } from '../../../../core/form-components/search-select/search-select.component';
import { UserSelectFormatPipe } from './user-select-format.pipe';
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';

@Component({
  selector: 'app-user-select',
  templateUrl: './user-select.component.html',
  imports: [SearchSelectComponent, ReactiveFormsModule, UserSelectFormatPipe],
})
export class UserSelectComponent {
  constructor(private readonly userService: UserAdministrationService) {}

  @Input() form!: FormGroup;
  @Input() searchInAtlas = false;
  @Input() applicationType?: ApplicationType;

  @Output() selectionChange: EventEmitter<User> = new EventEmitter<User>();
  userSearchResults$: Observable<User[]> = of([]);

  search(searchQuery: string): void {
    if (!searchQuery) {
      return;
    }
    if (this.searchInAtlas) {
      this.userSearchResults$ = this.userService.searchUsersInAtlas(
        searchQuery,
        this.applicationType!
      );
    } else {
      this.userSearchResults$ = this.userService.searchUsers(searchQuery);
    }
  }
}
