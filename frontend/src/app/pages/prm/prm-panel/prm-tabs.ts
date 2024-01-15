import { Pages } from '../../pages';

export const PrmTabs = {
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
  PrmTabs.STOP_POINT,
  PrmTabs.REFERENCE_POINT,
  PrmTabs.PLATFORM,
  PrmTabs.TICKET_COUNTER,
  PrmTabs.INFORMATION_DESK,
  PrmTabs.TOILET,
  PrmTabs.PARKING_LOT,
  PrmTabs.CONNECTION,
];

export const PRM_REDUCED_TABS = [
  PrmTabs.STOP_POINT,
  PrmTabs.PLATFORM,
  PrmTabs.TICKET_COUNTER,
  PrmTabs.INFORMATION_DESK,
  PrmTabs.TOILET,
  PrmTabs.PARKING_LOT,
];

export const PRM_COMPLETE_TABS = [PrmTabs.REFERENCE_POINT, PrmTabs.CONNECTION];
