import { ChangeDetectionStrategy, Component, inject, input, Input, OnInit, output, signal } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Field, FormField } from '@angular/forms/signals';
import { Observable, of, Subject } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { AsyncPipe } from '@angular/common';
import { NgLabelTemplateDirective, NgOptionTemplateDirective, NgSelectComponent } from '@ng-select/ng-select';
import { TranslatePipe } from '@ngx-translate/core';
import { ApplicationType, User } from '../../../../api';
import { SearchSelectComponent } from '../../../../core/form-components/search-select/search-select.component';
import { UserSelectFormatPipe } from './user-select-format.pipe';
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';
import { TthUserAdministrationService } from '../../../../api/service/user-administration/tth-user-administration.service';

export type SearchMode = 'default' | 'inAtlas' | 'boDossierAnsweringUsers';

@Component({
  selector: 'atlas-user-select',
  templateUrl: './user-select.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  imports: [
    SearchSelectComponent,
    UserSelectFormatPipe,
    FormField,
    NgSelectComponent,
    NgLabelTemplateDirective,
    NgOptionTemplateDirective,
    AsyncPipe,
    TranslatePipe,
  ],
})
export class UserSelectComponent implements OnInit {
  private readonly userService = inject(UserAdministrationService);
  private readonly tthUserService = inject(TthUserAdministrationService);

  @Input() form?: FormGroup;

  @Input() applicationType?: ApplicationType;

  readonly field = input<Field<User | null>>();
  readonly searchMode = input<SearchMode>('default');
  readonly controlName = input<string>('userSearch');
  readonly bindValue = input<string>('');

  readonly selectionChange = output<User>();
  readonly searchResults = signal<Observable<User[]>>(of([]));
  protected readonly searchTriggerSubject = new Subject<string>();

  constructor() {
    this.searchTriggerSubject.pipe(takeUntilDestroyed()).subscribe((query) => this.search(query));
  }

  ngOnInit() {
    const initialValue = this.field()?.().value() ?? this.form?.controls[this.controlName()]?.value;
    if (typeof initialValue === 'string') {
      this.search(initialValue);
    }
  }

  get legacyForm(): FormGroup {
    if (!this.form) {
      throw new Error('User select requires a form or signal field');
    }
    return this.form;
  }

  search(searchQuery: string): void {
    if (!searchQuery) {
      return;
    }
    switch (this.searchMode()) {
      case 'default':
        this.searchResults.set(this.userService.searchUsers(searchQuery));
        break;
      case 'inAtlas':
        if (!this.applicationType) {
          throw new Error('Application type is required to search Atlas users');
        }
        this.searchResults.set(this.userService.searchUsersInAtlas(searchQuery, this.applicationType));
        break;
      case 'boDossierAnsweringUsers':
        this.searchResults.set(this.tthUserService.searchBoDossierAnsweringUsers(searchQuery));
        break;
    }
  }
}
