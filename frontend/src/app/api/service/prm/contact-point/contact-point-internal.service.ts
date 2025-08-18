import {inject, Injectable} from '@angular/core';
import {AtlasApiService} from "../../atlas-api.service";
import {Observable} from "rxjs";
import {ContactPointOverview} from "../../../model/contactPointOverview";

@Injectable({
  providedIn: 'root'
})
export class ContactPointInternalService {

  private readonly V1_PLATFORMS = '/prm-directory/internal/contact-points/overview';

  private readonly atlasApiService = inject(AtlasApiService);

  public getContactPointOverview(parentServicePointSloid: String): Observable<Array<ContactPointOverview>> {
    this.atlasApiService.validateParams({parentServicePointSloid: parentServicePointSloid});
    return this.atlasApiService.get(`${this.V1_PLATFORMS}/${parentServicePointSloid}`);
  }


}
