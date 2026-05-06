import { Component, inject, input, Input, OnInit, output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Observable, of } from 'rxjs';
import { ApplicationType, User } from '../../../../api';
import { SearchSelectComponent } from '../../../../core/form-components/search-select/search-select.component';
import { UserSelectFormatPipe } from './user-select-format.pipe';
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';
import { TthUserAdministrationService } from '../../../../api/service/user-administration/tth-user-administration.service';

export type SearchMode = 'default' | 'inAtlas' | 'boDossierAnsweringUsers';

@Component({
  selector: 'atlas-user-select',
  templateUrl: './user-select.component.html',
  imports: [SearchSelectComponent, ReactiveFormsModule, UserSelectFormatPipe],
})
export class UserSelectComponent implements OnInit {
  private readonly userService = inject(UserAdministrationService);
  private readonly tthUserService = inject(TthUserAdministrationService);


  @Input() form!: FormGroup;

  @Input() applicationType?: ApplicationType;

  readonly searchMode = input<SearchMode>('default');
  readonly controlName = input<string>('userSearch');
  readonly bindValue = input<string>('');

  readonly selectionChange = output<User>();
  userSearchResults$: Observable<User[]> = of([]);

  ngOnInit() {
    const initialValue = this.form.controls[this.controlName()]?.value;
    this.search(initialValue);
  }

  search(searchQuery: string): void {
    if (!searchQuery) {
      return;
    }
    switch (this.searchMode()) {
      case 'default':
        this.userSearchResults$ = this.userService.searchUsers(searchQuery);
        break;
      case 'inAtlas':
        this.userSearchResults$ = this.userService.searchUsersInAtlas(searchQuery, this.applicationType!);
        break;
      case 'boDossierAnsweringUsers':
        this.userSearchResults$ = this.tthUserService.searchBoDossierAnsweringUsers(searchQuery);
        break;
    }
  }
}
