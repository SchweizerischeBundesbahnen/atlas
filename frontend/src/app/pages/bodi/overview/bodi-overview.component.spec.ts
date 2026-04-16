import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BodiOverviewComponent } from './bodi-overview.component';
import { BusinessOrganisationComponent } from '../business-organisations/business-organisation.component';
import { AppTestingModule } from '../../../app.testing.module';
import { AtlasButtonComponent } from '../../../core/components/button/atlas-button.component';
import { PermissionService } from '../../../core/auth/permission/permission.service';
import { adminPermissionServiceMock } from '../../../app.testing.mocks';
import { beforeEach, describe, expect, it } from 'vitest';

describe('BoDiOverviewComponent', () => {
  let component: BodiOverviewComponent;
  let fixture: ComponentFixture<BodiOverviewComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule, BodiOverviewComponent, BusinessOrganisationComponent, AtlasButtonComponent],
      providers: [{ provide: PermissionService, useValue: adminPermissionServiceMock }],
    }).compileComponents();

    fixture = TestBed.createComponent(BodiOverviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
