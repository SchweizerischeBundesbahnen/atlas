import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationBasicComponent } from './user-administration-basic.component';

describe('UserAdministrationBasicComponent', () => {
  let component: UserAdministrationBasicComponent;
  let fixture: ComponentFixture<UserAdministrationBasicComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAdministrationBasicComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationBasicComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
