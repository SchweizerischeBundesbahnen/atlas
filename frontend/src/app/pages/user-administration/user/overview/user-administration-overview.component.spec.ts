import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { UserService } from '../../service/user.service';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { of } from 'rxjs';
import { Component, Input } from '@angular/core';
import { RouterTestingModule } from '@angular/router/testing';
import { User } from '../../../../api';
import { MaterialModule } from '../../../../core/module/material.module';
import { FormGroup, FormsModule } from '@angular/forms';
import { AtlasButtonComponent } from '../../../../core/components/button/atlas-button.component';
import { AuthService } from '../../../../core/auth/auth.service';
import { UserAdministrationUserOverviewComponent } from './user-administration-overview.component';
import { MockTableComponent } from '../../../../app.testing.mocks';
import { TableService } from '../../../../core/components/table/table.service';

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

describe('UserAdministrationUserOverviewComponent', () => {
  let component: UserAdministrationUserOverviewComponent;
  let fixture: ComponentFixture<UserAdministrationUserOverviewComponent>;

  const userServiceMock = jasmine.createSpyObj(['getUsers']);
  userServiceMock.getUsers.and.returnValue(of({ users: [], totalCount: 0 }));

  let tableService: TableService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        UserAdministrationUserOverviewComponent,
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

    fixture = TestBed.createComponent(UserAdministrationUserOverviewComponent);
    component = fixture.componentInstance;
    tableService = fixture.debugElement.injector.get(TableService);
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
      }),
    );
    tableService.pageSize = 10;
    tableService.pageIndex = 10;

    component.loadUsers({ page: 5, size: 5 });
    tick();
    expect(userServiceMock.getUsers).toHaveBeenCalledOnceWith(5, 5);
    expect(component.userSearchForm.get('userSearch')?.value).toBeNull();
    expect(component.boForm.get('boSearch')?.value).toBeNull();
    expect(component.selectedApplicationOptions).toEqual([]);
    expect(component.userPageResult).toEqual({
      users: [{ sbbUserId: 'u123456' }, { sbbUserId: 'e654321' }],
      totalCount: 50,
    });
    expect(tableService.pageIndex).toBe(5);
    expect(tableService.pageSize).toBe(5);
  }));

  it('test checkIfUserExists with undefined user', () => {
    spyOn(component, 'loadUsers');
    tableService.pageSize = 10;
    component.checkIfUserExists(undefined!);
    expect(component.loadUsers).toHaveBeenCalledOnceWith({ page: 0, size: 10 });
  });

  it('test checkIfUserExists with undefined sbbUserId', () => {
    tableService.pageIndex = 10;
    component.userPageResult = { users: [{ sbbUserId: 'u123456' }], totalCount: 10 };
    component.checkIfUserExists({ sbbUserId: undefined });
    expect(component.userPageResult).toEqual({ users: [], totalCount: 0 });
    expect(tableService.pageIndex).toBe(0);
  });

  it('test checkIfUserExists normal', () => {
    tableService.pageIndex = 10;

    userServiceMock.hasUserPermissions = jasmine.createSpy().and.returnValue(of(true));
    component.checkIfUserExists({ sbbUserId: 'u123456' });
    expect(component.userPageResult).toEqual({ users: [{ sbbUserId: 'u123456' }], totalCount: 1 });
    expect(tableService.pageIndex).toBe(0);
  });

  it('test selectedSearchChanged', () => {
    spyOn(component, 'loadUsers');
    component.selectedSearchChanged();
    expect(component.loadUsers).toHaveBeenCalledOnceWith({ page: 0, size: 10 });
  });

  it('test filterChanged', () => {
    userServiceMock.getUsers = jasmine
      .createSpy()
      .and.returnValue(of({ totalCount: 1, users: [{ sbbUserId: 'u123456' }] }));

    tableService.pageSize = 10;
    tableService.pageIndex = 10;

    component.filterChanged();

    expect(userServiceMock.getUsers).toHaveBeenCalledOnceWith(
      0,
      10,
      new Set([null]),
      'CANTON',
      new Set([]),
    );
    expect(component.userPageResult).toEqual({ totalCount: 1, users: [{ sbbUserId: 'u123456' }] });
    expect(tableService.pageIndex).toBe(0);
    expect(tableService.pageSize).toBe(10);
  });

  it('test reloadTableWithCurrentSettings, USER', () => {
    spyOn(component, 'checkIfUserExists');
    tableService.pageSize = 10;
    tableService.pageIndex = 10;
    component.reloadTableWithCurrentSettings();
    expect(component.checkIfUserExists).toHaveBeenCalledOnceWith(null!, 10);
  });

  it('test reloadTableWithCurrentSettings, FILTER', () => {
    spyOn(component, 'filterChanged');
    tableService.pageSize = 10;
    tableService.pageIndex = 10;
    component.selectedSearch = 'FILTER';
    component.reloadTableWithCurrentSettings();
    expect(component.filterChanged).toHaveBeenCalledOnceWith(10);
  });
});
