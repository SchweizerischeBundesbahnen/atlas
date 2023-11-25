import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrmHomeSearchComponent } from './prm-home-search.component';
import { TranslatePipe } from '@ngx-translate/core';
import { AppTestingModule } from '../../../app.testing.module';
import { SearchServicePointComponent } from '../../../core/search-service-point/search-service-point.component';

describe('PrmHomeSearchComponent', () => {
  let component: PrmHomeSearchComponent;
  let fixture: ComponentFixture<PrmHomeSearchComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PrmHomeSearchComponent, SearchServicePointComponent],
      imports: [AppTestingModule],
      providers: [{ provide: TranslatePipe }],
    });
    fixture = TestBed.createComponent(PrmHomeSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
