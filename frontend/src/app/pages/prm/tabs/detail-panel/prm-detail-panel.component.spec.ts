import {ComponentFixture, TestBed} from '@angular/core/testing';

import {PrmDetailPanelComponent} from './prm-detail-panel.component';
import {AppTestingModule} from "../../../../app.testing.module";
import {TranslatePipe} from "@ngx-translate/core";
import {DateRangeTextComponent} from "../../../../core/versioning/date-range-text/date-range-text.component";
import {DisplayDatePipe} from "../../../../core/pipe/display-date.pipe";

describe('PrmDetailPanelComponent', () => {
  let component: PrmDetailPanelComponent;
  let fixture: ComponentFixture<PrmDetailPanelComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
    imports: [AppTestingModule, PrmDetailPanelComponent,
        DateRangeTextComponent,
        DisplayDatePipe],
    providers: [
        TranslatePipe,
    ],
});
    fixture = TestBed.createComponent(PrmDetailPanelComponent);
    component = fixture.componentInstance;
  });

  it('should init new', () => {
    component.isNew = true;
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });

  it('should init existing', () => {
    component.selectedVersion = {sloid: 'ch:1:sloid:352'};
    component.maxValidity = {
      validFrom: new Date('2020-01-01'),
      validTo: new Date('2020-01-01'),
    };
    component.isNew = false;
    fixture.detectChanges();

    expect(component).toBeTruthy();
  });

});
