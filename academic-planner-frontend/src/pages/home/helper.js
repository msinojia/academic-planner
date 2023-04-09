import moment from 'moment';

export const formatEventData = (data) => {
  return data.map((event) => {
    return {
      start: moment(
        event.startDate + ' ' + event.startTime,
        'YYYY-MM-DD HH:mm:ss'
      ).toDate(),
      end: moment(
        event.endDate + ' ' + event.endTime,
        'YYYY-MM-DD HH:mm:ss'
      ).toDate(),
      title: event.name,
      eventType: event.eventType,
    };
  });
};
