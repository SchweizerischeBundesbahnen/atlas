import {RouteToDialogService} from "./route-to-dialog.service";

describe('RouteToDialogService', () => {
  let service: RouteToDialogService;
  beforeEach(() => {
    service = new RouteToDialogService();
  });

  it('should not have dialog by default', () => {
    expect(service.hasDialog()).toBeFalsy();
  });

  it('should not fail when closeDialog without dialog reference', () => {
    service.closeDialog();
    expect().nothing();
  });

  it('should have dialog when setDialogRef', () => {
    service.setDialogRef({} as any);
    expect(service.hasDialog()).toBeTruthy();
  });

  it('should have dialog when setDialogRef', () => {
    service.setDialogRef({} as any);
    expect(service.hasDialog()).toBeTruthy();
    expect(service.getDialog()).toBeTruthy();
  });

  it('should remove dialog when clearDialogRef', () => {
    service.setDialogRef({} as any);
    expect(service.hasDialog()).toBeTruthy();
    service.clearDialogRer();
    expect(service.hasDialog()).toBeFalsy();
  });
});
