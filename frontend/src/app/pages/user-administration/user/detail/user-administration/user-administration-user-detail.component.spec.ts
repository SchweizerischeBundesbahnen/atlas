import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationUserDetailComponent } from './user-administration-user-detail.component';
import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-user-administration-create',
    template: '',
    standalone: false
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
      providers: [{ provide: ActivatedRoute, useValue: { snapshot: { data: { user: {} } } } }],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationUserDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
