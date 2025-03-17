import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SepodiMapviewComponent} from './sepodi-mapview.component';
import {AuthService} from '../../../core/auth/auth.service';
import {AppTestingModule} from '../../../app.testing.module';
import {Component, Input} from '@angular/core';
import {ServicePointSearchType} from "../../../core/search-service-point/service-point-search";
import {AtlasButtonComponent} from "../../../core/components/button/atlas-button.component";

@Component({
    selector: 'atlas-map',
    template: '',
    standalone: false
})
export class MockAtlasMapComponent {
  @Input() isSidePanelOpen = false;
}

@Component({
    selector: 'app-search-service-point-panel',
    template: '<h1>SearchServicePointMockComponent</h1>',
    standalone: false
})
class SearchServicePointMockComponent {
  @Input() searchType!: ServicePointSearchType;
}


const authService: Partial<AuthService> = {};

describe('SepodiMapviewComponent', () => {
  let component: SepodiMapviewComponent;
  let fixture: ComponentFixture<SepodiMapviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SepodiMapviewComponent, MockAtlasMapComponent, SearchServicePointMockComponent, AtlasButtonComponent],
      imports: [AppTestingModule],
      providers: [{provide: AuthService, useValue: authService}],
    }).compileComponents();

    fixture = TestBed.createComponent(SepodiMapviewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should style side panel to open and closed', () => {
    component.setRouteActive(true);
    expect(component.detailContainer.nativeElement.classList).toContain('side-panel-open');

    component.setRouteActive(false);
    expect(component.detailContainer.nativeElement.classList.value).toEqual('detail-container');
  });
});
