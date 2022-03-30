import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SideNavComponent } from './side-nav.component';
import { AppRoutingModule } from '../../../app-routing.module';
import { AppTestingModule } from '../../../app.testing.module';

describe('SideNavComponent', () => {
  let component: SideNavComponent;
  let fixture: ComponentFixture<SideNavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SideNavComponent],
      imports: [AppTestingModule, AppRoutingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SideNavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show side-nav', () => {
    //given
    fixture.detectChanges();
    //when
    const result = fixture.nativeElement.querySelectorAll('div.link-border');
    //then
    expect(result).toBeDefined();
    const firstSideNavItem = result[0].querySelector('.mat-list-item-content');
    const secondSideNavItem = result[1].querySelector('.mat-list-item-content');
    expect(firstSideNavItem.textContent.trim()).toBe(component.pages[0].titleMenu);
    expect(secondSideNavItem.textContent.trim()).toBe(component.pages[1].titleMenu);
  });
});
