import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailWithRelationTabComponent } from './detail-with-relation-tab.component';
import { AppTestingModule } from '../../../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { MockAtlasButtonComponent } from '../../../../../app.testing.mocks';
import { STOP_POINT } from '../../../util/stop-point-test-data.spec';
import { BERN_WYLEREGG } from '../../../../../../test/data/service-point';

describe('DetailWithRelationTabComponent', () => {
  let component: DetailWithRelationTabComponent;
  let fixture: ComponentFixture<DetailWithRelationTabComponent>;

  const activatedRouteMock = {
    parent: { snapshot: { data: { stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] } } },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
    imports: [AppTestingModule, DetailWithRelationTabComponent, MockAtlasButtonComponent],
    providers: [{ provide: ActivatedRoute, useValue: activatedRouteMock }],
});
    fixture = TestBed.createComponent(DetailWithRelationTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
