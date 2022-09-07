import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationCreateComponent } from './user-administration-create.component';

describe('CreateComponent', () => {
  let component: UserAdministrationCreateComponent;
  let fixture: ComponentFixture<UserAdministrationCreateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAdministrationCreateComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationCreateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
