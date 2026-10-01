import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, type Mocked, vi } from 'vitest';
import { UserSelectComponent } from './user-select.component';
import { firstValueFrom, of } from 'rxjs';
import { ChangeDetectionStrategy, Component, input, signal } from '@angular/core';
import { ApplicationType, Permission, User } from '../../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { FormGroup } from '@angular/forms';
import { SearchSelectComponent } from '../../../../core/form-components/search-select/search-select.component';
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';
import { form } from '@angular/forms/signals';
import { By } from '@angular/platform-browser';
import { NgSelectComponent } from '@ng-select/ng-select';
import { translateServiceProvider } from '../../../../app.testing.mocks';

@Component({
  selector: 'atlas-form-search-select',
  template: '<p>form-search-select</p>',
  changeDetection: ChangeDetectionStrategy.Eager,
})
class MockFormSearchSelectComponent {
  readonly items$ = input(of([]));
  readonly formGroup = input(undefined);
  readonly controlName = input('');
  readonly bindValueInp = input('');
  readonly getSelectOption = input(undefined);
}

describe('UserSelectComponent', () => {
  let component: UserSelectComponent;
  let fixture: ComponentFixture<UserSelectComponent>;
  let userAdministrationService: Mocked<Pick<UserAdministrationService, 'searchUsers' | 'searchUsersInAtlas'>>;

  beforeEach(() => {
    userAdministrationService = {
      searchUsers: vi.fn(),
      searchUsersInAtlas: vi.fn(),
    };
    TestBed.configureTestingModule({
      imports: [UserSelectComponent],
      providers: [
        translateServiceProvider,
        TranslatePipe,
        {
          provide: UserAdministrationService,
          useValue: userAdministrationService,
        },
      ],
    }).overrideComponent(UserSelectComponent, {
      remove: { imports: [SearchSelectComponent] },
      add: { imports: [MockFormSearchSelectComponent] },
    });

    fixture = TestBed.createComponent(UserSelectComponent);
    component = fixture.componentInstance;
    component.form = new FormGroup({});
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('test searchUser', async () => {
    userAdministrationService.searchUsers.mockReturnValue(
      of([
        {
          sbbUserId: 'user1',
          permissions: new Set<Permission>(),
        },
      ])
    );
    component.search('testQuery');
    fixture.componentRef.setInput('searchMode', 'default');
    fixture.detectChanges();
    expect(userAdministrationService.searchUsers).toHaveBeenCalledExactlyOnceWith('testQuery');
    const val = await firstValueFrom(component.searchResults());
    expect(val).toEqual([
      {
        sbbUserId: 'user1',
        permissions: new Set<Permission>(),
      },
    ]);
  });

  it('test searchUser in atlas', async () => {
    userAdministrationService.searchUsersInAtlas.mockReturnValue(
      of([
        {
          sbbUserId: 'user1',
          permissions: new Set<Permission>(),
        },
      ])
    );
    fixture.componentRef.setInput('searchMode', 'inAtlas');
    component.applicationType = ApplicationType.Sepodi;
    fixture.detectChanges();
    component.search('testQuery');
    expect(userAdministrationService.searchUsersInAtlas).toHaveBeenCalledExactlyOnceWith(
      'testQuery',
      ApplicationType.Sepodi
    );
    const val = await firstValueFrom(component.searchResults());
    expect(val).toEqual([
      {
        sbbUserId: 'user1',
        permissions: new Set<Permission>(),
      },
    ]);
  });

  it('binds a signal form field without requiring a reactive form group', () => {
    const searchForm = TestBed.runInInjectionContext(() =>
      form(signal<{ userSearch: User | null }>({ userSearch: null }))
    );
    component.form = undefined;
    fixture.componentRef.setInput('field', searchForm.userSearch);
    fixture.detectChanges();

    expect(fixture.debugElement.query(By.directive(NgSelectComponent))).not.toBeNull();
    expect(searchForm.userSearch().value()).toBeNull();

    const selectedUser: User = { sbbUserId: 'user1', permissions: new Set<Permission>() };
    userAdministrationService.searchUsers.mockReturnValue(of([selectedUser]));
    const selectionChange = vi.fn();
    component.selectionChange.subscribe(selectionChange);
    component.search('user1');
    fixture.detectChanges();

    const select = fixture.debugElement.query(By.directive(NgSelectComponent)).componentInstance as NgSelectComponent;
    select.select(select.itemsList.items[0]);
    fixture.detectChanges();

    expect(selectionChange).toHaveBeenCalledWith(selectedUser);
    expect(searchForm.userSearch().value()).toEqual(selectedUser);
  });
});
