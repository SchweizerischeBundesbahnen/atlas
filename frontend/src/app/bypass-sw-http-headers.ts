import {HttpEvent, HttpHandlerFn, HttpRequest} from "@angular/common/http";
import {Observable} from "rxjs";

export function serviceWorkerBypassHeaders(request: HttpRequest<unknown>, next: HttpHandlerFn): Observable<HttpEvent<unknown>> {
  const newRequest = request.clone({headers: request.headers.append('ngsw-bypass', 'true')});
  return next(newRequest);
}
