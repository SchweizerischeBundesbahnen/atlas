import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationClientDetailComponent } from './user-administration-client-detail.component';
import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
    selector: 'app-client-credential-administration-create',
    template: ''
})
class MockAppClientCredentialAdministrationCreateComponent {}

describe('UserAdministrationClientDetailComponent', () => {
  let component: UserAdministrationClientDetailComponent;
  let fixture: ComponentFixture<UserAdministrationClientDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
    imports: [UserAdministrationClientDetailComponent,
        MockAppClientCredentialAdministrationCreateComponent],
    providers: [
        { provide: ActivatedRoute, useValue: { snapshot: { data: { clientCredential: {} } } } },
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
