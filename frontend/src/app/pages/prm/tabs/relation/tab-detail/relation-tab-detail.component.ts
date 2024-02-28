import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {PersonWithReducedMobilityService, ReadReferencePointVersion} from "../../../../../api";
import {PrmMeanOfTransportHelper} from "../../../util/prm-mean-of-transport-helper";
import {Pages} from "../../../../pages";

@Component({
  selector: 'app-relation-tab-detail',
  templateUrl: './relation-tab-detail.component.html',
})
export class RelationTabDetailComponent implements OnInit {

  referencePoints: ReadReferencePointVersion[] = [];
  selectedReferencePointSloid!: string;
  elementSloid!: string;

  readonly extractSloid = (option: ReadReferencePointVersion) => option.sloid;
  readonly displayExtractor = (option: ReadReferencePointVersion) => `${option.designation} - ${option.sloid}`;

  constructor(
    readonly router: Router,
    private route: ActivatedRoute,
    private personWithReducedMobilityService: PersonWithReducedMobilityService,
  ) {
  }

  ngOnInit(): void {
    this.checkIfRelationsAvailable();

    this.elementSloid = this.route.parent!.snapshot.params.sloid!;

    const parentServicePointSloid = this.route.parent!.snapshot.params.stopPointSloid;
    this.personWithReducedMobilityService
      .getReferencePointsOverview(parentServicePointSloid)
      .subscribe((overviewRows) => {
        this.referencePoints = overviewRows;
        if (this.referencePoints.length == 1) {
          this.selectedReferencePointSloid = this.referencePoints[0].sloid!;
        }
      });
  }

  checkIfRelationsAvailable() {
    const stopPoint = this.route.parent!.snapshot.data.stopPoint;
    const reduced = PrmMeanOfTransportHelper.isReduced(stopPoint[0].meansOfTransport);
    if (reduced) {
      this.router.navigate([Pages.PRM.path, Pages.STOP_POINTS.path, stopPoint[0].sloid]).then();
    }
  }

}
