import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';

import { UserAdministrationOverviewComponent } from './user-administration-overview.component';
import { UserService } from '../service/user.service';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { Component, Input } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { User } from '../../../api/model/user';
import { MaterialModule } from '../../../core/module/material.module';
import { FormGroup, FormsModule } from '@angular/forms';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { AuthService } from '../../../core/auth/auth.service';

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

@Component({
  selector: 'app-user-select',
  template: '<p>app-user-select</p>',
})
class MockUserSelectComponent {
  @Input() form?: FormGroup;
}

describe('UserAdministrationOverviewComponent', () => {
  let component: UserAdministrationOverviewComponent;
  let fixture: ComponentFixture<UserAdministrationOverviewComponent>;

  let userServiceMock: UserServiceMock;

  class UserServiceMock {
    getUsers: any = jasmine.createSpy().and.returnValue(of({ users: [], totalCount: 0 }));
    hasUserPermissions: any = undefined;
  }

  beforeEach(async () => {
    userServiceMock = new UserServiceMock();

    await TestBed.configureTestingModule({
      declarations: [
        UserAdministrationOverviewComponent,
        AtlasButtonComponent,
        MockTableComponent,
        MockFormSearchSelectComponent,
        MockUserSelectComponent,
      ],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
        RouterTestingModule,
        MaterialModule,
        FormsModule,
      ],
      providers: [
        {
          provide: UserService,
          useValue: userServiceMock,
        },
        {
          provide: AuthService,
          useValue: jasmine.createSpyObj<AuthService>('AuthService', ['hasPermissionsToCreate']),
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

  it('test loadUsers', fakeAsync(() => {
    component.userSearchForm.get('userSearch')?.setValue('test');
    component.boForm.get('boSearch')?.setValue('test');
    component.selectedApplicationOptions = ['TTFN'];
    expect(component.userSearchForm.get('userSearch')?.value).toBe('test');
    expect(component.boForm.get('boSearch')?.value).toBe('test');

    userServiceMock.getUsers = jasmine.createSpy().and.returnValue(
      of({
        users: [{ sbbUserId: 'u123456' }, { sbbUserId: 'e654321' }] as User[],
        totalCount: 50,
      })
    );
    component.tableComponent = { paginator: { pageSize: 10, pageIndex: 10 } } as any;

    component.loadUsers({ page: 5, size: 5 });
    tick();
    expect(userServiceMock.getUsers).toHaveBeenCalledOnceWith(5, 5);
    expect(component.userSearchForm.get('userSearch')?.value).toBeNull();
    expect(component.boForm.get('boSearch')?.value).toBeNull();
    expect(component.selectedApplicationOptions).toEqual([]);
    expect(component.tableIsLoading).toBeFalse();
    expect(component.userPageResult).toEqual({
      users: [{ sbbUserId: 'u123456' }, { sbbUserId: 'e654321' }],
      totalCount: 50,
    });
    expect(component.tableComponent.paginator.pageIndex).toBe(5);
    expect(component.tableComponent.paginator.pageSize).toBe(5);
  }));

  it('test checkIfUserExists with undefined user', () => {
    spyOn(component, 'loadUsers');
    component.tableComponent = { paginator: { pageSize: 10 } } as any;
    component.checkIfUserExists(undefined!);
    expect(component.loadUsers).toHaveBeenCalledOnceWith({ page: 0, size: 10 });
  });

  it('test checkIfUserExists with undefined sbbUserId', () => {
    component.tableComponent = { paginator: { pageIndex: 10 } } as any;
    component.userPageResult = { users: [{ sbbUserId: 'u123456' }], totalCount: 10 };
    component.checkIfUserExists({ sbbUserId: undefined });
    expect(component.userPageResult).toEqual({ users: [], totalCount: 0 });
    expect(component.tableComponent.paginator.pageIndex).toBe(0);
  });

  it('test checkIfUserExists normal', () => {
    component.tableComponent = { paginator: { pageIndex: 10 } } as any;

    userServiceMock.hasUserPermissions = jasmine.createSpy().and.returnValue(of(true));
    component.checkIfUserExists({ sbbUserId: 'u123456' });
    expect(component.userPageResult).toEqual({ users: [{ sbbUserId: 'u123456' }], totalCount: 1 });
    expect(component.tableComponent.paginator.pageIndex).toBe(0);
  });

  it('test selectedSearchChanged', () => {
    spyOn(component, 'ngOnInit');
    component.selectedSearchChanged();
    expect(component.ngOnInit).toHaveBeenCalledOnceWith();
  });

  it('test filterChanged', () => {
    userServiceMock.getUsers = jasmine
      .createSpy()
      .and.returnValue(of({ totalCount: 1, users: [{ sbbUserId: 'u123456' }] }));
    component.tableComponent = { paginator: { pageIndex: 10, pageSize: 10 } } as any;

    component.filterChanged();

    expect(userServiceMock.getUsers).toHaveBeenCalledOnceWith(
      0,
      10,
      new Set([null]),
      'CANTON',
      new Set([])
    );
    expect(component.userPageResult).toEqual({ totalCount: 1, users: [{ sbbUserId: 'u123456' }] });
    expect(component.tableIsLoading).toBeFalse();
    expect(component.tableComponent.paginator.pageIndex).toBe(0);
    expect(component.tableComponent.paginator.pageSize).toBe(10);
  });

  it('test reloadTableWithCurrentSettings, USER', () => {
    spyOn(component, 'checkIfUserExists');
    component.tableComponent = { paginator: { pageIndex: 10, pageSize: 10 } } as any;
    component.reloadTableWithCurrentSettings();
    expect(component.checkIfUserExists).toHaveBeenCalledOnceWith(null!, 10);
  });

  it('test reloadTableWithCurrentSettings, FILTER', () => {
    spyOn(component, 'filterChanged');
    component.tableComponent = { paginator: { pageIndex: 10, pageSize: 10 } } as any;
    component.selectedSearch = 'FILTER';
    component.reloadTableWithCurrentSettings();
    expect(component.filterChanged).toHaveBeenCalledOnceWith(10);
  });
});
