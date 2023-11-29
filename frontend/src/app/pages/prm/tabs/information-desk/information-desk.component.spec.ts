import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InformationDeskComponent } from './information-desk.component';
import { BERN_WYLEREGG } from '../../../sepodi/service-point-test-data';
import { MockAtlasButtonComponent } from '../../../../app.testing.mocks';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { STOP_POINT } from '../../util/stop-point-test-data.spec';

describe('InformationDeskComponent', () => {
  let component: InformationDeskComponent;
  let fixture: ComponentFixture<InformationDeskComponent>;
  const activatedRouteMock = {
    parent: { snapshot: { data: { stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] } } },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [InformationDeskComponent, MockAtlasButtonComponent],
      imports: [AppTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteMock }],
    });
    fixture = TestBed.createComponent(InformationDeskComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
