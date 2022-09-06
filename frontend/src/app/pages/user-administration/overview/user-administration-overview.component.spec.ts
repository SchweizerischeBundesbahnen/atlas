import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationOverviewComponent } from './user-administration-overview.component';
import { UserService } from '../service/user.service';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { Component, Input } from '@angular/core';
import { UserModel } from '../../../api';

@Component({
  selector: 'app-table',
  template: '<p>Mock Table Component</p>',
})
class MockTableComponent {
  @Input() loadTableSearch = false;
  @Input() isLoading = false;
  @Input() tableData = [];
  @Input() tableColumns = [];
  @Input() displayStatusSearch = false;
  @Input() displayValidOnSearch = false;
  @Input() searchTextColumnStyle = '';
  @Input() pageSizeOptions = [];
  @Input() totalCount = 0;
  @Input() sortingDisabled = false;
}

@Component({
  selector: 'form-search-select',
  template: '',
})
class MockFormSearchSelectComponent {
  @Input() items$ = of([]);
  @Input() formGroup = null;
  @Input() controlName = '';
  @Input() getSelectOption = null;
}

describe('UserAdministrationOverviewComponent', () => {
  let component: UserAdministrationOverviewComponent;
  let fixture: ComponentFixture<UserAdministrationOverviewComponent>;

  let userServiceMock: UserServiceMock;

  class UserServiceMock {
    searchUsers: any = undefined;
    getUsers: any = jasmine.createSpy().and.returnValue(of({ users: [], totalCount: 0 }));
    getUser: any = undefined;
  }

  beforeEach(async () => {
    userServiceMock = new UserServiceMock();

    await TestBed.configureTestingModule({
      declarations: [
        UserAdministrationOverviewComponent,
        MockTableComponent,
        MockFormSearchSelectComponent,
      ],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [
        {
          provide: UserService,
          useValue: userServiceMock,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('test searchUser', () => {
    const observable = of<UserModel[]>([{ sbbUserId: 'u123456' }, { sbbUserId: 'e654321' }]);
    userServiceMock.searchUsers = jasmine.createSpy().and.returnValue(observable);

    component.searchUser('test');
    expect(component.userSearchResults$).toBe(observable);
    expect(userServiceMock.searchUsers).toHaveBeenCalledOnceWith('test');
  });

  it('test loadUsers', () => {
    component.form.get('userSearch')?.setValue('test');
    expect(component.form.get('userSearch')?.value).toBe('test');

    userServiceMock.getUsers = jasmine.createSpy().and.returnValue(
      of({
        users: [{ sbbUserId: 'u123456' }, { sbbUserId: 'e654321' }] as UserModel[],
        totalCount: 50,
      })
    );

    component.loadUsers({ page: 5, size: 5 });

    expect(userServiceMock.getUsers).toHaveBeenCalledOnceWith(5, 5);
    expect(component.form.get('userSearch')?.value).toBeNull();
    expect(component.tableIsLoading).toBeFalse();
    expect(component.userPageResult).toEqual({
      users: [{ sbbUserId: 'u123456' }, { sbbUserId: 'e654321' }],
      totalCount: 50,
    });
  });

  it('test checkIfUserExists with undefined user', () => {
    spyOn(component, 'loadUsers');
    component.tableComponent = { paginator: { pageSize: 10 } } as any;
    component.checkIfUserExists(undefined!);
    expect(component.loadUsers).toHaveBeenCalledOnceWith({ page: 0, size: 10 });
  });

  it('test checkIfUserExists with undefined sbbUserId', () => {
    component.userPageResult = { users: [{ sbbUserId: 'u123456' }], totalCount: 10 };
    component.checkIfUserExists({ sbbUserId: undefined });
    expect(component.userPageResult).toEqual({ users: [], totalCount: 0 });
  });

  it('test checkIfUserExists normal', () => {
    component.tableComponent = { paginator: { pageIndex: 10 } } as any;

    userServiceMock.getUser = jasmine
      .createSpy()
      .and.returnValue(of({ sbbUserId: 'u123456', permissions: [{ id: 1 }] }));
    component.checkIfUserExists({ sbbUserId: 'u123456' });
    expect(userServiceMock.getUser).toHaveBeenCalledOnceWith('u123456');
    expect(component.userPageResult).toEqual({ users: [{ sbbUserId: 'u123456' }], totalCount: 1 });
    expect(component.tableComponent.paginator.pageIndex).toBe(0);
  });
});
