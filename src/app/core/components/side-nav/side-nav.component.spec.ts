import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SideNavComponent } from './side-nav.component';
import { MaterialModule } from '../../module/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { AppRoutingModule } from '../../../app-routing.module';

describe('SideNavComponent', () => {
  let component: SideNavComponent;
  let fixture: ComponentFixture<SideNavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SideNavComponent],
      imports: [
        MaterialModule,
        BrowserAnimationsModule,
        AppRoutingModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
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
    const firstSideNavItem = result[0].querySelector('div.mat-list-item-content');
    const secondSideNavItem = result[1].querySelector('div.mat-list-item-content');
    expect(firstSideNavItem.textContent.trim()).toBe(component.pages[0].title);
    expect(firstSideNavItem.querySelector('i').className).toContain(component.pages[0].icon);
    expect(secondSideNavItem.textContent.trim()).toBe(component.pages[1].title);
    expect(secondSideNavItem.querySelector('i').className).toContain(component.pages[1].icon);
  });
});
