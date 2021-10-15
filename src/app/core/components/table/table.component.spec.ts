import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TableComponent } from './table.component';
import {
  TranslateFakeLoader,
  TranslateLoader,
  TranslateModule,
  TranslatePipe,
} from '@ngx-translate/core';
import { MaterialModule } from '../../module/material.module';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { By } from '@angular/platform-browser';
import { LoadingSpinnerComponent } from '../loading-spinner/loading-spinner.component';

describe('TableComponent', () => {
  /*eslint-disable */
  let component: TableComponent<any>;
  let fixture: ComponentFixture<TableComponent<any>>;
  /*eslint-enable */

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TableComponent, LoadingSpinnerComponent],
      imports: [
        MaterialModule,
        BrowserAnimationsModule,
        TranslateModule.forRoot({
          loader: { provide: TranslateLoader, useClass: TranslateFakeLoader },
        }),
      ],
      providers: [TranslatePipe],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TableComponent);
    component = fixture.componentInstance;
    component.tableColumns = [
      {
        headerTitle: 'TTFN.VALID_FROM',
        value: 'validFrom',
        formatAsDate: true,
      },
      {
        headerTitle: 'TTFN.VALID_TO',
        value: 'validTo',
        formatAsDate: true,
      },
      {
        headerTitle: 'TTFN.NAME',
        value: 'name',
      },
    ];
    component.tableData = [
      { validFrom: new Date('2021-12-31'), validTo: new Date('2099-12-31'), name: 'Aarau' },
      { validFrom: new Date('2021-12-31'), validTo: new Date('2099-12-31'), name: 'Bern' },
      { validFrom: new Date('2021-12-31'), validTo: new Date('2099-12-31'), name: 'Basel' },
    ];
    component.totalCount = 10;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render date nicely', () => {
    const firstTableCell = fixture.debugElement.query(By.css('td'));
    expect(firstTableCell.nativeElement.innerText).toBe('31.12.2021');
  });

  it('should not render add if user may not edit', () => {
    component.canEdit = false;
    fixture.detectChanges();

    const addButton = fixture.debugElement.query(By.css('.bi-plus'));
    expect(addButton).toBeFalsy();
  });

  it('should render add if user can edit', () => {
    component.canEdit = true;
    fixture.detectChanges();

    const addButton = fixture.debugElement.query(By.css('.bi-plus'));
    expect(addButton).toBeTruthy();
  });

  it('should output new event', () => {
    spyOn(component.newElementEvent, 'emit');

    const addButton = fixture.debugElement.query(By.css('.bi-plus'));
    addButton.nativeElement.click();

    expect(component.newElementEvent.emit).toHaveBeenCalled();
  });

  it('should output edit event', () => {
    spyOn(component.editElementEvent, 'emit');

    const firstTableCell = fixture.debugElement.query(By.css('td'));
    firstTableCell.nativeElement.click();

    expect(component.editElementEvent.emit).toHaveBeenCalled();
  });

  it('should render pagination', () => {
    const paginator = fixture.debugElement.query(By.css('mat-paginator'));

    expect(paginator).toBeDefined();
    expect(paginator.nativeElement.getAttribute('ng-reflect-length')).toBe('10');
  });

  it('should click on show 5 element', () => {
    spyOn(component.getTableElementsEvent, 'emit');

    const paginator = fixture.debugElement.query(By.css('mat-paginator'));
    paginator.nativeNode.setAttribute('ng-reflect-page-size-oprions', [5, 10, 20]);
    fixture.detectChanges();

    const matSelector = fixture.debugElement.query(By.css('.mat-select-trigger'));
    matSelector.nativeElement.click();
    fixture.detectChanges();

    const matOption = fixture.debugElement.query(By.css('mat-option'));
    matOption.nativeElement.click();
    fixture.detectChanges();

    expect(matSelector).toBeDefined();
    expect(component.getTableElementsEvent.emit).toHaveBeenCalledWith(Object({ page: 0, size: 5 }));
  });

  it('should click on sort name', () => {
    spyOn(component.getTableElementsEvent, 'emit');

    const buttonSortHeaderName = fixture.debugElement.query(By.css('.mat-sort-header-container'));
    buttonSortHeaderName.nativeElement.click();
    fixture.detectChanges();

    expect(buttonSortHeaderName).toBeDefined();
    expect(component.getTableElementsEvent.emit).toHaveBeenCalledWith(
      Object({ page: 0, size: 10, sort: 'validFrom,ASC' })
    );
  });
});
