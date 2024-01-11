import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlatformTableComponent } from './platform-table.component';
import { MockAtlasButtonComponent, MockTableComponent } from '../../../../app.testing.mocks';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { STOP_POINT } from '../../util/stop-point-test-data.spec';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';
import { TableComponent } from '../../../../core/components/table/table.component';

describe('PlatformComponent', () => {
  let component: PlatformTableComponent;
  let fixture: ComponentFixture<PlatformTableComponent>;
  const activatedRouteMock = {
    parent: { snapshot: { data: { stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] } } },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PlatformTableComponent, MockAtlasButtonComponent, MockTableComponent],
      imports: [AppTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteMock }],
    });
    fixture = TestBed.createComponent(PlatformTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
