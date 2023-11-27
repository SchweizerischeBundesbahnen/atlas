import { Pages } from '../../pages';
import { Tab } from '../../tab';

export class PrmTab {
  public static STOP_POINT: Tab = {
    link: Pages.PRM_STOP_POINT_TAB.path,
    title: Pages.PRM_STOP_POINT_TAB.title,
  };

  public static REFERENCE_POINT: Tab = {
    link: 'reference-point',
    title: 'PRM.TABS.REFERENCE_POINT',
  };

  public static PLATFORM: Tab = {
    link: 'platform',
    title: 'PRM.TABS.PLATFORM',
  };

  public static TICKET_COUNTER: Tab = {
    link: 'ticket-counter',
    title: 'PRM.TABS.TICKET_COUNTER',
  };

  public static INFORMATION_DESK: Tab = {
    link: 'information-desk',
    title: 'PRM.TABS.INFORMATION_DESK',
  };
  public static TOILET: Tab = {
    link: 'toilet',
    title: 'PRM.TABS.TOILET',
  };
  public static PARKING_LOT: Tab = {
    link: 'parking-lot',
    title: 'PRM.TABS.PARKING_LOT',
  };
  public static CONNECTION: Tab = {
    link: 'connection',
    title: 'PRM.TABS.CONNECTION',
  };

  public static tabs: Tab[] = [
    PrmTab.STOP_POINT,
    PrmTab.REFERENCE_POINT,
    PrmTab.PLATFORM,
    PrmTab.TICKET_COUNTER,
    PrmTab.INFORMATION_DESK,
    PrmTab.TOILET,
    PrmTab.PARKING_LOT,
    PrmTab.CONNECTION,
  ];

  public static reducedTabs: Tab[] = [
    PrmTab.STOP_POINT,
    PrmTab.PLATFORM,
    PrmTab.TICKET_COUNTER,
    PrmTab.INFORMATION_DESK,
    PrmTab.TOILET,
    PrmTab.PARKING_LOT,
  ];

  public static completeTabs: Tab[] = [PrmTab.REFERENCE_POINT, PrmTab.CONNECTION];
}
