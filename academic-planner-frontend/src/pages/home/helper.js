import moment from 'moment';

export const formatEventData = (data) => {
  return data.map((event) => ({
    start: moment(
      event.startDate + ' ' + event.startTime,
      'YYYY-MM-DD HH:mm:ss'
    ),
    end: moment(event.endDate + ' ' + event.endTime, 'YYYY-MM-DD HH:mm:ss'),
    title: event.name,
  }));
};
