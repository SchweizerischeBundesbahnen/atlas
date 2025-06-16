import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TableComponent } from './table.component';
import { TranslateModule, TranslatePipe } from '@ngx-translate/core';
import { By } from '@angular/platform-browser';
import { TableService } from './table.service';
import { StatementStatus } from '../../../api';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { TableFilterComponent } from '../table-filter/table-filter.component';
import { FormatPipe } from './pipe/format.pipe';
import { provideNoopAnimations } from '@angular/platform-browser/animations';

export interface Obj {
  prop: string;
}

describe('TableComponent', () => {
  /*eslint-disable */
  let component: TableComponent<any>;
  let fixture: ComponentFixture<TableComponent<any>>;
  /*eslint-enable */

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TableComponent,
        TranslateModule.forRoot(),
        TableFilterComponent,
      ],
      providers: [
        TranslatePipe,
        FormatPipe,
        TableService,
        provideNoopAnimations(),
      ],
    });

    fixture = TestBed.createComponent(TableComponent);
    component = fixture.componentInstance;

    function mapToCommaSeparated(props: Obj[]) {
      return props
        .map((value) => value.prop)
        .sort()
        .join(', ');
    }

    function changeSelection() {
      console.log('change me');
    }

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
      {
        headerTitle: 'TTFN.RELATIONS',
        value: 'relations',
        callback: mapToCommaSeparated,
      },
      {
        headerTitle: 'TTFN.STATUS',
        value: 'status',
        dropdown: {
          disabled: false,
          options: Object.values(StatementStatus),
          changeSelectionCallback: changeSelection,
          translate: {
            withPrefix: 'TTFN.',
          },
          selectedOption: StatementStatus.Accepted,
        },
      },
    ];
    component.tableData = [
      {
        validFrom: new Date('2021-12-31'),
        validTo: new Date('2099-12-31'),
        name: 'Aarau',
        relations: [{ prop: 'c' }, { prop: 'a' }, { prop: 'b' }],
      },
      {
        validFrom: new Date('2021-12-31'),
        validTo: new Date('2099-12-31'),
        name: 'Bern',
        relations: [{ prop: 'd' }, { prop: 'f' }, { prop: 'g' }],
      },
      {
        validFrom: new Date('2021-12-31'),
        validTo: new Date('2099-12-31'),
        name: 'Basel',
        relations: [],
      },
    ];
    component.totalCount = 10;
    component.isLoading = false;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should get relations comma separated', () => {
    const tableCells = fixture.debugElement.queryAll(By.css('td'));
    expect(tableCells).toBeDefined();
    expect(tableCells[3].nativeElement.innerText).toEqual('a, b, c');
    expect(tableCells[8].nativeElement.innerText).toEqual('d, f, g');
    expect(tableCells[13].nativeElement.innerText).toEqual('');
  });

  it('should get dropdown', () => {
    const tableCells = fixture.debugElement.queryAll(
      By.css('td .atlas-select')
    );
    expect(tableCells).toBeDefined();
    expect(tableCells.length).toEqual(3);
    tableCells.forEach((value) => {
      expect(value.nativeElement.innerText).toEqual(
        'FORM.DROPDOWN_PLACEHOLDER'
      );
    });
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
  });

  it('should click on show 5 element', () => {
    component.sortData({ active: 'validFrom', direction: 'asc' });
    spyOn(component.tableChanged, 'emit');

    const paginator = fixture.debugElement.query(By.css('mat-paginator'));
    paginator.nativeNode.setAttribute(
      'ng-reflect-page-size-options',
      [5, 10, 20]
    );
    fixture.detectChanges();

    const matSelector = paginator.query(By.css('.mat-mdc-select-trigger'));
    matSelector.nativeElement.click();
    fixture.detectChanges();

    const matOption = paginator.query(By.css('mat-option'));
    matOption.nativeElement.click();
    fixture.detectChanges();

    expect(matSelector).toBeDefined();
    expect(component.tableChanged.emit).toHaveBeenCalledWith(
      Object({
        page: 0,
        size: 5,
        sort: 'validFrom,asc',
      })
    );
  });

  it('should click on sort name', () => {
    spyOn(component.tableChanged, 'emit');

    const buttonSortHeaderName = fixture.debugElement.query(
      By.css('.mat-sort-header-container')
    );
    buttonSortHeaderName.nativeElement.click();
    fixture.detectChanges();

    expect(buttonSortHeaderName).toBeDefined();
    expect(component.tableChanged.emit).toHaveBeenCalledWith(
      Object({
        page: 0,
        size: 10,
        sort: 'validFrom,asc',
      })
    );
  });

  it('should emit selection change on checkbox click', () => {
    spyOn(component.checkedBoxEvent, 'emit');

    component.toggleCheckBox(new MatCheckboxChange(), { prop: 'row' });

    expect(component.checkedBoxEvent.emit).toHaveBeenCalled();
  });
});
