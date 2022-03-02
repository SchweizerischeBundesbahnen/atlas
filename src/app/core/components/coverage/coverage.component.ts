import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { Coverage, CoverageType, LinesService, SublinesService } from '../../../api';
import { Pages } from '../../../pages/pages';
import { Record } from '../detail-wrapper/record';
import { Page } from '../../model/page';

@Component({
  selector: 'app-coverage',
  templateUrl: './coverage.component.html',
  styleUrls: ['./coverage.component.scss']
})
export class CoverageComponent implements OnInit, OnChanges {

  @Input() currentRecord!: Record;
  @Input() pageType!: Page;
  coverage!: Coverage;

  constructor(private linesService: LinesService, private sublineService: SublinesService) {
  }

  ngOnInit(): void {
    this.displayCoverage();
  }

  displayCoverage() {
    if (this.pageType === Pages.LINES) {
      // @ts-ignore
      this.linesService.getLineCoverage(this.currentRecord.slnid).subscribe(value => {
        console.log(value);
        this.coverage = value;
      });
    }
    if (this.pageType === Pages.SUBLINES) {
      // @ts-ignore
      this.sublineService.getSublineCoverage(this.currentRecord.slnid).subscribe(value => {
        console.log(value);
        this.coverage = value;
      });
    }
  }

  getIcon(coverageType: CoverageType) {
    if (coverageType === CoverageType.Complete) {
      return 'bi bi-check-circle-fill';
    }
    return 'bi bi-exclamation-triangle-fill';

  }

  ngOnChanges(): void {
    this.displayCoverage();
  }
}
