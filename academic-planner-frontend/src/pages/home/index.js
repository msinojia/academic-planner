import React, { useEffect, useState } from 'react';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import VariableEventModal from './components/variableEvent';
import dayjs from 'dayjs';
import { fetchEvents } from './api';
import { formatEventData } from './helper';

const localizer = momentLocalizer(moment);
const momentFormatString = 'ddd MMM DD YYYY HH:mm:ss [GMT]ZZ';
const formatString = 'HH:mm:ss';

const CalendarViewHome = () => {
  const DATE_FORMAT = 'YYYY-MM-DD';

  useEffect(() => {
    var today = moment();
    const start = today.startOf('week').format(DATE_FORMAT);
    const end = today.endOf('week').format(DATE_FORMAT);

    fetchEventsByRange(start, end);
  }, []);

  const [events, setEvents] = useState([
    {
      start: moment().subtract(4, 'hours').toDate(),
      end: moment().subtract(3, 'hours').toDate(),
      title: 'Event 1',
    },
    {
      start: moment().toDate(),
      end: moment().add(1, 'hours').toDate(),
      title: 'Event 2',
    },
  ]);
  const [formValues, setFormValues] = useState({
    isAdd: true,
    isModalOpen: false,
    handleCancel: () =>
      setFormValues({ ...formValues, isModalOpen: false, data: {} }),
    data: {},
  });

  const fetchEventsByRange = async (start, end) => {
    const res = await fetchEvents(start, end);
    console.log(res.data);
    setEvents(formatEventData(res.data));
    console.log(formatEventData(res.data));
  };

  // const [view, setView] = useState('week');
  // const [dateRange, setDateRange] = useState({});

  const handleNavigate = (dateOrObject, view) => {
    const { start, end } =
      dateOrObject instanceof Date
        ? {
            start: dateOrObject,
            end: moment(dateOrObject).endOf(view).toDate(),
          }
        : dateOrObject;
    console.log({ start, end, view });
    fetchEventsByRange(
      moment(start).startOf(view).format(DATE_FORMAT),
      moment(end).format(DATE_FORMAT)
    );
  };

  // console.log('formValues', formValues);
  const handleSelect = ({ start, end }) => {
    // const title = window.prompt('New Event name');
    // console.log(start);
    // console.log(end);

    const momDate = moment(start, momentFormatString);
    // console.log(
    //   start,
    //   momDate,
    //   momDate.format('LTS'),
    //   moment(end, momentFormatString).format('LTS'),
    //   momDate.format('L')
    // );

    setFormValues({
      ...formValues,
      isAdd: true,
      isModalOpen: true,
      data: {
        eventDate: moment(momDate.format('L'), 'L'),
        startTime: dayjs(momDate.format(formatString), formatString),
        endTime: dayjs(
          moment(end, momentFormatString).format(formatString),
          formatString
        ),
      },
    });
    // if (title) {
    //   setEvents([...events, { start, end, title }]);
    // }
  };

  const handleEventClick = (event) => {
    // const title = window.prompt('New Event name', event.title);
    // console.log(typeof event.start);
    // if (title) {
    //   const updatedEvents = events.map((e) => {
    //     if (e === event) {
    //       return { ...e, title };
    //     } else {
    //       return e;
    //     }
    //   });
    //   setEvents(updatedEvents);
    // }
    const { start, end, title } = event;
    const momDate = moment(start);
    setFormValues({
      ...formValues,
      isAdd: true,
      isModalOpen: true,
      data: {
        name: title,
        eventDate: moment(momDate.format('L')),
        startTime: dayjs(momDate.format('LTS'), 'HH:mm:ss A'),
        endTime: dayjs(moment(end).format('LTS'), 'HH:mm:ss A'),
      },
    });
  };

  return (
    <div className='myCustomHeight'>
      <Calendar
        localizer={localizer}
        events={events}
        defaultDate={new Date()}
        defaultView={'week'}
        startAccessor='start'
        endAccessor='end'
        selectable
        onNavigate={handleNavigate}
        onSelectSlot={handleSelect}
        onSelectEvent={handleEventClick}
        style={{ height: 700 }}
      />
      {formValues.data && <VariableEventModal {...formValues} />}
    </div>
  );
};

export default CalendarViewHome;
