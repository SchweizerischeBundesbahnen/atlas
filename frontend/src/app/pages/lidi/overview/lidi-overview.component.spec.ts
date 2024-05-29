import {ComponentFixture, TestBed} from '@angular/core/testing';

import {LidiOverviewComponent} from './lidi-overview.component';
import {LinesComponent} from '../lines/lines.component';
import {SublinesComponent} from '../sublines/sublines.component';
import {AppTestingModule} from '../../../app.testing.module';
import {AtlasButtonComponent} from '../../../core/components/button/atlas-button.component';
import {PermissionService} from "../../../core/auth/permission/permission.service";
import {adminPermissionServiceMock} from "../../../app.testing.mocks";

describe('LidiOverviewComponent', () => {
  let component: LidiOverviewComponent;
  let fixture: ComponentFixture<LidiOverviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [
        LidiOverviewComponent,
        LinesComponent,
        SublinesComponent,
        AtlasButtonComponent,
      ],
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
    fixture = TestBed.createComponent(LidiOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
