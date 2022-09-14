import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationApplicationConfigComponent } from './user-administration-application-config.component';

describe('ApplicationConfigComponent', () => {
  let component: UserAdministrationApplicationConfigComponent;
  let fixture: ComponentFixture<UserAdministrationApplicationConfigComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAdministrationApplicationConfigComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationApplicationConfigComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  // TODO: tests
});
