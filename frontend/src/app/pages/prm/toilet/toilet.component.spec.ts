import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToiletComponent } from './toilet.component';
import { STOP_POINT } from '../stop-point-test-data';
import { BERN_WYLEREGG } from '../../sepodi/service-point-test-data';
import { MockAtlasButtonComponent } from '../../../app.testing.mocks';
import { AppTestingModule } from '../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';

describe('ToiletteComponent', () => {
  let component: ToiletComponent;
  let fixture: ComponentFixture<ToiletComponent>;
  const activatedRouteMock = {
    parent: { snapshot: { data: { stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] } } },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ToiletComponent, MockAtlasButtonComponent],
      imports: [AppTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteMock }],
    });
    fixture = TestBed.createComponent(ToiletComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
