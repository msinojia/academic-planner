import React, { useEffect, useState } from 'react';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment';
import 'react-big-calendar/lib/css/react-big-calendar.css';
import VariableEventModal from './components/variableEvent';
import dayjs from 'dayjs';
import { fetchEvents } from './api';
import { formatEventData } from './helper';
import { Spin, message } from 'antd';
import { LoadingOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';

const localizer = momentLocalizer(moment);
const momentFormatString = 'ddd MMM DD YYYY HH:mm:ss [GMT]ZZ';
const formatString = 'HH:mm:ss';

const CalendarViewHome = () => {
  const DATE_FORMAT = 'YYYY-MM-DD';
  const navigate = useNavigate();
  useEffect(() => {
    if (!localStorage.getItem('jwtToken')) navigate('/login');
    else {
      var today = moment();
      const start = today.startOf('week').format(DATE_FORMAT);
      const end = today.endOf('week').format(DATE_FORMAT);

      fetchEventsByRange(start, end);
    }
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
  const blanckForm = {
    isAdd: true,
    isModalOpen: false,
    handleCancel: () =>
      setFormValues({ ...formValues, isModalOpen: false, data: {} }),
    data: {},
  };
  const [formValues, setFormValues] = useState({ ...blanckForm });

  const [startDate, setStartDate] = useState(moment());

  const [isLoading, setIsLoading] = useState(false);

  const fetchEventsByRange = async (start, end) => {
    setIsLoading(true);
    try {
      const res = await fetchEvents(start, end);
      console.log({ res }, 'res.data', res.data);
      setEvents(formatEventData(res.data));
      console.log('formatEventData', formatEventData(res.data));
    } catch (e) {
      message.error('Something went wrong while fetching data');
    }
    setIsLoading(false);
  };

  const handleNavigate = (dateOrObject, view) => {
    const { start, end } =
      dateOrObject instanceof Date
        ? {
            start: dateOrObject,
            end: moment(dateOrObject).endOf(view).toDate(),
          }
        : dateOrObject;
    console.log({ start, end, view });
    setStartDate(moment(start));
    fetchEventsByRange(
      moment(start).startOf(view).format(DATE_FORMAT),
      moment(end).format(DATE_FORMAT)
    );
  };

  const handleSelect = ({ start, end }) => {
    const momDate = moment(start, momentFormatString);

    setFormValues({
      ...blanckForm,
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
  };

  const handleEventClick = (event) => {
    const { start, end, title } = event;
    const momDate = moment(start);

    setFormValues({
      ...blanckForm,
      isAdd: false,
      isModalOpen: true,
      data: {
        name: title,
        eventDate: moment(momDate.format('L')),
        startTime: dayjs(momDate.format('LTS'), 'HH:mm:ss A'),
        endTime: dayjs(moment(end).format('LTS'), 'HH:mm:ss A'),
      },
    });
  };

  const CustomToolbar = (props) => (
    <div className='rbc-toolbar'>
      <span className='rbc-btn-group'>
        <span className='rbc-btn-group'>
          <button onClick={() => props.onNavigate('TODAY')}>Today</button>
          <button onClick={() => props.onNavigate('PREV')}>Back</button>
          <button onClick={() => props.onNavigate('NEXT')}>Next</button>
        </span>
      </span>
      <span className='rbc-toolbar-label'>{props.label}</span>
      <span className='rbc-btn-group'>
        {props.views.map((view) => (
          <button
            key={view}
            className={view === props.view ? 'rbc-active' : ''}
            onClick={() => props.onView(view)}
          >
            {view}
          </button>
        ))}
      </span>
    </div>
  );

  const handleViewChange = (view, temp) => {
    console.log('view', view);
    fetchEventsByRange(
      moment(startDate).startOf(view).format(DATE_FORMAT),
      moment(startDate).endOf(view).format(DATE_FORMAT)
    );
  };

  const eventStyleGetter = (event, start, end, isSelected) => {
    const backgroundColor = event.eventType === 'FIXED' ? undefined : '#6F2DA8';
    const style = {
      backgroundColor,
      borderRadius: '5px',
      opacity: 0.8,
      color: 'white',
      border: '0px',
      display: 'block',
    };
    return {
      style: style,
    };
  };

  const views = {
    month: true,
    week: true,
    day: true,
    agenda: false,
  };

  return (
    <div className='myCustomHeight'>
      <Spin
        spinning={isLoading}
        tip='Loading...'
        indicator={<LoadingOutlined style={{ fontSize: 24 }} spin />}
      >
        <Calendar
          localizer={localizer}
          events={events}
          defaultDate={new Date()}
          defaultView={'week'}
          startAccessor='start'
          endAccessor='end'
          onView={handleViewChange}
          views={views}
          components={{
            toolbar: CustomToolbar,
          }}
          selectable
          onNavigate={handleNavigate}
          onSelectSlot={handleSelect}
          onSelectEvent={handleEventClick}
          style={{ height: 700, margin: '20px' }}
          eventPropGetter={eventStyleGetter}
        />
      </Spin>
      {formValues.isModalOpen && <VariableEventModal {...formValues} />}
    </div>
  );
};

export default CalendarViewHome;
