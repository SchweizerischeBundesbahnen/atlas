import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationUserDetailComponent } from './user-administration-user-detail.component';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component } from '@angular/core';

@Component({
  selector: 'app-user-administration-create',
  template: '',
})
class MockAppUserAdministrationCreateComponent {}

describe('UserAdministrationUserDetailComponent', () => {
  let component: UserAdministrationUserDetailComponent;
  let fixture: ComponentFixture<UserAdministrationUserDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        UserAdministrationUserDetailComponent,
        MockAppUserAdministrationCreateComponent,
      ],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: { user: {} },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationUserDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
