import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationDetailComponent } from './user-administration-detail.component';

describe('DetailComponent', () => {
  let component: UserAdministrationDetailComponent;
  let fixture: ComponentFixture<UserAdministrationDetailComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAdministrationDetailComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
