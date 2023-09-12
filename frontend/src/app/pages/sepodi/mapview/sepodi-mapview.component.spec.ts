import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SepodiMapviewComponent } from './sepodi-mapview.component';
import { AuthService } from '../../../core/auth/auth.service';
import { AppTestingModule } from '../../../app.testing.module';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'atlas-map',
  template: '',
})
export class MockAtlasMapComponent {
  @Input() showSearch = true;
}

const authService: Partial<AuthService> = {};

describe('SepodiMapviewComponent', () => {
  let component: SepodiMapviewComponent;
  let fixture: ComponentFixture<SepodiMapviewComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SepodiMapviewComponent, MockAtlasMapComponent],
      imports: [AppTestingModule],
      providers: [{ provide: AuthService, useValue: authService }],
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
