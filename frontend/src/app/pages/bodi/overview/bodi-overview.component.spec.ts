import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BodiOverviewComponent } from './bodi-overview.component';
import { BusinessOrganisationComponent } from '../business-organisations/business-organisation.component';
import { AppTestingModule } from '../../../app.testing.module';
import { AuthService } from '../../../core/auth/auth.service';

describe('BoDiOverviewComponent', () => {
  let component: BodiOverviewComponent;
  let fixture: ComponentFixture<BodiOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BodiOverviewComponent, BusinessOrganisationComponent],
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
    fixture = TestBed.createComponent(BodiOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
