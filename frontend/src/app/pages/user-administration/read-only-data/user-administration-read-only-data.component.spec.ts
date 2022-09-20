import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationReadOnlyDataComponent } from './user-administration-read-only-data.component';

describe('UserAdministrationReadOnlyDataComponent', () => {
  let component: UserAdministrationReadOnlyDataComponent<any>;
  let fixture: ComponentFixture<UserAdministrationReadOnlyDataComponent<any>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAdministrationReadOnlyDataComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationReadOnlyDataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
