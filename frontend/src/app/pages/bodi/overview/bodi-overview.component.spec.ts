import {ComponentFixture, TestBed} from '@angular/core/testing';

import {BodiOverviewComponent} from './bodi-overview.component';
import {BusinessOrganisationComponent} from '../business-organisations/business-organisation.component';
import {AppTestingModule} from '../../../app.testing.module';
import {AtlasButtonComponent} from '../../../core/components/button/atlas-button.component';
import {PermissionService} from "../../../core/auth/permission/permission.service";
import {adminPermissionServiceMock} from "../../../app.testing.mocks";

describe('BoDiOverviewComponent', () => {
  let component: BodiOverviewComponent;
  let fixture: ComponentFixture<BodiOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BodiOverviewComponent, BusinessOrganisationComponent, AtlasButtonComponent],
      imports: [AppTestingModule],
      providers: [
        {
          provide: PermissionService,
          useValue: adminPermissionServiceMock,
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
