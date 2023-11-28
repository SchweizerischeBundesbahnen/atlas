import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReferencePointComponent } from './reference-point.component';
import { STOP_POINT } from '../../util/stop-point-test-data';
import { BERN_WYLEREGG } from '../../../sepodi/service-point-test-data';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { MockAtlasButtonComponent } from '../../../../app.testing.mocks';

describe('ReferencePointComponent', () => {
  let component: ReferencePointComponent;
  let fixture: ComponentFixture<ReferencePointComponent>;
  const activatedRouteMock = {
    parent: { snapshot: { data: { stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] } } },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ReferencePointComponent, MockAtlasButtonComponent],
      imports: [AppTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteMock }],
    });
    fixture = TestBed.createComponent(ReferencePointComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
