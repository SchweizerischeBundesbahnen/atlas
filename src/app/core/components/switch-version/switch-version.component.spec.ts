import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SwitchVersionComponent } from './switch-version.component';
import { TranslateFakeLoader, TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { Record } from '../detail-wrapper/record';
import moment from 'moment';
import { Pages } from '../../../pages/pages';

describe('SwitchVersionComponent', () => {
  let component: SwitchVersionComponent;
  let fixture: ComponentFixture<SwitchVersionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SwitchVersionComponent],
      imports: [
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SwitchVersionComponent);
    component = fixture.componentInstance;
    const firstRecord: Record = {
      id: 1,
      validFrom: moment('1.1.2000', 'DD.MM.YYYY').toDate(),
      validTo: moment('31.12.2000', 'DD.MM.YYYY').toDate(),
    };
    const secondRecord: Record = {
      id: 2,
      validFrom: moment('1.1.2001', 'DD.MM.YYYY').toDate(),
      validTo: moment('31.12.2001', 'DD.MM.YYYY').toDate(),
    };
    const thirdRecord: Record = {
      id: 3,
      validFrom: moment('1.1.2002', 'DD.MM.YYYY').toDate(),
      validTo: moment('31.12.2002', 'DD.MM.YYYY').toDate(),
    };
    const records: Array<Record> = [firstRecord, secondRecord, thirdRecord];
    component.currentRecord = firstRecord;
    component.records = records;
    component.pageType = Pages.TTFN;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should switch right', () => {
    //given
    spyOn(component.switchVersion, 'emit');
    //when
    component.switchRight();
    //then
    expect(component.currentIndex).toBe(1);
    expect(component.switchVersion.emit).toHaveBeenCalledWith(1);
  });

  it('should switch left', () => {
    //given
    component.switchRight();
    spyOn(component.switchVersion, 'emit');
    //when
    component.switchLeft();
    //then
    expect(component.currentIndex).toBe(0);
    expect(component.switchVersion.emit).toHaveBeenCalledWith(0);
  });

  it('should get current index', () => {
    //when
    component.getCurrentIndex();
    //then
    expect(component.currentIndex).toBe(0);
  });

  it('should display versions items', () => {
    //when
    const result = component.displayVersionsItems();
    //then
    expect(result).toBe('1 / 3');
  });

  it('should get initial current data range', () => {
    //when
    const result = component.getInitialCurrentDataRage();
    //then
    expect(result).toBe('01.01.2000');
  });

  it('should get end current data range', () => {
    //when
    const result = component.getEndCurrentDataRage();
    //then
    expect(result).toBe('31.12.2000');
  });

  it('should get initial data range', () => {
    //when
    const result = component.getInitialDataRage();
    //then
    expect(result).toBe('01.01.2000');
  });

  it('should get end data range', () => {
    //when
    const result = component.getEndDataRage();
    //then
    expect(result).toBe('31.12.2002');
  });
});
