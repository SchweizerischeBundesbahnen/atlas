import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationEditComponent } from './user-administration-edit.component';

describe('UserAdministrationEditComponent', () => {
  let component: UserAdministrationEditComponent;
  let fixture: ComponentFixture<UserAdministrationEditComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAdministrationEditComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationEditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
