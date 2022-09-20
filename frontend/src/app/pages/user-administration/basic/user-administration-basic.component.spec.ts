import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationBasicComponent } from './user-administration-basic.component';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component } from '@angular/core';

@Component({
  selector: 'app-user-administration-create',
  template: '',
})
class MockAppUserAdministrationComponent {}

describe('UserAdministrationBasicComponent', () => {
  let component: UserAdministrationBasicComponent;
  let fixture: ComponentFixture<UserAdministrationBasicComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAdministrationBasicComponent, MockAppUserAdministrationComponent],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: { user: {} },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationBasicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
