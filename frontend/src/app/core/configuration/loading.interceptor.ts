import { Injectable } from '@angular/core';
import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { finalize, Observable } from 'rxjs';
import { LoadingSpinnerService } from '../components/loading-spinner/loading-spinner.service';

@Injectable()
export class LoadingInterceptor implements HttpInterceptor {
  constructor(private readonly loadingSpinnerService: LoadingSpinnerService) {}

  // todo: mby queing
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.log(req);
    const setLoading = setTimeout(() => {
      this.loadingSpinnerService.loading.next(true);
    }, 100);

    return next.handle(req).pipe(
      finalize(() => {
        clearTimeout(setLoading);
        this.loadingSpinnerService.loading.next(false);
      }),
    );
  }
}
