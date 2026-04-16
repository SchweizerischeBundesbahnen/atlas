import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CompanyDetailComponent } from './company-detail.component';
import { ActivatedRoute } from '@angular/router';
import { translateServiceProvider } from '../../../../app.testing.mocks';
import { beforeEach, describe, expect, it } from 'vitest';

describe('CompanyDetailComponent', () => {
  let component: CompanyDetailComponent;
  let fixture: ComponentFixture<CompanyDetailComponent>;

  const mockData = {
    companyDetail: {
      uicCode: '1234',
      name: 'SBB',
    },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [{ provide: ActivatedRoute, useValue: { snapshot: { data: mockData } } }, translateServiceProvider],
    });

    fixture = TestBed.createComponent(CompanyDetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('should prepare url for link opening', () => {
    expect(component.prependHttp(undefined)).toBeUndefined();
    expect(component.prependHttp('www.betonplus-al.com')).toBe('https://www.betonplus-al.com');
    expect(component.prependHttp(' www.betonplus-al.com ')).toBe('https://www.betonplus-al.com');
    expect(component.prependHttp('betonplus-al.com ')).toBe('https://betonplus-al.com');
    expect(component.prependHttp('http://www.betonplus-al.com')).toBe('http://www.betonplus-al.com');
    expect(component.prependHttp('https://www.betonplus-al.com')).toBe('https://www.betonplus-al.com');
  });
});
