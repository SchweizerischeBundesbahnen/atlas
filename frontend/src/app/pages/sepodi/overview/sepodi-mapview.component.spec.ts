import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SepodiMapviewComponent } from './sepodi-mapview.component';
import { AuthService } from '../../../core/auth/auth.service';

const authService: Partial<AuthService> = {};

describe('SepodiOverviewComponent', () => {
  let component: SepodiMapviewComponent;
  let fixture: ComponentFixture<SepodiMapviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SepodiMapviewComponent],
      providers: [{ provide: AuthService, useValue: authService }],
    }).compileComponents();

    fixture = TestBed.createComponent(SepodiMapviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
