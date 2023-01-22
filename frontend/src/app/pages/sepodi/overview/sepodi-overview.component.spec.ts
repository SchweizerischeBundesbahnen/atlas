import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SepodiOverviewComponent } from './sepodi-overview.component';
import { AuthService } from '../../../core/auth/auth.service';

const authService: Partial<AuthService> = {};

describe('SepodiOverviewComponent', () => {
  let component: SepodiOverviewComponent;
  let fixture: ComponentFixture<SepodiOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SepodiOverviewComponent],
      providers: [{ provide: AuthService, useValue: authService }],
    }).compileComponents();

    fixture = TestBed.createComponent(SepodiOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
