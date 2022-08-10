import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserAdministrationOverviewComponent } from './user-administration-overview.component';

describe('OverviewComponent', () => {
  let component: UserAdministrationOverviewComponent;
  let fixture: ComponentFixture<UserAdministrationOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [UserAdministrationOverviewComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(UserAdministrationOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
