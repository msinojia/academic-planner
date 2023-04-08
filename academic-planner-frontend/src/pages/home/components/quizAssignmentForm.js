import React, { useEffect } from 'react';
import {
  TimePicker,
  Modal,
  Form,
  Input,
  DatePicker,
  Radio,
  InputNumber,
} from 'antd';
import moment from 'moment';
import { addVariableEvent } from '../api';

const QuizAssignmentForm = (props) => {
  const [form] = Form.useForm();

  useEffect(() => {
    !props.isModalOpen && form.resetFields();
  }, [props]);

  const onSubmit = () => {
    form
      .validateFields()
      .then((values) => {
        console.log(values);
        const reqBody = {
          name: values.name,
          details: values.details,
          eventPriority: values.eventPriority,
          deadline: moment(
            moment(values.eventDate).format('YYYY-MM-DD') +
              ' ' +
              moment(values.startTime).format('HH:mm:ss')
          ).format('YYYY-MM-DDTHH:mm:ss'),
          duration: `PT0H${values.duration}M`,
        };
        addVariableEvent(reqBody);
        window.location.reload();
      })
      .catch((err) => {
        console.log(err);
      });
  };
  // WeekDays;
  console.log({ props });
  return props.isModalOpen ? (
    <Modal
      title={'Add New Schedule'}
      open={props.isModalOpen}
      onOk={onSubmit}
      okText={'Save'}
      onCancel={props.handleCancel}
    >
      <Form form={form}>
        <Form.Item
          name={'name'}
          label='Name'
          rules={[{ required: 'true', message: 'Enter Schedule Name!' }]}
        >
          <Input placeholder='Enter Name' />
        </Form.Item>
        <Form.Item label='Description' name={'details'}>
          <Input placeholder='Enter Description' />
        </Form.Item>
        <Form.Item
          name='eventDate'
          label='Date of Deadline'
          hasFeedback
          rules={[{ required: true, message: 'Please select a day!' }]}
        >
          <DatePicker />
        </Form.Item>
        <Form.Item
          label='Time of Deadline'
          name={'startTime'}
          rules={[
            {
              type: 'object',
              required: 'true',
              message: 'Select Start Time!',
            },
          ]}
        >
          <TimePicker format={'HH:mm'} />
        </Form.Item>
        <Form.Item
          label='Priority'
          name='eventPriority'
          rules={[
            {
              required: 'true',
              message: 'Select Priority!',
            },
          ]}
        >
          <Radio.Group>
            <Radio.Button value='HIGH'>High</Radio.Button>
            <Radio.Button value='MEDIUM'>Medium</Radio.Button>
            <Radio.Button value='LOW'>Low</Radio.Button>
          </Radio.Group>
        </Form.Item>
        <Form.Item
          label='Approx Time Duration'
          name={'duration'}
          rules={[
            {
              type: 'number',
              required: 'true',
              message: 'Enter approx time duration!',
            },
          ]}
        >
          <InputNumber min={1} />
        </Form.Item>
      </Form>
    </Modal>
  ) : (
    <></>
  );
};

export default QuizAssignmentForm;
