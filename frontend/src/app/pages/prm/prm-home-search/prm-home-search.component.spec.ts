import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PrmHomeSearchComponent} from './prm-home-search.component';
import {TranslatePipe} from '@ngx-translate/core';
import {AppTestingModule} from '../../../app.testing.module';
import {PrmInfoBoxComponent} from './prm-info-box/prm-info-box.component';
import {RouterTestingHarness} from "@angular/router/testing";
import {provideRouter} from "@angular/router";
import {Component, Input} from "@angular/core";
import {ServicePointSearchType} from "../../../core/search-service-point/service-point-search";


@Component({
    selector: 'app-search-service-point-panel',
    template: '<h1>SearchServicePointMockComponent</h1>',
    standalone: false
})
class SearchServicePointPanelMockComponent {
  @Input() searchType!: ServicePointSearchType;
}


describe('PrmHomeSearchComponent', () => {
  let component: PrmHomeSearchComponent;
  let fixture: ComponentFixture<PrmHomeSearchComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PrmHomeSearchComponent, SearchServicePointPanelMockComponent, PrmInfoBoxComponent],
      imports: [AppTestingModule],
      providers: [
        {provide: TranslatePipe},
        provideRouter([
          {path: 'prm-directory', component: PrmHomeSearchComponent},
          {path: 'prm-directory/stop-points', component: PrmHomeSearchComponent}
        ])
      ],
    });
    fixture = TestBed.createComponent(PrmHomeSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to prm-directory', async () => {
    const harness = await RouterTestingHarness.create('prm-directory');
    await harness.navigateByUrl('prm-directory');
    expect(component.isPrmHome).toBeTruthy();
  });

  it('should navigate to prm-directory/stop-points', async () => {
    const harness = await RouterTestingHarness.create('prm-directory/stop-points');
    await harness.navigateByUrl('prm-directory/stop-points');
    expect(component.isPrmHome).toBeFalsy();
  });

});
