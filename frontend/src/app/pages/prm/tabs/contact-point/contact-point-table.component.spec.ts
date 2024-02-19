import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ContactPointTableComponent } from './contact-point-table.component';
import { STOP_POINT } from '../../util/stop-point-test-data.spec';
import { MockAtlasButtonComponent, MockTableComponent } from '../../../../app.testing.mocks';
import { AppTestingModule } from '../../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { BERN_WYLEREGG } from '../../../../../test/data/service-point';

describe('ContactPointTableComponent', () => {
  let component: ContactPointTableComponent;
  let fixture: ComponentFixture<ContactPointTableComponent>;
  const activatedRouteMock = {
    parent: { snapshot: { data: { stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] } } },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ContactPointTableComponent, MockAtlasButtonComponent, MockTableComponent],
      imports: [AppTestingModule],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteMock }],
    });
    fixture = TestBed.createComponent(ContactPointTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
