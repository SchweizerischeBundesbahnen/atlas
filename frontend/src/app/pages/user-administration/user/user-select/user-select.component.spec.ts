import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserSelectComponent } from './user-select.component';
import { of } from 'rxjs';
import { Component, Input } from '@angular/core';
import { ApplicationType, Permission } from '../../../../api';
import { TranslatePipe } from '@ngx-translate/core';
import { FormGroup } from '@angular/forms';
import { SearchSelectComponent } from '../../../../core/form-components/search-select/search-select.component';
import { UserAdministrationService } from '../../../../api/service/user-administration/user-administration.service';

@Component({
  selector: 'form-search-select',
  template: '<p>form-search-select</p>',
})
class MockFormSearchSelectComponent {
  @Input() items$ = of([]);
  @Input() formGroup = undefined;
  @Input() controlName = '';
  @Input() getSelectOption = undefined;
}

describe('UserSelectComponent', () => {
  let component: UserSelectComponent;
  let fixture: ComponentFixture<UserSelectComponent>;

  const userAdministrationServiceSpy = jasmine.createSpyObj(
    'UserAdministrationService',
    ['searchUsers', 'searchUsersInAtlas']
  );

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserSelectComponent],
      providers: [
        TranslatePipe,
        {
          provide: UserAdministrationService,
          useValue: userAdministrationServiceSpy,
        },
      ],
    })
      .overrideComponent(UserSelectComponent, {
        remove: { imports: [SearchSelectComponent] },
        add: { imports: [MockFormSearchSelectComponent] },
      })
      .compileComponents();

    fixture = TestBed.createComponent(UserSelectComponent);
    component = fixture.componentInstance;
    component.form = new FormGroup({});
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('test searchUser', (done) => {
    userAdministrationServiceSpy.searchUsers.and.returnValue(
      of([
        {
          sbbUserId: '***REMOVED***',
          permissions: new Set<Permission>(),
        },
      ])
    );
    component.searchUser('testQuery');
    component.searchInAtlas = false;
    fixture.detectChanges();
    expect(userAdministrationServiceSpy.searchUsers).toHaveBeenCalledOnceWith(
      'testQuery'
    );
    component.userSearchResults$.subscribe((val) => {
      expect(val).toEqual([
        {
          sbbUserId: '***REMOVED***',
          permissions: new Set<Permission>(),
        },
      ]);
      done();
    });
  });

  it('test searchUser in atlas', (done) => {
    userAdministrationServiceSpy.searchUsersInAtlas.and.returnValue(
      of([
        {
          sbbUserId: '***REMOVED***',
          permissions: new Set<Permission>(),
        },
      ])
    );
    component.searchInAtlas = true;
    component.applicationType = ApplicationType.Sepodi;
    fixture.detectChanges();
    component.searchUserInAtlas('testQuery');
    expect(
      userAdministrationServiceSpy.searchUsersInAtlas
    ).toHaveBeenCalledOnceWith('testQuery', ApplicationType.Sepodi);
    component.userSearchResults$.subscribe((val) => {
      expect(val).toEqual([
        {
          sbbUserId: '***REMOVED***',
          permissions: new Set<Permission>(),
        },
      ]);
      done();
    });
  });
});
