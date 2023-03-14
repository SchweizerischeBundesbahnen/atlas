import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserSelectComponent } from './user-select.component';
import { UserService } from '../service/user.service';
import { of } from 'rxjs';
import { Component, Input } from '@angular/core';

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

  const userServiceSpy = jasmine.createSpyObj('UserService', ['searchUsers']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserSelectComponent, MockFormSearchSelectComponent],
      providers: [{ provide: UserService, useValue: userServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(UserSelectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('test searchUser', (done) => {
    userServiceSpy.searchUsers.and.returnValue(
      of([
        {
          sbbUserId: 'u236171',
        },
      ])
    );
    component.searchUser('testQuery');
    expect(userServiceSpy.searchUsers).toHaveBeenCalledOnceWith('testQuery');
    component.userSearchResults$.subscribe((val) => {
      expect(val).toEqual([
        {
          sbbUserId: 'u236171',
        },
      ]);
      done();
    });
  });
});
