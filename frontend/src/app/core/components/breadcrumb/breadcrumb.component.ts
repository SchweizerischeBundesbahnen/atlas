import { Component, OnDestroy, ViewEncapsulation } from '@angular/core';
import {
  ActivatedRoute,
  ActivatedRouteSnapshot,
  NavigationEnd,
  Router,
  UrlSegment,
} from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Pages } from '../../../pages/pages';

@Component({
  selector: 'app-breadcrumb',
  encapsulation: ViewEncapsulation.None,
  templateUrl: './breadcrumb.component.html',
})
export class BreadcrumbComponent implements OnDestroy {
  public isHome = false;
  public breadcrumbs: { name: string; url: string }[] = [];
  private ngUnsubscribe = new Subject();

  constructor(public router: Router, public activatedRoute: ActivatedRoute) {
    this.router.events.pipe(takeUntil(this.ngUnsubscribe)).subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.breadcrumbs = [];
        this.parseRoute(this.router.routerState.snapshot.root);
      }
    });
  }

  parseRoute(node: ActivatedRouteSnapshot) {
    if (node.data.breadcrumb) {
      if (node.url.length) {
        let urlSegments: UrlSegment[] = [];
        node.pathFromRoot.forEach((routerState) => {
          urlSegments = urlSegments.concat(routerState.url);
        });
        const url = urlSegments
          .map((urlSegment) => {
            return urlSegment.path;
          })
          .join('/');

        this.breadcrumbs.push({
          name: node.data.breadcrumb,
          url: '/' + url,
        });
      }
    }
    if (node.firstChild) {
      this.isHome = Pages.HOME.title === node.firstChild.data.breadcrumb;
      this.parseRoute(node.firstChild);
    }
  }

  ngOnDestroy(): void {
    this.ngUnsubscribe.next(this);
    this.ngUnsubscribe.complete();
  }
}
