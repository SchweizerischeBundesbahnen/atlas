import { Pages } from '../../pages';

export const PrmTab = {
  STOP_POINT: {
    link: Pages.PRM_STOP_POINT_TAB.path,
    title: Pages.PRM_STOP_POINT_TAB.title,
  },
  REFERENCE_POINT: {
    link: 'reference-point',
    title: 'PRM.TABS.REFERENCE_POINT',
  },
  PLATFORM: {
    link: 'platform',
    title: 'PRM.TABS.PLATFORM',
  },

  TICKET_COUNTER: {
    link: 'ticket-counter',
    title: 'PRM.TABS.TICKET_COUNTER',
  },

  INFORMATION_DESK: {
    link: 'information-desk',
    title: 'PRM.TABS.INFORMATION_DESK',
  },

  TOILET: {
    link: 'toilet',
    title: 'PRM.TABS.TOILET',
  },
  PARKING_LOT: {
    link: 'parking-lot',
    title: 'PRM.TABS.PARKING_LOT',
  },
  CONNECTION: {
    link: 'connection',
    title: 'PRM.TABS.CONNECTION',
  },
};
export const PRM_TABS = [
  PrmTab.STOP_POINT,
  PrmTab.REFERENCE_POINT,
  PrmTab.PLATFORM,
  PrmTab.TICKET_COUNTER,
  PrmTab.INFORMATION_DESK,
  PrmTab.TOILET,
  PrmTab.PARKING_LOT,
  PrmTab.CONNECTION,
];

export const PRM_REDUCED_TABS = [
  PrmTab.STOP_POINT,
  PrmTab.PLATFORM,
  PrmTab.TICKET_COUNTER,
  PrmTab.INFORMATION_DESK,
  PrmTab.TOILET,
  PrmTab.PARKING_LOT,
];

export const PRM_COMPLETE_TABS = [PrmTab.REFERENCE_POINT, PrmTab.CONNECTION];
