import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationClientDetailComponent } from './user-administration-client-detail.component';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component } from '@angular/core';

@Component({
  selector: 'app-client-credential-administration-create',
  template: '',
})
class MockAppClientCredentialAdministrationCreateComponent {}

describe('UserAdministrationClientDetailComponent', () => {
  let component: UserAdministrationClientDetailComponent;
  let fixture: ComponentFixture<UserAdministrationClientDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        UserAdministrationClientDetailComponent,
        MockAppClientCredentialAdministrationCreateComponent,
      ],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: { clientCredential: {} },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationClientDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
