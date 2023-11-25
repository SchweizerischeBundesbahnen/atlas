import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParkingLotComponent } from './parking-lot.component';
import { ActivatedRoute } from '@angular/router';
import { AppTestingModule } from '../../../app.testing.module';
import { STOP_POINT } from '../stop-point-test-data';
import { BERN_WYLEREGG } from '../../sepodi/service-point-test-data';
import { MockAtlasButtonComponent } from '../../../app.testing.mocks';

describe('ParkingLotComponent', () => {
  let component: ParkingLotComponent;
  let fixture: ComponentFixture<ParkingLotComponent>;
  const activatedRouteMock = {
    parent: { snapshot: { data: { stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] } } },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ParkingLotComponent, MockAtlasButtonComponent],
      imports: [AppTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteMock }],
    });
    fixture = TestBed.createComponent(ParkingLotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
