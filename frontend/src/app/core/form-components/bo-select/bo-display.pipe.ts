import { Pipe, PipeTransform } from '@angular/core';
import { Observable } from 'rxjs';
import { BoSelectionDisplayPipe } from './bo-selection-display.pipe';
import { map } from 'rxjs/operators';
import { VersionsHandlingService } from '../../versioning/versions-handling.service';
import { BusinessOrganisationService } from '../../../api/service/bodi/business-organisation.service';

@Pipe({
  name: 'boDisplay',
  pure: true,
})
export class BoDisplayPipe implements PipeTransform {
  constructor(
    private readonly boSelectionDisplayPipe: BoSelectionDisplayPipe,
    private readonly businessOrganisationsService: BusinessOrganisationService
  ) {}

  transform(sboid: string): Observable<string> {
    return this.businessOrganisationsService.getVersions(sboid).pipe(
      map((businessOrganisation) => {
        const version =
          VersionsHandlingService.determineDefaultVersionByValidity(
            businessOrganisation
          );
        return this.boSelectionDisplayPipe.transform(version);
      })
    );
  }
}
