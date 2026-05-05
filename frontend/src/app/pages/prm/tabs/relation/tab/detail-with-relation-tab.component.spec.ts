import { ComponentFixture, TestBed } from '@angular/core/testing';
import { beforeEach, describe, expect, it } from 'vitest';
import { DetailWithRelationTabComponent } from './detail-with-relation-tab.component';
import { AppTestingModule } from '../../../../../app.testing.module';
import { ActivatedRoute } from '@angular/router';
import { MockAtlasButtonComponent } from '../../../../../app.testing.mocks';
import { STOP_POINT } from '../../../util/stop-point-test-data';
import { BERN_WYLEREGG } from '../../../../../../test/data/service-point';
import { inputBinding } from '@angular/core';

describe('DetailWithRelationTabComponent', () => {
  let component: DetailWithRelationTabComponent;
  let fixture: ComponentFixture<DetailWithRelationTabComponent>;

  const activatedRouteMock = {
    parent: {
      snapshot: {
        data: { stopPoints: [STOP_POINT], servicePoints: [BERN_WYLEREGG] },
      },
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AppTestingModule, DetailWithRelationTabComponent, MockAtlasButtonComponent],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteMock }],
    });

    const detailTitleInputName: keyof DetailWithRelationTabComponent = 'detailTitle';
    fixture = TestBed.createComponent(DetailWithRelationTabComponent, {
      bindings: [inputBinding(detailTitleInputName, () => 'test title')],
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
