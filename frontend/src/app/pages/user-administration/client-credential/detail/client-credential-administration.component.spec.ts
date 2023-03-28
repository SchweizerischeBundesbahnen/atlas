import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ClientCredentialAdministrationComponent } from './client-credential-administration.component';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Component } from '@angular/core';

@Component({
  selector: 'app-user-administration-create',
  template: '',
})
class MockAppUserAdministrationCreateComponent {}

describe('ClientCredentialAdministrationComponent', () => {
  let component: ClientCredentialAdministrationComponent;
  let fixture: ComponentFixture<ClientCredentialAdministrationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        ClientCredentialAdministrationComponent,
        MockAppUserAdministrationCreateComponent,
      ],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: { clientCredential: {} },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ClientCredentialAdministrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
