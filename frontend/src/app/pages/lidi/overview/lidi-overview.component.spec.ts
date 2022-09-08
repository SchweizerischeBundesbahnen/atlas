import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LidiOverviewComponent } from './lidi-overview.component';
import { LinesComponent } from '../lines/lines.component';
import { SublinesComponent } from '../sublines/sublines.component';
import { AppTestingModule } from '../../../app.testing.module';
import { AuthService } from '../../../core/auth/auth.service';

describe('LidiOverviewComponent', () => {
  let component: LidiOverviewComponent;
  let fixture: ComponentFixture<LidiOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [LidiOverviewComponent, LinesComponent, SublinesComponent],
      imports: [AppTestingModule],
      providers: [
        {
          provide: AuthService,
          useValue: jasmine.createSpyObj<AuthService>('AuthService', ['hasPermissionsToCreate']),
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LidiOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
