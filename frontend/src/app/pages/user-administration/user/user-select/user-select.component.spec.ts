import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it, vi, type Mocked } from 'vitest';
import { UserSelectComponent } from './user-select.component';
import { firstValueFrom, of } from 'rxjs';
import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { ApplicationType, Permission } from '../../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { FormGroup } from '@angular/forms';
import { SearchSelectComponent } from '../../../../core/form-components/search-select/search-select.component';
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';

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
    const val = await firstValueFrom(component.userSearchResults$);
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
    const val = await firstValueFrom(component.userSearchResults$);
    expect(val).toEqual([
      {
        sbbUserId: 'user1',
        permissions: new Set<Permission>(),
      },
    ]);
  });
});
