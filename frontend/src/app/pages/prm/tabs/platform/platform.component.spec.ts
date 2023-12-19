import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlatformComponent } from './platform.component';
import { MockAtlasButtonComponent } from '../../../../app.testing.mocks';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { STOP_POINT } from '../../util/stop-point-test-data.spec';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';

describe('PlatformComponent', () => {
  let component: PlatformComponent;
  let fixture: ComponentFixture<PlatformComponent>;
  const activatedRouteMock = {
    parent: { snapshot: { data: { stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] } } },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PlatformComponent, MockAtlasButtonComponent],
      imports: [AppTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteMock }],
    });
    fixture = TestBed.createComponent(PlatformComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
