import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationComponent } from './user-administration.component';
import { MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA } from '@angular/material/legacy-dialog';
import { Component } from '@angular/core';

@Component({
  selector: 'app-user-administration-create',
  template: '',
})
class MockAppUserAdministrationCreateComponent {}

describe('UserAdministrationComponent', () => {
  let component: UserAdministrationComponent;
  let fixture: ComponentFixture<UserAdministrationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAdministrationComponent, MockAppUserAdministrationCreateComponent],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: { user: {} },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
