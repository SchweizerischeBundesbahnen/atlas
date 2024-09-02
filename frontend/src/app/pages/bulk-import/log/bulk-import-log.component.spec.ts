import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BulkImportLogComponent } from './bulk-import-log.component';

describe('LogComponent', () => {
  let component: BulkImportLogComponent;
  let fixture: ComponentFixture<BulkImportLogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BulkImportLogComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BulkImportLogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
