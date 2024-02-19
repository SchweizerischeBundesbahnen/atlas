import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParkingLotTableComponent } from './parking-lot-table.component';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { MockAtlasButtonComponent, MockTableComponent } from '../../../../app.testing.mocks';
import { STOP_POINT } from '../../util/stop-point-test-data.spec';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';

describe('ParkingLotTableComponent', () => {
  let component: ParkingLotTableComponent;
  let fixture: ComponentFixture<ParkingLotTableComponent>;
  const activatedRouteMock = {
    parent: { snapshot: { data: { stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] } } },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ParkingLotTableComponent, MockAtlasButtonComponent, MockTableComponent],
      imports: [AppTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteMock }],
    });
    fixture = TestBed.createComponent(ParkingLotTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
